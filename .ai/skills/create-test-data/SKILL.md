---
name: create-test-data
description: Draft factories for unique users/payment under agent-generated/ (not src/).
---

# create-test-data

## Purpose
Keep tests parallel-safe and free of hardcoded credentials via factories.

## When to use
New entity needed (user variant, address, search fixture, practice payment tweak).

## Required inputs
Entity type; uniqueness rules; cleanup notes.

## Preconditions
Read `.ai/knowledge/test-data-strategy.md` and `agent-generated-sandbox.md`.  
Prefer **reusing** framework factories in `src/`; draft new ones only in the sandbox.

## Output location (mandatory)
`agent-generated/src/main/java/com/agenticqa/data/factories/...`  
`agent-generated/src/main/java/com/agenticqa/data/models/...`  

Do not edit `src/main/java/com/agenticqa/data/...` unless promoting.

## Workflow
1. Check inventory — reuse `RegistrationDataFactory` / `CheckoutDataFactory` if enough
2. If new factory needed, write under **`agent-generated/`**
3. Use Java Faker + timestamp/UUID for unique emails
4. Return `data.models` types (`RegistrationData`, `PaymentData`, …)
5. Document cleanup: UI delete or `UserApiClient.deleteAccount`
6. Catalogue anchors stay in framework **`KnownProducts` / `KnownBrands`**

## Example
```java
public static RegistrationData uniqueUser() {
    return new RegistrationData(
            UserFactory.uniqueName(),
            UserFactory.uniqueEmail(),
            UserFactory.strongPassword(),
            /* DOB + address via AddressFactory */);
}
```

## Prohibited
- Hardcoded credentials in tests
- Writing new factories straight into `src/` without promotion
- Real credit cards
