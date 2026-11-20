package com.agenticqa.ui.pages;

import com.agenticqa.core.reporting.ExtentLogger;
import com.agenticqa.data.models.PaymentData;
import com.agenticqa.ui.messages.UiMessages;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the practice payment form and order confirmation.
 *
 * @author Akash Aggarwal
 */
public class PaymentPage extends BasePage {

    public PaymentPage() {
        super();
    }

    public PaymentPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(byDataQa("pay-button")) || isVisible(byDataQa("name-on-card"));
    }

    public PaymentPage fillPracticeCard(PaymentData data) {
        ExtentLogger.step("Fill practice payment card details");
        type(byDataQa("name-on-card"), data.nameOnCard());
        type(byDataQa("card-number"), data.cardNumber());
        type(byDataQa("cvc"), data.cvc());
        type(byDataQa("expiry-month"), data.expiryMonth());
        type(byDataQa("expiry-year"), data.expiryYear());
        ExtentLogger.screenshot("Payment form filled");
        return this;
    }

    public PaymentPage payAndConfirm() {
        ExtentLogger.step("Confirm practice payment");
        dismissOverlaysIfPresent();
        click(byDataQa("pay-button"));
        waitForDomReady();
        dismissOverlaysIfPresent();
        waitDefault().until(d -> isOrderPlaced());
        ExtentLogger.screenshot("Order confirmation");
        return this;
    }

    public boolean isOrderPlaced() {
        return isVisible(byDataQa("order-placed"))
                || isVisible(byTextContains(UiMessages.ORDER_CONFIRMED))
                || isVisible(byTextContains(UiMessages.ORDER_PLACED));
    }
}
