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
| E — cấu hình/tài liệu | Biến môi trường bắt buộc, cấu hình mẫu, gitignore, chuyển báo cáo | Mật khẩu gốc đã bỏ; application-example.properties, README, hướng dẫn triển khai; VERIFICATION_REPORT.md chuyển vào docs; lịch sử Git vẫn có mật khẩu cũ, cần thay mới | PASS |

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

## Lệnh và output thô tiêu biểu

Mỗi file SQL bằng chứng ngoài repository có `IF DB_NAME()<>N'<database thử>' THROW ...` đứng đầu. Chạy bằng `sqlcmd -I -S localhost,1433 -U <tài khoản> -C -b -d <database thử> -f 65001 -i <file.sql>`, mật khẩu qua SQLCMDPASSWORD.

```sql
SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE DATA_TYPE IN('varchar','char','text') ORDER BY TABLE_NAME,ORDINAL_POSITION;
SELECT name,UNICODE(SUBSTRING(name,5,1)),CONVERT(VARBINARY(MAX),name)
FROM products WHERE name=N'Nguyễn Thị Ánh - Đà Nẵng - Hủy đơn';
```

Output form trên shop_fresh (cả 10 trường trong a-unicode-proof.json cùng kết quả):

```text
type=nvarchar; htmlStatus=200
value=Nguyễn Thị Ánh - Đà Nẵng - Hủy đơn
unicode_fifth=7877
binary_value=4E00670075007900C51E6E00200054006800CB1E2000C1006E00680020002D0020001001E00020004E00B51E6E00670020002D0020004800E71E790020001101A1016E00
```

Doanh thu độc lập so trang, b-paid-sql.sql/b-failed-sql.sql:

```sql
SELECT COALESCE(SUM(final_amount),0) revenue,COUNT_BIG(*) orders FROM orders
WHERE payment_status='PAID' AND status NOT IN('CANCELLED','DELIVERY_FAILED')
AND CAST(created_at AS date)=CAST(SYSDATETIME() AS date);
```

```text
Trước: SQL 930000 / 3; Statistics 930000 / 3; Dashboard 930000
Sau:   SQL 700000 / 2; Statistics 700000 / 2; Dashboard 700000
Đơn 5657: final_amount=230000; kho 10 → 9 → 10; Home sold 1 → 0
```

Điều kiện dùng chung: `repository/donhang/RevenueCondition.java:5`. Dashboard, Statistics, aggregate bán chạy của Home đều dùng constant này. Hạn chuyển khoản: `service/donhang/BankTransferDeadline.java:18`, `:27`; fragment `templates/fragments/han-chuyen-khoan.html:2`. Entity ví dụ `entity/sanpham/Product.java:20`; cấu hình datasource chỉ môi trường tại `application.properties:3–5`.

### Bản sao thật

Quyền bổ sung của bạn: “Cho phép BACKUP shopbangiay và restore sang shopbangiay_thu; không sửa dữ liệu nguồn”. Backup COPY_ONLY CHECKSUM lưu tại:

`C:\Program Files\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQL\Backup\shopbangiay_release_20261023.bak`

```text
BACKUP DATABASE successfully processed 1193 pages
The backup set on file 1 is valid.
RESTORE DATABASE successfully processed 1193 pages
```

COUNT_BIG trước/sau migration (chưa tính dữ liệu mới của luồng diễn tập):

| Bảng | Trước | Sau |
|---|---:|---:|
| categories | 6 | 6 |
| customers | 14 | 14 |
| employees | 5 | 5 |
| inventory_logs | 63 | 63 |
| invoices | 28 | 28 |
| order_details | 35 | 35 |
| orders | 28 | 28 |
| pos_held_items | 0 | 0 |
| pos_held_orders | 0 | 0 |
| product_variants | 20 | 20 |
| products | 10 | 10 |
| promotion_details | 7 | 7 |
| promotions | 5 | 5 |
| roles | 2 | 2 |
| sizes | 8 | 8 |
| users | 12 | 12 |
| vouchers | 15 | 15 |
| chat_lieu | chưa có | 8 |
| product_images | chưa có | 6 |

Trước migration: chưa có chat_lieu, product_images, material snapshot, cờ kho. Sau: các bảng/cột đủ, 008 đổi 8 cột VARCHAR còn thiếu trên bản sao. Query COUNT_BIG cho từng bảng, cùng kiểm kê đầy đủ, nằm trong `database/kiem_tra_trien_khai.sql` và d-preflight-before/after.sql/.txt.

