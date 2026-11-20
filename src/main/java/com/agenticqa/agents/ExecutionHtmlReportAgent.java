package com.agenticqa.agents;

import com.agenticqa.agents.output.AgentOutputWriter;
import com.agenticqa.agents.pipeline.Agent;
import com.agenticqa.agents.pipeline.PipelineContext;
import com.agenticqa.agents.report.ExecutionHtmlReportGenerator;
import com.agenticqa.agents.report.ExecutionResultParser;
import com.agenticqa.agents.report.ExecutionTestResult;
import com.agenticqa.agents.report.SurefireResultReader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes a simple HTML pass/fail table into the AgentCli run folder. No LLM.
 */
public final class ExecutionHtmlReportAgent implements Agent {

    public static final String REPORT_FILE = "11-execution-report.html";

    private final AgentOutputWriter writer;

    public ExecutionHtmlReportAgent(AgentOutputWriter writer) {
        this.writer = writer;
    }

    @Override
    public String name() {
        return "execution-html-report";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        String executionLog = context.artifact("executionLog");
        String reexecutionLog = context.artifact("reexecutionLog");
        String combined = executionLog;
        if (reexecutionLog != null && !reexecutionLog.isBlank()
                && !reexecutionLog.startsWith("No failures to re-execute")) {
            combined = executionLog + "\n" + reexecutionLog;
        }

        // Surefire XMLs are cleared before each executor run, so they match this run.
        List<ExecutionTestResult> surefire = SurefireResultReader.readDirectory(Path.of("target/surefire-reports"));
        List<ExecutionTestResult> fromLog = ExecutionResultParser.parse(combined);
        List<ExecutionTestResult> results = !surefire.isEmpty() ? surefire : fromLog;
        ExecutionResultParser.SuiteCounts counts = ExecutionResultParser.parseSuiteCounts(combined);
        if (counts.total() == 0 && !results.isEmpty()) {
            int pass = (int) results.stream().filter(r -> r.status() == ExecutionTestResult.Status.PASS).count();
            int fail = (int) results.stream().filter(r -> r.status() == ExecutionTestResult.Status.FAIL).count();
            int skip = (int) results.stream().filter(r -> r.status() == ExecutionTestResult.Status.SKIP).count();
            counts = new ExecutionResultParser.SuiteCounts(pass, fail, skip);
        }

        Object exitObj = context.meta("executionExitCode");
        Integer exitCode = exitObj instanceof Integer i ? i : null;
        Object groups = context.meta("executeGroups");
        String runLabel = context.outputDir().getFileName().toString();

        String html = ExecutionHtmlReportGenerator.generate(
                runLabel,
                groups == null ? "smoke" : String.valueOf(groups),
                exitCode,
                results == null ? new ArrayList<>() : results,
                counts
        );
        Path report = writer.write(REPORT_FILE, html);
        context.putMeta("executionHtmlReport", report.toAbsolutePath().toString());
        context.putMeta("executionHtmlPassCount", counts.passed());
        context.putMeta("executionHtmlFailCount", counts.failed());
    }
}
