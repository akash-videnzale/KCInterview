package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response for message endpoints (createAccount, verifyLogin, deleteAccount, errors).
 *
 * <pre>{ "responseCode": 201, "message": "User created!" }</pre>
 *
 * @author Akash Aggarwal
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiMessageResponse {

    private int responseCode;
    private String message;

    public ApiMessageResponse() {
    }

    public ApiMessageResponse(int responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
