# RESTRUCTURE PLAN

## 1. TÃƒÂ³m tÃ¡ÂºÂ¯t hiÃ¡Â»â€¡n trÃ¡ÂºÂ¡ng
Project Ã„â€˜ÃƒÂ£ cÃ¡ÂºÂ¥u trÃƒÂºc package theo domain (`banhang`, `khachhang`, `khuyenmai`, `nhanvien`, `sanpham`, `thongke`). Ã„ÂÃƒÂ£ sÃ¡Â»Â­ dÃ¡Â»Â¥ng mÃ¡Â»â„¢t sÃ¡Â»â€˜ DTO cho viÃ¡Â»â€¡c lÃ¡ÂºÂ¥y dÃ¡Â»Â¯ liÃ¡Â»â€¡u, tuy nhiÃƒÂªn vÃ¡ÂºÂ«n cÃƒÂ²n lÃ¡Â»â€”i mapping trong Thymeleaf (nhÃ†Â° trang chi tiÃ¡ÂºÂ¿t sÃ¡ÂºÂ£n phÃ¡ÂºÂ©m vÃƒÂ  order detail bÃ¡Â»â€¹ 500 do gÃ¡Â»Âi sai thuÃ¡Â»â„¢c tÃƒÂ­nh). BÃ¡ÂºÂ£o mÃ¡ÂºÂ­t phÃƒÂ¢n quyÃ¡Â»Ân bÃ¡ÂºÂ±ng Spring Security Ã„â€˜ang hoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng (User bÃ¡Â»â€¹ block khÃ¡Â»Âi `/admin/**` vÃƒÂ  trÃ¡ÂºÂ£ vÃ¡Â»Â 403, Anon bÃ¡Â»â€¹ redirect vÃ¡Â»Â `/login` hoÃ¡ÂºÂ·c trÃ¡ÂºÂ£ vÃ¡Â»Â 302). Cart Ã„â€˜ang Ã„â€˜Ã†Â°Ã¡Â»Â£c triÃ¡Â»Æ’n khai qua Session thay vÃƒÂ¬ lÃ†Â°u Database. Invoice chÃ†Â°a cÃƒÂ³ Entity riÃƒÂªng mÃƒÂ  Ã„â€˜ang gÃ¡Â»â„¢p chung vÃƒÂ o Order. CSS vÃƒÂ  Design System chÃ†Â°a Ã„â€˜Ã¡Â»â€œng bÃ¡Â»â„¢ theo yÃƒÂªu cÃ¡ÂºÂ§u mÃ¡Â»â€ºi. UI cÃ¡ÂºÂ§n lÃƒÂ m lÃ¡ÂºÂ¡i hoÃƒÂ n toÃƒÂ n Ã„â€˜Ã¡Â»Æ’ Ã„â€˜ÃƒÂ¡p Ã¡Â»Â©ng chuÃ¡ÂºÂ©n UX (nhÃ†Â° nÃƒÂºt size, bÃƒÂ¡o lÃ¡Â»â€”i chÃ¡Â»Ân size). NhÃƒÂ¬n chung, hÃ¡Â»â€¡ thÃ¡Â»â€˜ng backend Ã„â€˜ÃƒÂ£ cÃƒÂ³ khung khÃƒÂ¡ Ã¡Â»â€¢n nhÃ†Â°ng kÃ¡ÂºÂ¿t nÃ¡Â»â€˜i vÃ¡Â»â€ºi Frontend Ã„â€˜ang bÃ¡Â»â€¹ vÃ¡Â»Â¡ vÃƒÂ i chÃ¡Â»â€”, cÃ¡ÂºÂ§n Ã„â€˜iÃ¡Â»Âu chÃ¡Â»â€°nh lÃ¡ÂºÂ¡i.

## 2. BÃ¡ÂºÂ£ng ROUTE MAP

