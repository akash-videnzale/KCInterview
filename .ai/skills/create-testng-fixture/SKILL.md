---
name: create-testng-fixture
description: Extend TestNG lifecycle (UiBaseTest/ApiBaseTest) without putting test logic in hooks.
---

# create-testng-fixture

## Purpose
Adjust shared setup/teardown safely. Prefer existing bases before inventing new ones.

## When to use
Need beyond `UiBaseTest` / `ApiBaseTest` (rare). Part 2 may ask for a new fixture — keep it thin.

## Required inputs
Scope (suite/class/method); resources managed.

## Preconditions
Confirm `UiBaseTest` / `ApiBaseTest` cannot cover the need. Read `.ai/knowledge/testng-patterns.md`.

## Workflow
1. Prefer composition over deep inheritance
2. UI: one WebDriver per `@BeforeMethod` via `SessionFactory` + `UiSessionHolder` (ThreadLocal)
3. `@AfterMethod(alwaysRun = true)` closes session + captures screenshot on failure
4. API: logging context only — no browser
5. Do not put business asserts in hooks
6. Groups belong on `@Test`, not on hooks

## Existing pattern (do not break)
```java
// UiBaseTest
@BeforeMethod(alwaysRun = true)
public void openUiSession(Method method) { … SessionFactory.create … UiSessionHolder.set … }

@AfterMethod(alwaysRun = true)
public void closeUiSession(ITestResult result) { … screenshot on fail … close … clear … }
```

## Parallel note
Defaults are **single + headed**. Parallel uses the same ThreadLocal sessions when `-DPARALLEL=true`.

## Prohibited
- Shared static WebDriver across tests
- Sleeps in hooks
- Skipping `alwaysRun = true` on cleanup

## Validation
```bash
mvn test -Dgroups=ui -DHEADLESS=true
mvn test -Dgroups=ui -DPARALLEL=true -DTHREAD_COUNT=2 -DHEADLESS=true
```
