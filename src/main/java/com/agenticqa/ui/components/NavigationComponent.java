package com.agenticqa.ui.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Reusable navigation helper for main menu links.
 *
 * @author Akash Aggarwal
 */
public class NavigationComponent {

    private final WebDriver driver;

    public NavigationComponent(WebDriver driver) {
        this.driver = driver;
    }

    public void openCategory(String categoryName) {
        driver.findElement(By.xpath("//div[@id='accordian']//a[contains(normalize-space(),'" + categoryName + "')]"))
                .click();
    }

    public void openBrand(String brandName) {
        driver.findElement(By.xpath("//div[contains(@class,'brands_products')]//a[contains(normalize-space(),'"
                        + brandName + "')]"))
                .click();
    }
}
