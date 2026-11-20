package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.nio.file.Path;
import java.util.Set;

/**
 * Blocks tests from touching UiSessionHolder/BrowserManager directly.
 *
 * @author Akash Aggarwal
 */
public class NoDirectContextInTestsRule implements ValidationRule {

    private static final Set<String> FORBIDDEN = Set.of(
            "ChromeOptions", "FirefoxOptions", "EdgeOptions", "SafariOptions"
    );

    @Override
    public String id() {
        return "AQ-BRW-002";
    }

    @Override
    public String name() {
        return "No direct browser options / session bootstrap in tests";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        if (!file.toString().contains("tests") && !file.toString().contains("flawed")) {
            return;
        }
        cu.findAll(ObjectCreationExpr.class).forEach(expr -> {
            String type = expr.getType().getNameAsString();
            if (FORBIDDEN.contains(type)) {
                report.add(new Violation(
                        file.toString(),
                        expr.getBegin().map(p -> p.line).orElse(1),
                        id(),
                        Severity.ERROR,
                        "Direct " + type + " construction in test class",
                        "Use UiBaseTest session lifecycle (SessionFactory)"
                ));
            }
        });
        if (source.contains("newContext(") || source.contains("new ChromeOptions")
                || source.contains("new FirefoxOptions")) {
            boolean already = report.allViolations().stream()
                    .anyMatch(v -> id().equals(v.ruleId()) && v.filePath().equals(file.toString()));
            if (!already) {
                report.add(new Violation(file.toString(), 1, id(), Severity.ERROR,
                        "Direct browser options/session bootstrap detected", "Use UiBaseTest"));
            }
        }
    }
}
