package com.agenticqa.agents;

import com.agenticqa.agents.llm.LlmConfig;
import com.agenticqa.agents.output.AgentOutputWriter;
import com.agenticqa.agents.pipeline.AgentPipeline;
import com.agenticqa.agents.pipeline.PipelineContext;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * CLI entrypoint for the LLM agent pipeline.
 *
 * Examples:
 *   mvn -q exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
 *     -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"
 *
 *   mvn -q exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
 *     -Dexec.args="full --requirement docs/examples/sample-requirement.md --groups smoke"
 */
public final class AgentCli {

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || "--help".equals(args[0]) || "help".equals(args[0])) {
            printHelp();
            return;
        }

        String mode = args[0];
        Path requirementPath = null;
        String requirementText = null;
        String changeDiff = "";
        String groups = "smoke";

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--requirement" -> requirementPath = Path.of(args[++i]);
                case "--requirement-text" -> requirementText = args[++i];
                case "--diff" -> changeDiff = Files.readString(Path.of(args[++i]));
                case "--groups" -> groups = args[++i];
                default -> throw new IllegalArgumentException("Unknown arg: " + args[i]);
            }
        }

        Path out = AgentOutputWriter.defaultRunDir();
        AgentOutputWriter writer = new AgentOutputWriter(out);
        PipelineContext context = new PipelineContext(out);
        if (requirementText != null) {
            context.putArtifact("requirement", requirementText);
        }
        if (requirementPath != null) {
            context.putMeta("requirementPath", requirementPath);
            context.putArtifact("requirement", Files.readString(requirementPath));
        }
        context.putArtifact("changeDiff", changeDiff);
        context.putMeta("executeGroups", groups);

        LlmConfig config = new LlmConfig();
        AgentPipeline pipeline = switch (mode) {
            case "analyze" -> AgentPipeline.analysisOnly(config, writer);
            case "full" -> AgentPipeline.full(config, writer);
            case "execute" -> new AgentPipeline(java.util.List.of(
                    new AutomationExecutorAgent(writer),
                    new FailedTestReexecutorAgent(writer),
                    new ReportAnalyzerAgent(config, new com.agenticqa.agents.llm.LlmClientFactory(config), writer),
                    new EmailReportSenderAgent(config, writer)
            ), writer);
            default -> throw new IllegalArgumentException("Unknown mode: " + mode + " (use analyze|full|execute)");
        };

        pipeline.run(context);
        System.out.println("Agent pipeline complete. Output: " + out.toAbsolutePath());
        System.out.println("Provider for requirement analysis: " + context.meta("requirementProvider"));
    }

    private static void printHelp() {
        System.out.println("""
                AgentCli modes:
                  analyze  — requirement analysis + test case gen + reviewer
                  full     — analyze + automators + execute + reexec + report + email
                  execute  — run tests + reexec + report + email

                Options:
                  --requirement <file>
                  --requirement-text <text>
                  --diff <patch-file>
                  --groups <testng-groups>   (default: smoke)
                """);
    }
}
