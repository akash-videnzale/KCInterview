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
public record ApiMessageResponse(int responseCode, String message) {
}
