# Automation Exercise Reconnaissance

Application: https://automationexercise.com/
Inspected: 2026-11-20

## UI Journeys (verified)

| Journey | Entry | Notes |
|---|---|---|
| Signup | `/login` New User Signup | Name + email → account information form → account created |
| Login | `/login` | `data-qa`: login-email, login-password, login-button |
| Products | `/products` | Search `#search_product` / `#submit_search`; cards `.product-image-wrapper` |
| Product details | `/product_details/{id}` | `.product-information` |
| Cart | `/view_cart` | Proceed to checkout when authenticated |
| Checkout / Payment | `/checkout` → `/payment` | Practice card fields; order confirmation |
| Delete account | Header after login | Deletes registered user |
| Contact Us | `/contact_us` | name, email, subject, message |
| Newsletter | Footer subscribe | `#susbscribe_email` (site typo) |

## Stable selectors (verified `data-qa`)

- login-email, login-password, login-button
- signup-name, signup-email, signup-button
- contact: name, email, subject, message, submit-button

Additional registration / account / payment `data-qa` attributes used by the framework were confirmed against live page HTML during page-object authoring (create-account, account-created, continue-button, delete-account, account-deleted, name_on_card, card_number, cvc, expiry_month, expiry_year, pay-button, order-placed, etc.).

## APIs (verified live)

| Method | Path | Status / behavior |
|---|---|---|
| GET | `/api/productsList` | 200 + products[] |
| POST | `/api/productsList` | 405 method not supported |
| GET | `/api/brandsList` | 200 + brands[] |
| POST | `/api/searchProduct` | 200 + products[]; missing param → 400 |
| POST | `/api/searchProduct` empty match | 200 + products: [] |
| POST | `/api/verifyLogin` | 200 User exists! / 404 User not found! |
| POST | `/api/createAccount` | 201 User created! |
| DELETE | `/api/deleteAccount` | 200 Account deleted! |
| PUT | `/api/updateAccount` | 200 User updated! |
| GET | `/api/getUserDetailByEmail?email=` | 200 user detail / 404 |

## Limitations

- API auth is form-urlencoded credential verify, not browser session cookies.
- Product POST/PUT brands return 405 by design (practice negative cases).
- Checkout uses practice payment only; no real payment gateway.
- Site may show ads/modals that interrupt automation (consent/continue overlays).
