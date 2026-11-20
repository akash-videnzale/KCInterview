package com.agenticqa.agents;

import com.agenticqa.agents.output.AgentOutputWriter;
import com.agenticqa.agents.pipeline.Agent;
import com.agenticqa.agents.pipeline.PipelineContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Re-runs failed tests once to reconfirm flakes vs real failures. No LLM.
 */
public final class FailedTestReexecutorAgent implements Agent {

    private static final Pattern CLASS_METHOD = Pattern.compile("([\\w.]+)\\.(\\w+)\\s*(?:--|<<<)");

    private final AgentOutputWriter writer;

    public FailedTestReexecutorAgent(AgentOutputWriter writer) {
        this.writer = writer;
    }

    @Override
    public String name() {
        return "failed-test-reexecutor";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        Object exit = context.meta("executionExitCode");
        if (exit instanceof Integer code && code == 0) {
            writer.write("08-reexecution-log.txt", "No failures to re-execute (previous exit code 0).");
            context.putMeta("reexecutionSkipped", true);
            return;
        }

        Set<String> methods = new LinkedHashSet<>();
        Object failedObj = context.meta("failedTests");
        if (failedObj instanceof List<?> list) {
            for (Object item : list) {
                methods.addAll(extractTestSelectors(String.valueOf(item)));
            }
        }
        // Also scan surefire reports if present
        Path reports = Path.of("target/surefire-reports");
        if (Files.isDirectory(reports)) {
            try (var walk = Files.walk(reports)) {
                walk.filter(p -> p.getFileName().toString().endsWith(".txt"))
                        .forEach(p -> {
                            try {
                                methods.addAll(extractTestSelectors(Files.readString(p)));
                            } catch (Exception ignored) {
                                // best-effort
                            }
                        });
            }
        }

        if (methods.isEmpty()) {
            // Fallback: re-run smoke group once
            methods.add("");
        }

        StringBuilder all = new StringBuilder();
        int lastCode = 0;
        for (String selector : methods) {
            List<String> cmd = new ArrayList<>();
            cmd.add("mvn");
            cmd.add("-q");
            cmd.add("test");
            if (selector == null || selector.isBlank()) {
                cmd.add("-Dgroups=smoke");
                all.append("Re-running groups=smoke\n");
            } else {
                cmd.add("-Dtest=" + selector);
                all.append("Re-running -Dtest=").append(selector).append('\n');
            }
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            pb.directory(Path.of(".").toAbsolutePath().normalize().toFile());
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    all.append(line).append('\n');
                }
            }
            boolean finished = process.waitFor(30, TimeUnit.MINUTES);
            lastCode = finished ? process.exitValue() : -1;
            all.append("exitCode=").append(lastCode).append("\n\n");
            if (!finished) {
                process.destroyForcibly();
                break;
            }
        }

        writer.write("08-reexecution-log.txt", all.toString());
        context.putArtifact("reexecutionLog", all.toString());
        context.putMeta("reexecutionExitCode", lastCode);
    }

    static List<String> extractTestSelectors(String text) {
        List<String> selectors = new ArrayList<>();
        Matcher matcher = CLASS_METHOD.matcher(text);
        while (matcher.find()) {
            String fqcn = matcher.group(1);
            String method = matcher.group(2);
            if (fqcn.contains("Test") || fqcn.contains("IT")) {
                String simple = fqcn.contains(".") ? fqcn.substring(fqcn.lastIndexOf('.') + 1) : fqcn;
                selectors.add(simple + "#" + method);
            }
        }
        return selectors;
    }
}
