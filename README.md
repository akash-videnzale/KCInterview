# Agentic QA Automation Framework

Enterprise-grade, agentic-first Quality Engineering platform for [Automation Exercise](https://automationexercise.com/).

**Stack:** Java 21 · Playwright Java · TestNG · TestNG Assert/SoftAssert · REST Assured · Maven · Allure · JavaParser governance

AI coding agents are empowered with skills, specialized roles, and workflows — and constrained by deterministic validators. Tests never depend on an LLM API at runtime.

---

## 1. Architecture

```text
┌──────────────────────────────────────────────────────────┐
│ Layer 5: Engineering Governance (validators, scripts)    │
├──────────────────────────────────────────────────────────┤
│ Layer 4: AI Ecosystem (skills, agents, workflows, rules) │
├──────────────────────────────────────────────────────────┤
│ Layer 3: Tests (UI / API / integration / unit)           │
├──────────────────────────────────────────────────────────┤
│ Layer 2: Services (pages, components, API clients, data) │
├──────────────────────────────────────────────────────────┤
│ Layer 1: Core (browser lifecycle, config, logging, report)│
└──────────────────────────────────────────────────────────┘
```

UI lifecycle: suite-scoped `BrowserManager` → per-test `SessionFactory` (isolated `BrowserContext`/`Page`) → page objects.

API lifecycle: `ApiBaseTest` → `*ApiClient` → REST Assured (no Playwright).

---

## 2. Technology stack & versions

| Component | Version (pom.xml) |
|---|---|
| Java | 21 |
| Playwright | 1.49.0 |
| TestNG | 7.10.2 |
| REST Assured | 5.5.0 |
| Jackson | 2.18.2 |
| SLF4J / Logback | 2.0.16 / 1.5.12 |
| Allure TestNG | 2.29.1 |
| Java Faker | 1.0.2 |
| JavaParser | 3.26.3 |
| JaCoCo | 0.8.12 |
| Checkstyle plugin | 3.6.0 |
| SpotBugs plugin | 4.8.6.6 |

---

## 3. Prerequisites

- JDK 21+
- Maven 3.9+
- Network access to `https://automationexercise.com`
- Playwright Chromium (installed via command below)

---

## 4. Installation

```bash
cd agentic-qa-automation-framework
cp .env.example .env
mvn -DskipTests dependency:resolve
mvn -DskipTests exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
mvn -DskipTests compile test-compile
```

---

## 5. Configuration

`src/main/resources/config/framework-config.yaml` plus environment overrides:

| Variable | Purpose |
|---|---|
| `BASE_URL` | UI base URL |
| `API_BASE_URL` | API base URL |
| `BROWSER` | chromium / firefox / webkit |
| `HEADLESS` | true/false |
| `QA_PROFILE` | optional YAML profile suffix |

---

## 6. Test execution

```bash
# Clean and compile
mvn clean compile

# Compile test classes
mvn test-compile

# Run all tests
mvn clean test

# Groups
mvn test -Dgroups=ui
mvn test -Dgroups=api
mvn test -Dgroups=integration
mvn test -Dgroups=smoke
mvn test -Dgroups=regression
mvn test -Dgroups=unit

# Parallel (API-focused suite)
mvn test -DsuiteXmlFile=testng-parallel.xml

# Coverage
mvn verify

# Allure
mvn allure:report

# Validators
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.validation.ArchitectureValidator" -Dexec.args="."
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.validation.AgentValidator" -Dexec.args="src/test/java"
```

---

## 7. Implemented tests

| # | Type | Class | Groups |
|---|---|---|---|
| 1 | UI Signup | `SignupUiTest` | ui, smoke, regression |
| 2 | UI Discovery | `ProductDiscoveryUiTest` | ui, smoke, regression |
| 3 | UI Checkout | `CheckoutUiTest` | ui, regression |
| 4 | API Products | `ProductListApiTest` | api, smoke, regression |
| 5 | API Search | `ProductSearchApiTest` | api, smoke/regression |
| 6 | API Users/Auth | `UserRegistrationAuthApiTest` | api, smoke, regression |
| + | Unit | `AgentValidatorTest` | unit, smoke, regression |

---

## 8. AI platform

- **Skills:** `.ai/skills/*/SKILL.md` (14)
- **Agents:** `.ai/agents/*.md` (6)
- **Workflows:** `.ai/workflows/*.md`
- **Knowledge:** `.ai/knowledge/*.md`
- **Cursor:** `.cursor/rules`, `.cursor/commands`, `.cursor/hooks/README.md`
- **Claude Code:** `CLAUDE.md`
- **Copilot:** `.github/copilot-instructions.md`

---

## 9. Contribution validation

```bash
scripts/agent-validator/run.sh src/test/java
scripts/architecture-validator/run.sh
scripts/secret-detector/run.sh
scripts/fixed-wait-detector/run.sh
```

Flawed fixture: `evaluations/scenarios/flawed-examples/FlawedCheckoutTest.java` (must be REJECTED).

---

## 10. MCP readiness

Optional MCP tools (Playwright exploration, GitHub inspection, docs retrieval) may assist agents. They are **not required** to run tests. See `docs/ai-platform/mcp-readiness.md`.

---

## 11. Troubleshooting

| Symptom | Action |
|---|---|
| Browser not found | Re-run Playwright `install chromium` |
| Consent/ad overlay | `BasePage.dismissOverlaysIfPresent()` |
| Empty Allure | Ensure `target/allure-results` after `mvn test` |
| API business code ≠ HTTP | Assert JSON `responseCode` (AE pattern) |

---

## 12. Known limitations

- Automation Exercise may show ads/consent banners.
- API auth is credential verify, not browser session cookies.
- Failure classification is heuristic.
- Live multi-agent evaluation results are recorded only after real runs (see `evaluations/README.md`).

---

## 13. License

MIT — see `LICENSE`.
