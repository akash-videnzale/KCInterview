package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Flags public scenario-style methods missing @Test.
 *
 * @author Akash Aggarwal
 */
public class MissingTestAnnotationRule implements ValidationRule {

    @Override
    public String id() {
        return "AQ-TST-002";
    }

    @Override
    public String name() {
        return "Public scenario methods should use @Test";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        String path = file.toString().replace('\\', '/');
        if (!path.contains("/tests/") && !path.contains("flawed")) {
            return;
        }
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            if (!method.isPublic() || method.isStatic()) {
                continue;
            }
            String name = method.getNameAsString().toLowerCase(Locale.ROOT);
            boolean looksLikeTest = name.startsWith("should") || name.startsWith("test") || name.startsWith("verify");
            boolean hasTest = method.getAnnotations().stream().anyMatch(a -> "Test".equals(a.getNameAsString()));
            boolean isLifecycle = method.getAnnotations().stream().anyMatch(a ->
                    a.getNameAsString().startsWith("Before") || a.getNameAsString().startsWith("After"));
            if (looksLikeTest && !hasTest && !isLifecycle) {
                report.add(new Violation(file.toString(), method.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.WARNING,
                        "Method '" + method.getNameAsString() + "' looks like a test but lacks @Test",
                        "Annotate with @Test or rename if helper"));
            }
        }
    }
}
