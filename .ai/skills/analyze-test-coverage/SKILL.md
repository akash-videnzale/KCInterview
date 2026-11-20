---
name: analyze-test-coverage
description: Identify missing coverage across signup, discovery, checkout, and API contracts.
---

# analyze-test-coverage

## Purpose
Identify missing coverage across signup, discovery, checkout, and API contracts.

## When to use
Planning regression or after feature changes.

## Required inputs
Changed files; optional JaCoCo report.

## Preconditions
Recon docs list supported journeys/APIs.

## Workflow
1. Map journeys to existing tests
2. List unverified API scenarios from api_list
3. Recommend highest-value gaps
4. Do not invent unsupported endpoints


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
```markdown
Covered: productsList GET, searchProduct, create/verify/delete user, signup UI, search UI, checkout UI
Gap: brandsList GET happy path; contact us UI; newsletter subscribe UI
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
