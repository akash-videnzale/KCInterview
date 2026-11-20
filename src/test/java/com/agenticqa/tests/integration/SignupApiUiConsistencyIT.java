package com.agenticqa.tests.integration;

import com.agenticqa.api.assertions.ApiResponseAssertions;
import com.agenticqa.api.clients.AuthenticationApiClient;
import com.agenticqa.api.clients.UserApiClient;
import com.agenticqa.api.fixtures.ApiBaseTest;
import com.agenticqa.api.models.UserDetailResponse;
import com.agenticqa.data.factories.RegistrationDataFactory;
import com.agenticqa.data.models.RegistrationData;
import org.testng.annotations.Test;

/**
 * API create → login → user detail → delete, with exact field checks.
 *
 * @author Akash Aggarwal
 */
public class SignupApiUiConsistencyIT extends ApiBaseTest {

    @Test(groups = {"integration", "regression", "api"})
    public void shouldCreateVerifyAndDeleteUserViaApi() {
        RegistrationData data = RegistrationDataFactory.uniqueUser();
        UserApiClient users = new UserApiClient();
        AuthenticationApiClient auth = new AuthenticationApiClient();

        ApiResponseAssertions.assertUserCreated(users.createAccount(data));
        ApiResponseAssertions.assertUserExists(auth.verifyLogin(data.email(), data.password()));

        UserDetailResponse detail = users.getUserDetailByEmail(data.email());
        ApiResponseAssertions.assertUserDetailResponse(detail, data);

        ApiResponseAssertions.assertAccountDeleted(users.deleteAccount(data.email(), data.password()));
    }
}