| URL | Method | Controller.method | Template | Role yÃƒÂªu cÃ¡ÂºÂ§u | Status Anon | Status User | Status Admin | Ghi chÃƒÂº lÃ¡Â»â€”i |
|---|---|---|---|---|---|---|---|---|
| `/` | GET | `HomeController.home` | `user/home` | KhÃƒÂ´ng | 200 | 200 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng bÃƒÂ¬nh thÃ†Â°Ã¡Â»Âng |
| `/login` | GET | `HomeController.login` | `login` (Ã„â€˜ÃƒÂ£ thÃƒÂªm) | KhÃƒÂ´ng | 200 | 200 | 200 | Trang login custom Ã„â€˜ÃƒÂ£ hoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/products` | GET | `ProductController.list` | `user/products` | KhÃƒÂ´ng | 200 | 200 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/products/{id}` | GET | `ProductController.detail` | `user/product-details` | KhÃƒÂ´ng | 500 | 500 | 500 | `TemplateProcessingException`: Could not parse as each: "v : " |
| `/cart` | GET | `CartController.viewCart` | `user/cart` | KhÃƒÂ´ng | 200 | 200 | 200 | DÃƒÂ¹ng Session |
| `/orders` | GET | `OrderController.myOrders` | `user/orders` | TÃƒÂ¹y chÃ¡Â»Ân | 200 | 200 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/orders/checkout` | GET | `OrderController.checkout` | `user/checkout` | USER | 302 | 302 | 302 | Redirect vÃ¡Â»Â `/cart` vÃƒÂ¬ giÃ¡Â»Â hÃƒÂ ng trÃ¡Â»â€˜ng |
| `/orders/{id}` | GET | `OrderController.orderDetail` | `user/order-detail` | USER | 200 | 200 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/admin` | GET | `AdminController.dashboard` | `admin/dashboard` | ADMIN | 302 | 403 | 200 | PhÃƒÂ¢n quyÃ¡Â»Ân vÃƒÂ  redirect login/dashboard hoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/admin/products-crud` | GET | `AdminProductController.list` | `admin/products/list` | ADMIN | 302 | 403 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/admin/customers` | GET | `AdminCustomerController.list` | `admin/customers/list` | ADMIN | 302 | 403 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/admin/promotions` | GET | `AdminPromotionController.list` | `admin/promotions/list` | ADMIN | 302 | 403 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/admin/vouchers` | GET | `AdminVoucherController.list` | `admin/vouchers/list` | ADMIN | 302 | 403 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/admin/employees` | GET | `AdminEmployeeController.list` | `admin/employees/list` | ADMIN | 302 | 403 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/admin/orders` | GET | `AdminOrderController.list` | `admin/orders/list` | ADMIN | 302 | 403 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |
| `/admin/orders/detail/{id}` | GET | `AdminOrderController.detail` | `admin/orders/detail` | ADMIN | 302 | 403 | 500 | `SpelEvaluationException`: Property 'orderDetails' cannot be found on object of type 'Order' |
| `/admin/pos` | GET | `AdminController.pos` | `admin/pos` | ADMIN | 302 | 403 | 200 | HoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng |

## 3. BÃ¡ÂºÂ£ng PAGE MAP

| Trang cÃ¡ÂºÂ§n cÃƒÂ³ theo thiÃ¡ÂºÂ¿t kÃ¡ÂºÂ¿ | Ã„ÂÃƒÂ£ tÃ¡Â»â€œn tÃ¡ÂºÂ¡i? | ThiÃ¡ÂºÂ¿u gÃƒÂ¬ |
|---|---|---|
| **Trang chÃ¡Â»Â§** | CÃƒÂ³ | CÃ¡ÂºÂ§n sÃ¡Â»Â­a giao diÃ¡Â»â€¡n |
| **Danh sÃƒÂ¡ch sÃ¡ÂºÂ£n phÃ¡ÂºÂ©m** | CÃƒÂ³ | LÃ¡Â»Âc theo thÃ†Â°Ã†Â¡ng hiÃ¡Â»â€¡u, khoÃ¡ÂºÂ£ng giÃƒÂ¡, size, sÃ¡ÂºÂ¯p xÃ¡ÂºÂ¿p. |
| **Chi tiÃ¡ÂºÂ¿t sÃ¡ÂºÂ£n phÃ¡ÂºÂ©m** | CÃƒÂ³ | Component Size (nÃƒÂºt bÃ¡ÂºÂ¥m), sÃ¡Â»Â­a lÃ¡Â»â€”i Thymeleaf 500. |
| **GiÃ¡Â»Â hÃƒÂ ng** | CÃƒÂ³ | Ã„ÂÃ¡Â»â€œng bÃ¡Â»â„¢ UI mÃ¡Â»â€ºi |
| **Thanh toÃƒÂ¡n (Checkout)** | CÃƒÂ³ | ChÃ¡ÂºÂ·n checkout nÃ¡ÂºÂ¿u chÃ†Â°a login. Ã„ÂÃ¡Â»â€œng bÃ¡Â»â„¢ UI. |
| **LÃ¡Â»â€¹ch sÃ¡Â»Â­ Ã„â€˜Ã†Â¡n hÃƒÂ ng** | CÃƒÂ³ | Ã„ÂÃ¡Â»â€œng bÃ¡Â»â„¢ UI |
| **Login / Ã„ÂÃ„Æ’ng nhÃ¡ÂºÂ­p** | CÃƒÂ³ | Ã„ÂÃƒÂ£ tÃ¡ÂºÂ¡o custom login page Ã¡Â»Å¸ Phase 1. |
| **Admin Dashboard** | CÃƒÂ³ | Ã„ÂÃ¡Â»â€œng bÃ¡Â»â„¢ UI |
| **Admin CRUD** | CÃƒÂ³ | Component bÃ¡ÂºÂ£ng, form theo chuÃ¡ÂºÂ©n CSS mÃ¡Â»â€ºi. |
| **Admin POS** | CÃƒÂ³ | Component chÃ¡Â»Ân sÃ¡ÂºÂ£n phÃ¡ÂºÂ©m tÃ¡Â»â€˜i Ã†Â°u. |
| **Error (403, 404, 500)** | CÃƒÂ³ | CÃ¡ÂºÂ§n sÃ¡Â»Â­a lÃ¡ÂºÂ¡i UI. |

