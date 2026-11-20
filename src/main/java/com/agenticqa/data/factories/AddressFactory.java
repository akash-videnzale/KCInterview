package com.agenticqa.data.factories;

import com.agenticqa.data.models.AddressData;
import com.github.javafaker.Faker;

import java.util.Locale;

/**
 * Generates address fields used during registration (India practice profile).
 *
 * @author Akash Aggarwal
 */
public final class AddressFactory {

    private static final Faker FAKER = new Faker(Locale.US);

    private AddressFactory() {
    }

    public static AddressData indiaPractice() {
        return new AddressData(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                FAKER.company().name(),
                FAKER.address().streetAddress(),
                "Suite " + FAKER.number().digits(3),
                "India",
                "Delhi",
                "New Delhi",
                "110001",
                "9" + FAKER.number().digits(9)
        );
    }
}
