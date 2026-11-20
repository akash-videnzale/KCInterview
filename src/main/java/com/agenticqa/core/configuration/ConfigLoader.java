package com.agenticqa.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Loads framework configuration from classpath YAML with environment variable overrides.
 */
public final class ConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String DEFAULT_CONFIG = "config/framework-config.yaml";
    private static volatile FrameworkConfig cached;

    private ConfigLoader() {
    }

    public static FrameworkConfig get() {
        FrameworkConfig local = cached;
        if (local == null) {
            synchronized (ConfigLoader.class) {
                local = cached;
                if (local == null) {
                    cached = local = load();
                }
            }
        }
        return local;
    }

    static void resetForTests() {
        cached = null;
    }

    private static FrameworkConfig load() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();

        String profile = firstNonBlank(
                System.getProperty("qa.profile"),
                System.getenv("QA_PROFILE"),
                dotenv.get("QA_PROFILE"),
                "default"
        );

        String resource = "default".equals(profile)
                ? DEFAULT_CONFIG
                : "config/framework-config-" + profile + ".yaml";

        FrameworkConfig base = readYaml(resource);
        if (base == null && !DEFAULT_CONFIG.equals(resource)) {
            LOG.warn("Profile config {} not found; falling back to {}", resource, DEFAULT_CONFIG);
            base = readYaml(DEFAULT_CONFIG);
        }
        if (base == null) {
            throw new IllegalStateException("Unable to load framework configuration from " + resource);
        }

        String baseUrl = firstNonBlank(
                System.getProperty("BASE_URL"),
                System.getenv("BASE_URL"),
                dotenv.get("BASE_URL"),
                base.baseUrl()
        );
        String apiBaseUrl = firstNonBlank(
                System.getProperty("API_BASE_URL"),
                System.getenv("API_BASE_URL"),
                dotenv.get("API_BASE_URL"),
                base.apiBaseUrl()
        );

        boolean headless = parseBoolean(
                firstNonBlank(System.getProperty("HEADLESS"), System.getenv("HEADLESS"), dotenv.get("HEADLESS")),
                base.browser().headless()
        );
        String browserType = firstNonBlank(
                System.getProperty("BROWSER"),
                System.getenv("BROWSER"),
                dotenv.get("BROWSER"),
                base.browser().type()
        );

        boolean capturePageSource = base.browser().capturePageSourceOnFailure();
        FrameworkConfig.BrowserConfig browser = new FrameworkConfig.BrowserConfig(
                browserType,
                headless,
                capturePageSource,
                base.browser().screenshotOnFailure(),
                base.browser().viewportWidth(),
                base.browser().viewportHeight()
        );

        boolean parallelEnabled = parseBoolean(
                firstNonBlank(System.getProperty("PARALLEL"), System.getenv("PARALLEL"), dotenv.get("PARALLEL")),
                base.execution() == null ? false : base.execution().parallelEnabled()
        );
        String parallelMode = firstNonBlank(
                System.getProperty("PARALLEL_MODE"),
                System.getenv("PARALLEL_MODE"),
                dotenv.get("PARALLEL_MODE"),
                base.execution() == null ? "methods" : base.execution().mode()
        );
        int threadCount = parseInt(
                firstNonBlank(
                        System.getProperty("THREAD_COUNT"),
                        System.getenv("THREAD_COUNT"),
                        dotenv.get("THREAD_COUNT")
                ),
                base.execution() == null ? 2 : base.execution().threads()
        );
        FrameworkConfig.ExecutionConfig execution = new FrameworkConfig.ExecutionConfig(
                parallelEnabled,
                parallelMode,
                threadCount
        );

        FrameworkConfig resolved = new FrameworkConfig(
                trimTrailingSlash(baseUrl),
                trimTrailingSlash(apiBaseUrl),
                browser,
                execution,
                base.timeouts(),
                base.artifacts(),
                base.reporting()
        );
        LOG.info("Loaded config baseUrl={}, apiBaseUrl={}, browser={}, headless={}, parallel={}, mode={}, threads={}",
                resolved.baseUrl(),
                resolved.apiBaseUrl(),
                resolved.browser().type(),
                resolved.browser().headless(),
                resolved.execution().parallelEnabled(),
                resolved.execution().mode(),
                resolved.execution().threads());
        return resolved;
    }

    private static FrameworkConfig readYaml(String resource) {
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(in, FrameworkConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading config resource: " + resource, e);
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private static boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Invalid integer '{}', using default {}", value, defaultValue);
            return defaultValue;
        }
    }

    private static String trimTrailingSlash(String url) {
        if (url == null) {
            return null;
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static Optional<String> env(String key) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
        return Optional.ofNullable(firstNonBlank(System.getProperty(key), System.getenv(key), dotenv.get(key)));
    }
}
