package com.agenticqa.api.clients;

import com.agenticqa.api.models.ApiMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

/**
 * Client for verifyLogin.
 *
 * @author Akash Aggarwal
 */
public class AuthenticationApiClient extends BaseApiClient {

    private final ObjectMapper mapper = new ObjectMapper();

    public Response verifyLoginRaw(String email, String password) {
        Response response = withOverloadRetry("verifyLogin", () -> givenForm()
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .post("/api/verifyLogin"));
        attachResponse("verifyLogin", response);
        return response;
    }

    public ApiMessageResponse verifyLogin(String email, String password) {
        Response response = verifyLoginRaw(email, password);
        try {
            return mapper.readValue(normalizeBody(response.asString()), ApiMessageResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse verifyLogin response: " + normalizeBody(response.asString()), e);
        }
    }

    public Response verifyLoginMissingEmail(String password) {
        Response response = withOverloadRetry("verifyLoginMissingEmail", () -> givenForm()
                .formParam("password", password)
                .when()
                .post("/api/verifyLogin"));
        attachResponse("verifyLoginMissingEmail", response);
        return response;
    }
}
