# Architectural Decisions

## ADR-001: Selenium Java + TestNG instead of Playwright TypeScript

1. **Context:** The written brief lists Playwright + TypeScript as the default stack. For this submission we needed a Java/Selenium WebDriver framework.
2. **Chosen approach:** Java 21 + Selenium 4 WebDriver + TestNG + REST Assured + Maven.
3. **Rejected alternatives:** Playwright TypeScript; Playwright Java; mixed TS+Java repos.
4. **Reasoning:** Confirmed with **Vivek** (assignment owner) that **Selenium Java is acceptable** for Part 1. Java + Selenium is the stack we can operate and extend confidently in the live Part 2 review. TestNG groups/suites fit UI+API depth-over-breadth delivery.
5. **Trade-offs:** Less built-in auto-wait/tracing than Playwright; agent examples online skew toward TS/Playwright, so we invested in `.ai/skills` and validators tailored to Selenium/TestNG.
6. **Would change if:** Vivek/reviewers required Playwright TypeScript as a hard gate, or Part 2 mandated a TS-only extension surface.

## ADR-002: Agentic-first = skills + deterministic validators (not docs alone)

1. **Context:** “Agentic-first” was intentionally open; AI coding agents will author tests/pages/factories.
2. **Chosen approach:** Layered agent ecosystem (`.ai/skills`, `.ai/agents`, `.cursor/rules/commands`) plus JavaParser `AgentValidator` / detectors that **REJECT** sleeps, hardcoded secrets/URLs, and direct `WebDriver` bootstrap in tests.
3. **Rejected alternatives:** README-only guidance; prompt-only “please follow conventions”; trusting LLM output without a gate.
4. **Reasoning:** Agents are empowered but not trusted. Design shows up when bad contributions fail a machine check before merge.
5. **Trade-offs:** Rule maintenance and occasional false positives.
6. **Would change if:** A typed DSL made the forbidden patterns unrepresentable, reducing the need for AST scanning.

## ADR-003: TestNG Assert / SoftAssert only (no AssertJ/Hamcrest/JUnit)

1. **Context:** Assertion and runner consistency for agent-generated tests.
2. **Chosen approach:** TestNG as sole runner; `Assert` + `SoftAssert` only (`assertAll()` required for soft checks).
3. **Rejected alternatives:** JUnit 5; AssertJ; Hamcrest.
4. **Reasoning:** One assertion style reduces agent drift; SoftAssert fits multi-field UI/API payload checks.
5. **Trade-offs:** Less fluent assertion DSL.
6. **Would change if:** SoftAssert proved insufficient for complex collection assertions and the constraint was lifted.

## ADR-004: Shared auth/data via factories + API/UI lifecycle fixtures

1. **Context:** Signup/login/checkout need unique users without shared mutable credentials.
2. **Chosen approach:** `RegistrationDataFactory` / address/payment factories; API create/verify/delete clients; UI tests extend `UiBaseTest` (per-test WebDriver); API tests extend `ApiBaseTest` (no browser).
3. **Rejected alternatives:** Hardcoded demo accounts; shared static `WebDriver`; inventing AUT endpoints/selectors.
4. **Reasoning:** Unique emails + cleanup keep runs parallel-safe and locally repeatable against Automation Exercise.
5. **Trade-offs:** Depends on AE availability (occasional 503 / ads); explicit waits needed under Selenium.
6. **Would change if:** The AUT provided stable seeded tenants or OAuth test users.

## ADR-005: No LLM calls inside TestNG assertions; CI deferred

1. **Context:** Runtime AI in assertions is flaky/expensive; CI was listed out of scope.
2. **Chosen approach:** Optional `AgentCli` LLM pipeline (analyze/generate/review/draft/execute) separate from `@Test` methods. Document CI as a follow-up (GitHub Actions: JDK 21, Chrome, `mvn test -Dgroups=smoke`) rather than shipping a pipeline in Part 1.
3. **Rejected alternatives:** LLM-as-oracle assertions; mandatory CI green badge for submission.
4. **Reasoning:** Depth of local agentic design + working tests matters more than CI theater for Part 1; Dockerfile exists for optional containerized runs.
5. **Trade-offs:** No always-on cloud gate until CI is added.
6. **Would change if:** Reviewers required CI evidence, or LLM-assisted visual checks became a funded non-blocking lane.
