package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.nio.file.Path;

/**
 * Blocks hardcoded environment URLs in tests.
 *
 * @author Akash Aggarwal
 */
public class NoHardcodedEnvUrlRule implements ValidationRule {

    @Override
    public String id() {
        return "AQ-CFG-001";
    }

    @Override
    public String name() {
        return "No hardcoded environment URLs in tests";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        String path = file.toString().replace('\\', '/');
        boolean isTest = path.contains("/tests/") || path.contains("flawed");
        if (!isTest) {
            return;
        }
        cu.findAll(StringLiteralExpr.class).forEach(lit -> {
            String value = lit.getValue();
            if (value.startsWith("http://") || value.startsWith("https://")) {
                report.add(new Violation(file.toString(), lit.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.ERROR,
                        "Hardcoded environment URL in test: " + value,
                        "Use ConfigLoader.get().baseUrl() / apiBaseUrl() or relative navigation"));
            }
        });
    }
}
