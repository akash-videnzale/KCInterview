package com.agenticqa.tests.ui.signup;

import com.agenticqa.data.factories.RegistrationDataFactory;
import com.agenticqa.data.models.RegistrationData;
import com.agenticqa.ui.assertions.UiAssertions;
import com.agenticqa.ui.fixtures.UiBaseTest;
import com.agenticqa.ui.pages.AccountPage;
import com.agenticqa.ui.pages.HomePage;
import com.agenticqa.ui.pages.LoginPage;
import com.agenticqa.ui.pages.SignupPage;
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
        UiAssertions.assertLoginFormVisible(loginPage);

        SignupPage signupPage = loginPage.startSignup(data.name(), data.email());
        UiAssertions.assertAccountInfoVisible(signupPage);

        AccountPage accountPage = signupPage.completeRegistration(data);
        UiAssertions.assertAccountCreated(accountPage);

        HomePage home = accountPage.continueAfterCreated();
        UiAssertions.assertLoggedIn(home);

        accountPage.deleteAccount();
        UiAssertions.assertAccountDeleted(accountPage);
        accountPage.continueAfterDeleted();
    }
}
