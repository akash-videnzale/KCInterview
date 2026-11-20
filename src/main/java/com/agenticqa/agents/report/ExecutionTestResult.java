package com.agenticqa.agents.report;

/**
 * One TestNG method outcome parsed from the automation executor log.
 */
public record ExecutionTestResult(
        String className,
        String methodName,
        Status status,
        long durationMs,
        String classification
) {
    public enum Status {
        PASS, FAIL, SKIP
    }

    public String displayName() {
        if (className == null || className.isBlank()) {
            return methodName;
        }
        return className + "." + methodName;
    }
}
