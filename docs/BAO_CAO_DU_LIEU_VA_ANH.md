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

Đã triển khai trên nhánh hiện tại. Migration 004 giữ `products.image_url` làm ảnh chính tương thích các query hiện có; bảng ảnh mới dùng `duong_dan NVARCHAR(2048)`, FK ON DELETE CASCADE, unique filtered index chỉ một ảnh chính/sản phẩm. Decoder WEBP dùng [TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys), không chỉ kiểm tra đuôi file.

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| Tạo 4 ảnh qua form thật | Playwright chọn 4 PNG, xem trước, submit; SELECT product_images | Product 466, image 28–31; POST 302; 4 dòng đúng thứ tự, ảnh 28 chính | PASS |
| Đổi thứ tự/ảnh chính/xóa một ảnh | Multipart POST edit có CSRF, SELECT trước/sau và kiểm tra đĩa | 31→30→28, ảnh 30 chính; image 29/file bị xóa; POST 302 | PASS |
| Nút lên/xuống và đặt ảnh chính | Playwright trên edit/466, lưu lại và SELECT | order [30,31,28]→[31,30,28], primary existing:31; view/466; errors=[] | PASS |
| Confirm xóa ảnh | Playwright click Xóa ảnh, dialog thực tế | “Bạn muốn xóa ảnh này? Ảnh chỉ bị xóa khi bạn lưu sản phẩm.”; hủy, giữ 3 ảnh | PASS |
| .exe giả .jpg | POST fake.jpg chứa MZ với MIME image/jpeg | HTTP 200 form có “Nội dung file”; COUNT products/images không đổi | PASS |
| 6 MB | POST large.png 6 MB | HTTP 200 form có “vượt dung lượng”; COUNT không đổi | PASS |
| 9 ảnh | POST 9 PNG | HTTP 200 form có “tối đa 8”; COUNT không đổi | PASS |
| WEBP hợp lệ | POST valid.webp; browser naturalWidth | POST 302, product 470, ảnh WEBP load=true | PASS |
| Active cần ảnh; ID ảnh thuộc sản phẩm; rollback file | ProductGalleryIntegrationTest | 4 test, 0 failure/error; CatalogIntegrationTest 12 test, 0 failure/error | PASS |
| Migration ảnh cũ/idempotent | Fixture legacy 521; chạy 004 hai lần | Cả hai COMMIT shop_verify image_count=5; legacy có đúng 1 ảnh, thứ tự 0/chính 1 | PASS |
| Cột chữ mới NVARCHAR | INFORMATION_SCHEMA.COLUMNS | nvarchar 2048 | PASS |
| Mũi tên, thumbnail, bàn phím | Playwright browser-gallery.js | Ảnh 2/4→1/4→4/4→1/4, errors=[] | PASS |
| Vuốt mobile | CDP Input.dispatchTouchEvent trên Chromium | Ảnh 2/3→3/3, errors=[] | PASS |
| Một ảnh ẩn controls | /products/470 trong browser | arrows=0, thumbnails=0, WEBP load=true | PASS |
| Responsive gallery | 1280px và 375px, scrollWidth đo trực tiếp | 1265≤1280; 360≤375; đã chụp và xem ảnh | PASS |
| Ảnh chính các nơi khác | HTTP body kiểm tra URL ảnh 30 | Home/List/Detail/Admin list/POS/Cart/Checkout/Invoice đều 200 và có URL | PASS |
| Chi tiết đơn online | User tạo đơn 386, GET /orders/386 | POST checkout 302; detail 200, có URL ảnh chính | PASS |
| Xóa sản phẩm không tham chiếu | Product 471, POST delete, SELECT và file.exists | POST 302; product=0, images=0, file=False | PASS |
| POST upload không CSRF | Admin POST save không token | 403 | PASS |
| User upload | User POST save có CSRF | 403 | PASS |
| Quét biểu thức template rỗng | rg mẫu $,*,@,# rỗng trong templates | 0 kết quả | PASS |
| Toàn bộ mvnw clean test | JDK 17, datasource shop_verify/profile verify | 80 test, 1 failure, 3 error, 0 skipped; BUILD FAILURE | FAIL |

SQL đối chiếu bộ ảnh (output đầy đủ: `b-four-before.txt`, `b-four-after.txt`, `b-ui-after.txt`):

```sql
SELECT p.id,p.image_url,i.id image_id,i.duong_dan,i.thu_tu,i.la_anh_chinh
FROM products p JOIN product_images i ON i.product_id=p.id
WHERE p.id=466 ORDER BY i.thu_tu;
```

