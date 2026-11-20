package com.agenticqa.core.reporting;

import com.agenticqa.core.context.UiSessionHolder;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;

/**
 * TestNG listener that drives Extent Spark HTML reporting with steps + screenshots.
 */
public class ExtentTestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(ExtentTestListener.class);

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.getInstance();
        LOG.info("Extent report initialized → {}", ExtentReportManager.latestReportPath());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();
        Path report = ExtentReportManager.latestReportPath().toAbsolutePath();
        LOG.info("Extent report written → {}", report);
        ReportOpener.openExtentOnce();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String name = TestDisplayName.from(result);
        String description = result.getMethod().getDescription();
        if (description == null || description.isBlank()) {
            description = result.getMethod().getQualifiedName();
        }
        ExtentTest test = ExtentReportManager.createTest(name, description);
        String[] groups = result.getMethod().getGroups();
        if (groups != null) {
            for (String group : groups) {
                test.assignCategory(group);
            }
        }
        test.log(Status.INFO, "Started: " + name);
        if (isUi(result)) {
            ExtentLogger.step("UI test started — browser context ready");
        } else {
            ExtentLogger.step("API/non-UI test started");
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            if (UiSessionHolder.isBound()) {
                ExtentLogger.screenshot("Final screenshot — PASS");
            }
            test.log(Status.PASS, MarkupHelper.createLabel(result.getMethod().getMethodName() + " PASSED", ExtentColor.GREEN));
        }
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            Throwable error = result.getThrowable();
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getMethod().getMethodName() + " FAILED", ExtentColor.RED));
            if (error != null) {
                test.log(Status.FAIL, error);
            }
            FailureClassifier.FailureType type = FailureClassifier.classify(error, false);
            test.log(Status.INFO, "Failure classification (heuristic): " + type);
            if (UiSessionHolder.isBound()) {
                ExtentLogger.screenshot("Final screenshot — FAIL");
            }
        }
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, MarkupHelper.createLabel(result.getMethod().getMethodName() + " SKIPPED", ExtentColor.ORANGE));
            if (result.getThrowable() != null) {
                test.log(Status.SKIP, result.getThrowable());
            }
        }
        ExtentReportManager.removeTest();
    }

    private static boolean isUi(ITestResult result) {
        String[] groups = result.getMethod().getGroups();
        if (groups != null) {
            for (String g : groups) {
                if ("ui".equalsIgnoreCase(g)) {
                    return true;
                }
            }
        }
        return result.getTestClass().getName().contains(".ui.");
    }
}
