package com.agenticqa.tests.unit.agents;

import com.agenticqa.agents.report.ExecutionResultParser;
import com.agenticqa.agents.report.ExecutionTestResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ExecutionResultParserTest {

    @Test(groups = {"unit", "smoke"})
    public void shouldParsePassFailAndSuiteCounts() {
        String log = """
                23:00:01.075 [main] INFO  c.a.core.reporting.TestListener - ProductListApiTest.shouldReturnProductCatalogue TEST PASS: shouldReturnProductCatalogue (1422 ms)
                23:00:02.000 [main] INFO  c.a.core.reporting.TestListener - LoginUiTest.shouldRejectInvalidPassword TEST FAIL: shouldRejectInvalidPassword classification=ASSERTION (2100 ms)
                23:00:03.000 [main] INFO  c.a.core.reporting.TestListener - Suite finish: All Tests passed=1 failed=1 skipped=0
                """;

        List<ExecutionTestResult> results = ExecutionResultParser.parse(log);
        Assert.assertEquals(results.size(), 2);
        Assert.assertEquals(results.get(0).status(), ExecutionTestResult.Status.PASS);
        Assert.assertEquals(results.get(0).className(), "ProductListApiTest");
        Assert.assertEquals(results.get(1).status(), ExecutionTestResult.Status.FAIL);
        Assert.assertEquals(results.get(1).classification(), "ASSERTION");

        ExecutionResultParser.SuiteCounts counts = ExecutionResultParser.parseSuiteCounts(log);
        Assert.assertEquals(counts.passed(), 1);
        Assert.assertEquals(counts.failed(), 1);
        Assert.assertEquals(counts.skipped(), 0);
    }
}
