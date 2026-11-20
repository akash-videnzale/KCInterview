package com.agenticqa.core.browser;

import com.agenticqa.core.configuration.ConfigLoader;
import com.agenticqa.core.configuration.FrameworkConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates Selenium WebDriver instances.
 * Driver binaries are resolved exclusively via Bonigarcia WebDriverManager.
 * Drivers remain per-test via {@link SessionFactory}; this class owns factory logic only.
 */
public final class BrowserManager {

    private static final Logger LOG = LoggerFactory.getLogger(BrowserManager.class);
    private static volatile boolean started;

    private BrowserManager() {
    }

    public static synchronized void start() {
        if (started) {
            return;
        }
        FrameworkConfig config = ConfigLoader.get();
        setupDriverManager(config.browser().type());
        LOG.info("Selenium browser factory ready type={} headless={} (WebDriverManager)",
                config.browser().type(), config.browser().headless());
        started = true;
    }

    public static WebDriver createDriver() {
        start();
        FrameworkConfig config = ConfigLoader.get();
        String type = config.browser().type() == null ? "chrome" : config.browser().type().toLowerCase();
        setupDriverManager(type);
        WebDriver driver = switch (type) {
            case "firefox" -> new FirefoxDriver(firefoxOptions(config));
            case "edge", "msedge" -> new EdgeDriver(edgeOptions(config));
            case "chromium", "chrome" -> new ChromeDriver(chromeOptions(config));
            default -> new ChromeDriver(chromeOptions(config));
        };
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(
                config.browser().width(), config.browser().height()));
        LOG.debug("Started Selenium WebDriver type={} via WebDriverManager", type);
        return driver;
    }

    public static synchronized void stop() {
        started = false;
        LOG.info("Stopped Selenium browser factory");
    }

    /**
     * Resolves and caches the matching driver binary for the requested browser.
     */
    private static void setupDriverManager(String browserType) {
        String type = browserType == null ? "chrome" : browserType.toLowerCase();
        switch (type) {
            case "firefox" -> WebDriverManager.firefoxdriver().setup();
            case "edge", "msedge" -> WebDriverManager.edgedriver().setup();
            case "chromium", "chrome" -> WebDriverManager.chromedriver().setup();
            default -> WebDriverManager.chromedriver().setup();
        }
    }

    private static ChromeOptions chromeOptions(FrameworkConfig config) {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        if (config.browser().headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--window-size=" + config.browser().width() + "," + config.browser().height(),
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--ignore-certificate-errors",
                "--disable-notifications",
                "--disable-popup-blocking"
        );
        return options;
    }

    private static FirefoxOptions firefoxOptions(FrameworkConfig config) {
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        if (config.browser().headless()) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(FrameworkConfig config) {
        EdgeOptions options = new EdgeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        if (config.browser().headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--window-size=" + config.browser().width() + "," + config.browser().height(),
                "--ignore-certificate-errors",
                "--disable-notifications"
        );
        return options;
    }
}
