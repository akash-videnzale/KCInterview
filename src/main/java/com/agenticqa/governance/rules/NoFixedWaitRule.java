package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Blocks hard-coded Thread.sleep-style waits.
 *
 * @author Akash Aggarwal
 */
public class NoFixedWaitRule implements ValidationRule {

    private static final Pattern TIME_UNIT_SLEEP = Pattern.compile("TimeUnit\\.[A-Z_]+\\.sleep\\s*\\(");
    private static final Pattern WAIT_FOR_TIMEOUT = Pattern.compile("waitForTimeout\\s*\\(");

    @Override
    public String id() {
        return "AQ-WAIT-002";
    }

    @Override
    public String name() {
        return "No arbitrary fixed waits";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        cu.findAll(MethodCallExpr.class).forEach(call -> {
            if ("waitForTimeout".equals(call.getNameAsString())) {
                report.add(new Violation(file.toString(), call.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.ERROR,
                        "waitForTimeout() is a fixed wait",
                        "Wait for a locator/condition with WebDriverWait instead"));
            }
            if ("sleep".equals(call.getNameAsString())
                    && call.getScope().map(Object::toString).orElse("").contains("TimeUnit")) {
                report.add(new Violation(file.toString(), call.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.ERROR,
                        "TimeUnit.sleep() is a fixed wait",
                        "Wait for a visible/clickable element with WebDriverWait"));
            }
        });
        if (WAIT_FOR_TIMEOUT.matcher(source).find()
                && cu.findAll(MethodCallExpr.class).stream().noneMatch(c -> "waitForTimeout".equals(c.getNameAsString()))) {
            report.add(new Violation(file.toString(), 1, id(), Severity.ERROR,
                    "Fixed wait waitForTimeout detected", "Use WebDriverWait / ExpectedConditions"));
        }
        if (TIME_UNIT_SLEEP.matcher(source).find()
                && report.allViolations().stream().noneMatch(v -> id().equals(v.ruleId()) && v.filePath().equals(file.toString()))) {
            report.add(new Violation(file.toString(), 1, id(), Severity.ERROR,
                    "Fixed wait TimeUnit.sleep detected", "Use WebDriverWait / ExpectedConditions"));
        }
    }
}
