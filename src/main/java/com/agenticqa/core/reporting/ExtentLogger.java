package com.agenticqa.core.reporting;

import com.agenticqa.core.context.UiSessionHolder;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * Step / screenshot logger for ExtentReports. Safe no-op when no active Extent test.
 */
public final class ExtentLogger {

    private static final Logger LOG = LoggerFactory.getLogger(ExtentLogger.class);

    private ExtentLogger() {
    }

    public static void step(String message) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.INFO, "STEP: " + message);
        }
        LOG.info("STEP: {}", message);
    }

    public static void pass(String message) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.PASS, message);
        }
    }

    public static void fail(String message) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, message);
        }
    }

    public static void info(String message) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.INFO, message);
        }
        LOG.info("{}", message);
    }

    public static void apiRequest(String details) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.INFO, "API REQUEST:<br/><pre>" + escape(details) + "</pre>");
        }
        LOG.info("API REQUEST:\n{}", details);
    }

    public static void apiResponse(String details) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.INFO, "API RESPONSE:<br/><pre>" + escape(details) + "</pre>");
        }
        LOG.info("API RESPONSE:\n{}", details);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public static void screenshot(String label) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test == null || !UiSessionHolder.isBound()) {
            return;
        }
        try {
            WebDriver driver = UiSessionHolder.get().driver();
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String base64 = Base64.getEncoder().encodeToString(png);
            test.info(label,
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64, label).build());
        } catch (Exception e) {
            LOG.warn("Extent screenshot '{}' failed: {}", label, e.getMessage());
            info("Screenshot skipped: " + e.getMessage());
        }
    }

    public static void stepWithScreenshot(String message) {
        step(message);
        screenshot(message);
    }
}
