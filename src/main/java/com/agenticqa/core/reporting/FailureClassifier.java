package com.agenticqa.core.reporting;

/**
 * Heuristic failure classifier. Not guaranteed accurate — evidence must be reviewed.
 */
public final class FailureClassifier {

    public enum FailureType {
        APPLICATION_DEFECT,
        AUTOMATION_DEFECT,
        TEST_DATA_DEFECT,
        ENVIRONMENT_DEFECT,
        UNKNOWN
    }

    private FailureClassifier() {
    }

    public static FailureType classify(Throwable error, boolean hasConsoleErrors) {
        if (error == null) {
            return FailureType.UNKNOWN;
        }
        String message = String.valueOf(error.getMessage()).toLowerCase();
        if (message.contains("timeout") && message.contains("waiting for")) {
            return FailureType.AUTOMATION_DEFECT;
        }
        if (message.contains("net::") || message.contains("err_connection") || message.contains("503") || message.contains("502")) {
            return FailureType.ENVIRONMENT_DEFECT;
        }
        if (message.contains("already exists") || message.contains("duplicate") || message.contains("email")) {
            return FailureType.TEST_DATA_DEFECT;
        }
        if (hasConsoleErrors || message.contains("assertion") || message.contains("expected")) {
            return FailureType.APPLICATION_DEFECT;
        }
        return FailureType.UNKNOWN;
    }
}
