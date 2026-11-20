# API contracts — how the website’s APIs work

Website API base: `https://automationexercise.com`  
(Use `ConfigLoader` / clients — don’t hardcode the URL in tests.)

More detail: `docs/recon/automation-exercise-findings.md`

---

## The #1 thing to remember

HTTP status can be **200**, but the real result is inside JSON:

```json
{ "responseCode": 201, "message": "User created!" }
```

So we assert **`responseCode`** and **`message`**, not only HTTP status.

Use:

- `ApiMessages` for exact message text  
- `ApiResponseAssertions` for checks  

---

## APIs we use

| What you want | Client | Path |
|---|---|---|
| All products | `ProductApiClient.getAllProducts()` | GET `/api/productsList` |
| Search products | `ProductApiClient.searchProducts(...)` | POST `/api/searchProduct` |
| All brands | `BrandApiClient.getAllBrands()` | GET `/api/brandsList` |
| Create user | `UserApiClient.createAccount(...)` | POST `/api/createAccount` |
| Delete user | `UserApiClient.deleteAccount(...)` | DELETE `/api/deleteAccount` |
| User details | `UserApiClient.getUserDetailByEmail(...)` | GET `/api/getUserDetailByEmail` |
| Verify login | `AuthenticationApiClient.verifyLogin(...)` | POST `/api/verifyLogin` |

---

## Common success / error codes

| Situation | `responseCode` | Message (see `ApiMessages`) |
|---|---|---|
| User created | 201 | User created! |
| Login OK | 200 | User exists! |
| Account deleted | 200 | Account deleted! |
| User missing | 404 | User not found! |
| Email already used | 400 | Email already exists! |
| Search param missing | 400 | Bad request, search_product… |

---

## Java objects for JSON

These are **POJOs** (getters/setters):

- `ApiMessageResponse` → code + message  
- `ProductsListResponse` → list of `Product`  
- `BrandsListResponse` → list of `Brand`  
- `UserDetailResponse` → nested `UserDetail`  

Example:

```java
ProductsListResponse body = productApi.getAllProducts();
ApiResponseAssertions.assertProductsListData(body);

Product blueTop = body.requireById(KnownProducts.BLUE_TOP.getId());
Assert.assertEquals(blueTop.getName(), KnownProducts.BLUE_TOP.getName());
```

---

## Stable products / brands (don’t invent values)

```text
KnownProducts.BLUE_TOP        → id 1, Blue Top, Rs. 500, Polo
KnownProducts.MEN_TSHIRT      → id 2
KnownProducts.SLEEVELESS_DRESS → id 3
KnownProducts.SEARCH_TERM_TOP → "top"

KnownBrands.POLO / HM / MADAME
```

Use these for exact asserts.

---

## Simple test recipe

1. Call the client  
2. Parse to POJO (client usually does this)  
3. Call `ApiResponseAssertions...`  
4. Optionally check one known product/brand with getters  
