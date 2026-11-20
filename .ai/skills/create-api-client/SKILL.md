---
name: create-api-client
description: Add a REST Assured client method for a verified endpoint.
---

# create-api-client

## Purpose
Add a REST Assured client method for a verified endpoint.

## When to use
Tests need a new API operation.

## Required inputs
Method, path, params; response model.

## Preconditions
Confirm endpoint in api_list / recon docs.

## Workflow
1. Extend BaseApiClient
2. Use givenForm()/givenJson()
3. Attach redacted Allure payloads
4. Return Response and typed model helpers
5. Add/adjust JSON schema if stable


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
public Response searchProductRaw(String searchProduct) {
  Response response = givenForm().formParam("search_product", searchProduct)
      .when().post("/api/searchProduct");
  attachResponse("searchProduct", response);
  return response;
}
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
