package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.nio.file.Path;
import java.util.Set;

/**
 * Deterministic AgentValidator rule (MeaningfulAssertionRule) that rejects unsafe AI-authored patterns.
 *
 * <p>This type belongs to the Agentic QA Automation Framework targeting
 * https://automationexercise.com/. UI automation uses Selenium WebDriver;
 * API automation uses REST Assured + Jackson POJOs; tests run on TestNG.
 *
 * <p>Design intent: keep selectors/HTTP details out of test classes, prefer
 * factories for unique data, and keep LLM usage outside TestNG assertions.
 *
 * @author Akash Aggarwal
 */
public class MeaningfulAssertionRule implements ValidationRule {

    private static final Set<String> ASSERT_METHODS = Set.of(
            "assertEquals", "assertTrue", "assertFalse", "assertNotNull", "assertNull", "fail", "assertAll"
    );

    @Override
    public String id() {
        return "AQ-AST-001";
    }

    @Override
    public String name() {
        return "Meaningful TestNG assertions";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        if (!isTestFile(file)) {
            return;
        }
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            boolean isTest = method.getAnnotations().stream().anyMatch(a -> "Test".equals(a.getNameAsString()));
            if (!isTest) {
                continue;
            }
            long asserts = method.findAll(MethodCallExpr.class).stream()
                    .filter(c -> isAssertionCall(c.getNameAsString()))
                    .count();
            if (asserts == 0) {
                report.add(new Violation(file.toString(), method.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.ERROR,
                        "Test method '" + method.getNameAsString() + "' has no TestNG assertions",
                        "Add Assert/SoftAssert or *Assertions.assert* helpers for business outcomes"));
            }
            method.findAll(MethodCallExpr.class).forEach(call -> {
                if ("assertTrue".equals(call.getNameAsString()) && call.getArguments().size() == 1
                        && "true".equals(call.getArgument(0).toString())) {
                    report.add(new Violation(file.toString(), call.getBegin().map(p -> p.line).orElse(1),
                            id(), Severity.WARNING,
                            "Meaningless assertTrue(true) detected",
                            "Assert a business-relevant condition with a message"));
                }
            });
        }
    }

    /** TestNG Assert/SoftAssert methods, plus framework helpers named {@code assert*}. */
    private static boolean isAssertionCall(String name) {
        return ASSERT_METHODS.contains(name) || name.startsWith("assert");
    }

    private static boolean isTestFile(Path file) {
        String p = file.toString();
        return p.contains("tests") || p.contains("flawed") || p.endsWith("Test.java");
    }
}
