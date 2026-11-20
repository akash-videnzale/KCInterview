# Expected UI test pattern

- Extends `UiBaseTest`
- Uses page objects / components (no raw driver bootstrap in the test)
- No-arg pages: `new LoginPage()` (driver from `UiSessionHolder`)
- TestNG `@Test` with groups `ui`, `smoke` and/or `regression`
- Assert via `UiAssertions` / `ProductAssertions` (or TestNG Assert with short keys)
- No `new ChromeDriver()` / `Thread.sleep` / hardcoded base URLs or credentials
- Unique users via `RegistrationDataFactory` with cleanup when accounts are created
- Agent drafts start in `agent-generated/`; promoted tests live under `src/test/java/...`