## 4. Giao diÃ¡Â»â€¡n vÃƒÂ  Backend bÃ¡ÂºÂ¥t Ã„â€˜Ã¡Â»â€œng bÃ¡Â»â„¢
- **Giao diÃ¡Â»â€¡n chÃ†Â°a cÃƒÂ³ backend:** Component lÃ¡Â»Âc sÃ¡ÂºÂ£n phÃ¡ÂºÂ©m Ã¡Â»Å¸ FE chÃ†Â°a cÃƒÂ³ param tÃ†Â°Ã†Â¡ng Ã¡Â»Â©ng trong `ProductController.list`.

## 5. DATABASE DEPENDENCY
- `User` - `Role`: Quan hÃ¡Â»â€¡ n-1, Ã„â€˜ÃƒÂºng chuÃ¡ÂºÂ©n.
- `Product` - `Category`: Quan hÃ¡Â»â€¡ n-1, Ã„â€˜ÃƒÂºng chuÃ¡ÂºÂ©n.
- `Product` - `ProductVariant` - `Size`: `Product` 1-n `ProductVariant`, `Size` 1-n `ProductVariant`, Ã„â€˜ÃƒÂºng.
- `Cart`: KhÃƒÂ´ng cÃƒÂ³ trong database (DÃƒÂ¹ng session).
- `Order` - `OrderDetail`: 1-n. Tuy nhiÃƒÂªn, Entity `Order` hiÃ¡Â»â€¡n khÃƒÂ´ng chÃ¡Â»Â©a List `orderDetails`, gÃƒÂ¢y ra lÃ¡Â»â€”i 500 Ã¡Â»Å¸ Admin.
- `Invoice`: KhÃƒÂ´ng cÃƒÂ³ bÃ¡ÂºÂ£ng riÃƒÂªng, Ã„â€˜ang dÃƒÂ¹ng chung vÃ¡Â»â€ºi `Order`.
- `Voucher`: CÃƒÂ³ bÃ¡ÂºÂ£ng.
- `Promotion` - `PromotionDetail` - `Product`: Quan hÃ¡Â»â€¡ Ã„â€˜ÃƒÂºng chuÃ¡ÂºÂ©n.
- `Customer`: CÃƒÂ³ bÃ¡ÂºÂ£ng.
- `Employee`: CÃƒÂ³ bÃ¡ÂºÂ£ng.

