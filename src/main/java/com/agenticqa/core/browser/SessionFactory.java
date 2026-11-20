package com.agenticqa.core.browser;

import com.agenticqa.core.configuration.ConfigLoader;
import com.agenticqa.core.configuration.FrameworkConfig;
import com.agenticqa.core.context.TestSession;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Creates isolated Selenium WebDriver sessions for each UI test.
 */
public final class SessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFactory.class);

    private SessionFactory() {
    }

    public static TestSession create(String testName) {
        FrameworkConfig config = ConfigLoader.get();
        Path artifactDir = Path.of(config.artifacts().directory(), sanitize(testName),
                Instant.now().toEpochMilli() + "-" + UUID.randomUUID());
        try {
            Files.createDirectories(artifactDir);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create artifact directory: " + artifactDir, e);
        }

        WebDriver driver = BrowserManager.createDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(config.timeouts().navigationTimeoutMs()));
        driver.manage().timeouts().scriptTimeout(Duration.ofMillis(config.timeouts().defaultTimeoutMs()));

        Path pageSourcePath = artifactDir.resolve("page-source.html");
        TestSession session = new TestSession(driver, artifactDir, pageSourcePath);
        LOG.debug("Created UI session for test={} artifacts={}", testName, artifactDir);
        return session;
    }

    public static void close(TestSession session, boolean capturePageSource) {
        if (session == null) {
            return;
        }
        WebDriver driver = session.driver();
        try {
            collectBrowserLogs(session);
            if (capturePageSource) {
                try {
                    Files.writeString(session.pageSourcePath(), driver.getPageSource());
                } catch (Exception e) {
                    LOG.warn("Failed writing page source: {}", e.getMessage());
                }
            }
        } finally {
            try {
                driver.quit();
            } catch (Exception e) {
                LOG.warn("Failed quitting WebDriver: {}", e.getMessage());
            }
        }
    }

    private static void collectBrowserLogs(TestSession session) {
        if (!ConfigLoader.get().artifacts().collectConsoleErrors()) {
            return;
        }
        try {
            LogEntries entries = session.driver().manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : entries) {
                if (entry.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue()) {
                    session.addConsoleError(entry.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.debug("Browser log collection unavailable: {}", e.getMessage());
        }
    }

    private static String sanitize(String name) {
        return name == null ? "unknown" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
