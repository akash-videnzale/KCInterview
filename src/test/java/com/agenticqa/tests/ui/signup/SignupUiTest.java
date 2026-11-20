package com.agenticqa.tests.ui.signup;

import com.agenticqa.data.factories.RegistrationDataFactory;
import com.agenticqa.data.models.RegistrationData;
import com.agenticqa.ui.fixtures.UiBaseTest;
import com.agenticqa.ui.pages.AccountPage;
import com.agenticqa.ui.pages.HomePage;
import com.agenticqa.ui.pages.LoginPage;
import com.agenticqa.ui.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * UI signup — register, verify logged-in, delete account.
 *
 * @author Akash Aggarwal
 */
public class SignupUiTest extends UiBaseTest {

    @Test(groups = {"ui", "smoke", "regression"})
    public void shouldRegisterUniqueUserAndDeleteAccount() {
        RegistrationData data = RegistrationDataFactory.uniqueUser();

        LoginPage loginPage = new LoginPage();
        loginPage.open();
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login/Signup page should be visible");

        SignupPage signupPage = loginPage.startSignup(data.name(), data.email());
        Assert.assertTrue(signupPage.isAccountInfoVisible(), "Account info form should be visible");

        AccountPage accountPage = signupPage.completeRegistration(data);
        Assert.assertTrue(accountPage.isAccountCreatedVisible(), "Account created should be visible");

        HomePage home = accountPage.continueAfterCreated();
        Assert.assertTrue(home.header().isLoggedIn(), "User should be logged in");

        accountPage.deleteAccount();
        Assert.assertTrue(accountPage.isAccountDeletedVisible(), "Account deleted should be visible");
        accountPage.continueAfterDeleted();
    }
}
