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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Executes Maven/TestNG automation. No LLM dependency.
 */
public final class AutomationExecutorAgent implements Agent {

    private final AgentOutputWriter writer;

    public AutomationExecutorAgent(AgentOutputWriter writer) {
        this.writer = writer;
    }

    @Override
    public String name() {
        return "automation-executor";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        String groups = String.valueOf(context.meta("executeGroups"));
        if (groups == null || groups.isBlank() || "null".equals(groups)) {
            groups = "smoke";
        }
        // Drop stale Surefire XMLs so the HTML report only reflects this run.
        clearSurefireReports();

        List<String> command = List.of("mvn", "-q", "test", "-Dgroups=" + groups);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        pb.directory(Path.of(".").toAbsolutePath().normalize().toFile());

        Process process = pb.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        }
        boolean finished = process.waitFor(45, TimeUnit.MINUTES);
        int code = finished ? process.exitValue() : -1;
        if (!finished) {
            process.destroyForcibly();
            output.append("\nERROR: timed out after 45 minutes\n");
        }

        Path log = writer.write("07-execution-log.txt", output.toString());
        context.putArtifact("executionLog", output.toString());
        context.putMeta("executionExitCode", code);
        context.putMeta("executionLogPath", log);
        context.putMeta("failedTests", parseFailedTests(output.toString()));
    }

    private static void clearSurefireReports() {
        Path reports = Path.of("target/surefire-reports");
        if (!Files.isDirectory(reports)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(reports)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    if (!path.equals(reports)) {
                        Files.deleteIfExists(path);
                    }
                } catch (Exception ignored) {
                    // best-effort cleanup
                }
            });
        } catch (Exception ignored) {
            // best-effort cleanup
        }
    }

    @SuppressWarnings("unchecked")
    static List<String> parseFailedTests(String log) {
        List<String> failed = new ArrayList<>();
        for (String line : log.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.contains("<<< FAILURE!") || trimmed.matches(".*Test\\.\\w+.*FAILURE.*")) {
                failed.add(trimmed);
            }
            if (trimmed.startsWith("[ERROR]") && trimmed.contains("Test.")) {
                failed.add(trimmed);
            }
        }
        return failed;
    }
}
