package com.agenticqa.tests.ui.authentication;

import com.agenticqa.api.assertions.ApiResponseAssertions;
import com.agenticqa.api.clients.UserApiClient;
import com.agenticqa.api.models.ApiMessageResponse;
import com.agenticqa.data.factories.RegistrationDataFactory;
import com.agenticqa.data.factories.UserFactory;
import com.agenticqa.data.models.RegistrationData;
import com.agenticqa.ui.assertions.UiAssertions;
import com.agenticqa.ui.fixtures.UiBaseTest;
import com.agenticqa.ui.pages.AccountPage;
import com.agenticqa.ui.pages.HomePage;
import com.agenticqa.ui.pages.LoginPage;
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
        ApiResponseAssertions.assertUserCreated(created);

        try {
            LoginPage loginPage = new LoginPage();
            loginPage.open();
            UiAssertions.assertLoginFormVisible(loginPage);

            HomePage home = loginPage.login(user.email(), user.password());
            UiAssertions.assertLoggedIn(home);

            AccountPage accountPage = new AccountPage();
            accountPage.deleteAccount();
            UiAssertions.assertAccountDeleted(accountPage);
        } finally {
            userApi.deleteAccount(user.email(), user.password());
        }
    }

    @Test(groups = {"ui", "regression"})
    public void shouldRejectInvalidLoginCredentials() {
        LoginPage loginPage = new LoginPage();
        loginPage.open();
        UiAssertions.assertLoginFormVisible(loginPage);

        loginPage.login(UserFactory.uniqueEmail(), UserFactory.strongPassword());
        UiAssertions.assertInvalidCredentialsVisible(loginPage);
        UiAssertions.assertLoggedOut(new HomePage());
    }
}
