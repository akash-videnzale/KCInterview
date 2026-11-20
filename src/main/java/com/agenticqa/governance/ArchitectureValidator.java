package com.agenticqa.governance;

import com.agenticqa.governance.report.ValidationReport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Agentic governance component (ArchitectureValidator) that validates contributed automation code.
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
public final class ArchitectureValidator {

    public ValidationReport validate(Path projectRoot) throws Exception {
        ValidationReport report = new ValidationReport();
        List<String> requiredDirs = List.of(
                "src/main/java/com/agenticqa/core",
                "src/main/java/com/agenticqa/pages",
                "src/main/java/com/agenticqa/api/clients",
                "src/main/java/com/agenticqa/factories",
                "src/main/java/com/agenticqa/validation",
                "src/test/java/com/agenticqa/tests/ui",
                "src/test/java/com/agenticqa/tests/api",
                ".ai/skills",
                ".ai/agents",
                ".cursor/rules"
        );
        List<String> missing = new ArrayList<>();
        for (String dir : requiredDirs) {
            if (!Files.isDirectory(projectRoot.resolve(dir))) {
                missing.add(dir);
            }
        }
        if (missing.isEmpty()) {
            report.pass("Required architecture directories present");
        } else {
            for (String dir : missing) {
                report.add(new com.agenticqa.governance.model.Violation(
                        dir, 1, "AQ-ARCH-001",
                        com.agenticqa.governance.model.Severity.ERROR,
                        "Missing required architecture directory",
                        "Create the directory and populate with real implementations"
                ));
            }
        }

        // Delegate detailed Java rule scanning for production tests (exclude deliberate flawed samples)
        AgentValidator agentValidator = new AgentValidator();
        Path tests = projectRoot.resolve("src/test/java/com/agenticqa/tests");
        if (Files.isDirectory(tests)) {
            ValidationReport testReport = agentValidator.validatePath(tests);
            testReport.allViolations().forEach(report::add);
            if (testReport.errorCount() == 0) {
                report.pass("Production tests pass contribution rules");
            }
        }
        return report;
    }

    public static void main(String[] args) throws Exception {
        Path root = args.length > 0 ? Path.of(args[0]) : Path.of(".");
        ArchitectureValidator validator = new ArchitectureValidator();
        ValidationReport report = validator.validate(root.toAbsolutePath().normalize());
        System.out.println(report.render());
        if (!report.accepted()) {
            throw new IllegalStateException("Architecture validation REJECTED with "
                    + report.errorCount() + " error(s)");
        }
    }
}
