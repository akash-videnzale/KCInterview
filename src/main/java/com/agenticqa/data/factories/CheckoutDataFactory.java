package com.agenticqa.data.factories;

import com.agenticqa.data.models.PaymentData;

/**
 * Builds practice payment card data for checkout UI tests.
 *
 * @author Akash Aggarwal
 */
public final class CheckoutDataFactory {

    private CheckoutDataFactory() {
    }

    /**
     * Practice card values only — Automation Exercise does not process real payments.
     */
    public static PaymentData practicePayment() {
        return new PaymentData(
                UserFactory.uniqueName(),
                "4111111111111111",
                "311",
                "12",
                "2030"
        );
    }

    public static String orderComment() {
        return "Agentic QA practice order " + System.currentTimeMillis();
    }
}
