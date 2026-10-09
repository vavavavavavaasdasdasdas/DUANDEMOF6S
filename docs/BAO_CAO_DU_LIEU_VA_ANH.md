# Dọn dữ liệu kiểm thử và ảnh nhiều góc

Nhánh `don-dep`. Chỉ SQL/app trên `shop_verify`; không chạy trên `shopbangiay`. Bằng chứng thô tại `C:/Users/ADMIN/AppData/Local/Temp/ab-20261009/`; app log tại thư mục temp `don-dep-20261008/`. Không commit cookie, mật khẩu hoặc upload kiểm thử.

## A — Dọn dữ liệu

Backup SQL Server thành công trước khi dọn: `BACKUP DATABASE shop_verify ... WITH COPY_ONLY,CHECKSUM`, 961 pages. File: `C:\Program Files\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQL\Backup\shop_verify_before_003_7815FB4A-C797-48E6-9A0F-D87A3729E85C.bak`. Lần thử backup vào Temp bị Access denied; đã dùng thư mục backup của SQL Server, không thay quyền filesystem.

Lệnh thực tế: `python <temp>/db.py database/migrations/003_don_du_lieu_kiem_thu.sql <temp>/a-after.txt`. Helper cố định `sqlcmd -d shop_verify`, kiểm tra `DB_NAME()` trước thực thi. SQL 003 không USE/CREATE database, một transaction, XACT_ABORT/TRY CATCH/ROLLBACK và kiểm tra cuối. Không reset identity.

| Bảng | COUNT trước | Sau script | Sau khởi động lần 2 |
|---|---:|---:|---:|
| products | 6 | 0 | 0 |
| product_variants | 10 | 0 | 0 |
| customers | 1 | 0 | 0 |
| orders | 6 | 0 | 0 |
| order_details | 8 | 0 | 0 |
| invoices | 6 | 0 | 0 |
| inventory_logs | 8 | 0 | 0 |
| promotion_details | 2 | 0 | 0 |
| pos_held_orders | 7 | 0 | 0 |
| pos_held_items | 7 | 0 | 0 |
| employees | 15 | 15 | 15 |
| users | 17 | 16 | 16 |
| roles | 2 | 2 | 2 |
| categories | 3 | 3 | 3 |
| sizes | 3 | 3 | 3 |
| vouchers | 1 | 1 | 1 |
| promotions | 1 | 1 | 1 |

Query COUNT từng bảng nằm trong `a-before.sql`; output `a-before.txt`, `a-after.txt`, `a-after-restart2.txt`. User đã xóa là ROLE_USER của khách bị xóa; admin/User của Employee giữ nguyên. Voucher giữ toàn bộ bản ghi, chỉ bỏ FK customer_id để xóa Customer; voucher riêng không trở thành công khai. SQL chỉ xác nhận admin có status/role/mật khẩu băm; xác nhận mật khẩu thật qua HTTP sau migration.

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| Transaction dọn/FK | SQL 003 và COUNT trước/sau | COMMIT shop_verify; 10 bảng mục tiêu=0 | PASS |
| Giữ dữ liệu nền/nhân viên | SQL EXCEPT snapshot cuối transaction | Không đổi bản ghi bảo vệ; số lượng như bảng trên | PASS |
| Admin đăng nhập | GET /login token, POST admin/admin123, GET /admin | 200,302,200 | PASS |
| Crawl User trống | /, /products, /search, /category/1, /cart, /checkout, /orders, /profile, /login, /register | 200,200,200,200,200,302,302,302,200,200; anon trang bảo vệ chuyển login | PASS |
| Crawl Admin trống | /admin, /admin/products, /admin/variants, /admin/inventory, /admin/customers, /admin/orders, /admin/invoices, /admin/pos, /admin/statistics, /admin/dashboard | 200,302,200,200,200,200,200,200,200,200; products redirect route CRUD | PASS |
| Không seed dữ liệu mẫu | Restart app hai lần profile verify, COUNT SQL | Sản phẩm/khách/đơn vẫn 0; cấu hình nền không tăng | PASS |
| Unit idempotence | DataInitializerTest.twoRunsCreateOnlyFoundationOnce | 1 test, 0 failure, 0 error; BUILD SUCCESS | PASS |
| Chuyển ảnh cũ | SELECT image_url WHERE LIKE /uploads/% và kiểm tra verify/uploads | Không URL ảnh upload; thư mục verify upload chưa có, 0 file cần chuyển | PASS |
| Empty state/biểu đồ trong trình duyệt | Chưa chạy browser | HTTP render được; chưa xác nhận console/ảnh | NOT TESTED |

DataInitializer mặc định không seed sản phẩm, khách, user mẫu hay đơn. Không tạo profile demo vì không cần dữ liệu demo trong phạm vi này. Admin mới vẫn gắn Employee để giữ invariant hiện có.

## B — Ảnh nhiều góc

Đang thực hiện; chưa ghi PASS khi chưa có bằng chứng.
