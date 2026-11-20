# Automation Requirements — Automation Exercise

**AUT:** https://automationexercise.com/  
**Framework:** Agentic QA Automation Framework (Java 21 · Selenium · TestNG · REST Assured)  
**Assignment:** Part 1 take-home (depth over breadth) + Part 2 live extension

---

## 1. Product scope (application journeys)

| ID | Journey | In Part 1 depth? | Notes |
|---|---|---|---|
| REQ-AUTH-01 | User registration | **Yes (P0)** | UI signup + API createAccount |
| REQ-AUTH-02 | User login / authentication | **Yes (P0)** | UI login + API verifyLogin |
| REQ-AUTH-03 | Account delete / cleanup | **Yes (P0)** | Required after create |
| REQ-CAT-01 | Product catalogue browsing | **Yes (P0)** | API productsList + UI search entry |
| REQ-CAT-02 | Product search | **Yes (P0)** | UI + API searchProduct |
| REQ-CAT-03 | Product detail page | **Yes (P0)** | UI details exact name/price |
| REQ-CAT-04 | Brand listing | **Yes (P1)** | API brandsList |
| REQ-CAT-05 | Category / brand filtering (UI) | Part 2 candidate (P2) | Not automated UI yet |
| REQ-CART-01 | Add to cart / cart view | **Yes (P0)** | Inside checkout journey |
| REQ-CHK-01 | Checkout + practice payment | **Yes (P0)** | UI only (no real gateway) |
| REQ-ACC-01 | Get user detail by email | **Yes (P1)** | API |
| REQ-ACC-02 | Update account | Optional (P2) | API exists; not automated |
| REQ-CNT-01 | Contact Us form | Part 2 candidate (P2) | |
| REQ-NEWS-01 | Newsletter subscription | Part 2 candidate (P2) | Footer typo id `susbscribe_email` |
| REQ-REV-01 | Product reviews | Part 2 candidate (P2) | |

---

## 2. Framework / agentic requirements

| ID | Requirement |
|---|---|
| REQ-FW-01 | Working tests covering Signup, Product Discovery, Checkout |
| REQ-FW-02 | Both **UI and API** coverage |
| REQ-FW-03 | Shared auth/data via factories + cleanup |
| REQ-FW-04 | Agentic-first: skills, knowledge, agents, deterministic validators |
| REQ-FW-05 | Locally runnable (`mvn test`); headed + single by default |
| REQ-FW-06 | `DECISIONS.md`, `AI_USAGE.md`, README at repo root |
| REQ-FW-07 | No LLM inside TestNG assertions |
| REQ-FW-08 | Agents must **reuse** existing tests/pages/clients before creating new ones |

---

## 3. Priority definitions (for generated test cases)

| Priority | Meaning | Examples |
|---|---|---|
| **P0** | Assignment-critical / smoke | Signup, discovery search+details, checkout, productsList, searchProduct, create/verify/delete user |
| **P1** | Important negatives / adjacent | Invalid login, duplicate email, brandsList, search missing param, user detail |
| **P2** | Nice-to-have / Part 2 | Contact Us, newsletter, reviews, category filter UI, updateAccount |

---

## 4. Already automated (source of truth)

See live inventory: `.ai/knowledge/automation-inventory.md`

Agents **must** open that file first and treat listed tests as existing coverage.

---

## 5. How agents should use this doc

1. Read this requirements doc  
2. Read `.ai/knowledge/automation-inventory.md`  
3. Diff new ask vs covered REQ-* IDs  
4. Produce prioritized cases (P0/P1/P2) only for **gaps**  
5. Automate gaps via create-ui-test / create-api-test skills  
6. Validate with AgentValidator + targeted `mvn test`  

Full chain: `.ai/workflows/agentic-delivery-chain.md`
