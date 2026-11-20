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
public record UserDetailResponse(int responseCode, UserDetail user) {

    public boolean isSuccess() {
        return responseCode == 200 && user != null;
    }
}
