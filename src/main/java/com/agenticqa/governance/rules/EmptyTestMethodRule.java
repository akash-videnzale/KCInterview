package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.nio.file.Path;

/**
 * Flags empty @Test methods.
 *
 * @author Akash Aggarwal
 */
public class EmptyTestMethodRule implements ValidationRule {

    @Override
    public String id() {
        return "AQ-TST-001";
    }

    @Override
    public String name() {
        return "No empty test methods";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            boolean isTest = method.getAnnotations().stream().anyMatch(a -> "Test".equals(a.getNameAsString()));
            if (!isTest) {
                continue;
            }
            boolean empty = method.getBody().map(b -> b.getStatements().isEmpty()).orElse(true);
            if (empty) {
                report.add(new Violation(file.toString(), method.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.ERROR,
                        "Empty @Test method '" + method.getNameAsString() + "'",
                        "Implement the scenario or remove the method"));
            }
        }
    }
}
