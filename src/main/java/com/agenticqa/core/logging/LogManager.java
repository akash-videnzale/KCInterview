package com.agenticqa.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Thin logging facade with MDC helpers for test correlation.
 */
public final class LogManager {

    private LogManager() {
    }

    public static Logger getLogger(Class<?> type) {
        return LoggerFactory.getLogger(type);
    }

    public static void putTestContext(String testName) {
        MDC.put("testName", testName);
    }

    public static void clearTestContext() {
        MDC.remove("testName");
    }
}
