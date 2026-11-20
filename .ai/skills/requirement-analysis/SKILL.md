---
name: requirement-analysis
description: Turn a feature requirement into a testable plan aligned to Automation Exercise.
---

# requirement-analysis

## Purpose
Turn a feature requirement into a testable plan aligned to Automation Exercise.

## When to use
Before creating any new UI/API/E2E test.

## Required inputs
Requirement text; optional journey name (signup/discovery/checkout).

## Preconditions
Read docs/recon/automation-exercise-findings.md; do not invent APIs.

## Workflow
1. Identify UI surfaces and API endpoints from recon docs
2. Map to existing page objects/clients
3. Choose layer: ui / api / integration
4. List assertions and data needs
5. Flag unsupported application behavior


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
## Plan
- Layer: API
- Client: ProductApiClient.searchProduct
- Endpoint: POST /api/searchProduct (verified)
- Assertions: responseCode 200, products non-empty for term 'top'
- Negative: missing search_product -> business 400
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
