# Selenium patterns — how UI automation works here

---

## One browser per test

Every UI test gets its **own** Chrome session.

- Start/stop is handled by **`UiBaseTest`**  
- In the test, just write: `new LoginPage()`  
- Do **not** create the driver yourself  

This also works when tests run in parallel.

---

## Waiting (important)

Wait for the element — never sleep.

```java
// GOOD
waitVisible(byDataQa("pay-button"));

// BAD — will be rejected
Thread.sleep(3000);
```

---

## Page objects

Pages know **how** to use the website.

Tests know **what** journey to run.

```text
Test  →  LoginPage.open()  →  startSignup(...)  →  SignupPage
```

- Put clicks and typing in the page  
- Put asserts in **`UiAssertions`**  
- Log important steps with `ExtentLogger.step(...)`  

---

## Ads and popups

Automation Exercise often shows consent / ads.

Always:

```java
dismissOverlaysIfPresent();
```

If the header click is blocked, open the URL directly:

```java
navigate("/products");
```

---

## Cart tip

After “Add to cart”:

1. Wait for `#cartModal`  
2. Click the cart link inside the modal  
3. If the product list is blocked, open `/product_details/1` and add from there  

---

## How to assert in UI tests

```java
UiAssertions.assertAccountCreated(accountPage);
UiAssertions.assertOrderPlaced(paymentPage);
ProductAssertions.assertKnownBlueTopDetails(name, price);
```

Keep assert wording inside those helpers — not in the test.

---

## How to run UI tests

```bash
# Normal: see the browser, one test at a time
mvn test -Dgroups=ui

# No browser window
mvn test -Dgroups=ui -DHEADLESS=true

# Parallel
mvn test -Dgroups=ui -DPARALLEL=true
```

---

## Never do this

- `new ChromeDriver()` in a test  
- Playwright code (`Page`, `waitForTimeout`, …)  
- One shared static browser for all tests  
- Hardcoded `"Blue Top"` / `"Rs. 500"` → use `KnownProducts`  
