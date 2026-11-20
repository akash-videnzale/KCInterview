package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.nio.file.Path;
import java.util.Set;

/**
 * Blocks direct WebDriver/Playwright bootstrap inside test classes.
 *
 * @author Akash Aggarwal
 */
public class NoDirectBrowserInTestsRule implements ValidationRule {

    private static final Set<String> FORBIDDEN_DRIVERS = Set.of(
            "ChromeDriver", "FirefoxDriver", "EdgeDriver", "SafariDriver", "RemoteWebDriver"
    );

    @Override
    public String id() {
        return "AQ-BRW-001";
    }

    @Override
    public String name() {
        return "No direct browser creation in tests";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        if (!isTestClass(file)) {
            return;
        }
        cu.findAll(ObjectCreationExpr.class).forEach(expr -> {
            String type = expr.getType().getNameAsString();
            if (FORBIDDEN_DRIVERS.contains(type) || "Playwright".equals(type)) {
                report.add(violation(file, expr.getBegin().map(p -> p.line).orElse(1),
                        "Direct " + type + " construction in test",
                        "Use UiBaseTest / BrowserManager lifecycle instead"));
            }
        });
        cu.findAll(MethodCallExpr.class).forEach(call -> {
            String name = call.getNameAsString();
            if ("create".equals(name) && call.getScope().map(Object::toString).orElse("").contains("Playwright")) {
                report.add(violation(file, call.getBegin().map(p -> p.line).orElse(1),
                        "Direct Playwright.create() in test class",
                        "Use UiBaseTest / BrowserManager lifecycle instead"));
            }
            if ("createDriver".equals(name)
                    && call.getScope().map(Object::toString).orElse("").contains("BrowserManager")
                    && file.toString().contains("/tests/")) {
                report.add(violation(file, call.getBegin().map(p -> p.line).orElse(1),
                        "Direct BrowserManager.createDriver() in test class",
                        "Extend UiBaseTest and use page()/driver()"));
            }
        });
        if (source.contains("new ChromeDriver") || source.contains("new FirefoxDriver")
                || source.contains("Playwright.create()") || source.contains(".chromium().launch")) {
            boolean already = report.allViolations().stream().anyMatch(v -> id().equals(v.ruleId())
                    && v.filePath().equals(file.toString()));
            if (!already) {
                int line = lineOf(source, "new ChromeDriver");
                if (line < 0) {
                    line = lineOf(source, "Playwright.create()");
                }
                if (line < 0) {
                    line = lineOf(source, "new FirefoxDriver");
                }
                report.add(violation(file, Math.max(line, 1),
                        "Direct WebDriver/browser bootstrap detected in test source",
                        "Extend UiBaseTest and use page()/driver()"));
            }
        }
    }

    private Violation violation(Path file, int line, String explanation, String fix) {
        return new Violation(file.toString(), line, id(), Severity.ERROR, explanation, fix);
    }

    private static boolean isTestClass(Path file) {
        String p = file.toString().replace('\\', '/');
        return p.contains("/tests/") || p.endsWith("Test.java") || p.contains("flawed");
    }

    private static int lineOf(String source, String token) {
        int idx = source.indexOf(token);
        if (idx < 0) {
            return -1;
        }
        return source.substring(0, idx).split("\n", -1).length;
    }
}
