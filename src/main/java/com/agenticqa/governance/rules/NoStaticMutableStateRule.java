package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.nio.file.Path;

/**
 * Blocks static mutable shared driver/state in tests.
 *
 * @author Akash Aggarwal
 */
public class NoStaticMutableStateRule implements ValidationRule {

    @Override
    public String id() {
        return "AQ-THR-001";
    }

    @Override
    public String name() {
        return "No static mutable shared browser/test state";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        String path = file.toString().replace('\\', '/');
        if (!path.contains("/tests/") && !path.contains("flawed")) {
            return;
        }
        cu.findAll(FieldDeclaration.class).forEach(field -> {
            if (!field.isStatic() || field.isFinal()) {
                return;
            }
            String type = field.getElementType().asString();
            if (type.contains("Page") || type.contains("Browser") || type.contains("BrowserContext")
                    || type.contains("List") || type.contains("Map") || type.contains("RegistrationData")) {
                report.add(new Violation(file.toString(), field.getBegin().map(p -> p.line).orElse(1),
                        id(), Severity.ERROR,
                        "Static mutable shared state of type " + type,
                        "Keep per-test state local or use ThreadLocal with reliable cleanup in fixtures only"));
            }
        });
    }
}
