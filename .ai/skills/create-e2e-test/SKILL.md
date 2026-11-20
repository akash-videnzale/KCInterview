---
name: create-e2e-test
description: Build multi-step UI journeys (Signup → Discovery → Checkout) with cleanup.
---

# create-e2e-test

## Purpose
Compose a full business journey across pages. Primary assignment flows: **Signup, Product Discovery, Checkout**.

## When to use
Journey spans 2+ pages (e.g. register → shop → pay → delete account).

## Required inputs
Journey name; cleanup strategy; groups.

## Preconditions
- Pages for each step exist
- Practice payment only (`CheckoutDataFactory.practicePayment()`)
- Golden reference: **`CheckoutUiTest`**

## Output location (mandatory)
`agent-generated/src/test/java/com/agenticqa/tests/ui/<area>/YourE2eUiTest.java`  
Do **not** overwrite `CheckoutUiTest` in `src/` — that P0 is already automated.

## Workflow
1. Confirm gap vs inventory (Checkout happy path already exists)
2. Extend `UiBaseTest`; write under **`agent-generated/`**
3. `RegistrationDataFactory.uniqueUser()` (+ payment factory if needed)
4. Drive only through page objects + `UiAssertions` at each gate
5. Delete account at end (happy path) and/or API `finally` if hybrid
6. Keep test independent (no shared static user)
7. Groups usually `{"ui", "regression"}` (smoke only if short)

## Example (CheckoutUiTest shape)
```java
RegistrationData user = RegistrationDataFactory.uniqueUser();
PaymentData payment = CheckoutDataFactory.practicePayment();

LoginPage loginPage = new LoginPage().open();
AccountPage accountPage = loginPage.startSignup(user.name(), user.email())
        .completeRegistration(user);
UiAssertions.assertAccountCreated(accountPage);
accountPage.continueAfterCreated();

ProductsPage productsPage = new ProductsPage().open();
productsPage.addFirstProductToCart();
CartPage cartPage = productsPage.viewCartFromModal();
UiAssertions.assertCartLoaded(cartPage);
UiAssertions.assertCartHasItems(cartPage);

CheckoutPage checkoutPage = cartPage.proceedToCheckout();
UiAssertions.assertAddressDetailsVisible(checkoutPage);
checkoutPage.enterOrderComment(CheckoutDataFactory.orderComment());

PaymentPage paymentPage = checkoutPage.placeOrder();
UiAssertions.assertPaymentPageLoaded(paymentPage);
paymentPage.fillPracticeCard(payment).payAndConfirm();
UiAssertions.assertOrderPlaced(paymentPage);

accountPage.deleteAccount();
UiAssertions.assertAccountDeleted(accountPage);
```

## Assignment note
Part 1 already has Signup / Discovery / Checkout. For Part 2 extensions, reuse this pattern for adjacent journeys (contact us, newsletter, reviews, category filter) — depth, not a second full checkout clone.

## Prohibited
- Real payment data
- Skipping account cleanup
- Locators / sleeps in the test

## Validation
```bash
mvn -q -DskipTests exec:java \
  -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="agent-generated/src/test/java"
# Existing golden checkout (framework):
mvn test -Dtest=CheckoutUiTest -DHEADLESS=true
```
