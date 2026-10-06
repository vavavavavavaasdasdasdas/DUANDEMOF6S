## 7. Danh sách lỗi
| Mã lỗi | Mô tả | Trạng thái | Phase xử lý |
|---|---|---|---|
| E01 | Lỗi 500 trang `/products/1` (cú pháp th:each) | FIXED | PHASE 1 |
| E02 | Lỗi 500 trang `/admin/orders/detail/1` (thiếu orderDetails) | OPEN | PHASE 4 |
| E03 | Bỏ qua đăng nhập ở checkout | OPEN | PHASE 2 |
| E04 | Thiếu trang Login | FIXED | PHASE 1 |
| E05 | Lỗi UX/UI chưa có design system | OPEN | PHASE 3 |
| E06 | Lỗi 500 trang `/admin/products` (gọi product.price) | OPEN | PHASE 6 |
| E07 | Lỗi 500 ở `/products/1` do shell phá hỏng template | TRÙNG (E01) | PHASE 3A |
| E08 | Lỗi 500 ở `/admin/orders/detail/1` (SpEL orderDetails) | TRÙNG (E02) | PHASE 4 |
| E09 | Route /admin/products-crud trùng với /admin/products | OPEN | PHASE 6 |
