package com.agenticqa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response for getUserDetailByEmail.
 *
 * <pre>{ "responseCode": 200, "user": { "id": 1, "email": "...", ... } }</pre>
 *
 * @author Akash Aggarwal
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailResponse {

    private int responseCode;
    private UserDetail user;

    public UserDetailResponse() {
    }

    public UserDetailResponse(int responseCode, UserDetail user) {
        this.responseCode = responseCode;
        this.user = user;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public UserDetail getUser() {
        return user;
    }

    public void setUser(UserDetail user) {
        this.user = user;
    }

    public boolean isSuccess() {
        return responseCode == 200 && user != null;
    }
}
