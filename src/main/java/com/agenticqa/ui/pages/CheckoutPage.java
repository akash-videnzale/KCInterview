package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for checkout address review and order placement.
 *
 * @author Akash Aggarwal
 */
public class CheckoutPage extends BasePage {

    public CheckoutPage() {
        super();
    }

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAddressDetailsVisible() {
        return isVisible(By.id("address_delivery")) || isVisible(By.cssSelector(".checkout-information"));
    }

    public CheckoutPage enterOrderComment(String comment) {
        ExtentLogger.step("Enter order comment");
        type(By.cssSelector("textarea[name='message']"), comment);
        return this;
    }

    public PaymentPage placeOrder() {
        ExtentLogger.step("Place order and open payment");
        dismissOverlaysIfPresent();
        if (isVisible(By.cssSelector("a[href='/payment']"))) {
            click(By.cssSelector("a[href='/payment']"));
        } else if (isVisible(By.xpath("//a[contains(normalize-space(),'Place Order')]"))) {
            click(By.xpath("//a[contains(normalize-space(),'Place Order')]"));
        } else {
            navigate("/payment");
        }
        waitForDomReady();
        dismissOverlaysIfPresent();
        if (!isVisible(byDataQa("pay-button")) && !isVisible(byDataQa("name-on-card"))) {
            navigate("/payment");
            dismissOverlaysIfPresent();
        }
        waitVisible(byDataQa("pay-button"));
        ExtentLogger.screenshot("Payment page");
        return new PaymentPage();
    }

    public String deliveryAddressText() {
        return text(By.id("address_delivery"));
    }
}
