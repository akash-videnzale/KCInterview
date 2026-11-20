# Troubleshooting — common problems and fixes

---

## `-DHEADLESS` or `-DPARALLEL` not working

Look in the log for:

```text
Loaded config … headless=true, parallel=true …
```

If that line is wrong:

1. Pass flags on the Maven command: `mvn test -DHEADLESS=true`  
2. Remember order: **command line → env → `.env` → YAML**  
3. Defaults are: browser **visible**, tests **single**  

---

## Clicks fail because of ads / consent

1. Call `dismissOverlaysIfPresent()`  
2. Open the page by URL: `navigate("/login")`  
3. Run with a visible browser to see what is blocking  
4. Retry the action once if needed  

---

## Browser / Chrome issues

- Install Chrome (or try `-DBROWSER=firefox`)  
- Driver is downloaded automatically — don’t commit chromedriver  
- Headless mode: `mvn test -DHEADLESS=true`  

---

## API test fails even though HTTP is 200

Check JSON `responseCode`, not only HTTP.

```java
ApiResponseAssertions.assertUserCreated(response);
```

---

## “Email already exists”

- Always use `RegistrationDataFactory.uniqueUser()`  
- Delete accounts after the test  
- For the duplicate-email case, assert `ApiMessages.EMAIL_ALREADY_EXISTS`  

---

## Cart / checkout is flaky

- Wait for the cart modal (`#cartModal`)  
- Prefer links by `href` when possible  
- If product cards are blocked, open `/product_details/1` and add from there  
- Order success: look for `data-qa="order-placed"` (or `UiMessages` fallbacks)  

---

## Can’t find the report

After tests:

```text
target/extent-report/index.html
```

Failure screenshots: `target/artifacts/`

---

## AgentValidator says REJECTED

| Problem | Fix |
|---|---|
| Sleep in code | Use waits (`waitVisible`) |
| Hardcoded email/URL | Use factory + relative paths |
| No asserts | Use `UiAssertions` / `ApiResponseAssertions` |
| Driver created in test | Extend `UiBaseTest` and use pages |

---

## LLM agents (`AgentCli`)

These are **optional** and **not** part of `mvn test`.

They need keys in local `.env` and are started only when you run `AgentCli` yourself.

---

## Quick health check

```bash
mvn -q test-compile
mvn test -Dgroups=unit
mvn test -Dgroups=api -DHEADLESS=true
mvn test -Dgroups=ui -DHEADLESS=true
```
