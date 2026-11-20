package com.agenticqa.governance;

import com.agenticqa.governance.report.ValidationReport;
import com.agenticqa.governance.rules.DuplicateTestHeuristicRule;
import com.agenticqa.governance.rules.EmptyTestMethodRule;
import com.agenticqa.governance.rules.MeaningfulAssertionRule;
import com.agenticqa.governance.rules.MissingTestAnnotationRule;
import com.agenticqa.governance.rules.NoDirectBrowserInTestsRule;
import com.agenticqa.governance.rules.NoDirectContextInTestsRule;
import com.agenticqa.governance.rules.NoDirectHttpInTestsRule;
import com.agenticqa.governance.rules.NoFixedWaitRule;
import com.agenticqa.governance.rules.NoHardcodedCredentialsRule;
import com.agenticqa.governance.rules.NoHardcodedEnvUrlRule;
import com.agenticqa.governance.rules.NoLowLevelSeleniumInTestsRule;
import com.agenticqa.governance.rules.NoStaticMutableStateRule;
import com.agenticqa.governance.rules.NoThreadSleepRule;
import com.agenticqa.governance.rules.PackagePlacementRule;
import com.agenticqa.governance.rules.ValidationRule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Runs deterministic JavaParser rules against contributed test code and prints ACCEPTED/REJECTED.
 *
 * @author Akash Aggarwal
 */
public final class AgentValidator {

    private final List<ValidationRule> rules;

    public AgentValidator() {
        this.rules = List.of(
                new PackagePlacementRule(),
                new NoDirectBrowserInTestsRule(),
                new NoDirectContextInTestsRule(),
                new NoThreadSleepRule(),
                new NoFixedWaitRule(),
                new NoHardcodedCredentialsRule(),
                new NoHardcodedEnvUrlRule(),
                new MeaningfulAssertionRule(),
                new NoDirectHttpInTestsRule(),
                new NoStaticMutableStateRule(),
                new EmptyTestMethodRule(),
                new MissingTestAnnotationRule(),
                new DuplicateTestHeuristicRule(),
                new NoLowLevelSeleniumInTestsRule()
        );
    }

    public ValidationReport validatePath(Path root) throws IOException {
        ValidationReport report = new ValidationReport();
        List<Path> files = collectJavaFiles(root);
        for (ValidationRule rule : rules) {
            // Pass markers are recorded once per rule after scanning all files without ERROR for that rule id.
        }
        for (Path file : files) {
            String source = Files.readString(file, StandardCharsets.UTF_8);
            CompilationUnit cu;
            try {
                cu = StaticJavaParser.parse(source);
            } catch (Exception e) {
                // Still run textual rules via empty CU where possible
                cu = new CompilationUnit();
            }
            for (ValidationRule rule : rules) {
                rule.evaluate(file, cu, source, report);
            }
        }
        for (ValidationRule rule : rules) {
            boolean hasError = report.allViolations().stream()
                    .anyMatch(v -> rule.id().equals(v.ruleId()));
            if (!hasError) {
                report.pass(rule.name());
            }
        }
        return report;
    }

    public ValidationReport validateSource(String filePath, String source) {
        ValidationReport report = new ValidationReport();
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(source);
        } catch (Exception e) {
            cu = new CompilationUnit();
        }
        Path path = Path.of(filePath);
        for (ValidationRule rule : rules) {
            rule.evaluate(path, cu, source, report);
        }
        for (ValidationRule rule : rules) {
            boolean hasHit = report.allViolations().stream().anyMatch(v -> rule.id().equals(v.ruleId()));
            if (!hasHit) {
                report.pass(rule.name());
            }
        }
        return report;
    }

    private static List<Path> collectJavaFiles(Path root) throws IOException {
        if (Files.isRegularFile(root)) {
            return List.of(root);
        }
        List<Path> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(root)) {
            walk.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString().contains("/target/"))
                    .forEach(files::add);
        }
        return files;
    }

    public static void main(String[] args) throws Exception {
        Path target = args.length > 0 ? Path.of(args[0]) : Path.of("src/test/java");
        AgentValidator validator = new AgentValidator();
        ValidationReport report = validator.validatePath(target);
        System.out.println(report.render());
        if (!report.accepted()) {
            throw new IllegalStateException("Agent contribution validation REJECTED with "
                    + report.errorCount() + " error(s)");
        }
    }
}
