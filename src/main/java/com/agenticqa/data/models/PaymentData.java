package com.agenticqa.data.models;

/**
 * Value object for practice payment card details.
 *
 * @author Akash Aggarwal
 */
public record PaymentData(
        String nameOnCard,
        String cardNumber,
        String cvc,
        String expiryMonth,
        String expiryYear
) {
}
