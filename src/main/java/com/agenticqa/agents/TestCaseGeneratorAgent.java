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
 * Generates separate UI and API test cases tagged P0 / P1 / P2.
 */
public final class TestCaseGeneratorAgent implements Agent {

    private final LlmClient client;
    private final AgentOutputWriter writer;

    public TestCaseGeneratorAgent(LlmConfig config, LlmClientFactory factory, AgentOutputWriter writer) {
        this.client = factory.forRole("TESTGEN_LLM", LlmProvider.OPENAI);
        this.writer = writer;
    }

    @Override
    public String name() {
        return "test-case-generator";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        String analysis = context.artifact("requirementAnalysis");
        if (analysis.isBlank()) {
            throw new IllegalStateException("requirementAnalysis missing — run RequirementAnalysisAgent first");
        }

        String system = """
                You are a Staff QA Engineer. Generate structured test cases for Automation Exercise.
                Produce TWO Markdown documents in one response separated by the exact markers:
                <<<UI_TEST_CASES>>>
                ...ui cases...
                <<<API_TEST_CASES>>>
                ...api cases...
                <<<END>>>

                Rules:
                - Separate UI and API cases.
                - Tag every case with Priority: P0 | P1 | P2.
                - P0 = smoke/critical business path; P1 = important regression; P2 = edge/negative/nice-to-have.
                - Include: ID, Title, Priority, Preconditions, Steps, Expected Result.
                - Do not invent unsupported endpoints or UI pages.
                - Prefer verified AE APIs: productsList, searchProduct, createAccount, verifyLogin, deleteAccount, getUserDetailByEmail, brandsList.
                """;
        String user = "Requirement analysis:\n" + analysis;
        LlmResponse response = client.complete(new LlmRequest(system, user, 0.2, 8192));

        String content = response.content();
        String ui = extract(content, "<<<UI_TEST_CASES>>>", "<<<API_TEST_CASES>>>");
        String api = extract(content, "<<<API_TEST_CASES>>>", "<<<END>>>");
        if (ui.isBlank()) {
            ui = content;
        }
        if (api.isBlank()) {
            api = "(API section missing from model output — regenerate)";
        }

        writer.write("02-ui-test-cases.md", ui.trim());
        writer.write("03-api-test-cases.md", api.trim());
        writer.write("02-03-raw-test-generation.md", content);
        context.putArtifact("uiTestCases", ui.trim());
        context.putArtifact("apiTestCases", api.trim());
    }

    private static String extract(String content, String startMarker, String endMarker) {
        int start = content.indexOf(startMarker);
        if (start < 0) {
            return "";
        }
        start += startMarker.length();
        int end = content.indexOf(endMarker, start);
        if (end < 0) {
            return content.substring(start).trim();
        }
        return content.substring(start, end).trim();
    }
}
