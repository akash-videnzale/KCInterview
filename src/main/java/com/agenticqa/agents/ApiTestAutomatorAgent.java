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
 * Generates REST Assured + TestNG API automation drafts from approved API cases.
 */
public final class ApiTestAutomatorAgent implements Agent {

    private final LlmClient client;
    private final AgentOutputWriter writer;

    public ApiTestAutomatorAgent(LlmConfig config, LlmClientFactory factory, AgentOutputWriter writer) {
        this.client = factory.forRole("AUTOMATOR_LLM", LlmProvider.ANTHROPIC);
        this.writer = writer;
    }

    @Override
    public String name() {
        return "api-test-automator";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        String apiCases = context.artifact("apiTestCases");
        if (apiCases.isBlank()) {
            throw new IllegalStateException("apiTestCases missing");
        }

        String system = """
                You are an API Automation Agent for a Java 21 TestNG REST Assured framework.
                Generate draft Java test class(es) that:
                - extend ApiBaseTest
                - use ProductApiClient / UserApiClient / AuthenticationApiClient
                - use TestNG Assert/SoftAssert only
                - use groups api,smoke,regression
                - never call RestAssured.given() directly in tests
                - never hardcode credentials/URLs
                Prioritize P0 then P1 cases. Mark P2 as comments if skipped.
                Output only Java code blocks with file path comments like // FILE: src/test/java/...
                """;
        LlmResponse response = client.complete(LlmRequest.of(system, apiCases));
        writer.write("05-api-automation-draft.md", response.content());
        context.putArtifact("apiAutomationDraft", response.content());
    }
}
