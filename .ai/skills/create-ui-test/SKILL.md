---
name: create-ui-test
description: Create a TestNG UI test using Selenium Java page objects and TestNG Assert/SoftAssert.
---

# create-ui-test

## Purpose
Create a TestNG UI test using Selenium Java page objects and TestNG Assert/SoftAssert.

## When to use
Implementing a browser scenario.

## Required inputs
Scenario steps; page objects involved; groups.

## Preconditions
Relevant page objects exist or create-page-object skill applied first.

## Workflow
1. Extend UiBaseTest
2. Use page()/page objects only — never new ChromeDriver()
3. Use TestNG Assert for critical outcomes; SoftAssert + assertAll for multi-field checks
4. Assign groups ui + smoke/regression
5. Run AgentValidator on the new file


## Architecture constraints
- Layers: core → services (pages/api/factories) → tests → AI ecosystem → governance
- UI tests extend UiBaseTest; API tests extend ApiBaseTest
- Assertions: TestNG Assert and SoftAssert only
- Config via YAML + env vars (ConfigLoader)



## Prohibited practices
- Do not use JUnit, AssertJ, or Hamcrest
- Do not call Thread.sleep or TimeUnit.sleep; use WebDriverWait
- Do not hardcode credentials or environment URLs in tests
- Do not create WebDriver/ChromeDriver inside tests
- Do not call RestAssured.given() directly from tests
- Do not invent Automation Exercise endpoints or selectors
- Do not make tests depend on LLM APIs at runtime


## Output format
- Summary of changes
- Files created/modified
- TestNG groups used
- Validation commands run and results

## Examples
```java
@Test(groups = {"ui", "regression"})
public void shouldSearchProductsAndOpenDetails() {
  ProductsPage products = new ProductsPage(page()).open().search("top");
  Assert.assertTrue(products.productCount() > 0, "Expected search hits");
  SoftAssert soft = new SoftAssert();
  soft.assertFalse(products.productCards().get(0).name().isBlank(), "Name present");
  soft.assertAll();
}
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
