package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page object for a single product details view.
 *
 * @author Akash Aggarwal
 */
public class ProductDetailsPage extends BasePage {

    public ProductDetailsPage() {
        super();
    }

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetailsPage openById(int productId) {
        ExtentLogger.step("Open product details id=" + productId);
        navigate("/product_details/" + productId);
        dismissOverlaysIfPresent();
        waitVisible(By.cssSelector(".product-information"));
        return this;
    }

    public boolean isLoaded() {
        return isVisible(By.cssSelector(".product-information"));
    }

    public String productName() {
        return text(By.cssSelector(".product-information h2"));
    }

    public String productPrice() {
        var nested = findAll(By.cssSelector(".product-information span span"));
        if (!nested.isEmpty()) {
            return nested.get(0).getText().trim();
        }
        return text(By.cssSelector(".product-information span"));
    }

    public String productCategory() {
        return text(By.xpath("//div[contains(@class,'product-information')]//p[contains(.,'Category')]"));
    }

    public String productAvailability() {
        return text(By.xpath("//div[contains(@class,'product-information')]//p[contains(.,'Availability')]"));
    }

    public String productCondition() {
        return text(By.xpath("//div[contains(@class,'product-information')]//p[contains(.,'Condition')]"));
    }

    public String productBrand() {
        return text(By.xpath("//div[contains(@class,'product-information')]//p[contains(.,'Brand')]"));
    }

    public ProductDetailsPage setQuantity(int quantity) {
        ExtentLogger.step("Set product quantity to " + quantity);
        type(By.id("quantity"), String.valueOf(quantity));
        return this;
    }

    public ProductDetailsPage addToCart() {
        ExtentLogger.step("Add product to cart from details");
        if (isVisible(By.cssSelector("button.cart"))) {
            click(By.cssSelector("button.cart"));
        } else {
            click(By.xpath("//button[contains(normalize-space(),'Add to cart')]"));
        }
        waitDefault().until(ExpectedConditions.visibilityOfElementLocated(By.id("cartModal")));
        ExtentLogger.screenshot("Added to cart from details");
        return this;
    }

    public CartPage viewCartFromModal() {
        ExtentLogger.step("View cart from product details modal");
        click(By.cssSelector("#cartModal a[href='/view_cart']"));
        waitForDomReady();
        ExtentLogger.screenshot("Cart page");
        return new CartPage();
    }
}
