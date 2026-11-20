# Automation inventory — what already exists

**Agents must read this file first** before proposing or generating new tests.  
Do not duplicate these scripts. Extend or add only for **gaps**.

---

## 1. UI test scripts (automated)

| Priority | Requirement | Test class | Method | Groups | Script path |
|---|---|---|---|---|---|
| P0 | REQ-AUTH-01/03 | `SignupUiTest` | `shouldRegisterUniqueUserAndDeleteAccount` | ui, smoke, regression | `src/test/java/com/agenticqa/tests/ui/signup/SignupUiTest.java` |
| P0 | REQ-AUTH-02/03 | `LoginUiTest` | `shouldLoginWithValidCredentials` | ui, smoke, regression | `…/ui/authentication/LoginUiTest.java` |
| P1 | REQ-AUTH-02 | `LoginUiTest` | `shouldRejectInvalidLoginCredentials` | ui, regression | same |
| P0 | REQ-CAT-02/03 | `ProductDiscoveryUiTest` | `shouldSearchProductsAndOpenDetails` | ui, smoke, regression | `…/ui/products/ProductDiscoveryUiTest.java` |
| P0 | REQ-CHK-01 + cart | `CheckoutUiTest` | `shouldCompleteCheckoutWithPracticePayment` | ui, regression | `…/ui/checkout/CheckoutUiTest.java` |

**Run UI:** `mvn test -Dgroups=ui` (add `-DHEADLESS=true` if needed)

---

## 2. API test scripts (automated)

| Priority | Requirement | Test class | Method | Groups | Script path |
|---|---|---|---|---|---|
| P0 | REQ-CAT-01 | `ProductListApiTest` | `shouldReturnProductCatalogue` | api, smoke, regression | `…/api/products/ProductListApiTest.java` |
| P0 | REQ-CAT-02 | `ProductSearchApiTest` | `shouldSearchProductsByKnownTerm` | api, smoke, regression | `…/api/products/ProductSearchApiTest.java` |
| P1 | REQ-CAT-02 | `ProductSearchApiTest` | `shouldRejectSearchWithoutParameter` | api, regression | same |
| P1 | REQ-CAT-02 | `ProductSearchApiTest` | `shouldReturnEmptyListForUnknownTerm` | api, regression | same |
| P1 | REQ-CAT-04 | `BrandListApiTest` | `shouldReturnBrandCatalogue` | api, smoke, regression | `…/api/products/BrandListApiTest.java` |
| P0 | REQ-AUTH-01/02/03 + ACC-01 | `UserRegistrationAuthApiTest` | `shouldRegisterAuthenticateAndCleanupUser` | api, smoke, regression | `…/api/users/UserRegistrationAuthApiTest.java` |
| P1 | REQ-AUTH-01 | `UserRegistrationAuthApiTest` | `shouldRejectDuplicateRegistration` | api, regression | same |
| P0 | REQ-AUTH-* | `SignupApiUiConsistencyIT` | `shouldCreateVerifyAndDeleteUserViaApi` | integration, regression, api | `…/integration/SignupApiUiConsistencyIT.java` |

**Run API:** `mvn test -Dgroups=api`

---

## 3. Unit / governance scripts

| Test | Method | Purpose |
|---|---|---|
| `AgentValidatorTest` | `shouldDetectViolationsInFlawedExample` | Flawed fixture must be REJECTED |
| `AgentValidatorTest` | `shouldAcceptCleanSnippet` | Clean pattern ACCEPTED |

Path: `src/test/java/com/agenticqa/tests/unit/validation/AgentValidatorTest.java`

---

## 4. Test data (already automated)

| Factory / constant | Produces | Used by |
|---|---|---|
| `RegistrationDataFactory.uniqueUser()` | Unique signup user | Signup UI, Checkout UI, Login UI (API create), User API tests |
| `UserFactory.uniqueEmail()` / `strongPassword()` | Ad-hoc credentials | Invalid login |
| `CheckoutDataFactory.practicePayment()` | Practice card | Checkout UI |
| `CheckoutDataFactory.orderComment()` | Comment text | Checkout UI |
| `AddressFactory` | Address fields | Inside registration factory |
| `KnownProducts.BLUE_TOP` (+ MEN_TSHIRT, SLEEVELESS_DRESS) | Exact catalogue anchors | Discovery UI + Product API |
| `KnownProducts.SEARCH_TERM_TOP` | `"top"` | Search UI/API |
| `KnownBrands.POLO` / `HM` / `MADAME` | Brand anchors | Brand API |
| `ApiMessages.*` | Exact API message strings | API assertions |
| `UiMessages.*` | Exact UI copy for locators | Pages / UiAssertions |

---

## 5. Page objects & API clients (already built)

**Pages:** `HomePage`, `LoginPage`, `SignupPage`, `AccountPage`, `ProductsPage`, `ProductDetailsPage`, `CartPage`, `CheckoutPage`, `PaymentPage`  
**Components:** `HeaderComponent`, `FooterComponent`, `ProductCardComponent`, `CartItemComponent`  
**Clients:** `ProductApiClient`, `BrandApiClient`, `UserApiClient`, `AuthenticationApiClient`  
**Assert helpers:** `UiAssertions`, `ProductAssertions`, `ApiResponseAssertions`

---

## 6. How to report inventory in agent output

When any skill/agent starts, print:

```markdown
## Current automation inventory (summary)
### UI tests
- SignupUiTest#shouldRegisterUniqueUserAndDeleteAccount (P0)
- …
### API tests
- ProductListApiTest#shouldReturnProductCatalogue (P0)
- …
### Test data
- RegistrationDataFactory.uniqueUser(), KnownProducts.BLUE_TOP, …
### Gaps vs request
- (only list REQ-* not covered that match the new requirement)
```

Then continue with prioritized new cases **only for gaps**.
