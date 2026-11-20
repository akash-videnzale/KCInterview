---
name: create-e2e-test
description: Compose multi-step UI journeys spanning signup, catalogue, cart, checkout.
---

# create-e2e-test

## Purpose
Compose multi-step UI journeys spanning signup, catalogue, cart, checkout.

## When to use
Business journey crosses multiple pages.

## Required inputs
Journey name; cleanup strategy.

## Preconditions
Page objects for each step exist; practice payment data only.

## Workflow
1. Create unique user via RegistrationDataFactory
2. Drive journey through page objects
3. Assert at each business gate
4. Delete account in finally-equivalent flow
5. Keep test independent of order


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
RegistrationData user = RegistrationDataFactory.uniqueUser();
new HomePage(page()).open().goToSignupLogin().startSignup(user.name(), user.email())
  .completeRegistration(user).continueAfterCreated();
new ProductsPage(page()).open().addFirstProductToCart().viewCartFromModal()
  .proceedToCheckout().placeOrder().fillPracticeCard(CheckoutDataFactory.practicePayment()).payAndConfirm();
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
