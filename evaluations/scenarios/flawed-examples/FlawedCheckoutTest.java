package com.agenticqa.tests.ui.checkout;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * INTENTIONALLY FLAWED — used by AgentValidator evaluation only. Not executed by TestNG.
 */
public class FlawedCheckoutTest {

    public static WebDriver sharedDriver;

    @Test
    public void brokenCheckout() throws Exception {
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        sharedDriver = driver;
        driver.get("https://automationexercise.com/");
        Thread.sleep(5000);
        TimeUnit.SECONDS.sleep(3);
        String password = "Password123";
        driver.findElement(By.id("password")).sendKeys(password);
        RestAssured.given().get("https://automationexercise.com/api/productsList");
        Assert.assertTrue(true);
    }

    @Test
    public void emptyScenario() {
    }

    public void shouldLookLikeTestWithoutAnnotation() {
        Assert.assertTrue(true);
    }
}
