package com.agenticqa.ui.pages;

import com.agenticqa.ui.components.FooterComponent;
import com.agenticqa.ui.components.HeaderComponent;
import com.agenticqa.core.configuration.ConfigLoader;
import com.agenticqa.core.context.UiSessionHolder;
import com.agenticqa.core.reporting.ExtentLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Shared Selenium helpers for all page objects: navigation, waits, resilient click/type, overlay dismissal.
 * Uses the WebDriver bound to the current test thread via {@link UiSessionHolder} — no need to pass driver.
 *
 * @author Akash Aggarwal
 */
public abstract class BasePage {

    protected final WebDriver driver;
    private final HeaderComponent header;
    private final FooterComponent footer;

    /** Resolves the active UI session driver for this test thread. */
    protected BasePage() {
        this(UiSessionHolder.get().driver());
    }

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.header = new HeaderComponent(driver);
        this.footer = new FooterComponent(driver);
    }

    /** Shared site header (logo, nav, cart badge). */
    public HeaderComponent header() {
        return header;
    }

    /** Shared site footer links. */
    public FooterComponent footer() {
        return footer;
    }

    /** Explicit wait bound to {@code timeouts.defaultTimeoutMs} from framework config. */
    protected WebDriverWait waitDefault() {
        return new WebDriverWait(driver, Duration.ofMillis(ConfigLoader.get().timeouts().defaultTimeoutMs()));
    }

    /**
     * Opens a relative path under {@code baseUrl}, an absolute URL, or the home page.
     * Waits for {@code document.readyState == complete} afterwards.
     *
     * @param path relative path (e.g. {@code /products}), absolute URL, blank, or {@code /}
     */
    public void navigate(String path) {
        String base = ConfigLoader.get().baseUrl();
        if (path == null || path.isBlank() || "/".equals(path)) {
            driver.get(base + "/");
        } else if (path.startsWith("http://") || path.startsWith("https://")) {
            driver.get(path);
        } else {
            String normalized = path.startsWith("/") ? path : "/" + path;
            driver.get(base + normalized);
        }
        waitForDomReady();
    }

    /** Best-effort wait until the browser reports DOM load complete. */
    public void waitForDomReady() {
        try {
            new WebDriverWait(driver, Duration.ofMillis(ConfigLoader.get().timeouts().navigationTimeoutMs()))
                    .until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
        } catch (TimeoutException ignored) {
            // best-effort — AE pages sometimes keep connections open
        }
    }

    /**
     * Dismisses consent overlays and strips common ad iframes that intercept Selenium clicks
     * on Automation Exercise (Consent CMP + Google ad frames).
     */
    public void dismissOverlaysIfPresent() {
        clickIfVisible(By.cssSelector(".fc-cta-consent"));
        clickIfVisible(By.xpath("//button[normalize-space()='Consent']"));
        clickIfVisible(By.xpath("//button[normalize-space()='Close' or @aria-label='Close']"));
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll("
                            + "'iframe[id^=\"aswift_\"],iframe[id^=\"google_ads\"],#ad_position_box,"
                            + ".adsbygoogle,ins.adsbygoogle,.grippy-host'"
                            + ").forEach(e => e.remove());"
                            + "document.querySelectorAll('div[id^=\"google_image_div\"]').forEach(e => e.remove());"
            );
        } catch (RuntimeException ignored) {
            // best-effort
        }
    }

    /** Locator for Automation Exercise {@code data-qa} attributes used throughout the site. */
    protected By byDataQa(String value) {
        return By.cssSelector("[data-qa='" + value + "']");
    }

    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    protected WebElement waitVisible(By locator) {
        return waitDefault().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return waitDefault().until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Resilient click: dismiss overlays, scroll into view, retry on stale elements,
     * and fall back to a JavaScript click when the native click is intercepted.
     */
    protected void click(By locator) {
        dismissOverlaysIfPresent();
        RuntimeException last = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement el = waitClickable(locator);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
                el.click();
                return;
            } catch (StaleElementReferenceException stale) {
                last = stale;
            } catch (RuntimeException primary) {
                try {
                    WebElement el = waitVisible(locator);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                    return;
                } catch (RuntimeException secondary) {
                    last = secondary;
                }
            }
        }
        throw last == null ? new IllegalStateException("Click failed for " + locator) : last;
    }

    /**
     * Resilient type: clears then {@code sendKeys}, with a JS value+event fallback when
     * the element becomes stale or is covered by ads.
     */
    protected void type(By locator, String text) {
        dismissOverlaysIfPresent();
        String value = text == null ? "" : text;
        RuntimeException last = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement el = waitVisible(locator);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
                el.clear();
                el.sendKeys(value);
                return;
            } catch (StaleElementReferenceException stale) {
                last = stale;
            } catch (RuntimeException e) {
                try {
                    WebElement el = waitVisible(locator);
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].value = arguments[1];"
                                    + "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));"
                                    + "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                            el, value);
                    return;
                } catch (RuntimeException secondary) {
                    last = secondary;
                }
            }
        }
        throw last == null ? new IllegalStateException("Type failed for " + locator) : last;
    }

    /** Selects a {@code <select>} option by value attribute, retrying on stale DOM. */
    protected void selectByValue(By locator, String value) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                new Select(waitVisible(locator)).selectByValue(value);
                return;
            } catch (StaleElementReferenceException ignored) {
                // retry with a fresh element
            }
        }
        new Select(waitVisible(locator)).selectByValue(value);
    }

    /** Selects a {@code <select>} option by visible label text, retrying on stale DOM. */
    protected void selectByVisibleText(By locator, String text) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                new Select(waitVisible(locator)).selectByVisibleText(text);
                return;
            } catch (StaleElementReferenceException ignored) {
                // retry with a fresh element
            }
        }
        new Select(waitVisible(locator)).selectByVisibleText(text);
    }

    /** {@code true} when at least one matching element is present and displayed. */
    protected boolean isVisible(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected String text(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected int count(By locator) {
        return driver.findElements(locator).size();
    }

    /** Soft click used only for optional overlays — failures are logged, never thrown. */
    protected void clickIfVisible(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                elements.get(0).click();
            }
        } catch (RuntimeException e) {
            ExtentLogger.info("Overlay dismiss skipped: " + e.getMessage());
        }
    }
}
