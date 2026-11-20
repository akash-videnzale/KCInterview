package com.agenticqa.data.factories;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates unique emails, names, titles, and strong passwords via Faker + UUID.
 *
 * @author Akash Aggarwal
 */
public final class UserFactory {

    private static final Faker FAKER = new Faker(Locale.US);

    private UserFactory() {
    }

    public static String uniqueEmail() {
        return "agenticqa." + System.currentTimeMillis() + "." + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public static String uniqueName() {
        return FAKER.name().firstName() + " " + FAKER.name().lastName();
    }

    public static String strongPassword() {
        return "Aa1!" + FAKER.internet().password(10, 14, true, true, true);
    }

    public static String title() {
        return ThreadLocalRandom.current().nextBoolean() ? "Mr" : "Mrs";
    }
}
