package com.agenticqa.core.context;

import org.openqa.selenium.WebDriver;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Per-test UI session holding an isolated Selenium {@link WebDriver} plus diagnostics.
 */
public final class TestSession {

    private final WebDriver driver;
    private final Path artifactDir;
    private final Path pageSourcePath;
    private final List<String> consoleErrors = Collections.synchronizedList(new ArrayList<>());
    private final List<String> pageErrors = Collections.synchronizedList(new ArrayList<>());

    public TestSession(WebDriver driver, Path artifactDir, Path pageSourcePath) {
        this.driver = driver;
        this.artifactDir = artifactDir;
        this.pageSourcePath = pageSourcePath;
    }

    public WebDriver driver() {
        return driver;
    }

    /** Alias used by page objects / fixtures historically named page(). */
    public WebDriver page() {
        return driver;
    }

    public Path artifactDir() {
        return artifactDir;
    }

    public Path pageSourcePath() {
        return pageSourcePath;
    }

    public List<String> consoleErrors() {
        return List.copyOf(consoleErrors);
    }

    public List<String> pageErrors() {
        return List.copyOf(pageErrors);
    }

    public void addConsoleError(String message) {
        consoleErrors.add(message);
    }

    public void addPageError(String message) {
        pageErrors.add(message);
    }
}