```sql
SELECT id,quantity FROM product_variants WHERE id=3;
SELECT id,status,payment_method,da_tru_kho FROM orders WHERE id=675;
SELECT * FROM inventory_logs WHERE variant_id=3 ORDER BY id;
```

```text
Trước: quantity=18
Đặt: 675 PENDING COD 0; quantity=18
Xác nhận: 675 CONFIRMED COD 1; quantity=17
Log 833: quantity_change=-1, before=18, after=17, Trừ kho đơn DH000675 · ONLINE / COD
Hủy: 675 CANCELLED COD 0; quantity=18
Log 834: quantity_change=1, before=17, after=18, Hoàn kho đơn DH000675 · hủy hoặc giao hàng thất bại
```

Script test đầu bị lỗi tên cột `delta` (schema thật dùng quantity_change); sửa script test rồi chạy lại luồng đầy đủ, không sửa ứng dụng vì lỗi script. Đơn thử đầu 674 đang PENDING COD, chưa trừ kho; dữ liệu diễn tập giữ trên bản sao. Status 40 URL được liệt kê từng dòng trong d-flow-proof.json và http.json, bản tóm tắt sẽ nằm dưới.

Guard migration đã xử lý NULL bằng COALESCE; nếu không đặt SESSION_CONTEXT thì shopbangiay bị chặn. Bảng chân trị d-guard-truth-table.txt: ba database thử ALLOW; shopbangiay/NULL BLOCK; shopbangiay/1 ALLOW; database khác/1 BLOCK. Không chạy kiểm tra guard bằng cách thực thi migration trên nguồn thật.

### Phạm vi và dữ liệu còn lại

- Không sửa schema/dữ liệu shopbangiay; chỉ BACKUP COPY_ONLY đã được cấp quyền. Giữ backup và shopbangiay_thu để bạn đối chiếu.
- shop_fresh đã DROP sau nghiệm thu form và baseline. shop_verify giữ dữ liệu test.
- Báo cáo hiện có chuyển vào docs; không khôi phục/xóa thêm các báo cáo đã bị xóa từ trước phiên này.
- Không merge. Các thay đổi có sẵn ngoài nhóm A–E vẫn giữ nguyên trong working tree, không gom vào commit cuối.
- Mật khẩu SQL từng được track: file hiện tại đã dùng biến môi trường; lịch sử Git chưa viết lại. Bạn cần đổi mật khẩu cũ và thiết lập biến môi trường trước chạy.
- Ảnh/chứng chỉ/hiệu năng và các yêu cầu UI ngoài phạm vi giai đoạn 6 không nghiệm thu lại: NOT TESTED.

## Crawl bản sao: 15 URL User và 25 URL Admin

| Vai trò | URL | HTTP |
|---|---|---:|
| copyuser | `/` | 200 |
| copyuser | `/products` | 200 |
| copyuser | `/products/2` | 200 |
| copyuser | `/category/1` | 200 |
| copyuser | `/search?q=giay` | 200 |
| copyuser | `/cart` | 200 |
| copyuser | `/checkout` | 302 |
| copyuser | `/orders` | 200 |
| copyuser | `/orders/675` | 200 |
| copyuser | `/orders/success/675` | 200 |
| copyuser | `/profile` | 200 |
| copyuser | `/policy` | 200 |
| copyuser | `/login` | 200 |
| copyuser | `/register` | 200 |
| copyuser | `/products?page=99999` | 200 |
| copyadmin | `/admin` | 200 |
| copyadmin | `/admin/dashboard` | 200 |
| copyadmin | `/admin/products` | 302 |
| copyadmin | `/admin/products-crud` | 200 |
| copyadmin | `/admin/products-crud/create` | 200 |
| copyadmin | `/admin/products-crud/view/2` | 200 |
| copyadmin | `/admin/products-crud/edit/2` | 200 |
| copyadmin | `/admin/products-crud/2/variants` | 200 |
| copyadmin | `/admin/variants` | 200 |
| copyadmin | `/admin/variants/create` | 200 |
| copyadmin | `/admin/categories` | 200 |
| copyadmin | `/admin/sizes` | 200 |
| copyadmin | `/admin/materials` | 200 |
| copyadmin | `/admin/inventory` | 200 |
| copyadmin | `/admin/inventory/adjust/3` | 200 |
| copyadmin | `/admin/customers` | 200 |
| copyadmin | `/admin/customers/detail/100` | 200 |
| copyadmin | `/admin/employees` | 200 |
| copyadmin | `/admin/employees/detail/1` | 200 |
| copyadmin | `/admin/vouchers` | 200 |
| copyadmin | `/admin/promotions` | 200 |
| copyadmin | `/admin/promotions/1/products` | 200 |
| copyadmin | `/admin/orders` | 200 |
| copyadmin | `/admin/invoices` | 200 |
| copyadmin | `/admin/pos` | 200 |

