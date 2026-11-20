# Test data — how we create users and inputs

---

## Simple principles

1. **Every run gets a new email** (safe for parallel tests)  
2. Build data with **factories**, not by typing values into tests  
3. If you **create** an account, **delete** it when done  
4. Checkout uses **practice** card data only (fake payment)  
5. Never commit real passwords or API keys  

---

## What to call

| Need | Use this |
|---|---|
| New signup user | `RegistrationDataFactory.uniqueUser()` |
| Random email / password | `UserFactory.uniqueEmail()` / `strongPassword()` |
| Payment card | `CheckoutDataFactory.practicePayment()` |
| Order comment | `CheckoutDataFactory.orderComment()` |

Example:

```java
RegistrationData user = RegistrationDataFactory.uniqueUser();
signupPage.completeRegistration(user);
```

---

## Fixed catalogue data (from the website)

The practice site keeps the same demo products. Use constants — don’t retype them:

| Constant | Meaning |
|---|---|
| `KnownProducts.BLUE_TOP` | First product (id 1) |
| `KnownProducts.MEN_TSHIRT` | id 2 |
| `KnownProducts.SLEEVELESS_DRESS` | id 3 |
| `KnownProducts.SEARCH_TERM_TOP` | search word `"top"` |
| `KnownBrands.POLO` / `HM` / `MADAME` | Brand anchors |

---

## Where strings live

| String type | File |
|---|---|
| Website labels / errors | `UiMessages` |
| API messages | `ApiMessages` |

Add new text there once. Reuse everywhere.

---

## Cleanup checklist

- [ ] Email came from a factory  
- [ ] Account deleted at the end (UI or API)  
- [ ] No shared static “demo user” object  
- [ ] Payment data from `CheckoutDataFactory`  

---

## Example with cleanup

```java
RegistrationData user = RegistrationDataFactory.uniqueUser();
ApiMessageResponse created = userApi.createAccount(user);
ApiResponseAssertions.assertUserCreated(created);

try {
    // UI or API steps…
} finally {
    userApi.deleteAccount(user.email(), user.password());
}
```
