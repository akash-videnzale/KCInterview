package com.agenticqa.tests.ui.authentication;

import com.agenticqa.api.clients.UserApiClient;
import com.agenticqa.api.models.ApiMessageResponse;
import com.agenticqa.data.factories.RegistrationDataFactory;
import com.agenticqa.data.factories.UserFactory;
import com.agenticqa.data.models.RegistrationData;
import com.agenticqa.ui.fixtures.UiBaseTest;
import com.agenticqa.ui.pages.AccountPage;
import com.agenticqa.ui.pages.HomePage;
import com.agenticqa.ui.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * UI login — valid credentials and invalid credentials.
 *
 * @author Akash Aggarwal
 */
public class LoginUiTest extends UiBaseTest {

    private final UserApiClient userApi = new UserApiClient();

    @Test(groups = {"ui", "smoke", "regression"})
    public void shouldLoginWithValidCredentials() {
        RegistrationData user = RegistrationDataFactory.uniqueUser();
        ApiMessageResponse created = userApi.createAccount(user);
        Assert.assertEquals(created.responseCode(), 201, "API precondition: user created");

        try {
            LoginPage loginPage = new LoginPage();
            loginPage.open();
            Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form should be visible");

            HomePage home = loginPage.login(user.email(), user.password());
            Assert.assertTrue(home.header().isLoggedIn(), "User should be logged in");

            AccountPage accountPage = new AccountPage();
            accountPage.deleteAccount();
            Assert.assertTrue(accountPage.isAccountDeletedVisible(), "Account deleted should be visible");
        } finally {
            userApi.deleteAccount(user.email(), user.password());
        }
    }

    @Test(groups = {"ui", "regression"})
    public void shouldRejectInvalidLoginCredentials() {
        LoginPage loginPage = new LoginPage();
        loginPage.open();
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form should be visible");

        loginPage.login(UserFactory.uniqueEmail(), UserFactory.strongPassword());
        Assert.assertTrue(loginPage.isInvalidCredentialsVisible(), "Invalid credentials message should show");
        Assert.assertFalse(new HomePage().header().isLoggedIn(), "User must stay logged out");
    }
}
