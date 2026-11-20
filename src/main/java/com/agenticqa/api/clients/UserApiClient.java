package com.agenticqa.api.clients;

import com.agenticqa.api.models.ApiMessageResponse;
import com.agenticqa.api.models.UserDetailResponse;
import com.agenticqa.api.requests.CreateAccountRequest;
import com.agenticqa.data.models.RegistrationData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Client for createAccount, deleteAccount, and getUserDetailByEmail.
 *
 * @author Akash Aggarwal
 */
public class UserApiClient extends BaseApiClient {

    private final ObjectMapper mapper = new ObjectMapper();

    public Response createAccountRaw(RegistrationData data) {
        Map<String, Object> form = CreateAccountRequest.from(data);
        Response response = withOverloadRetry("createAccount", () -> {
            var request = givenForm();
            form.forEach((k, v) -> request.formParam(k, String.valueOf(v)));
            return request.when().post("/api/createAccount");
        });
        attachResponse("createAccount", response);
        return response;
    }

    public ApiMessageResponse createAccount(RegistrationData data) {
        return parseMessage(createAccountRaw(data));
    }

    public Response deleteAccountRaw(String email, String password) {
        Response response = withOverloadRetry("deleteAccount", () -> givenForm()
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .delete("/api/deleteAccount"));
        attachResponse("deleteAccount", response);
        return response;
    }

    public ApiMessageResponse deleteAccount(String email, String password) {
        return parseMessage(deleteAccountRaw(email, password));
    }

    public Response getUserDetailByEmailRaw(String email) {
        Response response = withOverloadRetry("getUserDetailByEmail", () -> givenJson()
                .queryParam("email", email)
                .when()
                .get("/api/getUserDetailByEmail"));
        attachResponse("getUserDetailByEmail", response);
        return response;
    }

    public UserDetailResponse getUserDetailByEmail(String email) {
        return parseUserDetailResponse(getUserDetailByEmailRaw(email));
    }

    public UserDetailResponse parseUserDetailResponse(Response response) {
        try {
            UserDetailResponse parsed = mapper.readValue(normalizeBody(response.asString()), UserDetailResponse.class);
            if (parsed.getUser() == null) {
                throw new IllegalStateException("user missing in response: " + normalizeBody(response.asString()));
            }
            return parsed;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse user detail: " + normalizeBody(response.asString()), e);
        }
    }

    public ApiMessageResponse parseMessage(Response response) {
        try {
            return mapper.readValue(normalizeBody(response.asString()), ApiMessageResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse API message: " + normalizeBody(response.asString()), e);
        }
    }
}