```text
Sau tạo 4 ảnh: image_id thu_tu la_anh_chinh
28 0 1
29 1 0
30 2 0
31 3 0
Sau sửa/xóa bằng HTTP:
31 0 0
30 1 1
28 2 0
products.image_url=/uploads/products/c097fbfc-4929-49dc-9f25-20a40d543f8a.png
Removed file absent: /uploads/products/0f93c1eb-66df-4d5c-b7e8-490cf27f58a5.png
Sau thao tác UI cuối:
31 0 1
30 1 0
28 2 0
products.image_url=/uploads/products/fdacb170-539d-482d-b65d-4e05809ecd53.png
```

SQL xóa sản phẩm không tham chiếu:

```sql
SELECT COUNT(*) FROM products WHERE id=471;
SELECT COUNT(*) FROM product_images WHERE product_id=471;
```

Trước: 1/1; sau: 0/0. File `d5f4e024-8a37-4497-802d-6ae49c0f876f.png` không còn. Giữ nguyên quy tắc cũ: sản phẩm có đơn/variant/lịch sử chuyển ngừng bán, giữ ảnh để bảo toàn dữ liệu; không tuyên bố xóa cứng trường hợp đó.

Ảnh chính được đồng bộ khi sửa với các query sản phẩm hiện có. Order Detail/Invoice dùng getter an toàn; Checkout có map variantImages. Ảnh thay thế chung là `/anh/anh-san-pham-thay-the.svg`. File mới UUID, CREATE_NEW, giới hạn 5 MB và 16 triệu pixel; ImageIO đọc nội dung thật; xóa file sau commit, file mới được dọn khi rollback. Kiểm thử bộ sưu tập có chặn ID ảnh của sản phẩm khác.

SQL và HTTP đơn thử để kiểm tra các nơi sử dụng ảnh:

```sql
SELECT COUNT(*) FROM orders;
SELECT COUNT(*) FROM invoices;
SELECT quantity FROM product_variants WHERE id=332;
```

```text
POS trước: orders=0 invoices=0 quantity=10
POST /admin/pos/checkout:302 -> /admin/pos/success/356
POS sau: orders=1 invoices=1 quantity=9
GET /admin/invoices/161:200, có ảnh chính
Online trước: orders=1 invoices=1 quantity=9
POST /checkout/place:302 -> /orders/success/386
Online sau: orders=2 invoices=2 quantity=8
DH000386 total_amount=150000.00 final_amount=180000.00
GET /orders/386:200, có ảnh chính
```

Tổng online 150.000 + phí vận chuyển cấu hình 30.000 = 180.000 đ. Chỉ đơn thử trên shop_verify. Raw `b-pos-before/after.txt`, `b-online-before/after.txt`, `security-cross-output.txt`, `final-extra-output.txt`; record HTTP trong `security-cross.json`, `final-extra.json` (không commit dữ liệu phiên).

## Bảo mật — 8 kịch bản chạy thật

Các nhóm dưới đây xác định rõ endpoint và kết quả, không suy ra PASS từ code:

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| 1. Anonymous vào Admin | GET /admin không cookie | 302 Location /login | PASS |
| 2. Admin đăng nhập/vào dashboard | Login CSRF admin/admin123 rồi GET /admin | POST 302, GET 200 | PASS |
| 3. Mật khẩu sai | Login password sai với CSRF | 302 Location /login?error | PASS |
| 4. User vào Admin | User img_user_verify GET /admin | 403 | PASS |
| 5. User gọi upload | POST /admin/products-crud/save có token User | 403 | PASS |
| 6. Thiếu CSRF | Admin POST /admin/products-crud/save không token | 403 | PASS |
| 7. Truy cập đơn không sở hữu | User GET /orders/356 và /orders/success/356 | 403,403 | PASS |
| 8. Logout và truy cập lại | POST /logout có token, GET /admin | 302 /login?logout,302 /login | PASS |

User thử được đăng ký mới bằng form vì dữ liệu khách cũ đã dọn. Không đổi mật khẩu admin thật. Đây là 8 nhóm kiểm tra trên endpoint thật, không thay thế kiểm toán toàn hệ thống trước đây.

## Vòng sửa và giới hạn kiểm chứng

Phát hiện khi mở trang có ảnh: Detail/Edit ban đầu 500 do Thymeleaf cấm bean reference trong thuộc tính URL ảnh. Đã thay bằng getter/model, thêm test render có 4 ảnh; rebuild, restart verify và chạy lại GET Detail/Edit 200, tạo/sửa/xóa/upload lỗi, gallery browser, các nơi dùng ảnh, CSRF/role. Không nới bộ lọc bảo mật Thymeleaf.

Output cuối `b-clean-test.log`:

