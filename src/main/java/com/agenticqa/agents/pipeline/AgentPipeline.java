package com.agenticqa.agents.pipeline;

import com.agenticqa.agents.ApiTestAutomatorAgent;
import com.agenticqa.agents.AutomationExecutorAgent;
import com.agenticqa.agents.EmailReportSenderAgent;
import com.agenticqa.agents.ExecutionHtmlReportAgent;
import com.agenticqa.agents.FailedTestReexecutorAgent;
import com.agenticqa.agents.ReportAnalyzerAgent;
import com.agenticqa.agents.RequirementAnalysisAgent;
import com.agenticqa.agents.TestCaseGeneratorAgent;
import com.agenticqa.agents.TestCaseReviewerAgent;
import com.agenticqa.agents.UiTestAutomatorAgent;
import com.agenticqa.agents.llm.LlmClientFactory;
import com.agenticqa.agents.llm.LlmConfig;
import com.agenticqa.agents.output.AgentOutputWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates LLM + deterministic agents.
 * TestNG tests themselves remain LLM-free.
 */
public final class AgentPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(AgentPipeline.class);

    private final List<Agent> agents;
    private final AgentOutputWriter writer;

    public AgentPipeline(List<Agent> agents, AgentOutputWriter writer) {
        this.agents = List.copyOf(agents);
        this.writer = writer;
    }

    public PipelineContext run(PipelineContext context) throws Exception {
        Files.createDirectories(context.outputDir());
        StringBuilder summary = new StringBuilder("# Pipeline run\n\n");
        for (Agent agent : agents) {
            LOG.info("Running agent: {}", agent.name());
            long start = System.currentTimeMillis();
            agent.run(context);
            long ms = System.currentTimeMillis() - start;
            summary.append("- ").append(agent.name()).append(" (").append(ms).append(" ms)\n");
            LOG.info("Completed agent: {} in {} ms", agent.name(), ms);
        }
        writer.write("00-pipeline-summary.md", summary.toString());
        return context;
    }

    public static AgentPipeline full(LlmConfig config, AgentOutputWriter writer) {
        LlmClientFactory factory = new LlmClientFactory(config);
        List<Agent> agents = new ArrayList<>();
        agents.add(new RequirementAnalysisAgent(config, factory, writer));
        agents.add(new TestCaseGeneratorAgent(config, factory, writer));
        agents.add(new TestCaseReviewerAgent(config, factory, writer));
        agents.add(new ApiTestAutomatorAgent(config, factory, writer));
        agents.add(new UiTestAutomatorAgent(config, factory, writer));
        agents.add(new AutomationExecutorAgent(writer));
        agents.add(new FailedTestReexecutorAgent(writer));
        agents.add(new ExecutionHtmlReportAgent(writer));
        agents.add(new ReportAnalyzerAgent(config, factory, writer));
        agents.add(new EmailReportSenderAgent(config, writer));
        return new AgentPipeline(agents, writer);
    }

    public static AgentPipeline analysisOnly(LlmConfig config, AgentOutputWriter writer) {
        LlmClientFactory factory = new LlmClientFactory(config);
        return new AgentPipeline(List.of(
                new RequirementAnalysisAgent(config, factory, writer),
                new TestCaseGeneratorAgent(config, factory, writer),
                new TestCaseReviewerAgent(config, factory, writer)
        ), writer);
    }
}
