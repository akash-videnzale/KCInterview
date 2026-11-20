---
name: create-page-object
description: Create a Selenium page object with data-qa locators and no-arg constructor.
---

# create-page-object

## Purpose
Encapsulate AE UI locators and actions in `com.agenticqa.ui.pages` (or components).

## When to use
Missing page for a journey (Contact Us, Reviews, Category filter, …) or extracting locators from a test.

## Required inputs
URL path; verified selectors (prefer `data-qa` from recon / live DOM).

## Preconditions
- Read `.ai/knowledge/selector-strategy.md` and `selenium-patterns.md`
- Inspect live page; do not invent `data-qa` names
- Reuse `HeaderComponent` / `ProductCardComponent` when possible

## Output location (mandatory)
Draft pages go to:

`agent-generated/src/main/java/com/agenticqa/ui/pages/YourPage.java`

**Not** `src/main/java/...` until human promotion.  
Prefer extending an existing framework page in `src/` only when explicitly asked to change production code.

## Workflow
1. Extend **`BasePage`** (import from framework)
2. Write file under **`agent-generated/src/main/java/...`**
3. Provide **no-arg** ctor + optional `WebDriver` ctor (match existing pages)
4. Prefer `byDataQa("…")`; put visible English in **`UiMessages`** (draft new constants in agent-generated or propose addition)
5. Call `dismissOverlaysIfPresent()` around navigate/submit
6. Prefer `navigate("/path")` when header clicks are blocked by ads
7. Return next page type from navigation actions
8. Keep **assertions out of the page** — tests call `UiAssertions`
9. Use `ExtentLogger.step` / screenshots at milestones

## Example (project style — save under agent-generated/)
```java
public class LoginPage extends BasePage {
    public LoginPage() { super(); }

    public LoginPage open() {
        ExtentLogger.step("Open login page");
        navigate("/login");
        dismissOverlaysIfPresent();
        waitVisible(byDataQa("login-button"));
        return this;
    }

    public SignupPage startSignup(String name, String email) {
        type(byDataQa("signup-name"), name);
        type(byDataQa("signup-email"), email);
        click(byDataQa("signup-button"));
        waitVisible(byDataQa("create-account"));
        return new SignupPage();
    }

    public boolean isInvalidCredentialsVisible() {
        return isVisible(byTagTextContains("p", UiMessages.INVALID_LOGIN));
    }
}
```

## Existing pages to extend before inventing new ones
`HomePage`, `LoginPage`, `SignupPage`, `AccountPage`, `ProductsPage`, `ProductDetailsPage`, `CartPage`, `CheckoutPage`, `PaymentPage`

## Prohibited
- Playwright `Page` / `.fill()` / `waitForTimeout`
- Locators with hardcoded English not in `UiMessages`
- `Thread.sleep`
- Asserts inside page objects (except tiny private sanity if unavoidable — prefer helpers)

## Validation
```bash
# After promotion into src/ only — drafts stay in agent-generated/
mvn -q test-compile
```
