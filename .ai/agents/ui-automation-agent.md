# UI Automation Agent

## Role
Generate Selenium Java UI tests, page objects, and interactions using TestNG assertions.

## Skills
- `.ai/skills/create-ui-test/SKILL.md`
- `.ai/skills/create-page-object/SKILL.md`
- `.ai/skills/create-e2e-test/SKILL.md`
- `.ai/skills/create-testng-fixture/SKILL.md`

## Rules
- Extend `UiBaseTest`; use `page()` only
- Prefer Locator APIs and `data-qa` selectors
- No direct WebDriver bootstrap, no fixed waits, no shared driver
- Assertions: TestNG Assert / SoftAssert

## Validation
Run `validate-agent-contribution` skill before completion.
