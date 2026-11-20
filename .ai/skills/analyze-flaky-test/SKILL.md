---
name: analyze-flaky-test
description: Identify non-determinism: shared state, fixed waits, racey locators, data collisions.
---

# analyze-flaky-test

## Purpose
Identify non-determinism: shared state, fixed waits, racey locators, data collisions.

## When to use
A test passes/fails intermittently.

## Required inputs
Test class; flake rate notes.

## Preconditions
Run the test repeatedly locally; collect traces on failure.

## Workflow
1. Check for static mutable fields
2. Search for Thread.sleep/waitForTimeout
3. Ensure unique emails via factories
4. Confirm isolated WebDriver per method
5. Tighten locators to data-qa / role-based


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
for i in $(seq 1 5); do mvn -Dtest=ProductDiscoveryUiTest test || break; done
scripts/fixed-wait-detector/run.sh
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
