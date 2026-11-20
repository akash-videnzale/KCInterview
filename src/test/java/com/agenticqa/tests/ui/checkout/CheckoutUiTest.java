package com.agenticqa.tests.ui.checkout;

import com.agenticqa.data.factories.CheckoutDataFactory;
import com.agenticqa.data.factories.RegistrationDataFactory;
import com.agenticqa.data.models.PaymentData;
import com.agenticqa.data.models.RegistrationData;
import com.agenticqa.ui.assertions.UiAssertions;
import com.agenticqa.ui.fixtures.UiBaseTest;
import com.agenticqa.ui.pages.AccountPage;
import com.agenticqa.ui.pages.CartPage;
import com.agenticqa.ui.pages.CheckoutPage;
import com.agenticqa.ui.pages.LoginPage;
import com.agenticqa.ui.pages.PaymentPage;
import com.agenticqa.ui.pages.ProductsPage;
import com.agenticqa.ui.pages.SignupPage;
import org.testng.annotations.Test;

/**
 * UI checkout — signup, add to cart, pay, confirm order.
 *
 * @author Akash Aggarwal
 */
public class CheckoutUiTest extends UiBaseTest {

    @Test(groups = {"ui", "regression"})
    public void shouldCompleteCheckoutWithPracticePayment() {
        RegistrationData user = RegistrationDataFactory.uniqueUser();
        PaymentData payment = CheckoutDataFactory.practicePayment();

        LoginPage loginPage = new LoginPage();
        loginPage.open();
        SignupPage signupPage = loginPage.startSignup(user.name(), user.email());
        AccountPage accountPage = signupPage.completeRegistration(user);
        UiAssertions.assertAccountCreated(accountPage);
        accountPage.continueAfterCreated();

        ProductsPage productsPage = new ProductsPage();
        productsPage.open();
        productsPage.addFirstProductToCart();
        CartPage cartPage = productsPage.viewCartFromModal();
        UiAssertions.assertCartLoaded(cartPage);
        UiAssertions.assertCartHasItems(cartPage);

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        UiAssertions.assertAddressDetailsVisible(checkoutPage);
        checkoutPage.enterOrderComment(CheckoutDataFactory.orderComment());

        PaymentPage paymentPage = checkoutPage.placeOrder();
        UiAssertions.assertPaymentPageLoaded(paymentPage);
        paymentPage.fillPracticeCard(payment).payAndConfirm();
        UiAssertions.assertOrderPlaced(paymentPage);

        accountPage.deleteAccount();
        UiAssertions.assertAccountDeleted(accountPage);
    }
}
