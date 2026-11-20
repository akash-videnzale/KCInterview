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

/**
 * Review-only sufficiency check focused on P0/P1/P2 coverage.
 */
public final class TestCaseReviewerAgent implements Agent {

    private final LlmClient client;
    private final AgentOutputWriter writer;

    public TestCaseReviewerAgent(LlmConfig config, LlmClientFactory factory, AgentOutputWriter writer) {
        this.client = factory.forRole("REVIEWER_LLM", LlmProvider.ANTHROPIC);
        this.writer = writer;
    }

    @Override
    public String name() {
        return "test-case-reviewer";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        String ui = context.artifact("uiTestCases");
        String api = context.artifact("apiTestCases");
        String analysis = context.artifact("requirementAnalysis");
        if (ui.isBlank() && api.isBlank()) {
            throw new IllegalStateException("No generated test cases to review");
        }

        String system = """
                You are a QA Reviewer. Review-only — do not rewrite full suites unless listing missing cases.
                Focus on P0 and P1 sufficiency first, then P2 gaps.
                Output Markdown:
                1. Verdict: SUFFICIENT | INSUFFICIENT
                2. P0 coverage assessment
                3. P1 coverage assessment
                4. P2 gaps (optional)
                5. Missing critical cases (bullet list)
                6. Redundant / low-value cases
                7. Recommendation for automation order
                """;
        String user = """
                Requirement analysis:
                %s

                UI cases:
                %s

                API cases:
                %s
                """.formatted(analysis, ui, api);

        LlmResponse response = client.complete(LlmRequest.of(system, user));
        writer.write("04-test-case-review.md", response.content());
        context.putArtifact("testCaseReview", response.content());
        boolean sufficient = response.content().toUpperCase().contains("SUFFICIENT")
                && !response.content().toUpperCase().contains("INSUFFICIENT");
        // Prefer explicit INSUFFICIENT if both words appear
        if (response.content().toUpperCase().contains("VERDICT: INSUFFICIENT")
                || response.content().toUpperCase().contains("VERDICT:INSUFFICIENT")) {
            sufficient = false;
        } else if (response.content().toUpperCase().contains("VERDICT: SUFFICIENT")
                || response.content().toUpperCase().contains("VERDICT:SUFFICIENT")) {
            sufficient = true;
        }
        context.putMeta("testCasesSufficient", sufficient);
    }
}
