# Requirement: Guest-to-member purchase path

## Goal
As a new shopper on Automation Exercise, I must be able to register, discover a product via search, add it to cart, complete checkout with practice payment, and delete my account afterward.

## Scope
- UI: Signup, product search, cart, checkout, practice payment, account deletion
- API: product list, product search, create account, verify login, delete account

## Non-goals
- Real payment gateway
- Social login
- Inventory reservation

## Acceptance criteria
1. Unique email registration succeeds and user is authenticated.
2. Searching a known term returns at least one product.
3. Authenticated checkout reaches order confirmation.
4. Account can be deleted via UI or API after the journey.
5. Negative API search without `search_product` returns business code 400.
