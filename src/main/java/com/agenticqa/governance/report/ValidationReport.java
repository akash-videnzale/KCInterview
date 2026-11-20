package com.agenticqa.governance.report;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects rule violations/passes and renders the contribution validation summary.
 *
 * @author Akash Aggarwal
 */
public final class ValidationReport {

    private final Map<String, List<Violation>> byFile = new LinkedHashMap<>();
    private final List<String> passedChecks = new ArrayList<>();

    public void pass(String checkName) {
        passedChecks.add(checkName);
    }

    public void add(Violation violation) {
        byFile.computeIfAbsent(violation.filePath(), k -> new ArrayList<>()).add(violation);
    }

    public List<Violation> allViolations() {
        return byFile.values().stream().flatMap(List::stream).toList();
    }

    public long errorCount() {
        return allViolations().stream().filter(v -> v.severity() == Severity.ERROR).count();
    }

    public long warningCount() {
        return allViolations().stream().filter(v -> v.severity() == Severity.WARNING).count();
    }

    public boolean accepted() {
        return errorCount() == 0;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("AGENT CONTRIBUTION VALIDATION\n\n");
        for (String check : passedChecks) {
            sb.append("[PASS] ").append(check).append('\n');
        }
        for (Map.Entry<String, List<Violation>> entry : byFile.entrySet()) {
            sb.append("\nFile: ").append(entry.getKey()).append('\n');
            for (Violation v : entry.getValue()) {
                String tag = switch (v.severity()) {
                    case ERROR -> "FAIL";
                    case WARNING -> "WARN";
                    case INFO -> "INFO";
                };
                sb.append('[').append(tag).append("] ")
                        .append(v.ruleId())
                        .append(" (line ").append(v.lineNumber()).append("): ")
                        .append(v.explanation())
                        .append(" | Fix: ").append(v.suggestedCorrection())
                        .append('\n');
            }
        }
        sb.append('\n')
                .append("RESULT: ").append(accepted() ? "ACCEPTED" : "REJECTED").append('\n')
                .append("Errors: ").append(errorCount()).append('\n')
                .append("Warnings: ").append(warningCount()).append('\n');
        return sb.toString();
    }
}
