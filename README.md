# Agentic QA Automation Framework

Enterprise-grade, **agentic-first** Quality Engineering platform for
[Automation Exercise](https://automationexercise.com/) — UI + API automation,
deterministic governance, and an AI skill/agent ecosystem that never runs inside TestNG assertions.

**Author:** Akash Aggarwal

---

## Table of contents

1. [What this project is](#1-what-this-project-is)
2. [Technology stack](#2-technology-stack)
3. [Architecture](#3-architecture)
4. [How it works](#4-how-it-works)
5. [Project structure](#5-project-structure)
6. [Prerequisites](#6-prerequisites)
7. [Installation](#7-installation)
8. [Configuration](#8-configuration)
9. [How to execute](#9-how-to-execute)
10. [Implemented tests](#10-implemented-tests)
11. [Reporting](#11-reporting)
12. [AI platform](#12-ai-platform)
13. [Governance & contribution](#13-governance--contribution)
14. [Troubleshooting](#14-troubleshooting)
15. [Known limitations](#15-known-limitations)
16. [License](#16-license)

---

## 1. What this project is

A Maven / TestNG framework that:

- Automates **UI** flows (signup, login, product search, checkout) with **Selenium 4**
- Automates **API** flows (products, brands, register / login / delete) with **REST Assured**
- Keeps tests thin: orchestration only — pages, clients, factories, and assertion helpers own the details
- Empowers AI coding agents via `.ai/skills`, agents, and workflows
- Constrains agents with **JavaParser validators** (no sleeps, no hardcoded secrets/URLs, meaningful asserts)
- Uses **Extent Reports** for HTML reporting (secrets redacted)

**Application under test (AUT):** `https://automationexercise.com`

---

## 2. Technology stack

| Component | Version |
|---|---|
| Java | 21 |
| Selenium WebDriver | 4.27.0 |
| WebDriverManager | 5.9.2 |
| TestNG | 7.10.2 |
| REST Assured | 5.5.0 |
| Jackson (JSON / YAML) | 2.18.2 |
| ExtentReports | 5.1.2 |
| Java Faker | 1.0.2 |
| JavaParser (governance) | 3.26.3 |
| SLF4J / Logback | 2.0.16 / 1.5.12 |
| JaCoCo / Checkstyle / SpotBugs | via Maven plugins |
| Build | Maven 3.9+ |

Assertions: **TestNG `Assert` / `SoftAssert` only** (no AssertJ / Hamcrest).

---

## 3. Architecture

Five layers — dependencies point **downward** only.

```text
┌──────────────────────────────────────────────────────────────┐
│ Layer 5: Engineering Governance                              │
│   AgentValidator · ArchitectureValidator · detector scripts  │
├──────────────────────────────────────────────────────────────┤
│ Layer 4: AI Engineering Ecosystem                            │
│   .ai/skills · agents · workflows · .cursor rules/commands   │
├──────────────────────────────────────────────────────────────┤
│ Layer 3: Tests                                               │
│   UI · API · integration · unit  (TestNG groups)             │
├──────────────────────────────────────────────────────────────┤
│ Layer 2: Automation Services                                 │
│   Pages / components · API clients · models · data factories │
│   UiAssertions · ApiResponseAssertions · UiMessages          │
├──────────────────────────────────────────────────────────────┤
│ Layer 1: Automation Core                                     │
│   BrowserManager · SessionFactory · ConfigLoader · Extent    │
└──────────────────────────────────────────────────────────────┘
```

### Layer rules

| Layer | Owns | Must not |
|---|---|---|
| **Tests** | Flow orchestration, group tags | Locators, raw HTTP, hardcoded users/URLs |
| **UI** | Selenium locators, page actions | REST Assured, TestNG `@Test` |
| **API** | Clients, Jackson POJOs, schemas | WebDriver |
| **Data** | Factories + value objects | Driver / HTTP |
| **Core** | Config, browser lifecycle, reporting | Business assertions |
| **Governance** | Deterministic AST rules | LLM calls |
| **Agents** | Optional offline LLM pipeline | Called from `@Test` assertions |

Design decisions: see [`DECISIONS.md`](DECISIONS.md) and [`docs/architecture/overview.md`](docs/architecture/overview.md).

---

## 4. How it works

### 4.1 Configuration resolution

```text
mvn -DHEADLESS=true / -DPARALLEL=true   ← highest priority
        ↓
OS environment variables
        ↓
.env  (from .env.example)
        ↓
src/main/resources/config/framework-config.yaml   ← defaults
```

`ConfigLoader` builds a cached `FrameworkConfig`. Surefire forwards `-D` flags into the forked test JVM via `systemPropertyVariables`.

**Defaults:** headed (`headless: false`) and single-threaded (`parallelEnabled: false`).

### 4.2 UI test lifecycle

```text
@AfterSuite          BrowserManager.stop()
@BeforeMethod        BrowserManager.start()
                     SessionFactory.create(testName) → WebDriver
                     UiSessionHolder.set(session)
@Test                new LoginPage() / ProductsPage() … (driver from UiSessionHolder)
@AfterMethod         screenshot + console attach on failure
                     SessionFactory.close(session)
                     UiSessionHolder.clear()
```

- **`UiBaseTest`** — every UI test extends this
- **`BasePage`** — navigate, wait, click/type with overlay dismiss, `byDataQa`, text helpers
- **Pages** — `LoginPage`, `SignupPage`, `AccountPage`, `ProductsPage`, `CartPage`, `CheckoutPage`, `PaymentPage`, …
- **`UiMessages`** — AE display copy in one place (no hardcoded strings in tests)
- **`UiAssertions` / `ProductAssertions`** — page-state and product field checks

Example flow (checkout):

```text
RegistrationDataFactory.uniqueUser()
  → LoginPage.open().startSignup(…)
  → SignupPage.completeRegistration(…)
  → UiAssertions.assertAccountCreated(…)
  → ProductsPage.addFirstProductToCart() → Cart → Checkout → Payment
  → UiAssertions.assertOrderPlaced(…)
  → AccountPage.deleteAccount()
```

### 4.3 API test lifecycle

```text
ApiBaseTest.@BeforeMethod   → bind log context (no browser)
*ApiClient                  → REST Assured + ExtentRestAssuredFilter
Jackson POJO                → ApiMessageResponse, ProductsListResponse, …
ApiResponseAssertions       → exact responseCode + message / catalogue anchors
```

Clients:

| Client | Responsibility |
|---|---|
| `BaseApiClient` | Specs, body normalize, AE `responseCode`, overload retry, attach |
| `ProductApiClient` | productsList, searchProduct |
| `BrandApiClient` | brandsList |
| `UserApiClient` | createAccount, deleteAccount, getUserDetailByEmail |
| `AuthenticationApiClient` | verifyLogin |

AE quirk: HTTP status is often `200` while business status lives in JSON `responseCode` — clients and assertions check the business code.

Stable catalogue anchors: `KnownProducts`, `KnownBrands`. Exact API strings: `ApiMessages`.

### 4.4 Parallel execution

`ParallelSuiteAlterer` (TestNG `IAlterSuiteListener`) reads `execution.parallelEnabled` / `threadCount` / `parallelMode` and rewrites the suite before run:

- `PARALLEL=false` (default) → `parallel=none`, 1 thread
- `PARALLEL=true` → e.g. `methods` with `THREAD_COUNT` workers

Each UI method still gets its **own** WebDriver via `UiSessionHolder` (ThreadLocal), so parallel UI is isolation-safe when unique emails come from factories.

### 4.5 Data

- `RegistrationDataFactory.uniqueUser()` — Faker + unique email (parallel-safe)
- `CheckoutDataFactory.practicePayment()` — practice card data
- Prefer factories; never hardcode credentials in tests

---

## 5. Project structure

```text
agentic-qa-automation-framework/
├── README.md
├── pom.xml · Dockerfile · LICENSE · DECISIONS.md
├── testng.xml · testng-parallel.xml
├── .env.example
├── docs/                 # architecture, onboarding, recon, AI usage
├── .ai/                  # skills, agents, workflows, knowledge
├── .cursor/              # rules, commands, hooks
├── .github/              # Copilot instructions
├── scripts/              # validators, detectors, Extent helpers
├── evaluations/          # agent evaluation scenarios + flawed fixtures
└── src/
    ├── main/java/com/agenticqa/
    │   ├── core/         # browser, config, context, logging, reporting
    │   ├── ui/           # pages, components, fixtures, assertions, messages
    │   ├── api/          # clients, models, requests, fixtures, assertions
    │   ├── data/         # factories + models
    │   ├── agents/       # optional LLM pipeline (AgentCli) — not in @Test
    │   └── governance/   # AgentValidator + ArchitectureValidator + rules
    ├── main/resources/
    │   ├── config/framework-config.yaml
    │   └── schemas/      # JSON schemas for API payloads
    └── test/java/com/agenticqa/tests/
        ├── ui/           # authentication, signup, products, checkout
        ├── api/          # products, users
        ├── integration/
        └── unit/         # AgentValidatorTest
```

---

## 6. Prerequisites

- **JDK 21+**
- **Maven 3.9+**
- Network access to `https://automationexercise.com`
- Chrome (or Firefox / Edge) installed for UI runs — WebDriverManager resolves the driver

---

## 7. Installation

```bash
cd agentic-qa-automation-framework

# Optional local overrides (never commit real secrets)
cp .env.example .env

mvn -DskipTests dependency:resolve
mvn -DskipTests compile test-compile
```

---

## 8. Configuration

### YAML defaults (`src/main/resources/config/framework-config.yaml`)

| Key | Default | Meaning |
|---|---|---|
| `browser.headless` | `false` | **With head** (visible browser) |
| `browser.type` | `chrome` | chrome / firefox / edge |
| `execution.parallelEnabled` | `false` | **Single** (sequential) |
| `execution.parallelMode` | `methods` | Used when parallel is on |
| `execution.threadCount` | `2` | Worker threads when parallel |
| `baseUrl` / `apiBaseUrl` | automationexercise.com | AUT endpoints |

### Terminal / env overrides

| `-D` / env | Purpose | Default when unset |
|---|---|---|
| `HEADLESS` | `true` / `false` | YAML → `false` |
| `PARALLEL` | `true` / `false` | YAML → `false` |
| `PARALLEL_MODE` | `methods` / `classes` / `tests` | `methods` |
| `THREAD_COUNT` | parallel workers | `2` |
| `BROWSER` | `chrome` / `firefox` / `edge` | `chrome` |
| `BASE_URL` | UI base URL | YAML |
| `API_BASE_URL` | API base URL | YAML |
| `QA_PROFILE` | optional `framework-config-<profile>.yaml` | `default` |

Priority: **`mvn -D…` → OS env → `.env` → YAML**.

---

## 9. How to execute

### Default (recommended locally)

```bash
# Single-threaded + headed browser
mvn test
```

### Headless / parallel

```bash
# Headless, still single
mvn test -DHEADLESS=true

# Parallel, still headed
mvn test -DPARALLEL=true
mvn test -DPARALLEL=true -DTHREAD_COUNT=4 -DPARALLEL_MODE=methods

# Parallel + headless
mvn test -DPARALLEL=true -DHEADLESS=true
```

### By TestNG group

```bash
mvn test -Dgroups=ui
mvn test -Dgroups=api
mvn test -Dgroups=integration
mvn test -Dgroups=smoke
mvn test -Dgroups=regression
mvn test -Dgroups=unit
```

### Suites

```bash
# Default suite (testng.xml)
mvn test

# Alternate XML (still needs -DPARALLEL=true to actually parallelize)
mvn test -DPARALLEL=true -DsuiteXmlFile=testng-parallel.xml
```

### Quality / reports

```bash
mvn verify                          # tests + JaCoCo report
open target/extent-report/index.html
```

### Governance scripts

```bash
scripts/agent-validator/run.sh src/test/java
scripts/architecture-validator/run.sh
scripts/secret-detector/run.sh
scripts/fixed-wait-detector/run.sh
```

Or via Maven:

```bash
mvn -DskipTests exec:java \
  -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="src/test/java"

mvn -DskipTests exec:java \
  -Dexec.mainClass="com.agenticqa.governance.ArchitectureValidator" \
  -Dexec.args="."
```

---

## 10. Implemented tests

| Type | Class | Groups | What it covers |
|---|---|---|---|
| UI | `SignupUiTest` | ui, smoke, regression | Register → logged in → delete account |
| UI | `LoginUiTest` | ui, smoke/regression | Valid login; invalid credentials |
| UI | `ProductDiscoveryUiTest` | ui, smoke, regression | Search “top”; exact Blue Top name/price/details |
| UI | `CheckoutUiTest` | ui, regression | Signup → cart → pay → order placed → cleanup |
| API | `ProductListApiTest` | api, smoke, regression | productsList + KnownProducts anchors |
| API | `ProductSearchApiTest` | api, smoke/regression | searchProduct; missing param; empty term |
| API | `BrandListApiTest` | api, smoke, regression | brandsList + KnownBrands anchors |
| API | `UserRegistrationAuthApiTest` | api, smoke/regression | create / verify / detail / delete; duplicate email |
| Integration | `SignupApiUiConsistencyIT` | integration, regression, api | API create ↔ detail ↔ delete consistency |
| Unit | `AgentValidatorTest` | unit, smoke/regression | Flawed fixture REJECTED; clean snippet ACCEPTED |

---

## 11. Reporting

| Artifact | Location |
|---|---|
| Extent HTML | `target/extent-report/index.html` |
| Screenshots / text attaches | `target/artifacts/` (on UI failure) |
| Logs | Logback → console / configured appenders |

Listeners (wired in `testng.xml` + Surefire):

- `TestListener` — pass/fail suite summary
- `ExtentTestListener` — Extent nodes; display names via `TestDisplayName` (`API_…` / `UI_…` style)
- `ParallelSuiteAlterer` — applies parallel config before suite start

API traffic is attached through `ExtentRestAssuredFilter` (passwords redacted when `reporting.redactSecrets: true`).

---

## 12. AI platform

AI helps **author** tests; it does **not** decide pass/fail at runtime.

### Agentic delivery chain

```text
Requirement → Knowledge/Inventory → Prioritized cases (P0/P1/P2) → Automate → Validate
```

| Piece | Path |
|---|---|
| Requirements | [`docs/requirements/AUTOMATION_REQUIREMENTS.md`](docs/requirements/AUTOMATION_REQUIREMENTS.md) |
| What is already automated | [`.ai/knowledge/automation-inventory.md`](.ai/knowledge/automation-inventory.md) |
| Chain workflow | [`.ai/workflows/agentic-delivery-chain.md`](.ai/workflows/agentic-delivery-chain.md) |
| Orchestrator agent | [`.ai/agents/agentic-delivery-orchestrator.md`](.ai/agents/agentic-delivery-orchestrator.md) |
| Chain skill | [`.ai/skills/agentic-delivery-chain/SKILL.md`](.ai/skills/agentic-delivery-chain/SKILL.md) |

Agents must **print the current inventory** (tests, data, scripts) before proposing new cases, and only automate **gaps**.

**Agent drafts never go into `src/`.** All generated cases/Java land in [`agent-generated/`](agent-generated/README.md) until a human promotes them into the framework.

| Area | Path |
|---|---|
| Skills | `.ai/skills/*/SKILL.md` |
| Agents | `.ai/agents/*.md` |
| Workflows | `.ai/workflows/*.md` |
| Knowledge | `.ai/knowledge/*.md` |
| Cursor | `.cursor/rules`, `.cursor/commands`, `.cursor/hooks` |
| Claude | `CLAUDE.md` |
| Copilot | `.github/copilot-instructions.md` |
| Optional LLM CLI | `com.agenticqa.agents` (keys in `.env` only) |

See [`AI_USAGE.md`](AI_USAGE.md), [`AGENTS.md`](AGENTS.md), [`docs/ai-platform/`](docs/ai-platform/).

---

## 13. Governance & contribution

Deterministic gates reject unsafe agent output:

| Rule themes | Examples |
|---|---|
| No fixed waits | `Thread.sleep`, Playwright-style `waitForTimeout` |
| No secrets / URLs in tests | Hardcoded emails/passwords, absolute AUT URLs |
| Layering | No raw `WebDriver` bootstrap in tests |
| Meaningful asserts | Tests must call `Assert.*` or `*Assertions.assert*` |

Flawed fixture (must be **REJECTED**):

`evaluations/scenarios/flawed-examples/FlawedCheckoutTest.java`

Suggested flow for a new test:

1. Follow `.ai/workflows/new-test.md` (or create-ui-test / create-api-test skill)
2. Use factories + page objects / API clients + assertion helpers
3. Run `scripts/agent-validator/run.sh` on your files
4. Run the relevant TestNG group

---

## 14. Troubleshooting

| Symptom | Action |
|---|---|
| `-DHEADLESS` / `-DPARALLEL` ignored | Confirm Surefire `systemPropertyVariables` in `pom.xml`; check log line `Loaded config … headless=… parallel=…` |
| Chrome / driver issues | Ensure Chrome installed; WebDriverManager downloads matching driver |
| Consent / ad overlays | `BasePage.dismissOverlaysIfPresent()` — pages call this around navigations |
| API assert fails on HTTP 200 | Assert JSON **`responseCode`** / use `ApiResponseAssertions` |
| Flaky signup under ads | Clients/pages retry once; prefer headed locally to observe |
| Empty Extent | Open `target/extent-report/index.html` after a full `mvn test` |

---

## 15. Known limitations

- Automation Exercise serves ads / consent banners that can intercept clicks
- API “auth” is credential verify (`verifyLogin`), not shared browser session cookies
- Failure classification is heuristic (`FailureClassifier`)
- Optional LLM agents need provider keys in local `.env` (never committed)

---

## 16. License

MIT — see [`LICENSE`](LICENSE).
