package com.agenticqa.governance.model;

/**
 * One governance finding (file, line, rule id, severity, message, fix hint).
 *
 * @author Akash Aggarwal
 */
public record Violation(
        String filePath,
        int lineNumber,
        String ruleId,
        Severity severity,
        String explanation,
        String suggestedCorrection
) {
}
