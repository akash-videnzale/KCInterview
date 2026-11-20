package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Blocks hardcoded emails/passwords in tests; push to factories/env.
 *
 * @author Akash Aggarwal
 */
public class NoHardcodedCredentialsRule implements ValidationRule {

    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    @Override
    public String id() {
        return "AQ-SEC-001";
    }

    @Override
    public String name() {
        return "No hardcoded credentials";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        cu.findAll(StringLiteralExpr.class).forEach(lit -> {
            String value = lit.getValue();
            int line = lit.getBegin().map(p -> p.line).orElse(1);
            if (EMAIL.matcher(value).matches() && !value.endsWith("@example.com") && !value.contains("agenticqa.")) {
                report.add(new Violation(file.toString(), line, id(), Severity.WARNING,
                        "Hardcoded email literal detected: " + value,
                        "Generate unique emails via UserFactory"));
            }
            if (looksLikePassword(value) && nearbyPasswordHint(source, lit.getBegin().map(p -> p.line).orElse(1))) {
                report.add(new Violation(file.toString(), line, id(), Severity.ERROR,
                        "Hardcoded credential/password literal detected",
                        "Use factories or environment-backed secrets; never commit credentials"));
            }
        });
        if (source.contains("\"Password123\"") || source.contains("\"Test@123\"") || source.contains("\"admin123\"")) {
            boolean already = report.allViolations().stream()
                    .anyMatch(v -> id().equals(v.ruleId()) && file.toString().equals(v.filePath()));
            if (!already) {
                report.add(new Violation(file.toString(), 1, id(), Severity.ERROR,
                        "Hardcoded password pattern detected",
                        "Use RegistrationDataFactory / env vars"));
            }
        }
    }

    private static boolean looksLikePassword(String value) {
        // Avoid flagging assertion messages (often contain spaces) and method-call prose.
        if (value.length() < 8 || value.contains(" ") || value.contains("\t")) {
            return false;
        }
        return value.matches(".*[A-Za-z].*") && value.matches(".*\\d.*");
    }

    private static boolean nearbyPasswordHint(String source, int line) {
        String[] lines = source.split("\n", -1);
        int idx = Math.max(0, line - 1);
        String window = (idx > 0 ? lines[idx - 1] : "") + " " + lines[idx] + " " + (idx + 1 < lines.length ? lines[idx + 1] : "");
        String lower = window.toLowerCase(Locale.ROOT);
        // Require an assignment/parameter style hint, not merely .password() accessors.
        return lower.contains("password =") || lower.contains("password=")
                || lower.contains("passwd") || lower.contains("pwd")
                || lower.contains("secret");
    }
}
