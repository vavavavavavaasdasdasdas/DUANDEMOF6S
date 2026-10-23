# Chốt trước khi triển khai — giai đoạn 6

Nhánh `don-dep`. Bằng chứng thô: `C:/Users/ADMIN/AppData/Local/Temp/release-20261023/`. Chỉ dùng `shop_verify`, `shop_fresh`, `shopbangiay_thu`; không truy cập `shopbangiay`.

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| A — kiểm kê kiểu cột | INFORMATION_SCHEMA trên shop_verify | Đã lưu toàn bộ varchar/char/text trước và sau; `shop_verify-varchar-before.txt`, `a-varchar-after.txt` | PASS |
| A — mapping Unicode | @Nationalized trên các trường văn bản cần Unicode, giữ mã/enum/username/password/actor | Build 100 tests, 0 lỗi; không bật Unicode toàn cục vì JDBC không đọc NString từ VARCHAR cũ | PASS |
| A — migration 008 | Chạy hai lần, so COUNT cả bảng | Lần 1 chuyển cột còn lại; lần 2 chuyển 0; COUNT không đổi; `a-008-run-1.txt`, `a-008-run-2.txt` | PASS |
| A — Hibernate tạo schema mới | CREATE shop_fresh; JAR ddl-auto=update | 10 trường nghiệm thu có kiểu NVARCHAR; `a-fresh-types.txt`, `a-fresh-app.log` | PASS |
| A — chuỗi qua form | Nhập đúng “Nguyễn Thị Ánh - Đà Nẵng - Hủy đơn” ở sản phẩm, danh mục, chất liệu, khách, nhân viên, voucher, promotion, địa chỉ đơn, ghi chú hold, lý do kho | Cả 10: SQL đúng chuỗi, UNICODE ký tự thứ 5=7877, VARBINARY khớp UTF-16LE từng byte, HTML 200 và đúng chuỗi sau giải mã entity; `a-unicode-proof.json` | PASS |
| A — baseline SQL | Tạo lại shop_fresh, chạy shopgiay.sql bằng sqlcmd -b | Thành công; baseline không còn tự tạo/chuyển tới shopbangiay, không seed khách/sản phẩm/đơn hoặc hash giả | PASS |
| A — dọn database tạm | DROP shop_fresh sau form và baseline, SELECT sys.databases | Không còn shop_fresh; `a-drop-fresh-final.sql`, `a-fresh-removed.txt` | PASS |
| B — doanh thu | Đơn chuyển khoản PAID → CONFIRMED → PREPARING → SHIPPING → DELIVERY_FAILED; SELECT độc lập so HTML | Doanh thu 930000 → 700000, số đơn 3 → 2; Dashboard/Statistics khớp; Home bán chạy loại sản phẩm đơn thất bại; kho 10 → 9 → 10; `b-proof.json`, `b-paid-*.sql/.txt`, `b-failed-*.sql/.txt` | PASS |
| C — hạn chuyển khoản | Chưa chạy | Đang thực hiện | NOT TESTED |
| D — bản sao thật | Kết nối localhost:1433, shopbangiay_thu | Không có database này trên instance được cấu hình; “Cannot open database shopbangiay_thu”; đã hỏi vị trí bản sao | NOT TESTED |
| E — cấu hình/tài liệu | Chưa hoàn tất | Đang thực hiện | NOT TESTED |

## Các cột VARCHAR còn lại sau 008 trên shop_verify

Mỗi cột được giữ có chủ đích vì là mã/enum/username hoặc mật khẩu băm. Database mới cũng giữ các trường này tương thích với JDBC getString; các trường cần Unicode dùng @Nationalized.

| Cột | Lý do giữ |
|---|---|
| customers.customer_code | Mã khách |
| employees.employee_code | Mã nhân viên |
| inventory_logs.actor | Username của người thực hiện, không phải họ tên |
| invoices.invoice_code | Mã hóa đơn |
| orders.order_code | Mã đơn |
| orders.order_type | Enum kênh bán |
| orders.payment_method | Enum phương thức thanh toán |
| orders.payment_status | Enum thanh toán |
| orders.status | Enum trạng thái đơn |
| pos_held_orders.code | Mã giỏ chờ |
| pos_held_orders.payment_method | Enum thanh toán |
| pos_held_orders.voucher_code | Mã voucher |
| products.code | Mã sản phẩm |
| promotions.discount_type | Enum kiểu giảm |
| roles.name | Mã ROLE_ADMIN/ROLE_USER |
| users.password | BCrypt, dữ liệu kỹ thuật ASCII |
| users.username | Tên đăng nhập theo validation ASCII |
| vouchers.code | Mã voucher |
| vouchers.discount_type | Enum kiểu giảm |

Không còn char/text hoặc cột văn bản hiển thị chưa được chuyển trên shop_verify. Số dòng và index duy nhất/bộ lọc được giữ trong cùng transaction của 008. Lỗi cú pháp COLLATE trong bản thử đầu đã rollback, sửa và chạy lại thành công; thử Unicode toàn cục bị JDBC từ chối nên phương án cuối là @Nationalized, không phối hợp hai cách.
