package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the Automation Exercise home page.
 *
 * @author Akash Aggarwal
 */
public class HomePage extends BasePage {

    public HomePage() {
        super();
    }

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        ExtentLogger.step("Open homepage");
        navigate("/");
        dismissOverlaysIfPresent();
        ExtentLogger.screenshot("Homepage loaded");
        return this;
    }

    public boolean isLoaded() {
        return isVisible(By.cssSelector("img[alt='Website for automation practice']"))
                || isVisible(By.cssSelector(".features_items"));
    }

    public LoginPage goToSignupLogin() {
        ExtentLogger.step("Navigate to Signup / Login");
        // Direct URL avoids homepage ad iframes intercepting the header click in Selenium
        return new LoginPage().open();
    }

    public ProductsPage goToProducts() {
        ExtentLogger.step("Navigate to Products");
        return new ProductsPage().open();
    }
}
