package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.nio.file.Path;
import java.util.Set;

/**
 * Blocks raw By/findElement usage inside test classes (use page objects).
 *
 * @author Akash Aggarwal
 */
public class NoLowLevelSeleniumInTestsRule implements ValidationRule {

    private static final Set<String> DISCOURAGED = Set.of(
            "executeScript", "executeAsyncScript", "findElement", "findElements"
    );

    @Override
    public String id() {
        return "AQ-UI-001";
    }

    @Override
    public String name() {
        return "Avoid unnecessary low-level Selenium usage in tests";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        String path = file.toString().replace('\\', '/');
        if (!path.contains("/tests/") && !path.contains("flawed")) {
            return;
        }
        cu.findAll(MethodCallExpr.class).forEach(call -> {
            if (DISCOURAGED.contains(call.getNameAsString()) && path.contains("/tests/ui/")) {
                report.add(new Violation(file.toString(), call.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.WARNING,
                        "Low-level Selenium API '" + call.getNameAsString() + "' used in test",
                        "Prefer page objects and component APIs"));
            }
        });
        long finds = source.split("\\.findElement\\(", -1).length - 1;
        if (finds >= 5 && path.contains("/tests/ui/")) {
            report.add(new Violation(file.toString(), 1, id(), Severity.INFO,
                    "Many raw findElement calls in UI test",
                    "Move interactions into page objects/components"));
        }
    }
}
