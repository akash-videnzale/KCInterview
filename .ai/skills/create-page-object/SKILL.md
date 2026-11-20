---
name: create-page-object
description: Create a Selenium Java page object encapsulating locators and user actions.
---

# create-page-object

## Purpose
Create a Selenium Java page object encapsulating locators and user actions.

## When to use
A UI surface lacks a page object or needs extraction from a test.

## Required inputs
Page URL; verified selectors (prefer data-qa).

## Preconditions
Inspect live DOM; document difficult locators.

## Workflow
1. Extend BasePage
2. Expose actions returning next page objects
3. Keep assertions in tests
4. Reuse Header/Footer/ProductCard components
5. Avoid Thread.sleep and waitForTimeout


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
public class LoginPage extends BasePage {
  public LoginPage(Page page) { super(page); }
  public SignupPage startSignup(String name, String email) {
    byDataQa("signup-name").fill(name);
    byDataQa("signup-email").fill(email);
    byDataQa("signup-button").click();
    waitForDomReady();
    return new SignupPage(page);
  }
}
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
