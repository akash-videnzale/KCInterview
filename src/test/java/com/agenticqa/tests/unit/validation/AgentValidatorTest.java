package com.agenticqa.tests.unit.validation;

import com.agenticqa.governance.AgentValidator;
import com.agenticqa.governance.report.ValidationReport;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class AgentValidatorTest {

    @Test(groups = {"unit", "smoke", "regression"})
    public void shouldDetectViolationsInFlawedExample() throws Exception {
        Path flawed = Path.of("evaluations/scenarios/flawed-examples/FlawedCheckoutTest.java");
        Assert.assertTrue(Files.exists(flawed), "Flawed example must exist for evaluation");

        AgentValidator validator = new AgentValidator();
        ValidationReport report = validator.validatePath(flawed);

        Assert.assertFalse(report.accepted(), "Flawed example must be REJECTED");
        Assert.assertTrue(report.errorCount() >= 5, "Expected multiple ERROR violations, got " + report.errorCount());

        String rendered = report.render();
        Assert.assertTrue(rendered.contains("REJECTED"), "Report should say REJECTED");
        Assert.assertTrue(containsRule(report, "AQ-WAIT-001") || rendered.contains("Thread.sleep"),
                "Should detect Thread.sleep");
        Assert.assertTrue(containsRule(report, "AQ-WAIT-002") || rendered.contains("waitForTimeout"),
                "Should detect fixed wait");
        Assert.assertTrue(containsRule(report, "AQ-BRW-001") || rendered.toLowerCase().contains("playwright"),
                "Should detect direct browser bootstrap");
        Assert.assertTrue(containsRule(report, "AQ-SEC-001") || rendered.toLowerCase().contains("credential")
                        || rendered.toLowerCase().contains("password"),
                "Should detect hardcoded credential");
        Assert.assertTrue(containsRule(report, "AQ-CFG-001") || rendered.toLowerCase().contains("url"),
                "Should detect hardcoded URL");
        Assert.assertTrue(containsRule(report, "AQ-TST-001") || rendered.toLowerCase().contains("empty"),
                "Should detect empty test");
    }

    @Test(groups = {"unit", "regression"})
    public void shouldAcceptCleanSnippet() {
        String source = """
                package com.agenticqa.tests.api.products;
                import org.testng.Assert;
                import org.testng.annotations.Test;
                import com.agenticqa.api.clients.ProductApiClient;
                public class CleanProductTest {
                  private final ProductApiClient productApi = new ProductApiClient();
                  @Test
                  public void shouldLoadProducts() {
                    Assert.assertTrue(productApi.getAllProducts().getProducts().size() > 0, "products present");
                  }
                }
                """;
        ValidationReport report = new AgentValidator().validateSource(
                "src/test/java/com/agenticqa/tests/api/products/CleanProductTest.java", source);
        Assert.assertTrue(report.accepted(), "Clean snippet should be ACCEPTED\n" + report.render());
        Assert.assertEquals(report.errorCount(), 0);
    }

    private static boolean containsRule(ValidationReport report, String ruleId) {
        return report.allViolations().stream().anyMatch(v -> ruleId.equals(v.ruleId()));
    }
}
