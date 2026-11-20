---
name: framework-onboarding
description: Onboard an AI agent or engineer to the Java 21 Selenium TestNG REST Assured framework.
---

# framework-onboarding

## Purpose
Onboard an AI agent or engineer to the Java 21 Selenium TestNG REST Assured framework.

## When to use
Starting work in a new session, before generating tests or page objects.

## Required inputs
None required; optional focus area (ui/api/validation).

## Preconditions
Repository cloned; AGENTS.md readable.

## Workflow
1. Read AGENTS.md and docs/architecture/overview.md
2. Inspect package layout under src/main/java/com/agenticqa
3. Review UiBaseTest and ApiBaseTest lifecycles
4. Review one UI and one API test as patterns
5. Summarize constraints before coding


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
// UI tests extend UiBaseTest and use page objects
public class SignupUiTest extends UiBaseTest {
  @Test(groups = {"ui", "smoke"})
  public void shouldRegisterUniqueUserAndDeleteAccount() {
    HomePage home = new HomePage(page()).open();
    Assert.assertTrue(home.isLoaded(), "Home page should load");
  }
}
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
