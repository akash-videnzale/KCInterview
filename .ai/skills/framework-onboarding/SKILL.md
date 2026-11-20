---
name: framework-onboarding
description: Onboard before writing any test, page, or client for Automation Exercise.
---

# framework-onboarding

## Purpose
Bring an agent up to speed on this **agentic-first** framework before coding.

## Assignment context (Part 1)
- AUT: https://automationexercise.com/
- Stack chosen (see `DECISIONS.md`): **Java 21 + Selenium + TestNG + REST Assured** (not Playwright+TS)
- Must cover **depth**: Signup, Product Discovery, Checkout + API
- Agentic design = skills + knowledge + deterministic validators (not docs alone)

## When to use
New chat/session, or before any create-* skill.

## Workflow
1. Read `README.md` and `DECISIONS.md` (why Selenium)
2. Read **`docs/requirements/AUTOMATION_REQUIREMENTS.md`**
3. Read **`.ai/knowledge/automation-inventory.md`** and **list current tests/data in your reply**
4. Read `.ai/knowledge/architecture.md` (+ topic knowledge as needed)
5. Skim recon: `docs/recon/automation-exercise-findings.md`
6. Open golden tests from the inventory (do not reinvent them)
7. For new work, use **agentic delivery chain**:  
   `.ai/skills/agentic-delivery-chain/SKILL.md` / `.ai/agents/agentic-delivery-orchestrator.md`
8. State constraints (stack, layers, no LLM in asserts) before coding

## Always apply
- UI → `UiBaseTest` + `new LoginPage()` (no-arg; driver from `UiSessionHolder`)
- API → `ApiBaseTest` + `*ApiClient` + POJO getters + `ApiResponseAssertions`
- Data → factories (`RegistrationDataFactory.uniqueUser()`)
- Assert helpers → `UiAssertions` / `ProductAssertions` / `ApiResponseAssertions`
- Text → `UiMessages` (UI) / `ApiMessages` (API) / `KnownProducts` (catalogue)
- Config default: **headed + single**; override `-DHEADLESS` / `-DPARALLEL`

## Prohibited
- Playwright, JUnit, AssertJ, Hamcrest
- `Thread.sleep`, `new ChromeDriver()`, raw `RestAssured.given()` in tests
- Hardcoded emails/passwords/full AUT URLs in tests
- Inventing endpoints/selectors not in recon or existing pages
- LLM calls inside `@Test` assertions

## Validation
```bash
mvn -q test-compile
mvn test -Dgroups=smoke -DHEADLESS=true
```
