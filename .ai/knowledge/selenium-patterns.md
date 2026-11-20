# Selenium Patterns

- One isolated `WebDriver` per `@Test` via `UiBaseTest` / `SessionFactory`
- Prefer `data-qa` attributes verified on Automation Exercise
- Wait with `WebDriverWait` + `ExpectedConditions`
- Never `Thread.sleep` / `TimeUnit.sleep` in tests
- Never `new ChromeDriver()` in tests — use `page()` from `UiBaseTest`
- Dismiss ads/consent with `dismissOverlaysIfPresent()`; prefer direct URL navigation when header clicks are intercepted
