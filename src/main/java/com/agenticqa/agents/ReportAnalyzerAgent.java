package com.agenticqa.agents;

import com.agenticqa.agents.llm.LlmClient;
import com.agenticqa.agents.llm.LlmClientFactory;
import com.agenticqa.agents.llm.LlmConfig;
import com.agenticqa.agents.llm.LlmProvider;
import com.agenticqa.agents.llm.LlmRequest;
import com.agenticqa.agents.llm.LlmResponse;
import com.agenticqa.agents.output.AgentOutputWriter;
import com.agenticqa.agents.pipeline.Agent;
import com.agenticqa.agents.pipeline.PipelineContext;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Analyzes execution + re-execution logs and produces a human report (DeepSeek by default).
 */
public final class ReportAnalyzerAgent implements Agent {

    private final LlmClient client;
    private final AgentOutputWriter writer;

    public ReportAnalyzerAgent(LlmConfig config, LlmClientFactory factory, AgentOutputWriter writer) {
        this.client = factory.forRole("REPORT_LLM", LlmProvider.DEEPSEEK);
        this.writer = writer;
    }

    @Override
    public String name() {
        return "report-analyzer";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        String execution = context.artifact("executionLog");
        String reexec = context.artifact("reexecutionLog");
        if (execution.isBlank() && Files.exists(Path.of("target/logs/agentic-qa.log"))) {
            execution = Files.readString(Path.of("target/logs/agentic-qa.log"));
        }

        String system = """
                You are a QA Report Analyst.
                Classify failures as APPLICATION_DEFECT / AUTOMATION_DEFECT / TEST_DATA_DEFECT / ENVIRONMENT_DEFECT / FLAKY / UNKNOWN.
                Note that re-execution is used to reconfirm flakes.
                Output Markdown executive summary with:
                1. Overall status
                2. Pass/fail highlights
                3. Failure classification table
                4. Flaky candidates (failed then passed on rerun, or unstable)
                5. Recommended next actions
                Be honest when evidence is insufficient.
                """;
        String user = """
                Execution log:
                %s

                Re-execution log:
                %s

                Meta exit codes: execution=%s reexecution=%s
                """.formatted(
                truncate(execution, 12000),
                truncate(reexec, 8000),
                String.valueOf(context.meta("executionExitCode")),
                String.valueOf(context.meta("reexecutionExitCode"))
        );

        LlmResponse response = client.complete(LlmRequest.of(system, user));
        writer.write("09-report-analysis.md", response.content());
        context.putArtifact("reportAnalysis", response.content());
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max) + "\n...[truncated]...";
    }
}
