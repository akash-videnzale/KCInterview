package com.agenticqa.governance.rules;

import com.agenticqa.governance.model.Severity;
import com.agenticqa.governance.model.Violation;
import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;

import java.nio.file.Path;

/**
 * Ensures Java files under src/test use com.agenticqa.tests.* packages.
 *
 * @author Akash Aggarwal
 */
public class PackagePlacementRule implements ValidationRule {

    @Override
    public String id() {
        return "AQ-PKG-001";
    }

    @Override
    public String name() {
        return "Package conventions";
    }

    @Override
    public void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report) {
        String path = file.toString().replace('\\', '/');
        if (!path.endsWith(".java")) {
            return;
        }
        String expectedPrefix = null;
        if (path.contains("/src/test/java/")) {
            expectedPrefix = "com.agenticqa.tests";
        } else if (path.contains("/src/main/java/")) {
            expectedPrefix = "com.agenticqa";
        }
        if (expectedPrefix == null) {
            return;
        }
        if (cu.getTypes().isEmpty() && cu.getPackageDeclaration().isEmpty()) {
            // Parse failure / empty CU — skip rather than false-positive
            return;
        }
        String pkg = cu.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse("");
        if (!pkg.startsWith(expectedPrefix) && !path.contains("/evaluations/") && !path.contains("flawed-examples")) {
            if (path.contains("flawed")) {
                return;
            }
            report.add(new Violation(file.toString(), 1, id(), Severity.ERROR,
                    "Incorrect package '" + pkg + "' for path",
                    "Use package under " + expectedPrefix));
        }
    }
}
