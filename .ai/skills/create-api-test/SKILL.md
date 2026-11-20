---
name: create-api-test
description: Create REST Assured API tests via API clients with schema and business-code checks.
---

# create-api-test

## Purpose
Create REST Assured API tests via API clients with schema and business-code checks.

## When to use
Automating verified Automation Exercise APIs.

## Required inputs
Endpoint + method; positive/negative cases.

## Preconditions
Endpoint documented in docs/recon; client method exists or create-api-client first.

## Workflow
1. Extend ApiBaseTest (no browser)
2. Call *ApiClient methods only — no RestAssured.given() in tests
3. Validate HTTP status and JSON responseCode
4. Apply SchemaValidator where schema exists
5. Cleanup created users via UserApiClient.deleteAccount


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
@Test(groups = {"api", "smoke"})
public void shouldReturnProductCatalogue() {
  Response raw = productApi.getAllProductsRaw();
  Assert.assertEquals(raw.statusCode(), 200, "HTTP 200");
  SchemaValidator.assertMatches(raw, "schemas/products-list-schema.json");
  Assert.assertEquals(productApi.getAllProducts().responseCode(), 200);
}
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
