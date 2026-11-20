# Expected UI test pattern

- Extends `UiBaseTest`
- Uses page objects / components (no raw driver bootstrap in the test)
- TestNG `@Test` with groups `ui`, `smoke` and/or `regression`
- `Assert` / `SoftAssert` with meaningful messages
- No `new ChromeDriver()` / `Thread.sleep` / hardcoded base URLs or credentials
- Unique users via `RegistrationDataFactory` with cleanup when accounts are created
