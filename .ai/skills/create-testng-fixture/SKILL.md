---
name: create-testng-fixture
description: Create or extend TestNG lifecycle fixtures without putting test logic in hooks.
---

# create-testng-fixture

## Purpose
Create or extend TestNG lifecycle fixtures without putting test logic in hooks.

## When to use
Shared setup/teardown is needed beyond UiBaseTest/ApiBaseTest.

## Required inputs
Scope (suite/class/method); resources to manage.

## Preconditions
Confirm existing base class cannot cover the need.

## Workflow
1. Prefer composition over deep inheritance
2. Use @BeforeMethod/@AfterMethod with alwaysRun=true for cleanup
3. Keep ThreadLocal cleanup reliable
4. Do not share WebDriver across tests
5. Add groups only on @Test methods


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
@BeforeMethod(alwaysRun = true)
public void openUiSession(Method method) {
  TestSession session = SessionFactory.create(method.getName());
  UiSessionHolder.set(session);
}
@AfterMethod(alwaysRun = true)
public void closeUiSession(ITestResult result) {
  try { SessionFactory.close(UiSessionHolder.get(), result.getStatus()==ITestResult.FAILURE); }
  finally { UiSessionHolder.clear(); }
}
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
