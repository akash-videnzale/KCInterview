package com.agenticqa.data.models;

/**
 * Value object holding fields needed to complete AE signup / createAccount.
 *
 * @author Akash Aggarwal
 */
public record RegistrationData(
        String name,
        String email,
        String password,
        String birthDay,
        String birthMonth,
        String birthYear,
        String firstName,
        String lastName,
        String company,
        String address1,
        String address2,
        String country,
        String state,
        String city,
        String zipcode,
        String mobileNumber
) {
}
