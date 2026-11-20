package com.agenticqa.core.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe ExtentReports manager (Spark HTML).
 */
public final class ExtentReportManager {

    private static final Object LOCK = new Object();
    private static ExtentReports extent;
    private static Path reportPath;
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            synchronized (LOCK) {
                if (extent == null) {
                    extent = create();
                }
            }
        }
        return extent;
    }

    private static ExtentReports create() {
        try {
            Path dir = Path.of("target", "extent-report");
            Files.createDirectories(dir);
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            reportPath = dir.resolve("ExtentReport-" + stamp + ".html");
            Path latest = dir.resolve("index.html");

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath.toString());
            spark.config().setDocumentTitle("Agentic QA Automation Report");
            spark.config().setReportName("Automation Exercise — UI/API Execution");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setTimeStampFormat("dd-MMM-yyyy HH:mm:ss");

            ExtentReports reports = new ExtentReports();
            reports.attachReporter(spark);
            reports.setSystemInfo("Application", "https://automationexercise.com");
            reports.setSystemInfo("Framework", "Java 21 + Selenium + TestNG + REST Assured");
            reports.setSystemInfo("OS", System.getProperty("os.name"));
            reports.setSystemInfo("Java", System.getProperty("java.version"));

            // Also write a stable latest path for easy opening
            ExtentSparkReporter latestSpark = new ExtentSparkReporter(latest.toString());
            latestSpark.config().setDocumentTitle("Agentic QA Automation Report");
            latestSpark.config().setReportName("Automation Exercise — UI/API Execution");
            latestSpark.config().setTheme(Theme.STANDARD);
            reports.attachReporter(latestSpark);

            return reports;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize ExtentReports", e);
        }
    }

    public static ExtentTest createTest(String name, String description) {
        ExtentTest test = getInstance().createTest(name, description);
        CURRENT.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return CURRENT.get();
    }

    public static void removeTest() {
        CURRENT.remove();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static Path reportPath() {
        return reportPath != null ? reportPath : Path.of("target", "extent-report", "index.html");
    }

    public static Path latestReportPath() {
        return Path.of("target", "extent-report", "index.html");
    }
}
