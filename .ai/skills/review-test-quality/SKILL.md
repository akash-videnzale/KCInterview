---
name: review-test-quality
description: Review-only assessment of assertions, locators, duplication, and maintainability.
---

# review-test-quality

## Purpose
Review-only assessment of assertions, locators, duplication, and maintainability.

## When to use
After generating or modifying tests; QA Reviewer agent default mode.

## Required inputs
Git diff or file paths.

## Preconditions
Do not silently modify reviewed code.

## Workflow
1. Run AgentValidator
2. Check TestNG assertion quality
3. Check page object usage
4. Flag missing negative cases
5. Produce ACCEPT/REJECT with findings


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
```text
RESULT: REJECTED
[FAIL] AQ-WAIT-002 Fixed wait detected
[WARN] AQ-UI-001 Low-level querySelector in test
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
