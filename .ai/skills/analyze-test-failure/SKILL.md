---
name: analyze-test-failure
description: Diagnose a failed TestNG run using logs, screenshots, traces, and Allure.
---

# analyze-test-failure

## Purpose
Diagnose a failed TestNG run using logs, screenshots, traces, and Allure.

## When to use
A test failed and root cause is unclear.

## Required inputs
Test name; surefire/Allure output; artifact path.

## Preconditions
Artifacts captured under target/artifacts and target/allure-results.

## Workflow
1. Read failure message and stack
2. Inspect screenshot and console/page errors
3. Open failure screenshot/page-source if present
4. Classify with FailureClassifier heuristics (not authoritative)
5. Propose a minimal fix and re-run the single test


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
```bash
mvn -Dtest=SignupUiTest#shouldRegisterUniqueUserAndDeleteAccount test
# Inspect target/artifacts/**/failure.png and trace.zip
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