`/admin/products` trả 302 sang `/admin/products-crud` theo controller hiện có; đích 200. Trang checkout giỏ rỗng redirect về cart, cart 200; không coi redirect nghiệp vụ là lỗi. Các Location và body nằm trong http.json/HTML tương ứng.

## Nghiệm thu cuối

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| 1 Anonymous | curl.exe, session cookie, CSRF khi hợp lệ | GET /login → 200; GET /orders → 302; GET /admin → 302 | PASS |
| 2 Wrong password | curl.exe, session cookie, CSRF khi hợp lệ | POST /login → 302 | PASS |
| 3 Admin login and dashboard | curl.exe, session cookie, CSRF khi hợp lệ | GET /admin → 200 | PASS |
| 4 User login and orders | curl.exe, session cookie, CSRF khi hợp lệ | GET /orders → 200 | PASS |
| 5 User forbidden admin | curl.exe, session cookie, CSRF khi hợp lệ | GET /admin → 403; GET /admin/products → 403; GET /admin/orders → 403; GET /admin/pos → 403; GET /admin/account → 403; GET /admin/statistics → 403; GET /admin/invoices → 403; GET /admin/employees → 403 | PASS |
| 6 Disabled blocks login and existing session | curl.exe, session cookie, CSRF khi hợp lệ | POST /login → 302; GET /orders → 302 | PASS |
| 7 Missing CSRF | curl.exe, session cookie, CSRF khi hợp lệ | POST /cart/add → 403; POST /admin/pos/checkout → 403 | PASS |
| 8 Logout session blocked | curl.exe, session cookie, CSRF khi hợp lệ | POST /logout → 302; GET /orders → 302 | PASS |
| Extra Ownership | curl.exe, session cookie, CSRF khi hợp lệ | GET /orders/4945 → 403; GET /orders/success/4945 → 403 | PASS |
| Build test | .\mvnw.cmd clean test, shop_verify, không skip | Tests run: 101, Failures: 0, Errors: 0, Skipped: 0; BUILD SUCCESS | PASS |
| Build package | .\mvnw.cmd clean package, shop_verify, không skip | Tests run: 101, Failures: 0, Errors: 0, Skipped: 0; BUILD SUCCESS | PASS |
| JAR cuối | java -jar target/DemoDATN-0.0.1-SNAPSHOT.jar --spring.profiles.active=verify --server.port=8081 | Khởi động được, 8 kịch bản chạy trên JAR vừa build | PASS |
| Cấu hình và template | final-audit.py, kiểm tra nội dung JAR và file hiện tại, git check-ignore | Không credential datasource literal; JAR dùng biến môi trường; biểu thức template rỗng=0; target/verify/uploads/log/local config được ignore | PASS |

```text
2026-10-23T22:07:23.850+07:00  INFO 26668 --- [           main] c.example.demodatn.DemoDatnApplication   : Started DemoDatnApplication in 16.479 seconds (process running for 17.549)
[INFO] Tests run: 101, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Bằng chứng cuối: final-security.json, final-disabled-before.sql/.txt, final-disabled-after.sql/.txt, final-audit.json, clean-test.log, clean-package.log, app.log. Trạng thái tài khoản tạm vô hiệu hóa đã được khôi phục trong finally. Truy cập đơn người khác kiểm tra cả chi tiết và success: HTTP 403.

Commits theo nhóm: A `4b57c98`, B `5595b80`, C `a7d9aba`, D `0102270`; E là commit cập nhật báo cáo này cùng cấu hình/tài liệu. Không merge. Ứng dụng kiểm thử chạy nền tại http://localhost:8081 với shop_verify; cấu hình chạy thật cần đủ ba biến môi trường datasource.
