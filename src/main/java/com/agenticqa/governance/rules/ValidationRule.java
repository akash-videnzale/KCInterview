package com.agenticqa.governance.rules;

import com.agenticqa.governance.report.ValidationReport;
import com.github.javaparser.ast.CompilationUnit;

import java.nio.file.Path;

/**
 * Interface implemented by each AgentValidator rule.
 *
 * @author Akash Aggarwal
 */
public interface ValidationRule {
    String id();

    String name();

    void evaluate(Path file, CompilationUnit cu, String source, ValidationReport report);
}
