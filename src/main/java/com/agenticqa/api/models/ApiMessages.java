package com.agenticqa.api.models;

/**
 * Exact Automation Exercise API message strings used in assertions.
 *
 * @author Akash Aggarwal
 */
public final class ApiMessages {

    public static final String USER_CREATED = "User created!";
    public static final String USER_EXISTS = "User exists!";
    public static final String ACCOUNT_DELETED = "Account deleted!";
    public static final String USER_NOT_FOUND = "User not found!";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists!";
    public static final String SEARCH_PRODUCT_PARAM_MISSING =
            "Bad request, search_product parameter is missing in POST request.";

    private ApiMessages() {
    }
}
