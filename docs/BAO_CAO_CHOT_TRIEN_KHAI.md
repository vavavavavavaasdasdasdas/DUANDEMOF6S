# Chốt trước khi triển khai — giai đoạn 6

Nhánh `don-dep`. Bằng chứng thô: `C:/Users/ADMIN/AppData/Local/Temp/release-20261023/`. Migration và kiểm thử chỉ dùng `shop_verify`, `shop_fresh`, `shopbangiay_thu`. Theo quyền bạn cấp bổ sung, đã BACKUP COPY_ONLY `shopbangiay` để dựng bản sao; không sửa dữ liệu/schema nguồn.

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
| C — hạn chuyển khoản | Tạo đơn chuyển khoản, SELECT DATEADD(hour,24,created_at), GET success/detail User và detail Admin | Cả 3 HTTP 200, nhãn đúng `21:44 24/10/2026`; sau PAID ẩn; Admin đã xác nhận không hứa tự hủy vì job chỉ xử lý PENDING; `c-proof.json`, `c-deadline-sql.sql/.txt` | PASS |
| D — backup/restore | BACKUP COPY_ONLY CHECKSUM, RESTORE VERIFYONLY, RESTORE WITH MOVE | 1193 pages; “The backup set on file 1 is valid”; restore thành shopbangiay_thu mới; `d-backup.txt`, `d-backup-verify.txt`, `d-restore.txt` | PASS |
| D — migration bản sao | sqlcmd -I -b, thứ tự 004–008; COUNT_BIG từng bảng trước/sau | Cả 5 exit 0; mọi bảng cũ giữ nguyên COUNT; thêm 8 chất liệu, 6 ảnh; `d-count-before.json`, `d-count-after.json`, `d-preflight-*.txt` | PASS |
| D — COD và crawl | JAR profile verify ddl-auto=none trên shopbangiay_thu; đặt → xác nhận → hủy; 15 URL User + 25 Admin | Đơn 675: tồn giữ nguyên khi đặt, giảm 1 khi xác nhận, hoàn 1 khi hủy; có InventoryLog; 40 URL không 4xx/5xx, thêm 5 trang chi tiết/thống kê 200; `d-flow-proof.json`, `d-cod-*.sql/.txt` | PASS |
| E — cấu hình/tài liệu | Biến môi trường bắt buộc, cấu hình mẫu, gitignore, chuyển báo cáo | Đã sửa; kiểm tra build cuối đang thực hiện | NOT TESTED |

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
