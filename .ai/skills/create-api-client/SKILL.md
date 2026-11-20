---
name: create-api-client
description: Add a REST Assured client method for a verified Automation Exercise endpoint.
---

# create-api-client

## Purpose
Expose a verified API operation on an `*ApiClient` so tests never call RestAssured directly.

## When to use
New endpoint needed by a test (must exist in recon first).

## Required inputs
HTTP method, path, params/body; response POJO shape.

## Preconditions
- Confirm in `docs/recon/automation-exercise-findings.md`
- Prefer extending `ProductApiClient`, `BrandApiClient`, `UserApiClient`, `AuthenticationApiClient`
- Read `.ai/knowledge/api-contracts.md`

## Output location (mandatory)
Draft clients/POJOs:

`agent-generated/src/main/java/com/agenticqa/api/clients/...`  
`agent-generated/src/main/java/com/agenticqa/api/models/...`

Do not modify framework `src/main/java` unless promoting.

## Workflow
1. Extend **`BaseApiClient`** helpers (import from framework)
2. Write drafts under **`agent-generated/src/main/java/...`**
3. Add method returning typed POJO and/or raw `Response` if tests need HTTP status
4. Add/extend **POJO** with getters/setters + `@JsonIgnoreProperties`
5. Propose `ApiMessages` / assertion helper additions in the draft package or as a promotion note
6. Optionally draft schema under `agent-generated/` (promote to `src/main/resources/schemas/` later)

## Example
```java
public ProductsListResponse searchProducts(String term) {
    Response raw = withOverloadRetry(() ->
            givenForm().formParam("search_product", term).when().post("/api/searchProduct"));
    attachResponse("searchProduct", raw);
    return parseProducts(raw);
}
```

## Prohibited
- Inventing paths
- Logging passwords in clear text (redaction via Extent filter / SecretRedactor)
- Putting RestAssured calls in test classes

## Validation
```bash
mvn -q test-compile
mvn test -Dgroups=api
```
