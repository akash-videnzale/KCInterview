# Selector strategy — how to find elements

Pick the **most stable** locator. Use this order:

---

## 1. Best: `data-qa`

```java
byDataQa("login-button")
byDataQa("pay-button")
```

Use this whenever the page has a `data-qa` attribute.

---

## 2. Good: clear id or name

```java
By.id("search_product")
By.id("cart_info")
```

---

## 3. OK: simple CSS

```java
By.cssSelector(".features_items .product-image-wrapper")
```

Use for lists of products, cart rows, etc.

---

## 4. Last resort: text on the page

Put the text in **`UiMessages`** first, then use it:

```java
byTagTextContains("p", UiMessages.INVALID_LOGIN)
```

Do **not** write English strings directly in the page/test.

---

## Simple rules

| Do | Don't |
|---|---|
| Prefer `data-qa` | Guess new `data-qa` names |
| Use `UiMessages` for visible text | Hardcode `"Place Order"` in many files |
| Prefer `navigate("/products")` if ads block clicks | Rely only on fragile header clicks |
| Scope finds inside a card/row when needed | Use random `(//div)[17]` paths |

---

## Common `data-qa` values we already use

**Login / signup:**  
`login-email`, `login-password`, `login-button`,  
`signup-name`, `signup-email`, `signup-button`,  
`create-account`, `account-created`, `account-deleted`

**Payment:**  
`name-on-card`, `card-number`, `cvc`,  
`expiry-month`, `expiry-year`, `pay-button`, `order-placed`

Full list / journeys: `docs/recon/automation-exercise-findings.md`

---

## If a click fails

1. Call `dismissOverlaysIfPresent()` (ads/consent)  
2. Go by URL: `navigate("/login")`  
3. Retry once if needed (see `LoginPage.startSignup`)  
