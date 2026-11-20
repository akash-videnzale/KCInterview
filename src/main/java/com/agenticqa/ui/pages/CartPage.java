package com.agenticqa.ui.pages;

import com.agenticqa.ui.components.CartItemComponent;
import com.agenticqa.core.reporting.ExtentLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for the shopping cart.
 *
 * @author Akash Aggarwal
 */
public class CartPage extends BasePage {

    public CartPage() {
        super();
    }

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage open() {
        navigate("/view_cart");
        dismissOverlaysIfPresent();
        return this;
    }

    public boolean isLoaded() {
        return isVisible(By.id("cart_info")) || isVisible(By.cssSelector(".cart_info"));
    }

    public List<CartItemComponent> items() {
        List<WebElement> rows = findAll(By.cssSelector("#cart_info tbody tr, .cart_info tbody tr"));
        List<CartItemComponent> items = new ArrayList<>();
        for (WebElement row : rows) {
            items.add(new CartItemComponent(row));
        }
        return items;
    }

    public int itemCount() {
        return count(By.cssSelector("#cart_info tbody tr, .cart_info tbody tr"));
    }

    public CheckoutPage proceedToCheckout() {
        ExtentLogger.step("Proceed to checkout");
        if (isVisible(By.cssSelector("a.check_out"))) {
            click(By.cssSelector("a.check_out"));
        } else {
            click(By.xpath("//a[contains(normalize-space(),'Proceed To Checkout')]"));
        }
        waitForDomReady();
        ExtentLogger.screenshot("Checkout page");
        return new CheckoutPage();
    }
}
