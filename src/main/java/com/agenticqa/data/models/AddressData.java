package com.agenticqa.data.models;

/**
 * Value object for address fields on the registration form.
 *
 * @author Akash Aggarwal
 */
public record AddressData(
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
