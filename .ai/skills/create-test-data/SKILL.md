---
name: create-test-data
description: Create factories producing isolated unique test data.
---

# create-test-data

## Purpose
Create factories producing isolated unique test data.

## When to use
Tests need users, addresses, search terms, or practice payment data.

## Required inputs
Entity type; uniqueness requirements.

## Preconditions
Do not hardcode production credentials.

## Workflow
1. Add/extend factory under com.agenticqa.data.factories
2. Use Java Faker + UUID/timestamp for uniqueness
3. Return immutable records
4. Document cleanup (deleteAccount) when users are created
5. Keep practice payment values clearly labeled


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
```java
public static RegistrationData uniqueUser() {
  AddressData address = AddressFactory.indiaPractice();
  return new RegistrationData(UserFactory.uniqueName(), UserFactory.uniqueEmail(),
      UserFactory.strongPassword(), "10", "5", "1990", address.firstName(), address.lastName(),
      address.company(), address.address1(), address.address2(), address.country(),
      address.state(), address.city(), address.zipcode(), address.mobileNumber());
}
```

## Validation requirements
- `mvn -DskipTests test-compile` succeeds for Java changes
- AgentValidator reports ACCEPTED (0 ERROR) for production tests
- New tests use meaningful assertion messages
