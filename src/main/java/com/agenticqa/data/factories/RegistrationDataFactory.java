package com.agenticqa.data.factories;

import com.agenticqa.data.models.AddressData;
import com.agenticqa.data.models.RegistrationData;

/**
 * Builds a unique RegistrationData user for signup/API flows (email uniqueness for parallel safety).
 *
 * @author Akash Aggarwal
 */
public final class RegistrationDataFactory {

    private RegistrationDataFactory() {
    }

    public static RegistrationData uniqueUser() {
        AddressData address = AddressFactory.indiaPractice();
        String email = UserFactory.uniqueEmail();
        return new RegistrationData(
                UserFactory.uniqueName(),
                email,
                UserFactory.strongPassword(),
                "10",
                "5",
                "1990",
                address.firstName(),
                address.lastName(),
                address.company(),
                address.address1(),
                address.address2(),
                address.country(),
                address.state(),
                address.city(),
                address.zipcode(),
                address.mobileNumber()
        );
    }
}
