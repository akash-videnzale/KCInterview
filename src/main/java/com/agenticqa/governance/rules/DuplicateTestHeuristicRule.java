package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Warns on suspiciously duplicated test method bodies.
 *
 * @author Akash Aggarwal
 */
public class DuplicateTestHeuristicRule implements ValidationRule {

    @Override
    public String id() {
        return "AQ-TST-003";
    }

    @Override
    public String name() {
        return "Duplicate test implementations";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        Map<String, Integer> names = new HashMap<>();
        Map<String, Integer> bodies = new HashMap<>();
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            boolean isTest = method.getAnnotations().stream().anyMatch(a -> "Test".equals(a.getNameAsString()));
            if (!isTest) {
                continue;
            }
            names.merge(method.getNameAsString(), 1, Integer::sum);
            String body = method.getBody().map(Object::toString).orElse("").replaceAll("\\s+", " ").trim();
            if (!body.isEmpty()) {
                bodies.merge(body, 1, Integer::sum);
            }
        }
        names.forEach((name, count) -> {
            if (count > 1) {
                report.add(new Violation(file.toString(), 1, id(), Severity.ERROR,
                        "Duplicate @Test method name '" + name + "'",
                        "Rename or consolidate duplicate tests"));
            }
        });
        bodies.forEach((body, count) -> {
            if (count > 1 && body.length() > 40) {
                report.add(new Violation(file.toString(), 1, id(), Severity.WARNING,
                        "Duplicate test method bodies detected",
                        "Extract shared setup or parameterize with @DataProvider"));
            }
        });
    }
}
