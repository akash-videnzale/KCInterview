package com.agenticqa.core.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Suite-level TestNG listener for execution logging and failure classification notes.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOG.info("Suite start: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info("Suite finish: {} passed={} failed={} skipped={}",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("TEST START: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("TEST PASS: {} ({} ms)", result.getMethod().getMethodName(), duration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        FailureClassifier.FailureType type = FailureClassifier.classify(result.getThrowable(), false);
        LOG.error("TEST FAIL: {} classification={} ({} ms)",
                result.getMethod().getMethodName(), type, duration(result));
        ArtifactCapture.attachText("failure-classification",
                "Classification: " + type + "\nNote: Heuristic only — review evidence before acting.");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("TEST SKIP: {}", result.getMethod().getMethodName());
    }

    private static long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
