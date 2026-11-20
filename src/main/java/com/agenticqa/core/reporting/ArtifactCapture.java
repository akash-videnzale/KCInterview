package com.agenticqa.core.reporting;

import com.agenticqa.core.context.TestSession;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Captures failure artifacts to disk and mirrors them into the active Extent test.
 *
 * <p>This type belongs to the Agentic QA Automation Framework targeting
 * https://automationexercise.com/. UI automation uses Selenium WebDriver;
 * API automation uses REST Assured + Jackson POJOs; tests run on TestNG.
 *
 * @author Akash Aggarwal
 */
public final class ArtifactCapture {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactCapture.class);

    private ArtifactCapture() {
    }

    /**
     * Writes a PNG screenshot under the session artifact directory and embeds it in Extent.
     *
     * @return path on disk, or {@code null} if capture failed
     */
    public static Path captureScreenshot(TestSession session, String name) {
        try {
            Path path = session.artifactDir().resolve(name + ".png");
            byte[] bytes = ((TakesScreenshot) session.driver()).getScreenshotAs(OutputType.BYTES);
            Files.write(path, bytes);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            var test = ExtentReportManager.getTest();
            if (test != null) {
                test.info(name,
                        com.aventstack.extentreports.MediaEntityBuilder
                                .createScreenCaptureFromBase64String(base64, name)
                                .build());
            }
            return path;
        } catch (Exception e) {
            LOG.warn("Screenshot capture failed: {}", e.getMessage());
            return null;
        }
    }

    /** Logs redacted text into Extent (and SLF4J via {@link ExtentLogger}). */
    public static void attachText(String name, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        ExtentLogger.info(name + ":\n" + SecretRedactor.redact(content));
    }

    /** Attaches saved page source content to Extent when the file exists. */
    public static void attachPageSourceIfPresent(TestSession session) {
        try {
            if (session.pageSourcePath() != null && Files.exists(session.pageSourcePath())) {
                String html = Files.readString(session.pageSourcePath());
                ExtentLogger.info("page-source (truncated):\n" + SecretRedactor.redact(
                        html.length() > 4000 ? html.substring(0, 4000) + "\n…(truncated)" : html));
            }
        } catch (Exception e) {
            LOG.warn("Page source attach failed: {}", e.getMessage());
        }
    }
}
