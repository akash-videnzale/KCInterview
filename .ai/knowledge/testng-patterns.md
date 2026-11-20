# TestNG Patterns
- @Test with groups: ui, api, integration, smoke, regression, unit
- SoftAssert must call assertAll()
- alwaysRun=true on cleanup hooks
- Parallel methods supported for API; UI uses isolated contexts
