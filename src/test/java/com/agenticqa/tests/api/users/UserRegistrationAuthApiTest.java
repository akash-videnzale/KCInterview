package com.agenticqa.tests.api.users;

import com.agenticqa.api.assertions.ApiResponseAssertions;
import com.agenticqa.api.clients.AuthenticationApiClient;
import com.agenticqa.api.clients.UserApiClient;
import com.agenticqa.api.fixtures.ApiBaseTest;
import com.agenticqa.api.models.ApiMessageResponse;
import com.agenticqa.api.models.UserDetailResponse;
import com.agenticqa.api.responses.SchemaValidator;
import com.agenticqa.data.factories.RegistrationDataFactory;
import com.agenticqa.data.models.RegistrationData;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * createAccount → verifyLogin → getUserDetailByEmail → deleteAccount.
 *
 * @author Akash Aggarwal
 */
public class UserRegistrationAuthApiTest extends ApiBaseTest {

    private final UserApiClient userApi = new UserApiClient();
    private final AuthenticationApiClient authApi = new AuthenticationApiClient();

    @Test(groups = {"api", "smoke", "regression"})
    public void shouldRegisterAuthenticateAndCleanupUser() {
        RegistrationData data = RegistrationDataFactory.uniqueUser();

        Response createRaw = userApi.createAccountRaw(data);
        Assert.assertEquals(createRaw.statusCode(), 200);
        SchemaValidator.assertMatches(createRaw, "schemas/api-message-schema.json");
        ApiResponseAssertions.assertUserCreated(userApi.parseMessage(createRaw));

        ApiMessageResponse login = authApi.verifyLogin(data.email(), data.password());
        ApiResponseAssertions.assertUserExists(login);

        Response detailRaw = userApi.getUserDetailByEmailRaw(data.email());
        Assert.assertEquals(detailRaw.statusCode(), 200);
        SchemaValidator.assertMatches(detailRaw, "schemas/user-detail-schema.json");
        UserDetailResponse detail = userApi.parseUserDetailResponse(detailRaw);
        ApiResponseAssertions.assertUserDetailResponse(detail, data);

        ApiMessageResponse deleted = userApi.deleteAccount(data.email(), data.password());
        ApiResponseAssertions.assertAccountDeleted(deleted);

        ApiMessageResponse missing = authApi.verifyLogin(data.email(), data.password());
        ApiResponseAssertions.assertUserNotFound(missing);
    }

    @Test(groups = {"api", "regression"})
    public void shouldRejectDuplicateRegistration() {
        RegistrationData data = RegistrationDataFactory.uniqueUser();

        ApiResponseAssertions.assertUserCreated(userApi.createAccount(data));
        ApiResponseAssertions.assertEmailAlreadyExists(userApi.createAccount(data));
        ApiResponseAssertions.assertAccountDeleted(userApi.deleteAccount(data.email(), data.password()));
    }
}
