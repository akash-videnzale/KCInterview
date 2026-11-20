package com.agenticqa.ui.components;

import com.agenticqa.core.configuration.ConfigLoader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Reusable header/nav component (logo, menu, cart).
 *
 * @author Akash Aggarwal
 */
public class HeaderComponent {

    private final WebDriver driver;

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
    }

    public void goHome() {
        clickNav(By.cssSelector("ul.nav a[href='/']"));
    }

    public void goProducts() {
        clickNav(By.cssSelector("a[href='/products']"));
    }

    public void goCart() {
        clickNav(By.cssSelector("a[href='/view_cart']"));
    }

    public void goSignupLogin() {
        clickNav(By.cssSelector("a[href='/login']"));
    }

    public void goContactUs() {
        clickNav(By.cssSelector("a[href='/contact_us']"));
    }

    public void deleteAccount() {
        clickNav(By.cssSelector("a[href='/delete_account']"));
    }

    public void logout() {
        clickNav(By.cssSelector("a[href='/logout']"));
    }

    public boolean isLoggedIn() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> !d.findElements(By.cssSelector("a[href='/logout']")).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String loggedInUserText() {
        return driver.findElement(By.xpath("//a[.//i[contains(@class,'fa-user')]]")).getText();
    }

    private void clickNav(By locator) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll('iframe[id^=\"aswift_\"],#ad_position_box').forEach(e => e.remove());"
            );
        } catch (RuntimeException ignored) {
            // best-effort
        }
        WebElement el = new WebDriverWait(driver, Duration.ofMillis(ConfigLoader.get().timeouts().defaultTimeoutMs()))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(locator))
                    .click();
        } catch (RuntimeException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}

