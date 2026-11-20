package com.agenticqa.ui.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Reusable footer component (subscription field).
 *
 * @author Akash Aggarwal
 */
public class FooterComponent {

    private final WebDriver driver;

    public FooterComponent(WebDriver driver) {
        this.driver = driver;
    }

    public void subscribe(String email) {
        // Site uses misspelled id "susbscribe_email"
        driver.findElement(By.id("susbscribe_email")).clear();
        driver.findElement(By.id("susbscribe_email")).sendKeys(email);
        driver.findElement(By.id("subscribe")).click();
    }

    public boolean isSubscriptionSuccessVisible() {
        return !driver.findElements(By.id("success-subscribe")).isEmpty()
                && driver.findElement(By.id("success-subscribe")).isDisplayed();
    }
}
