package com.agenticqa.ui.fixtures;

import com.agenticqa.core.browser.BrowserManager;
import com.agenticqa.core.browser.SessionFactory;
import com.agenticqa.core.configuration.ConfigLoader;
import com.agenticqa.core.context.TestSession;
import com.agenticqa.core.context.UiSessionHolder;
import com.agenticqa.core.logging.LogManager;
import com.agenticqa.core.reporting.ArtifactCapture;
import com.agenticqa.core.reporting.FailureClassifier;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * TestNG base for UI tests: starts/stops an isolated WebDriver session per method and captures artifacts on failure.
 *
 * @author Akash Aggarwal
 */
public abstract class UiBaseTest {

    private static final Logger LOG = LogManager.getLogger(UiBaseTest.class);

    @AfterSuite(alwaysRun = true)
    public void stopBrowserSuite() {
        BrowserManager.stop();
    }

    @BeforeMethod(alwaysRun = true)
    public void openUiSession(Method method) {
        BrowserManager.start();
        String testName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        LogManager.putTestContext(testName);
        TestSession session = SessionFactory.create(testName);
        UiSessionHolder.set(session);
        LOG.info("UI session ready baseUrl={}", ConfigLoader.get().baseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void closeUiSession(ITestResult result) {
        TestSession session = null;
        try {
            if (UiSessionHolder.isBound()) {
                session = UiSessionHolder.get();
                boolean failed = result.getStatus() == ITestResult.FAILURE;
                if (failed && ConfigLoader.get().browser().screenshotOnFailure()) {
                    ArtifactCapture.captureScreenshot(session, "failure");
                }
                if (failed) {
                    ArtifactCapture.attachText("console-errors", String.join("\n", session.consoleErrors()));
                    ArtifactCapture.attachText("page-errors", String.join("\n", session.pageErrors()));
                    FailureClassifier.FailureType type = FailureClassifier.classify(
                            result.getThrowable(),
                            !session.consoleErrors().isEmpty()
                    );
                    ArtifactCapture.attachText("failure-classification-detail", type.name());
                }
                SessionFactory.close(session, failed && ConfigLoader.get().browser().capturePageSourceOnFailure());
                if (failed) {
                    ArtifactCapture.attachPageSourceIfPresent(session);
                }
            }
        } finally {
            UiSessionHolder.clear();
            LogManager.clearTestContext();
        }
    }

    protected WebDriver driver() {
        return UiSessionHolder.get().driver();
    }

    protected TestSession session() {
        return UiSessionHolder.get();
    }
}
