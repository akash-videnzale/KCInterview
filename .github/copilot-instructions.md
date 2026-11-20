# GitHub Copilot Instructions

This repository is a Java 21 TestNG Selenium REST Assured automation platform.

## Generate code that

- Extends `UiBaseTest` for UI tests and `ApiBaseTest` for API tests
- Uses page objects under `com.agenticqa.pages` and API clients under `com.agenticqa.api.clients`
- Uses TestNG `@Test` with groups `ui|api|integration|smoke|regression|unit`
- Uses `Assert` / `SoftAssert` with meaningful messages (`assertAll()` required)
- Pulls configuration from `ConfigLoader` (never hardcode base URLs in tests)
- Creates unique users via `RegistrationDataFactory`

## Never generate

- JUnit / AssertJ / Hamcrest
- `Thread.sleep` or `TimeUnit.sleep` in tests
- Hardcoded passwords or non-example emails
- Direct `new ChromeDriver()` / browser options bootstrap in tests
- Direct `RestAssured.given()` in tests
- LLM API calls inside tests

## Validation

After proposing Java changes, recommend running `AgentValidator` and relevant `mvn test -Dgroups=...` commands.

## Optional LLM pipeline

Requirement analysis / case generation uses `com.agenticqa.agents.AgentCli` with keys in local `.env`.
Do not hardcode API keys. Do not call LLMs from `@Test` methods.
Cheapest verified defaults: OpenAI `gpt-5.4-nano`, Anthropic `claude-haiku-4-5`, DeepSeek `deepseek-flash`.
See `docs/ai-platform/llm-runtime-agents.md`.
