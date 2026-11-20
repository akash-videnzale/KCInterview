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
 * Generates Playwright Java + TestNG UI automation drafts from approved UI cases.
 */
public final class UiTestAutomatorAgent implements Agent {

    private final LlmClient client;
    private final AgentOutputWriter writer;

    public UiTestAutomatorAgent(LlmConfig config, LlmClientFactory factory, AgentOutputWriter writer) {
        this.client = factory.forRole("AUTOMATOR_LLM", LlmProvider.ANTHROPIC);
        this.writer = writer;
    }

    @Override
    public String name() {
        return "ui-test-automator";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        String uiCases = context.artifact("uiTestCases");
        if (uiCases.isBlank()) {
            throw new IllegalStateException("uiTestCases missing");
        }

        String system = """
                You are a UI Automation Agent for Java 21 Playwright TestNG.
                Generate draft Java tests that:
                - extend UiBaseTest
                - use existing page objects (HomePage, LoginPage, SignupPage, ProductsPage, CartPage, CheckoutPage, PaymentPage, AccountPage)
                - use TestNG Assert/SoftAssert
                - groups ui,smoke,regression
                - no Thread.sleep / waitForTimeout / Playwright.create in tests
                - use RegistrationDataFactory for unique users
                Prioritize P0 then P1. Output Java code blocks with // FILE: path comments.
                """;
        LlmResponse response = client.complete(LlmRequest.of(system, uiCases));
        writer.write("06-ui-automation-draft.md", response.content());
        context.putArtifact("uiAutomationDraft", response.content());
    }
}