```text
Tests run: 80, Failures: 1, Errors: 3, Skipped: 0
PeopleIntegrationTest.employeeInsertFailureRollsBackTheAlreadyInsertedUser:117
 Expected DataAccessException, but nothing was thrown.
UserUiIntegrationTest.historyAndOwnedDetailUseSharedLayoutAndResponsiveTables:23
 EmptyResultDataAccess: expected 1, actual 0
UserUiIntegrationTest.orderDetailAndSuccessRejectOtherCustomersAndMissingIds:30
 EmptyResultDataAccess: expected 1, actual 0
StatisticsIntegrationTest.metricsRankingsBoundariesChannelsAndStatusesUseSnapshotMoney:40
 Index 1 out of bounds for length 1
BUILD FAILURE, Total time:47.270 s
```

People test giả định insert tên dài gây lỗi DB; hai UserUi test đòi đơn của username `user` có sẵn; Statistics test lấy 2 variant thuộc 2 sản phẩm có sẵn. Sau dọn mẫu chỉ có một sản phẩm được thêm variant kiểm thử, nên Statistics thiếu fixture. Không sửa các hành vi nghiệp vụ ngoài A/B và không che failure. CatalogIntegrationTest 12/12, ProductGalleryIntegrationTest 4/4, DataInitializerTest 1/1 PASS trong lần clean test này.

Chưa kiểm tra browser các trang trống sau A; chưa mô phỏng filesystem từ chối xóa file; chưa test cảm ứng trên thiết bị vật lý. Các mục đó NOT TESTED, không suy ra từ desktop Chromium. Fixture ảnh được tạo phục vụ test, không phải ảnh sản phẩm thật. Cột `products.name/description` cũ của shop_verify là VARCHAR, tên có dấu từ form đã thành `?`; ghi nhận lỗi cũ, không đổi schema này trong B. Cột chữ mới của B là NVARCHAR đã SELECT xác nhận.

Ảnh bằng chứng ngoài project:

- `C:/Users/ADMIN/AppData/Local/Temp/ab-20261009/admin-four-preview.png`
- `C:/Users/ADMIN/AppData/Local/Temp/ab-20261009/gallery-final-1280.png`
- `C:/Users/ADMIN/AppData/Local/Temp/ab-20261009/gallery-final-375.png`

App chạy nền PID 35932 tại 8081; log `b-final-app.log`: `Started DemoDatnApplication in 12.385 seconds`. Profile verify, datasource shop_verify, ddl-auto=none. Không chạy 8080 hoặc kết nối database thật. `application.properties` giữ hash trước/sau A/B: `10CDAC630222A7CC7E729FD0AAB18E58A5EDA8D2950C953FFE5D7620AC2414E1` (file đã có sửa từ trước nhiệm vụ, không tuyên bố Git sạch). Các thay đổi chưa commit có sẵn ngoài A/B được giữ nguyên.

## Lệnh dành cho bạn sau khi sao lưu database thật

Các lệnh dưới đây chỉ là hướng dẫn, tôi KHÔNG chạy trên shopbangiay. Sao lưu database và thư mục ảnh upload riêng trước khi dùng. 003 xóa toàn bộ dữ liệu bán hàng/khách/sản phẩm, không chỉ bản ghi mang nhãn test; chỉ chạy nếu bạn muốn đưa các bảng đó về trống. Nếu cần giữ sản phẩm hiện tại và chỉ chuyển ảnh, chạy riêng 004.

PowerShell, từ thư mục gốc project; dùng SQL login của bạn:

```powershell
$env:SQLCMDPASSWORD = 'MAT_KHAU_SQL_CUA_BAN'
sqlcmd -S localhost,1433 -U TEN_DANG_NHAP_SQL -C -I -b -d shopbangiay -i .\database\migrations\003_don_du_lieu_kiem_thu.sql
if ($LASTEXITCODE -ne 0) { throw 'Migration 003 lỗi, dừng tại đây.' }
sqlcmd -S localhost,1433 -U TEN_DANG_NHAP_SQL -C -I -b -d shopbangiay -i .\database\migrations\004_anh_san_pham.sql
if ($LASTEXITCODE -ne 0) { throw 'Migration 004 lỗi.' }
Remove-Item Env:SQLCMDPASSWORD
```

Với Windows Authentication, thay `-U TEN_DANG_NHAP_SQL` bằng `-E`, không cần SQLCMDPASSWORD. Không chạy khi app còn ghi dữ liệu. Sau script 003, chuyển ảnh của sản phẩm vừa xóa vào `_cho_xoa` theo danh sách đã lưu trước đó; bản thử hiện tại có 0 ảnh cũ để chuyển. Migration SQL không tự xóa file trên đĩa.

Đã dừng riêng app verify PID 35932 và đóng phiên browser kiểm thử khi kết thúc. Đã giữ shop_verify và backup để bạn đối chiếu. Không merge. A commit `6f70014`; B commit riêng chứa phần ảnh và cập nhật báo cáo này.
