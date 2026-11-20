package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.nio.file.Path;

/**
 * Blocks RestAssured.given() directly inside test classes.
 *
 * @author Akash Aggarwal
 */
public class NoDirectHttpInTestsRule implements ValidationRule {

    @Override
    public String id() {
        return "AQ-API-001";
    }

    @Override
    public String name() {
        return "No direct HTTP bypassing API clients";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        String path = file.toString().replace('\\', '/');
        if (!path.contains("/tests/") && !path.contains("flawed")) {
            return;
        }
        if (source.contains("java.net.HttpURLConnection") || source.contains("java.net.http.HttpClient")
                || source.contains("new URL(") || source.contains("RestAssured.given()")) {
            boolean usesClient = source.contains("ApiClient") || source.contains("productApi") || source.contains("userApi")
                    || source.contains("authApi");
            if (source.contains("RestAssured.given()") && !usesClient) {
                report.add(new Violation(file.toString(), lineOf(source, "RestAssured.given()"), id(), Severity.ERROR,
                        "Direct RestAssured usage in test bypassing API clients",
                        "Call ProductApiClient / UserApiClient / AuthenticationApiClient"));
            }
            if (source.contains("HttpURLConnection") || source.contains("HttpClient")) {
                report.add(new Violation(file.toString(), 1, id(), Severity.ERROR,
                        "Direct JDK HTTP client usage in tests",
                        "Use framework API clients"));
            }
        }
        cu.findAll(ObjectCreationExpr.class).forEach(expr -> {
            String type = expr.getType().getNameAsString();
            if ("URL".equals(type) || "HttpClient".equals(type)) {
                report.add(new Violation(file.toString(), expr.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.ERROR, "Direct HTTP construction in test", "Use API clients"));
            }
        });
        cu.findAll(MethodCallExpr.class).forEach(call -> {
            if ("given".equals(call.getNameAsString())
                    && call.getScope().map(Object::toString).orElse("").contains("RestAssured")) {
                report.add(new Violation(file.toString(), call.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.ERROR, "RestAssured.given() in test class", "Use API client wrappers"));
            }
        });
    }

    private static int lineOf(String source, String token) {
        int idx = source.indexOf(token);
        if (idx < 0) {
            return 1;
        }
        return source.substring(0, idx).split("\n", -1).length;
    }
}
