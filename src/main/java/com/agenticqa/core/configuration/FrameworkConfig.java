package com.agenticqa.core.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Loads and exposes framework configuration (FrameworkConfig) from YAML/env.
 *
 * <p>This type belongs to the Agentic QA Automation Framework targeting
 * https://automationexercise.com/. UI automation uses Selenium WebDriver;
 * API automation uses REST Assured + Jackson POJOs; tests run on TestNG.
 *
 * @author Akash Aggarwal
 */
public record FrameworkConfig(
        String baseUrl,
        String apiBaseUrl,
        BrowserConfig browser,
        ExecutionConfig execution,
        TimeoutsConfig timeouts,
        ArtifactsConfig artifacts,
        ReportingConfig reporting
) {
    public FrameworkConfig {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl is required");
        }
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            apiBaseUrl = baseUrl;
        }
        if (browser == null) {
            browser = BrowserConfig.defaults();
        }
        if (execution == null) {
            execution = ExecutionConfig.defaults();
        }
        if (timeouts == null) {
            timeouts = TimeoutsConfig.defaults();
        }
        if (artifacts == null) {
            artifacts = ArtifactsConfig.defaults();
        }
        if (reporting == null) {
            reporting = ReportingConfig.defaults();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BrowserConfig(
            String type,
            boolean headless,
            boolean capturePageSourceOnFailure,
            boolean screenshotOnFailure,
            String viewportWidth,
            String viewportHeight
    ) {
        public static BrowserConfig defaults() {
            return new BrowserConfig("chrome", false, true, true, "1440", "900");
        }

        public int width() {
            return Integer.parseInt(viewportWidth == null ? "1440" : viewportWidth);
        }

        public int height() {
            return Integer.parseInt(viewportHeight == null ? "900" : viewportHeight);
        }
    }

    /**
     * TestNG execution settings. Parallel is off by default.
     *
     * @param parallelEnabled when true, suite runs with {@code parallelMode} and {@code threadCount}
     * @param parallelMode    methods | classes | tests | none
     * @param threadCount     worker threads when parallel is enabled
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExecutionConfig(
            boolean parallelEnabled,
            String parallelMode,
            int threadCount
    ) {
        public static ExecutionConfig defaults() {
            return new ExecutionConfig(false, "methods", 2);
        }

        public String mode() {
            return parallelMode == null || parallelMode.isBlank() ? "methods" : parallelMode.trim();
        }

        public int threads() {
            return threadCount <= 0 ? 2 : threadCount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TimeoutsConfig(long defaultTimeoutMs, long navigationTimeoutMs, long apiTimeoutMs) {
        public static TimeoutsConfig defaults() {
            return new TimeoutsConfig(30_000, 45_000, 30_000);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ArtifactsConfig(String directory, boolean collectConsoleErrors, boolean collectPageErrors) {
        public static ArtifactsConfig defaults() {
            return new ArtifactsConfig("target/artifacts", true, true);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ReportingConfig(boolean redactSecrets) {
        public static ReportingConfig defaults() {
            return new ReportingConfig(true);
        }
    }
}
