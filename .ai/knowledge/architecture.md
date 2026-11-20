# Architecture — how this project is built

**What we automate:** https://automationexercise.com/  
**Stack:** Java 21 · Selenium · TestNG · REST Assured · Extent Reports · Maven

---

## Big picture

Think of the project as **5 layers**.  
Each layer only talks to the one below it.

```text
5. Governance     → Checks that new code follows the rules
4. AI helpers     → Skills/docs for Cursor (not used during mvn test)
3. Tests          → Short scripts that say what to do
2. Pages/API/Data → How to click, call APIs, build test users
1. Core           → Browser, config, logging, reports
```

**Rule for agents:** put logic in layer 2 (pages/clients), keep tests short.

---

## UI tests — what happens when a test runs

1. Start a fresh Chrome for this test only  
2. Open pages like `new LoginPage()` (driver is already set)  
3. Click / type / assert  
4. On fail → take screenshot  
5. Close the browser for that test  

Base class: **`UiBaseTest`**

---

## API tests — what happens when a test runs

1. No browser  
2. Call something like `productApi.getAllProducts()`  
3. Turn JSON into a Java object (POJO)  
4. Assert with `ApiResponseAssertions`  

Base class: **`ApiBaseTest`**

---

## Must follow

| Do | Don't |
|---|---|
| Extend `UiBaseTest` or `ApiBaseTest` | Create `new ChromeDriver()` in a test |
| Use page objects and API clients | Put locators or HTTP calls in the test |
| Use factories for emails/users | Hardcode passwords or emails |
| Use `UiAssertions` / `ApiResponseAssertions` | Scatter long assert messages in tests |

---

## Default run mode

- Browser **visible** (not headless)  
- Tests run **one after another** (not parallel)  

Override from terminal:

```bash
mvn test -DHEADLESS=true
mvn test -DPARALLEL=true
```

---

## Where to read next

| Question | File |
|---|---|
| What tests/data already exist? | **`automation-inventory.md`** (read first) |
| Where agents write code | **`agent-generated-sandbox.md`** + `agent-generated/` |
| Product / assignment requirements | `docs/requirements/AUTOMATION_REQUIREMENTS.md` |
| Full agentic chain | `.ai/workflows/agentic-delivery-chain.md` |
| How do I write UI code? | `selenium-patterns.md` |
| How do I write API code? | `api-contracts.md` |
| How do I pick locators? | `selector-strategy.md` |
| How do I create users/data? | `test-data-strategy.md` |
| Something broke? | `troubleshooting.md` |
| Coding style? | `coding-standards.md` |
| TestNG groups / parallel? | `testng-patterns.md` |
