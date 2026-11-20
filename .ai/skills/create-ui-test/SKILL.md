---
name: create-ui-test
description: Create a Selenium TestNG UI test for Automation Exercise (Signup/Discovery/Checkout style).
---

# create-ui-test

## Purpose
Add a **thin** UI test that drives page objects and asserts via `UiAssertions`.

## When to use
Browser scenario for AE journeys (signup, login, search, cart, checkout, contact, …).

## Required inputs
Scenario steps; pages involved; groups (`ui` + `smoke` and/or `regression`).

## Preconditions
- Read `.ai/knowledge/automation-inventory.md` — **do not recreate** listed UI methods
- Page objects exist (or run `create-page-object` first)
- Read `.ai/knowledge/selenium-patterns.md` and `coding-standards.md`
- Prefer adding a method to an existing test class when the journey already has a file
- Mirror golden tests from inventory: `SignupUiTest`, `ProductDiscoveryUiTest`, `CheckoutUiTest`, `LoginUiTest`

## Output location (mandatory)
Write **only** under the agent sandbox — never into framework `src/`:

| Artifact | Path |
|---|---|
| Draft test | `agent-generated/src/test/java/com/agenticqa/tests/ui/<area>/YourUiTest.java` |
| Draft page (if new) | `agent-generated/src/main/java/com/agenticqa/ui/pages/...` |
| Cases markdown | `agent-generated/cases/<feature>-cases.md` |

See `.ai/knowledge/agent-generated-sandbox.md` and `agent-generated/README.md`.  
Promote into `src/` only when the human explicitly asks.

## Workflow
1. Dump inventory; confirm this is a **gap**
2. Create class under **`agent-generated/src/test/java/...`** extending **`UiBaseTest`**
3. Build data with factories (`RegistrationDataFactory`, `CheckoutDataFactory`, `KnownProducts`)
4. Use **no-arg** pages: `new LoginPage().open()` — never `page()` / Playwright / `new ChromeDriver()`
5. Assert with **`UiAssertions`** / **`ProductAssertions`** — not long inline Assert messages
6. Cleanup accounts you create (`deleteAccount` UI or API `finally`)
7. Groups: `{"ui", "smoke", "regression"}` or `{"ui", "regression"}`
8. Validate draft path with AgentValidator; do **not** add to `src/` yet

## Example (matches project style — save under agent-generated/)
```java
public class SignupUiTest extends UiBaseTest {
    @Test(groups = {"ui", "smoke", "regression"})
    public void shouldRegisterUniqueUserAndDeleteAccount() {
        RegistrationData data = RegistrationDataFactory.uniqueUser();
        LoginPage loginPage = new LoginPage().open();
        UiAssertions.assertLoginFormVisible(loginPage);

        SignupPage signupPage = loginPage.startSignup(data.name(), data.email());
        UiAssertions.assertAccountInfoVisible(signupPage);

        AccountPage accountPage = signupPage.completeRegistration(data);
        UiAssertions.assertAccountCreated(accountPage);

        HomePage home = accountPage.continueAfterCreated();
        UiAssertions.assertLoggedIn(home);

        accountPage.deleteAccount();
        UiAssertions.assertAccountDeleted(accountPage);
    }
}
```

## Prohibited
- Locators in the test class
- `Thread.sleep`, hardcoded users/URLs
- Asserting website English strings inline (use `UiMessages` via pages/helpers)
- LLM calls in the test

## Validation
```bash
mvn -q -DskipTests exec:java \
  -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="agent-generated/src/test/java"
# After human promotion into src/ only:
# mvn test -Dtest=YourUiTest -DHEADLESS=true
```
