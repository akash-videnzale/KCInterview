package com.agenticqa.ui.assertions;

import com.agenticqa.core.reporting.ExtentLogger;
import com.agenticqa.ui.pages.AccountPage;
import com.agenticqa.ui.pages.CartPage;
import com.agenticqa.ui.pages.CheckoutPage;
import com.agenticqa.ui.pages.HomePage;
import com.agenticqa.ui.pages.LoginPage;
import com.agenticqa.ui.pages.PaymentPage;
import com.agenticqa.ui.pages.ProductDetailsPage;
import com.agenticqa.ui.pages.ProductsPage;
import com.agenticqa.ui.pages.SignupPage;
import org.testng.Assert;

/**
 * Page-state assertions for UI flows. Tests call these — not Assert with inline messages.
 *
 * @author Akash Aggarwal
 */
public final class UiAssertions {

    private UiAssertions() {
    }

    public static void assertLoginFormVisible(LoginPage page) {
        Assert.assertTrue(page.isLoginFormVisible(), "login form visible");
        ExtentLogger.pass("Login form visible");
    }

    public static void assertInvalidCredentialsVisible(LoginPage page) {
        Assert.assertTrue(page.isInvalidCredentialsVisible(), "invalid credentials message");
        ExtentLogger.pass("Invalid credentials message visible");
    }

    public static void assertAccountInfoVisible(SignupPage page) {
        Assert.assertTrue(page.isAccountInfoVisible(), "account information form");
        ExtentLogger.pass("Account information form visible");
    }

    public static void assertAccountCreated(AccountPage page) {
        Assert.assertTrue(page.isAccountCreatedVisible(), "account created confirmation");
        ExtentLogger.pass("Account created confirmation visible");
    }

    public static void assertAccountDeleted(AccountPage page) {
        Assert.assertTrue(page.isAccountDeletedVisible(), "account deleted confirmation");
        ExtentLogger.pass("Account deleted confirmation visible");
    }

    public static void assertLoggedIn(HomePage home) {
        Assert.assertTrue(home.header().isLoggedIn(), "logged in");
        ExtentLogger.pass("User logged in");
    }

    public static void assertLoggedOut(HomePage home) {
        Assert.assertFalse(home.header().isLoggedIn(), "logged out");
        ExtentLogger.pass("User logged out");
    }

    public static void assertCartLoaded(CartPage page) {
        Assert.assertTrue(page.isLoaded(), "cart loaded");
        ExtentLogger.pass("Cart loaded");
    }

    public static void assertCartHasItems(CartPage page) {
        Assert.assertTrue(page.itemCount() >= 1, "cart item count");
        ExtentLogger.pass("Cart has items: " + page.itemCount());
    }

    public static void assertAddressDetailsVisible(CheckoutPage page) {
        Assert.assertTrue(page.isAddressDetailsVisible(), "checkout address details");
        ExtentLogger.pass("Checkout address details visible");
    }

    public static void assertPaymentPageLoaded(PaymentPage page) {
        Assert.assertTrue(page.isLoaded(), "payment page loaded");
        ExtentLogger.pass("Payment page loaded");
    }

    public static void assertOrderPlaced(PaymentPage page) {
        Assert.assertTrue(page.isOrderPlaced(), "order placed confirmation");
        ExtentLogger.pass("Order placed confirmation visible");
    }

    public static void assertSearchResultsVisible(ProductsPage page) {
        Assert.assertTrue(page.hasSearchResultsHeading(), "search results heading");
        Assert.assertTrue(page.productCount() > 0, "search product count");
        ExtentLogger.pass("Search results visible, count=" + page.productCount());
    }

    public static void assertProductDetailsLoaded(ProductDetailsPage page) {
        Assert.assertTrue(page.isLoaded(), "product details loaded");
        ExtentLogger.pass("Product details loaded");
    }

    public static void assertBrandContains(ProductDetailsPage page, String expectedBrand) {
        Assert.assertTrue(page.productBrand().contains(expectedBrand), "product brand");
        ExtentLogger.pass("Product brand contains: " + expectedBrand);
    }

    public static void assertAvailabilityPresent(ProductDetailsPage page) {
        Assert.assertFalse(page.productAvailability().isBlank(), "product availability");
        ExtentLogger.pass("Product availability present");
    }
}
