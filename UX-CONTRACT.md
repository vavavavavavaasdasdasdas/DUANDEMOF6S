# UX Contract
## Product context
ShoeAdmin phục vụ quản trị viên cửa hàng giày, tiếng Việt, tiền VND, giờ Asia/Ho_Chi_Minh. Native keyboard semantics, focus visible và nhãn đầy đủ là baseline.
## Business-context sources
| Scope | Source | Reviewed |
|---|---|---|
| Phase 6 CRUD/soft-delete/stock logs | Yêu cầu Phase 6 trong chat và PHASE6_REPORT.md | 2026-10-07 |
| Permissions | security/SecurityConfig.java: /admin/** requires ROLE_ADMIN | 2026-10-07 |
| Price/stock invariants | database/phase6.sql; InventoryService; AdminCatalogService | 2026-10-07 |
| Order snapshots | OrderService; OrderDetail | 2026-10-07 |
## Visual contract
DESIGN.md mirrors runtime CSS. Canonical tokens: design-system.css → admin.css → shared layout/fragments. Light theme, existing identity.
## Canonical UI Map
| Capability | Canonical owner | Source of truth | Allowed variants | Verification |
|---|---|---|---|---|
| Select/Listbox | Native select | DESIGN.md | native platform popup | keyboard/browser |
| Date | Native datetime-local | Phase 8 request + shop time zone | platform calendar | validation/browser |
| Table Selection | Native checkboxes + admin-discounts.js | Phase 8 product selection | hide rows, preserve selection | browser/transaction tests |
| Form | admin-catalog.js + admin.css + Bean Validation | This contract | create/edit/stock | validation E2E |
| Scrollbar | admin.css global baseline | DESIGN.md | table overflow | computed style |
| Toast | fragments/admin-flash.html | Phase 5 | success/error | live region |
| CRUD | Admin*Controller + AdminCatalogService | Phase 6 request | catalog detail / owning list / stock history | full-flow E2E |
| Dialog | shared admin-confirm dialog in admin-layout | This contract | delete / unsaved | Escape/focus/E2E |
## Dataset navigation
Products: server filtering/sorting/pagination 10/20/50, URL state includes keyword/category/status/sort/page/size. Search commits via Enter/Lọc, so no keystroke requests or races; clear resets query immediately and submits. Other catalog lists use page/size, history top 100 clearly labelled. Paging clamps after data changes.
## Flow ledger
| Operation | Pending | Success destination | Feedback | Failure |
|---|---|---|---|---|
| Product create/edit | Disable submit, aria-busy | Product detail to continue adding variants | shared flash | same form + field errors |
| Variant create/edit | same | owning product variants | same | same |
| Category/size save | same | owning list | same | same |
| Inventory adjustment | same | history for variant | same | preserve values, reload on stock conflict |
| Delete | accessible confirmation then POST | owning list | explicit delete vs stop-selling | FK/reference message |
Product detail destination is intentional: a product needs size/price/stock before it can be purchased.
## Navigation and responsive
Shared Admin shell. Native semantic table with local horizontal overflow. Pagination links keep filters. Invalid IDs are 404. User is forbidden server-side. Phase 6 does not use bulk selection/date pickers; Phase 8 native ownership is documented below.
## Overlays and feedback
Native HTML dialog.showModal provides inert background/focus containment; application owns content and buttons. Escape/cancel restores trigger focus. Deletion names object and consequences. Dirty forms warn on in-app navigation with the same dialog and use beforeunload only for external unload.
## Async and resilience
Pessimistic regular HTML POST + redirect. No optimistic stock. Duplicate submit blocked client-side, DB unique constraints prevent duplicate identities; stock writes lock variant; cancellation locks order and returns stock once. Absolute stock edits compare expectedQuantity to avoid overwriting concurrent changes. Add-stock checks the same snapshot to prevent repeat submissions applying twice. Offline submit stays on form with visible error, no silent retries.
## Validation
Bean Validation + service constraints + database constraints authoritative. Native metadata reused by shared client validator; novalidate disables browser bubbles. Field errors are associated through aria-describedby/aria-invalid; focus first error. Upload preserves existing image when omitted, type/size/content validated server-side. A rejected oversized HTTP multipart request needs file reselection.
## Migration and verification
Phase 6 screens adopt these rules; legacy unrelated forms are not a whole-repo redesign. Compare with Phase 5 dashboard. Verify build/tests, E2E create+3 variants, price propagation, duplicate/negative failures, referenced deletion, stock SQL/logs, mobile and keyboard. Evidence and limits in PHASE6_REPORT.md.


## Phase 7 — Customer / Employee
Reuse the Phase 6 Admin shell, catalog form/table/dialog owners and native selects. Customer/Employee search is server-side (name, phone, email), 10 rows/page. Status is absent from save DTOs; only a separate confirmed POST can change it. Detail screens own status/delete actions, show linked account state and, for customers, paged order history. Create/edit returns to detail with flash; errors stay at fields. Employee password is never echoed back and is optional only for edit. Employee role follows existing ROLE_ADMIN configuration; no new role or permission surface is introduced. Disable/delete of the signed-in account is rejected server-side as well as disabled in UI. Referenced records are deactivated; Employee and User create/delete atomically. Verification: PHASE7_REPORT.md and PeopleIntegrationTest.

## Phase 8 — Voucher / Promotion
Business authority: Phase 8 request in chat; existing PricingService highest-effective-discount behavior is retained. Promotion interval is [start,end), status must be true. Multiple promotions do not stack: the largest monetary reduction wins. Voucher is applied after product promotion, before shipping; percentage calculation rounds to nearest đồng (HALF_UP), then maxDiscount/subtotal caps apply without rounding above a cap. Fixed and cap decimal values retain DB precision. No voucher-usage refund on cancellation is introduced; cancellation restores stock through the existing ledger.
Native datetime-local owns the calendar/popup; application documents shop timezone Asia/Ho_Chi_Minh. Table Selection owner: native labelled product checkboxes + admin-discounts.js. Searching locally hides rows and preserves all selected IDs; saving replaces the full selection in one transaction, serialized by a promotion row lock and protected by a unique DB index. This screen loads the catalog; large-catalog server search is a documented future scaling concern.
Voucher create/edit returns to list. Promotion create/edit returns to product selection to complete the campaign; selection save stays there with flash. Delete is confirmed POST; referenced/used voucher is locked, other vouchers and promotion links are deleted transactionally. Invalid IDs return 404. Public vouchers clear customer binding on save; Private requires a real customer. usedCount cannot be bound by Admin save.
Shared voucher-preview.js owns Checkout/POS preview: abort old requests, discard stale responses, reset applied discount when code/customer changes, show network failure while preserving input. Preview never consumes usage. Order transaction revalidates actual variant prices, computes subtotal, locks and consumes voucher, then commits order/stock/usage together. Existing sessions do not make a preview a reservation. Checkout fee is computed again from transaction subtotal. POS customer is chosen explicitly for Private voucher; cashier identity comes from authentication.
Verification: PHASE8_REPORT.md; DiscountIntegrationTest; browser output/playwright/phase8. No new roles/libraries.

## Phase 9 — POS, invoices and order lifecycle
Business authority: Phase 9 request in chat. POS owns a session basket; server prices, stock and voucher are authoritative at checkout. Single-node session synchronization plus a basket token prevent repeated/stale basket submissions; tokens rotate after cart edits. This is not a distributed idempotency contract. Checkout draft (customer, payment, cash and voucher) stays in sessionStorage across cart edits; empty basket clears draft. Customer search filters currently loaded active customer options by name/phone and preserves the selected customer; large-catalog server lookup remains a scaling concern.
Both online and POS call OrderService.saveSale inside their public @Transactional entry points. Sorted variant locks, voucher consume, Order/Detail writes, stock ledger and Invoice commit together. Invoice order FK is unique; migration backfills historical orders. New invoices snapshot customer/staff names and phone; product/size/prices remain OrderDetail snapshots. Shop contact uses SHOP_NAME/SHOP_ADDRESS/SHOP_PHONE, defaulting to existing storefront sample contacts. Cash requires tendered >= server total; invoice stores tendered/change. Transfer starts UNPAID, requires confirmed receipt before completion.
Online lifecycle: PENDING → CONFIRMED → SHIPPING → DELIVERED; legacy PREPARING → SHIPPING. POS CONFIRMED → DELIVERED without shipment. Nonterminal orders can cancel; terminal states cannot reopen. Cancellation locks order, restores sorted variants exactly once and records logs; no voucher usage refund is introduced. COD becomes PAID on completion. Paid noncancelled revenue follows existing creation-date reporting, not paid-at accounting. Statistics reuse Dashboard queries with channel breakdown including POS.
Canonical owners: Admin shell/panels/tables, native selects/date inputs, admin-catalog confirmed status/payment mutations, shared voucher-preview, native product-dialog variant, MoneyFormat for sales/receipt currency. Invoice list uses server paging (20); searches escape SQL LIKE wildcards. POS search is server GET on active variants; customer search is local. Evidence: PHASE9_REPORT.md, PosInvoiceIntegrationTest, ignored output/playwright/phase9 captures.

## Phase 10 statistics
Authority: Phase 10 request, preserved Phase 9 paid/noncancelled creation-date rule. Revenue/count/average/top qualify PAID and not CANCELLED, including paid nonterminal orders; completed but unpaid orders are excluded. Product revenue uses stored OrderDetail.totalPrice after promotion, before order voucher/shipping; no unrequested proportional allocation. Status distribution covers every order in the date/channel filter and is explicitly separated from eligible revenue. Dates include both endpoints in shop timezone; default today minus 29 days through today; ALL/ONLINE/POS. GET URL state survives tab navigation; reversed dates/invalid channel preserve input and show error without query. At most 3661 inclusive days prevents unbounded chart materialization; malformed typed dates return HTTP 400. Chart CDN failure leaves tables visible with warning; an empty qualifying range has zero cards and an explicit recovery hint, no empty revenue canvas. Evidence: PHASE10_REPORT.md, StatisticsIntegrationTest and output/playwright/phase10.

## Phase 11 UI verification
Authority: Phase 11 request (no new business features). Restore existing User Orders/list/detail links to their existing views with the same customer ownership check as Success; missing IDs 404, other customer 403. Storefront uses shared store-ui native validation/loading/status/dialog; Cart update/remove use existing AJAX endpoints, disable during pending, preserve basket on network error and use app confirmation. All static CSS lives in design-system.css or admin.css. Native date/select behavior stays unchanged. GET filters have no dirty warning. Existing account placeholder is not a completed account module; review presentation only. Verify every route in PHASE11_REPORT.md at 375/768/1280, GET link crawl, readable contrast, focus, AJAX failure/pending/empty and keyboard states.

## Approved Account / POS hold addition (2026-10-08)
The current user request replaces the account placeholder. Active standalone User accounts may authenticate without Employee; an existing inactive Employee or Customer still disables authentication. POS payment still requires an active linked Employee. Account profile prefers Employee fields, falls back to User, updates only name/email/phone and preserves role/username. Password errors are field errors, values are never echoed, successful change redirects back and keeps the session.
Held carts are database drafts with no stored prices, inventory reservation, Order/Invoice or voucher consumption. Staff ownership and expires_at > shop Clock.now are enforced on list/open/delete/payment. New holds serialize on the User row for the five-active-hold limit. End-of-day and 24-hour expiry use shop.timezone; cleanup hard-deletes expired rows. Opening recomputes prices and stock; session display snapshots detect changed prices, while an unknown old price after restart requires explicit review. Voucher failure clears the applied code with flash feedback. Switching a nonempty basket uses save/discard/cancel, with save omitted at the limit. Held checkout locks the hold, calls the existing OrderService and deletes the hold in that same transaction; failures retain it. Native controls, admin-catalog validation/loading/confirmation, Admin flash and voucher-preview remain canonical. No new visual tokens or screen stylesheet.
