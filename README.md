# Agentic QA Automation Framework

**Agentic-first** UI + API test framework for [Automation Exercise](https://automationexercise.com/).

Stack: **Java 21 · Selenium 4 · TestNG · REST Assured · Extent Reports**  
Author: **Akash Aggarwal**  
Repo: [akash-videnzale/KCInterview](https://github.com/akash-videnzale/KCInterview)

Companion runbooks: [`HOW_TO_RUN.md`](HOW_TO_RUN.md) · [`docs/HOW_TO_RUN.html`](docs/HOW_TO_RUN.html)  
Architecture decisions: [`DECISIONS.md`](DECISIONS.md) · AI usage log: [`AI_USAGE.md`](AI_USAGE.md)

---

## Quick start (clone → run → report opens)

```bash
git clone https://github.com/akash-videnzale/KCInterview.git
cd KCInterview

# Optional: LLM keys only if you will run AgentCli
cp .env.example .env

mvn -DskipTests dependency:resolve
mvn -DskipTests compile test-compile

# Run the full suite (headed Chrome by default)
mvn test
```

When the suite finishes, **Extent HTML opens automatically** in your browser:

`target/extent-report/index.html`

Disable auto-open: `mvn test -DOPEN_REPORT=false`

### Useful run commands

```bash
# Smoke / groups
mvn test -Dgroups=smoke
mvn test -Dgroups=ui
mvn test -Dgroups=api
mvn test -Dgroups=integration
mvn test -Dgroups=unit

# Headless / parallel
mvn test -DHEADLESS=true
mvn test -DPARALLEL=true -DTHREAD_COUNT=4 -DHEADLESS=true

# Single class / method
mvn test -Dtest=SignupUiTest
mvn test -Dtest=CheckoutUiTest -DHEADLESS=true
mvn test -Dtest=ProductListApiTest#shouldReturnProductCatalogue
```

**Need:** JDK 21+, Maven 3.9+, Chrome (or Firefox/Edge), network to `https://automationexercise.com`.

---

## Architecture (how the framework is layered)

Dependencies point **downward only**. Tests stay thin; agents never decide pass/fail inside `@Test`.

```text
┌──────────────────────────────────────────────────────────────┐
│ Governance (AgentValidator AST rules — ACCEPT/REJECT)        │
├──────────────────────────────────────────────────────────────┤
│ AI ecosystem (.ai/skills · agents · workflows · AgentCli)    │
│   drafts → agent-generated/ only until human promote         │
├──────────────────────────────────────────────────────────────┤
│ Tests (TestNG)  UI · API · integration · unit                │
├──────────────────────────────────────────────────────────────┤
│ Services  Pages/components · API clients · POJOs · factories │
│           UiAssertions · ApiResponseAssertions · UiMessages  │
├──────────────────────────────────────────────────────────────┤
│ Core  BrowserManager · ConfigLoader · Extent · Session       │
└──────────────────────────────────────────────────────────────┘
```

| Layer | Owns | Must not |
|---|---|---|
| **Tests** | Flow orchestration + groups | Locators, raw HTTP, secrets, URLs |
| **UI** | Selenium pages / components | REST Assured, `@Test` |
| **API** | Clients + Jackson POJOs | WebDriver |
| **Data** | Factories + value objects | Driver / HTTP |
| **Core** | Config, browser, Extent | Business asserts |
| **Governance** | Deterministic JavaParser rules | LLM calls |
| **Agents** | Optional offline LLM pipeline | Called from `@Test` assertions |

**Runtime note:** `mvn test` uses UI/API/data/core only. `.ai/*` guides IDE agents. `AgentCli` is optional paid LLM orchestration and is **not** required to run tests.

More detail: [`docs/architecture/overview.md`](docs/architecture/overview.md) · [`.ai/knowledge/architecture.md`](.ai/knowledge/architecture.md)

---

## Reporting

| Report | Path | When |
|---|---|---|
| **Extent HTML** (primary) | `target/extent-report/index.html` | After `mvn test` — **auto-opens** |
| AgentCli pass/fail table | `agent-generated/runs/run-*/11-execution-report.html` | After `AgentCli full` / `execute` — **auto-opens** |
| Screenshots / failure text | `target/artifacts/` | UI failures |
| JaCoCo | `target/site/jacoco/index.html` | After `mvn verify` |

Manual open: `scripts/extent-report/open.sh` or `open target/extent-report/index.html`

---

## How to run AgentCli (runtime LLM agents)

Optional multi-provider pipeline (keys in local `.env` only — never commit secrets).

```bash
cp .env.example .env   # set OPENAI_API_KEY / ANTHROPIC_API_KEY / DEEPSEEK_API_KEY as needed

# Analyze requirement → cases → review
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="analyze --requirement docs/examples/sample-requirement.md"

# Full chain: analyze + draft + execute smoke + HTML report (auto-opens)
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="full --requirement docs/examples/sample-requirement.md --groups smoke"

# Execute existing suite + report only
mvn -q -DskipTests exec:java -Dexec.mainClass=com.agenticqa.agents.AgentCli \
  -Dexec.args="execute --groups smoke"
```

**What to expect**

- Output folder: `agent-generated/runs/run-<timestamp>/`
- `07-execution-log.txt` — Maven/TestNG console
- `11-execution-report.html` — simple pass/fail table (**opens in browser**)
- Drafts (if automators ran): `agent-generated/src/...` — **untrusted** until `AgentValidator` + human promote into `src/`
- Executor runs **existing** framework tests (`mvn test -Dgroups=…`), not draft Java directly

Cheapest verified defaults (see `DECISIONS.md` ADR-006): OpenAI `gpt-5.4-nano` · Anthropic `claude-haiku-4-5` · DeepSeek `deepseek-flash`.

---

## Coding agents & skills (Cursor / Claude / Copilot)

These guide the **IDE agent**. They do **not** call GPT/Claude/DeepSeek APIs by themselves.

```text
Ask: “Follow .ai/skills/agentic-delivery-chain/SKILL.md for Contact Us”
  or: “Act as .ai/agents/agentic-delivery-orchestrator.md”
```

Expect: print **automation inventory** → COVERED vs GAP (P0/P1/P2) → write only under `agent-generated/` → run validators → promote to `src/` only when you ask.

---

## Agents (one-liner each)

### Coding / role agents (`.ai/agents/`)

| Agent | One-liner |
|---|---|
| `agentic-delivery-orchestrator` | Runs the full chain: inventory → gaps → automate → validate (sandbox only). |
| `qa-architect` | Plans how a requirement fits the framework before anyone writes tests. |
| `ui-automation-agent` | Authors Selenium page objects and TestNG UI tests for AE. |
| `api-automation-agent` | Authors REST Assured clients, POJOs, and API tests for verified contracts. |
| `test-data-agent` | Owns factories, unique users, practice payment, catalogue anchors. |
| `qa-reviewer-agent` | Review-only quality gate for agent-authored code. |
| `failure-analysis-agent` | Diagnoses failed/flaky TestNG runs and proposes a minimal fix. |

### Runtime LLM agents (`AgentCli` — Java under `com.agenticqa.agents`)

| Agent | One-liner |
|---|---|
| `RequirementAnalysisAgent` | Turns a requirement/diff into journeys + risks (OpenAI nano). |
| `TestCaseGeneratorAgent` | Emits UI/API cases tagged P0/P1/P2 (OpenAI nano). |
| `TestCaseReviewerAgent` | Sufficiency gate on generated cases (Claude Haiku). |
| `ApiTestAutomatorAgent` / `UiTestAutomatorAgent` | Draft Java into `agent-generated/` (Claude Haiku). |
| `AutomationExecutorAgent` | Runs `mvn test` (no LLM). |
| `FailedTestReexecutorAgent` | Re-runs failures once (no LLM). |
| `ExecutionHtmlReportAgent` | Writes + opens pass/fail HTML (no LLM). |
| `ReportAnalyzerAgent` | Summarizes execution (DeepSeek flash). |
| `EmailReportSenderAgent` | Optional SES email when enabled (no LLM). |

---

## Skills (one-liner each)

| Skill | One-liner |
|---|---|
| `agentic-delivery-chain` | Full chain — requirement → inventory → prioritized cases → automate → validate. |
| `framework-onboarding` | Onboard before writing any test, page, or client. |
| `requirement-analysis` | Map a requirement to inventory + prioritized gap cases (no duplicates). |
| `analyze-test-coverage` | Compare requirements to inventory; list covered scripts and gaps. |
| `create-ui-test` | Create a Selenium TestNG UI test (Signup/Discovery/Checkout style). |
| `create-api-test` | Create REST Assured API tests via clients/POJOs/assertions. |
| `create-e2e-test` | Multi-step UI journey with cleanup. |
| `create-page-object` | Selenium page object with stable locators and no-arg constructor. |
| `create-api-client` | Add a REST Assured client method for a verified AE endpoint. |
| `create-test-data` | Draft factories under `agent-generated/` (not `src/`). |
| `create-testng-fixture` | Extend UiBaseTest/ApiBaseTest without putting logic in hooks. |
| `review-test-quality` | Review-only gate for agent-authored tests. |
| `validate-agent-contribution` | Run deterministic validators before accepting AI Java. |
| `analyze-test-failure` | Diagnose a failed run using logs, Extent, screenshots. |
| `analyze-flaky-test` | Find non-determinism (ads, shared state, sleeps, email collisions). |

---

## Knowledge (one-liner each)

| Knowledge file | One-liner |
|---|---|
| `automation-inventory` | **Read first** — what UI/API tests and data already exist. |
| `architecture` | Layer rules and how UI/API/governance/agents fit together. |
| `agent-generated-sandbox` | All new agent artifacts go under `agent-generated/` until promoted. |
| `api-contracts` | Verified Automation Exercise API endpoints and response shapes. |
| `coding-standards` | Java 21 conventions agents must follow. |
| `selector-strategy` | Locator priority (prefer stable `data-qa` / resilient CSS/XPath). |
| `selenium-patterns` | Per-test Chrome session, waits, overlay dismiss patterns. |
| `testng-patterns` | Groups, fixtures, Assert/SoftAssert usage patterns. |
| `test-data-strategy` | Unique emails, factories, practice payment, cleanup. |
| `troubleshooting` | Common AE/Selenium/API failures and where to look. |

---

## Governance (validators)

```bash
scripts/agent-validator/run.sh src/test/java
scripts/agent-validator/run.sh agent-generated/src/test/java
scripts/architecture-validator/run.sh
scripts/secret-detector/run.sh
scripts/fixed-wait-detector/run.sh
```

Expect **ACCEPTED** for clean code. Flawed fixture under `evaluations/` must stay **REJECTED**.

---

## Implemented critical tests

| Area | Class | Covers |
|---|---|---|
| UI | `SignupUiTest` | Register unique user + cleanup |
| UI | `ProductDiscoveryUiTest` | Search + product details |
| UI | `CheckoutUiTest` | Signup → cart → pay → order → delete |
| UI | `LoginUiTest` | Valid login + invalid credentials |
| API | `ProductListApiTest` / `ProductSearchApiTest` / `BrandListApiTest` | Catalogue APIs |
| API | `UserRegistrationAuthApiTest` | Create / verify / detail / delete |
| IT | `SignupApiUiConsistencyIT` | API lifecycle consistency |
| Unit | `AgentValidatorTest` | Governance ACCEPT/REJECT |

---

## Configuration defaults

| Setting | Default |
|---|---|
| Browser | Chrome **headed** (`headless: false`) |
| Parallel | **Off** (`parallelEnabled: false`) |
| Report auto-open | **On** (`OPEN_REPORT` unset/true) |

Priority: `mvn -D…` → env → `.env` → `framework-config.yaml`.

---

## Project layout (short)

```text
KCInterview/
├── README.md · HOW_TO_RUN.md · DECISIONS.md · AI_USAGE.md · pom.xml
├── .ai/                 # skills, agents, knowledge, workflows
├── docs/                # HOW_TO_RUN.html, architecture, requirements
├── scripts/             # validators, Extent open helper
├── agent-generated/     # AI drafts + run outputs (sandbox)
├── evaluations/         # flawed fixtures for validator demos
└── src/
    ├── main/java/com/agenticqa/   # core · ui · api · data · agents · governance
    └── test/java/com/agenticqa/tests/
```

---

## Troubleshooting

| Symptom | Fix |
|---|---|
| Chrome/CDP warnings | Usually non-blocking; upgrade Selenium if clicks fail |
| AE 503 / ads intercept clicks | Re-run; overlays are dismissed in `BasePage` |
| OpenAI `max_tokens` error | Framework uses `max_completion_tokens` for OpenAI |
| No browser for report | Use macOS/Linux desktop session, or open path manually |
| Want CI without browser popups | `mvn test -DOPEN_REPORT=false -DHEADLESS=true` |

---

## License

See [`LICENSE`](LICENSE).
