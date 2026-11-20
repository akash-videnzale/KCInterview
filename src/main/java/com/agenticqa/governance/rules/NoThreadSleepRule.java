package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.nio.file.Path;

/**
 * Blocks Thread.sleep / TimeUnit.sleep in tests.
 *
 * @author Akash Aggarwal
 */
public class NoThreadSleepRule implements ValidationRule {

    @Override
    public String id() {
        return "AQ-WAIT-001";
    }

    @Override
    public String name() {
        return "No Thread.sleep";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        cu.findAll(MethodCallExpr.class).forEach(call -> {
            if ("sleep".equals(call.getNameAsString())) {
                String scope = call.getScope().map(Object::toString).orElse("");
                if (scope.contains("Thread")) {
                    report.add(new Violation(file.toString(), call.getBegin().map(p -> p.line).orElse(1),
                            id(), Severity.ERROR,
                            "Thread.sleep() is prohibited",
                            "Use WebDriverWait / ExpectedConditions"));
                }
            }
        });
        if (source.contains("Thread.sleep")) {
            boolean found = cu.findAll(MethodCallExpr.class).stream()
                    .anyMatch(c -> "sleep".equals(c.getNameAsString()));
            if (!found) {
                report.add(new Violation(file.toString(), lineOf(source, "Thread.sleep"), id(), Severity.ERROR,
                        "Thread.sleep() detected", "Replace with WebDriverWait / ExpectedConditions"));
            }
        }
    }

    private static int lineOf(String source, String token) {
        int idx = source.indexOf(token);
        if (idx < 0) {
            return 1;
        }
        return source.substring(0, idx).split("\n", -1).length;
    }
}
