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
 * Requirement + change analysis using GPT by default (REQUIREMENT_LLM).
 */
public final class RequirementAnalysisAgent implements Agent {

    private final LlmClient client;
    private final AgentOutputWriter writer;

    public RequirementAnalysisAgent(LlmConfig config, LlmClientFactory factory, AgentOutputWriter writer) {
        this.client = factory.forRole("REQUIREMENT_LLM", LlmProvider.OPENAI);
        this.writer = writer;
    }

    @Override
    public String name() {
        return "requirement-analysis";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        String requirement = context.artifact("requirement");
        if (requirement.isBlank()) {
            Object path = context.meta("requirementPath");
            if (path instanceof Path p && Files.exists(p)) {
                requirement = Files.readString(p);
            }
        }
        if (requirement.isBlank()) {
            throw new IllegalArgumentException("No requirement text provided. Set artifact 'requirement' or meta requirementPath.");
        }

        String changeDiff = context.artifact("changeDiff");
        String system = """
                You are a Principal QA Architect for Automation Exercise (https://automationexercise.com/).
                Analyze requirements and optional change diffs.
                Output Markdown with sections:
                1. Summary
                2. In-scope journeys (UI / API)
                3. Out-of-scope / unsupported assumptions
                4. Impacted components
                5. Risks
                6. Suggested P0 / P1 / P2 test themes
                Do not invent unsupported APIs or UI flows.
                """;
        String user = """
                REQUIREMENT:
                %s

                CHANGE DIFF (optional):
                %s
                """.formatted(requirement, changeDiff.isBlank() ? "(none)" : changeDiff);

        LlmResponse response = client.complete(LlmRequest.of(system, user));
        Path file = writer.write("01-requirement-analysis.md", response.content());
        context.putArtifact("requirementAnalysis", response.content());
        context.putMeta("requirementAnalysisPath", file);
        context.putMeta("requirementProvider", response.provider() + "/" + response.model());
    }
}
