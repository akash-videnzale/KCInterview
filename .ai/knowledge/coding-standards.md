# Coding standards — write code the same way every time

---

## Easy rules

1. **Java 21** only  
2. Assertions: **TestNG only** (`Assert` / `SoftAssert`) — no AssertJ, no JUnit  
3. Tests should be **short** — they only orchestrate  
4. Real work lives in **pages**, **API clients**, and **factories**  
5. **Agent-generated code goes in `agent-generated/` only** — never mix into `src/` until promotion  

See `.ai/knowledge/agent-generated-sandbox.md`.

---

## Good vs bad test

```java
// GOOD — clear and thin
RegistrationData user = RegistrationDataFactory.uniqueUser();
LoginPage login = new LoginPage().open();
UiAssertions.assertLoginFormVisible(login);

// BAD — hard to maintain
Thread.sleep(2000);
driver.findElement(By.id("email")).sendKeys("fixed@test.com");
Assert.assertTrue(ok, "Cleanup delete should succeed");
```

---

## API response classes

Use **POJOs** (class + getters + setters), not records, for API JSON.

```java
product.getName()
body.getResponseCode()
```

Not: `product.name()` / `body.responseCode()`

---

## Where text belongs

| Kind of text | Put it here |
|---|---|
| Button / error text on the website | `UiMessages` |
| API message like `"User created!"` | `ApiMessages` |
| Product name/price we assert | `KnownProducts` / `KnownBrands` |
| Assert checks | `UiAssertions` / `ApiResponseAssertions` |

**Never** copy website text into the test class.

---

## UI pages

- Create with **no-arg** constructor: `new LoginPage()`  
- Driver comes from the framework automatically  
- Prefer `data-qa` locators  

---

## Things that will be rejected

- `Thread.sleep(...)`  
- Hardcoded email / password / full website URL in tests  
- Starting the browser inside a test  
- Empty tests or `assertTrue(true)`  
- Making up APIs or selectors that are not in recon / existing code  

---

## Before you finish

```bash
mvn -q test-compile
mvn test -Dgroups=ui      # or api, smoke, …
```

Also run the agent validator on new test files when possible.
