---
name: validate-agent-contribution
description: Run deterministic validation before accepting AI-generated code.
---

# validate-agent-contribution

## Purpose
Run deterministic validation before accepting AI-generated code.

## When to use
Before task completion / PR / merge of agent output.

## Required inputs
Paths of changed Java files.

## Preconditions
Code compiles or is parseable by JavaParser.

## Workflow
1. mvn -DskipTests compile test-compile
2. mvn exec:java -Dexec.mainClass=com.agenticqa.governance.AgentValidator
3. scripts/secret-detector/run.sh
4. scripts/fixed-wait-detector/run.sh
5. Reject on ERROR severity


## Architecture constraints
- Layers: core → services (pages/api/factories) → tests → AI ecosystem → governance
- UI tests extend UiBaseTest; API tests extend ApiBaseTest
- Assertions: TestNG Assert and SoftAssert only
- Config via YAML + env vars (ConfigLoader)



## Prohibited practices
- Do not use JUnit, AssertJ, or Hamcrest
- Do not call Thread.sleep or TimeUnit.sleep; use WebDriverWait
- Do not hardcode credentials or environment URLs in tests
- Do not create WebDriver/ChromeDriver inside tests
- Do not call RestAssured.given() directly from tests
- Do not invent Automation Exercise endpoints or selectors
- Do not make tests depend on LLM APIs at runtime


## Output format
- Summary of changes
- Files created/modified
- TestNG groups used
- Validation commands run and results

## Examples
```bash
mvn -q -DskipTests exec:java -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="src/test/java/com/agenticqa/tests"
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
