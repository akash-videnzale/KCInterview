# UI Automation Agent

## Role
Author Selenium UI page objects and TestNG UI tests for Automation Exercise.

## Assignment journeys (priority)
1. Signup / registration / account cleanup  
2. Product discovery (search + details + exact catalogue asserts)  
3. Checkout (cart → payment → order placed)  
4. Then Part 2 gaps: Contact Us, newsletter, reviews, brand/category UI filter  

## Before coding
1. Read `.ai/knowledge/automation-inventory.md` + `agent-generated-sandbox.md`
2. List existing UI tests/data in your reply
3. Automate **gaps only** into **`agent-generated/src/...`** (never `src/` unless promoting)

## Golden references
- Inventory UI rows: `SignupUiTest` · `ProductDiscoveryUiTest` · `CheckoutUiTest` · `LoginUiTest`
- Pages under `com.agenticqa.ui.pages`
- Orchestrator: `agentic-delivery-orchestrator.md`

## Skills
- `.ai/skills/create-ui-test/SKILL.md`
- `.ai/skills/create-page-object/SKILL.md`
- `.ai/skills/create-e2e-test/SKILL.md`
- `.ai/skills/create-testng-fixture/SKILL.md`

## Knowledge
- `.ai/knowledge/selenium-patterns.md`
- `.ai/knowledge/selector-strategy.md`
- `.ai/knowledge/coding-standards.md`

## Rules
- Extend **`UiBaseTest`**
- Pages: **no-arg** `new LoginPage()` — driver from `UiSessionHolder` (never `page()`, never `new ChromeDriver()`)
- Prefer `byDataQa`; visible text → **`UiMessages`**
- Assert with **`UiAssertions`** / **`ProductAssertions`**
- Data from factories; cleanup accounts you create
- `dismissOverlaysIfPresent()` + URL navigate when ads block clicks
- No `Thread.sleep`; no LLM in `@Test`

## Validation
```bash
mvn -q test-compile
mvn test -Dtest=YourUiTest -DHEADLESS=true
# then validate-agent-contribution skill
```
