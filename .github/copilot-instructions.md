# GitHub Copilot Instructions

This repository is a Java 21 TestNG **Selenium** REST Assured automation platform (Extent reporting).

## Generate code that

- Extends `UiBaseTest` for UI tests and `ApiBaseTest` for API tests
- Uses page objects under `com.agenticqa.ui.pages` and API clients under `com.agenticqa.api.clients`
- Uses TestNG `@Test` with groups `ui|api|integration|smoke|regression|unit`
- Uses `UiAssertions` / `ApiResponseAssertions` / TestNG `Assert` / `SoftAssert` (`assertAll()` required for soft)
- Pulls configuration from `ConfigLoader` (never hardcode base URLs in tests)
- Creates unique users via `RegistrationDataFactory`
- Writes **new** agent drafts under `agent-generated/` (not into `src/` unless promoting)

## Never generate

- Playwright / JUnit / AssertJ / Hamcrest
- `Thread.sleep` or `TimeUnit.sleep` in tests
- Hardcoded passwords or non-example emails
- Direct `new ChromeDriver()` in tests
- Direct `RestAssured.given()` in tests
- LLM API calls inside tests

## Validation

After proposing Java changes, recommend:

```bash
mvn -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="agent-generated/src/test/java"
mvn test -Dgroups=...
```

## Optional LLM pipeline

Requirement analysis / case generation uses `com.agenticqa.agents.AgentCli` with keys in local `.env`.
Outputs land in `agent-generated/runs/`.
Do not hardcode API keys. Do not call LLMs from `@Test` methods.
See `docs/ai-platform/llm-runtime-agents.md`.