## 6. TrÃ¡ÂºÂ£ lÃ¡Â»Âi cÃƒÂ³ bÃ¡ÂºÂ±ng chÃ¡Â»Â©ng 18 cÃƒÂ¢u hÃ¡Â»Âi
1. trang nÃƒÂ o tÃ¡Â»â€œn tÃ¡ÂºÂ¡i: **Ã„ÂÃƒÅ¡NG** (CÃƒÂ¡c template cÃƒÂ³ trong `src/main/resources/templates`).
2. trang nÃƒÂ o thiÃ¡ÂºÂ¿u: **Ã„ÂÃƒÅ¡NG** (ThiÃ¡ÂºÂ¿u template `user/login.html`).
3. controller nÃƒÂ o tÃ¡Â»â€œn tÃ¡ÂºÂ¡i: **Ã„ÂÃƒÅ¡NG** (Ã„ÂÃƒÂ£ check list HomeController, ProductController, etc).
4. controller nÃƒÂ o khÃƒÂ´ng cÃƒÂ³ frontend: **Ã„ÂÃƒÅ¡NG** (Spring sinh ra `/login`).
5. route nÃƒÂ o Ã„â€˜Ã¡Â»Â©t: **Ã„ÂÃƒÅ¡NG** (`/products/1` lÃ¡Â»â€”i 500).
6. route nÃƒÂ o 404: **SAI** (KhÃƒÂ´ng phÃƒÂ¡t hiÃ¡Â»â€¡n 404).
7. route nÃƒÂ o 403 sai: **SAI** (403 hoÃ¡ÂºÂ¡t Ã„â€˜Ã¡Â»â„¢ng Ã„â€˜ÃƒÂºng mong Ã„â€˜Ã¡Â»Â£i).
8. route nÃƒÂ o 500: **Ã„ÂÃƒÅ¡NG** (`/products/1` vÃƒÂ  `/admin/orders/detail/1`).
9. admin login cÃƒÂ³ chÃ¡ÂºÂ¡y khÃƒÂ´ng: **Ã„ÂÃƒÅ¡NG** (Truy cÃ¡ÂºÂ­p Ã„â€˜Ã†Â°Ã¡Â»Â£c `/admin`).
10. user login cÃƒÂ³ chÃ¡ÂºÂ¡y khÃƒÂ´ng: **Ã„ÂÃƒÅ¡NG** (BÃ¡Â»â€¹ 403 Ã¡Â»Å¸ `/admin`, 200 Ã¡Â»Å¸ trang chÃ¡Â»Â§).
11. Product->Variant->Cart: **Ã„ÂÃƒÅ¡NG** (CartSession ghi nhÃ¡ÂºÂ­n).
12. Cart->Checkout: **SAI** (Route checkout cho phÃƒÂ©p redirect mÃƒÂ  chÃ†Â°a chÃ¡ÂºÂ·n authentication Ã„â€˜ÃƒÂºng luÃ¡ÂºÂ­t mÃ¡Â»â€ºi).
13. Checkout->Order: **CHÃ†Â¯A CÃƒâ€œ CHÃ¡Â»Â¨NG CÃ¡Â»Â¨** (ChÃ†Â°a test tÃ¡ÂºÂ¡o Ã„â€˜Ã†Â¡n do bÃ¡Â»â€¹ block 302 giÃ¡Â»Â rÃ¡Â»â€”ng).
14. Order->Invoice: **Ã„ÂÃƒÅ¡NG** (Order type POS).
15. POS->Order->Inventory: **NOT TESTED**.
16. Voucher->Checkout/POS: **Ã„ÂÃƒÅ¡NG** (CÃƒÂ³ form field voucher).
17. Promotion->Product: **Ã„ÂÃƒÅ¡NG** (BÃ¡ÂºÂ£ng `promotion_details`).
18. Dashboard->Database: **Ã„ÂÃƒÅ¡NG**.

## 7. Danh sách lỗi
| Mã lỗi | Mô tả | Trạng thái | Phase xử lý |
|---|---|---|---|
| E01 | Lỗi 500 trang /products/1 (cú pháp th:each) | FIXED | PHASE 1 |
| E02 | Lỗi 500 trang /admin/orders/detail/1 (thiếu orderDetails) | OPEN | PHASE 4 |
| E03 | Bỏ qua đăng nhập ở checkout | FIXED | PHASE 2 |
| E04 | Thiếu trang Login | FIXED | PHASE 1 |
| E05 | Lỗi UX/UI chưa có design system | FIXED | PHASE 3 |
| E06 | Lỗi 500 trang /admin/products (gọi product.price) | OPEN | PHASE 6 |
| E07 | Lỗi 500 ở /products/1 do shell phá hỏng template | TRÙNG | PHASE 3A |
| E08 | Lỗi 500 ở /admin/orders/detail/1 (SpEL orderDetails) | TRÙNG | PHASE 4 |
| E09 | Route /admin/products-crud trùng với /admin/products | OPEN | PHASE 6 |

## 8. Thực tế triển khai
- **PHASE 1**: Setup Security custom login.
- **PHASE 2**: Bảo mật route, Đăng ký, Cấu trúc UI.
- **PHASE 3A**: PricingService & Chi tiết SP.
- **PHASE 3B-3C**: Trang chủ & Danh sách SP.
- **PHASE 4A**: Giỏ hàng (Cart) & Hồ sơ (Profile) [ĐÃ HOÀN THÀNH].