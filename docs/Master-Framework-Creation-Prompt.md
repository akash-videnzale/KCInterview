# Master Framework Creation Prompt

Build an enterprise-grade, agentic-first QA automation framework from scratch against Automation Exercise (https://automationexercise.com/).

This is for a take-home interview. Agentic-first means: designed so Cursor/Claude/Copilot can safely contribute tests, pages, clients, and factories — not documentation theater. AI helps author; deterministic validators gate quality; TestNG decides pass/fail. Never call LLMs inside @Test assertions.

================================================================================
STACK (LOCKED — do not use Playwright/TypeScript)
================================================================================
- Java 21
- Maven
- Selenium 4 + WebDriverManager
- TestNG (sole runner; Assert + SoftAssert only — NO JUnit, AssertJ, Hamcrest)
- REST Assured + Jackson
- ExtentReports Spark HTML (primary report)
- SLF4J + Logback
- Java Faker for test data
- JavaParser for governance validators
- Optional AWS SES SDK only for an email agent that defaults OFF
- Dockerfile optional; CI deferred (document in DECISIONS.md)

Assignment owner (Vivek) accepted Selenium Java instead of the brief’s Playwright+TS default. Record that as ADR-001.

================================================================================
TWO INDEPENDENT CAPABILITIES
================================================================================
1) Automation Framework — fully runnable with `mvn test` only. No LLM keys required.
2) CLI Agent Platform (`AgentCli`) — optional multi-provider LLM orchestration, independently runnable, may invoke `mvn test` for execution but never replaces TestNG as oracle.

Make this separation obvious in README with impressive, interview-ready language.

================================================================================
ARCHITECTURE (layers; dependencies point downward only)
================================================================================
com.agenticqa
├── core/          browser, config, context (ThreadLocal UiSessionHolder), logging, reporting
├── ui/            pages, components, fixtures (UiBaseTest), assertions, messages (UiMessages)
├── api/           clients, models (POJOs), requests, responses (body normalizer, schema), fixtures (ApiBaseTest), assertions
├── data/          factories + models (RegistrationData, PaymentData, AddressData)
├── governance/    AgentValidator + ArchitectureValidator + JavaParser rules
└── agents/        AgentCli pipeline + llm clients + output writer + execution HTML report

Rules:
- Tests: orchestration only — no locators, no raw HTTP, no secrets/URLs, no new ChromeDriver()
- UI pages: Selenium only; no-arg constructors resolve driver from UiSessionHolder
- API clients: REST Assured only; treat AE quirk — HTTP often 200 while business status is JSON responseCode
- Data: unique email every run (parallel-safe); never hardcode credentials in tests
- Governance: deterministic AST rules; ACCEPT/REJECT
- Agents: optional; drafts only under agent-generated/

================================================================================
CORE BEHAVIORS TO IMPLEMENT
================================================================================
Config:
- YAML: src/main/resources/config/framework-config.yaml
- Priority: mvn -D → env → .env → YAML
- Defaults: HEADLESS=false (headed), PARALLEL=false (single-threaded)
- Forward Surefire systemPropertyVariables: HEADLESS, PARALLEL, PARALLEL_MODE, THREAD_COUNT, BROWSER, BASE_URL, API_BASE_URL, OPEN_REPORT

Browser:
- BrowserManager + SessionFactory
- Per-test WebDriver via UiSessionHolder (ThreadLocal)
- ParallelSuiteAlterer (IAlterSuiteListener) applies parallel settings

UI BasePage:
- navigate, waits, resilient click/type, dismiss AE consent/ad overlays, byDataQa helpers
- HeaderComponent used for logged-in state; keep components lean (only what is used)

Reporting:
- ExtentReportManager writes target/extent-report/index.html (+ stamped copy)
- ExtentTestListener + TestListener
- ExtentRestAssuredFilter attaches API traffic; SecretRedactor redacts passwords
- ReportOpener: auto-open Extent HTML after suite finish; disable with OPEN_REPORT=false
- FailureClassifier heuristic on UI failure

API:
- BaseApiClient with body normalizer (AE sometimes wraps JSON in HTML), overload retry
- ProductApiClient, BrandApiClient, UserApiClient, AuthenticationApiClient
- POJOs for products/brands/user detail/message responses
- KnownProducts / KnownBrands anchors; ApiMessages exact strings
- ApiResponseAssertions helpers
- SchemaValidator optional JSON schema checks under src/main/resources/schemas

Data:
- RegistrationDataFactory.uniqueUser(), AddressFactory, CheckoutDataFactory.practicePayment(), ProductSearchFactory, UserFactory helpers

================================================================================
GOVERNANCE (agent gates — not LLM)
================================================================================
Implement AgentValidator (JavaParser) with rules including:
- NoThreadSleep / NoFixedWait
- NoHardcodedCredentials / NoHardcodedEnvUrl
- NoDirectBrowserInTests / NoLowLevelSeleniumInTests
- NoDirectHttpInTests / NoDirectContextInTests
- MeaningfulAssertion / EmptyTestMethod / MissingTestAnnotation
- PackagePlacement / NoStaticMutableState / DuplicateTestHeuristic

Scripts:
- scripts/agent-validator/run.sh
- scripts/architecture-validator/run.sh
- scripts/secret-detector/run.sh
- scripts/fixed-wait-detector/run.sh
- scripts/extent-report/open.sh

