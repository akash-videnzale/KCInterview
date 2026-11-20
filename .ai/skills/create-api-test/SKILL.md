---
name: create-api-test
description: Create REST Assured API tests via clients, POJOs, and ApiResponseAssertions.
---

# create-api-test

## Purpose
Add API tests for **verified** Automation Exercise endpoints. Prefer exact business asserts.

## When to use
productsList, searchProduct, brandsList, createAccount, verifyLogin, deleteAccount, getUserDetailByEmail, etc.

## Required inputs
Endpoint + method; positive/negative cases; groups (`api` + smoke/regression).

## Preconditions
- Read `.ai/knowledge/automation-inventory.md` — **do not recreate** listed API methods
- Endpoint in `docs/recon/automation-exercise-findings.md` and `.ai/knowledge/api-contracts.md`
- Client method exists (or run `create-api-client` first)
- Prefer adding to an existing `*ApiTest` class when the resource already has tests
- Mirror inventory: `ProductListApiTest`, `ProductSearchApiTest`, `BrandListApiTest`, `UserRegistrationAuthApiTest`

## Output location (mandatory)
| Artifact | Path |
|---|---|
| Draft test | `agent-generated/src/test/java/com/agenticqa/tests/api/<area>/YourApiTest.java` |
| Draft client/POJO | `agent-generated/src/main/java/com/agenticqa/api/...` |
| Cases | `agent-generated/cases/<feature>-cases.md` |

**Do not** write into `src/test` or `src/main` unless promoting.

## Workflow
1. Dump inventory; confirm gap
2. Create class under **`agent-generated/src/test/java/...`** extending **`ApiBaseTest`**
3. Call **`*ApiClient` only** — never `RestAssured.given()` in the test
4. Parse to **POJOs**; use **getters** (`getResponseCode()`, `getName()`)
5. Assert with **`ApiResponseAssertions`** + **`ApiMessages`** / **`KnownProducts`** / **`KnownBrands`**
6. Remember: HTTP may be 200 while JSON `responseCode` is the real status
7. Delete users created for the test
8. Groups: `{"api", "smoke", "regression"}` as appropriate
9. Validate draft under `agent-generated/` only

## Example (save under agent-generated/)
```java
public class ProductListApiTest extends ApiBaseTest {
    private final ProductApiClient productApi = new ProductApiClient();

    @Test(groups = {"api", "smoke", "regression"})
    public void shouldReturnProductsListWithKnownAnchors() {
        Assert.assertEquals(productApi.getAllProductsRaw().statusCode(), 200);
        ProductsListResponse body = productApi.getAllProducts();
        ApiResponseAssertions.assertProductsListData(body);

        Product blueTop = body.requireById(KnownProducts.BLUE_TOP.getId());
        Assert.assertEquals(blueTop.getName(), KnownProducts.BLUE_TOP.getName());
        Assert.assertEquals(blueTop.getPrice(), KnownProducts.BLUE_TOP.getPrice());
    }
}
```

## Prohibited
- Inventing endpoints
- Asserting only HTTP when `responseCode` matters
- Record-style accessors on response POJOs
- Hardcoded emails for createAccount (use factory)

## Validation
```bash
mvn -q -DskipTests exec:java \
  -Dexec.mainClass="com.agenticqa.governance.AgentValidator" \
  -Dexec.args="agent-generated/src/test/java"
```
