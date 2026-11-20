package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import com.agenticqa.ui.messages.UiMessages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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

    public int itemCount() {
        return count(By.cssSelector("#cart_info tbody tr, .cart_info tbody tr"));
    }

    public CheckoutPage proceedToCheckout() {
        ExtentLogger.step("Proceed to checkout");
        if (isVisible(By.cssSelector("a.check_out"))) {
            click(By.cssSelector("a.check_out"));
        } else {
            click(byTagTextContains("a", UiMessages.PROCEED_TO_CHECKOUT));
        }
        waitForDomReady();
        ExtentLogger.screenshot("Checkout page");
        return new CheckoutPage();
    }
}