Unit test: AgentValidatorTest — flawed fixture under evaluations/ must REJECT; clean snippet ACCEPT.

================================================================================
AGENTIC ECOSYSTEM (.ai/) — coding agents (IDE), not paid APIs by themselves
================================================================================
Create:

Skills (.ai/skills/*/SKILL.md):
- framework-onboarding
- requirement-analysis
- agentic-delivery-chain
- analyze-test-coverage
- create-ui-test, create-api-test, create-e2e-test
- create-page-object, create-api-client, create-test-data, create-testng-fixture
- review-test-quality, validate-agent-contribution
- analyze-test-failure, analyze-flaky-test

Agents (.ai/agents/*.md):
- agentic-delivery-orchestrator
- qa-architect, qa-reviewer-agent
- ui-automation-agent, api-automation-agent, test-data-agent
- failure-analysis-agent
- runtime docs: requirement-analysis / test-case-generator / test-case-reviewer runtime agents

Knowledge (.ai/knowledge/):
- architecture, automation-inventory (agents MUST read first), agent-generated-sandbox
- api-contracts, coding-standards, selector-strategy, selenium-patterns
- testng-patterns, test-data-strategy, troubleshooting

Workflows (.ai/workflows/): agentic-delivery-chain, new-test, new-page-object, etc.

Hard rule for all skills/agents:
- Print current automation inventory before proposing cases
- Only automate GAPS (P0/P1/P2)
- Write ALL new agent drafts under agent-generated/ (never directly into src/ until human promote)
- Run AgentValidator before claiming done

Also add .cursor/rules for architecture, Selenium, TestNG, API, governance.

================================================================================
CLI AGENT PLATFORM (AgentCli) — optional paid LLM runtime
================================================================================
Entry: com.agenticqa.agents.AgentCli
Modes: analyze | full | execute

Pipeline agents:
- RequirementAnalysisAgent (OpenAI gpt-5.4-nano default)
- TestCaseGeneratorAgent (OpenAI gpt-5.4-nano)
- TestCaseReviewerAgent (Anthropic claude-haiku-4-5)
- ApiTestAutomatorAgent + UiTestAutomatorAgent (Claude Haiku) → drafts in agent-generated/
- AutomationExecutorAgent → mvn test -Dgroups=… (clear stale surefire first)
- FailedTestReexecutorAgent
- ExecutionHtmlReportAgent → agent-generated/runs/run-*/11-execution-report.html + auto-open
- ReportAnalyzerAgent (DeepSeek deepseek-flash)
- EmailReportSenderAgent (SES; default disabled)

LLM clients:
- OpenAiCompatibleClient (OpenAI + DeepSeek) — use max_completion_tokens for OpenAI; max_tokens for DeepSeek
- AnthropicClient
- LlmConfig from .env; role mapping via REQUIREMENT_LLM / TESTGEN_LLM / REVIEWER_LLM / AUTOMATOR_LLM / REPORT_LLM

Outputs under agent-generated/runs/run-<timestamp>/ (markdown + logs + HTML).
Executor runs EXISTING framework tests, not draft Java.

================================================================================
DOCS / ROOT ARTIFACTS (required)
================================================================================
- README.md — clone → mvn test runs EVERYTHING; auto-open Extent; architecture; framework vs CLI independence; AgentCli commands; one-liners for agents/skills/knowledge
- HOW_TO_RUN.md + docs/HOW_TO_RUN.html
- DECISIONS.md — ADR-001…006 (stack, agentic validators, Assert-only, factories, no LLM in tests + CI deferred, cheapest multi-provider models)
- AI_USAGE.md — real AI usage log template + at least placeholder structure for “AI wrong → human caught”
- AGENTS.md / CLAUDE.md onboarding for coding agents
- .env.example (keys blank); document that mvn test needs no keys
- testng.xml + testng-parallel.xml
- LICENSE, Dockerfile optional
- docs/requirements, docs/examples/sample-requirement.md, docs/recon notes for AE

================================================================================
TESTS (minimum for a real scaffold)
================================================================================
Prefer scaffolding the full test package layout. Implement at least thin working examples if time allows:
- UI packages: authentication, signup, products, checkout
- API packages: products, users
- integration + unit/validation
Groups: ui, api, integration, unit, smoke, regression

If you must prioritize: ship the FULL framework + agentic platform + docs + validators first; add 1 smoke UI + 1 smoke API stub that compile and demonstrate patterns. Do NOT invent AE endpoints/selectors — recon live site/API first and record findings.

================================================================================
NON-NEGOTIABLES
================================================================================
1. `mvn test` runs the full suite with no AgentCli and no LLM keys
2. LLM never inside assertions
3. Agent drafts only in agent-generated/
4. Validators can REJECT bad agent Java
5. Extent report auto-opens after mvn test (OPEN_REPORT=false disables)
6. Headed + single-threaded defaults
7. Selenium Java — not Playwright
8. Depth over breadth; do not modify the AUT

================================================================================
DELIVER
================================================================================
Create the Maven project structure, implement the layers above, wire TestNG + Surefire, add .ai ecosystem, AgentCli, docs, and make `mvn -DskipTests compile test-compile` succeed. Then run `mvn test` (or a smoke subset) and fix until green against live Automation Exercise.

Start by creating the package skeleton and DECISIONS.md, then core → ui/api/data → governance → agents → .ai → README/HOW_TO_RUN.

