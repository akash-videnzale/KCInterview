package com.agenticqa.core.reporting;

import org.testng.ITestResult;

/**
 * Builds short Extent report titles like {@code API_REJECT_DUPLICATE_REGISTRATION}.
 *
 * @author Akash Aggarwal
 */
public final class TestDisplayName {

    private TestDisplayName() {
    }

    public static String from(ITestResult result) {
        String layer = layer(result);
        String purpose = purpose(result.getMethod().getMethodName());
        return layer + "_" + purpose;
    }

    private static String layer(ITestResult result) {
        String[] groups = result.getMethod().getGroups();
        if (groups != null) {
            for (String group : groups) {
                if ("api".equalsIgnoreCase(group)) {
                    return "API";
                }
                if ("ui".equalsIgnoreCase(group)) {
                    return "UI";
                }
                if ("integration".equalsIgnoreCase(group)) {
                    return "INTEGRATION";
                }
                if ("unit".equalsIgnoreCase(group)) {
                    return "UNIT";
                }
            }
        }
        String className = result.getTestClass().getName();
        if (className.contains(".api.")) {
            return "API";
        }
        if (className.contains(".ui.")) {
            return "UI";
        }
        if (className.contains(".integration.")) {
            return "INTEGRATION";
        }
        if (className.contains(".unit.")) {
            return "UNIT";
        }
        return "TEST";
    }

    /** {@code shouldRejectDuplicateRegistration} → {@code REJECT_DUPLICATE_REGISTRATION}. */
    static String purpose(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return "UNKNOWN";
        }
        String name = methodName.trim();
        if (name.startsWith("should") && name.length() > "should".length()) {
            name = name.substring("should".length());
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i > 0 && Character.isUpperCase(c) && Character.isLowerCase(name.charAt(i - 1))) {
                out.append('_');
            }
            out.append(Character.toUpperCase(c));
        }
        String purpose = out.toString().replaceAll("_+", "_");
        return purpose.isBlank() ? "UNKNOWN" : purpose;
    }
}
