package com.agenticqa.data.factories;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Provides known and unlikely search terms for product search tests.
 *
 * @author Akash Aggarwal
 */
public final class ProductSearchFactory {

    private static final List<String> KNOWN_TERMS = List.of("top", "tshirt", "jean", "dress", "saree");

    private ProductSearchFactory() {
    }

    public static String knownSearchTerm() {
        return KNOWN_TERMS.get(ThreadLocalRandom.current().nextInt(KNOWN_TERMS.size()));
    }

    public static String unlikelyMatchTerm() {
        return "zzzznonexistentxyz" + System.nanoTime();
    }
}
