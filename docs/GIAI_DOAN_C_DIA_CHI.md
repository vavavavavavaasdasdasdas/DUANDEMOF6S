# Giai đoạn C — Sổ địa chỉ nhận hàng

Ngày 24/10/2026, nhánh `hoan-thien-chat-lieu`, nền `ce5fa3e`. Chỉ thực hiện C và dừng. Mọi SQL/HTTP/test của C dùng **shop_verify**. Không kết nối hoặc chạy lệnh trên shopbangiay. Migration 009 chạy hai lần trên DB test; không tự áp dụng lên DB thật.

## Bảng nghiệm thu

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| Migration 009 và bù dữ liệu | sqlcmd hai lần; SELECT schema/indexes/khách cũ 4949 | Trước chưa có bảng; lần 1 thêm 1 dòng, lần 2 thêm 0; tên/địa chỉ NVARCHAR, phone VARCHAR, slot TINYINT; CHECK 1..3, UNIQUE customer+slot, filtered UNIQUE mặc định | PASS |
| Thêm địa chỉ 1, 2, 3 | POST form kèm CSRF; SELECT customer 5220 trước/sau | 0 → 3 dòng; ba POST 302; slot 1/2/3, chỉ slot 1 mặc định | PASS |
| Địa chỉ thứ 4 ở service | POST hợp lệ khi có 3; SELECT lại | HTTP 200 báo “Mỗi khách chỉ lưu tối đa 3 địa chỉ”; vẫn 3 dòng | PASS |
| Địa chỉ thứ 4 ở DB | INSERT slot 4; test duplicate slot và default | Error 547 CHECK CK_dia_chi_thu_tu; UNIQUE chặn trùng slot và mặc định thứ hai | PASS |
| Hai request đồng thời | Hai phiên cùng khách 5222 có 2 địa chỉ; HTTP+SQL; test service song song | Một 302 thành công, một 200 giới hạn; 2 → 3 dòng; khóa ghi customer trong transaction | PASS |
| Đổi mặc định | POST /addresses/33/default; SELECT hai bảng | slot 2 mặc định; customers.address = Địa chỉ nhận hàng số 2 | PASS |
| Xóa mặc định | POST /addresses/33/delete; SELECT | slot 1 tự làm mặc định; customers.address về địa chỉ số 1; slot 2 tái sử dụng được | PASS |
| Xóa cuối và sửa mặc định | Test tích hợp transaction | Xóa cuối đặt customers.address NULL; sửa mặc định cập nhật trường cũ | PASS |
| Validation | Test tên/địa chỉ rỗng, địa chỉ quá dài, phone sai; phone +84 hợp lệ | Chặn dữ liệu sai, thông báo tiếng Việt cạnh ô; validate cả service | PASS |
| Hồ sơ User | Browser desktop/mobile | Combo có 3 lựa chọn và nhãn Mặc định; ẩn Thêm và báo đủ 3; xác nhận Xóa; hủy chuyển khi sửa chưa lưu giữ dữ liệu | PASS |
| Checkout từng địa chỉ | Browser chọn 3 option, Đặt hàng; SELECT orders | Đơn 7230/7231/7232 khớp đủ họ tên/SĐT/địa chỉ; chọn sẵn mặc định | PASS |
| Sửa tạm checkout | Browser sửa 3 ô, tạo đơn 7233; so sánh sổ trước/sau | Snapshot tạm đúng; toàn bộ sổ trước == sau | PASS |
| Chưa có địa chỉ | Test tích hợp checkout/profile | Có ô nhập và gợi ý lưu; personal update không thể ghi đè sổ bằng field address giả | PASS |
| Admin chỉ xem | GET /admin/customers/detail/5220 và test render | 200; đủ địa chỉ, nhãn mặc định; không có form sửa địa chỉ | PASS |
| Unicode | SQL đọc lại, so sánh chuỗi và UTF-16 | “Số 12, đường Nguyễn Huệ, Đà Nẵng” đúng từng ký tự | PASS |
| Crawl User/Admin | 23 GET cuối live-http.json | 23/23 HTTP 200, bảng bên dưới | PASS |
| 8 ca bảo mật | HTTP thật + test tích hợp | Đủ 8 ca đúng 302/403/404, bảng bên dưới | PASS |
| JS và mobile | Playwright console, viewport 390×844 | 0 errors, 0 warnings; document scrollWidth 375, không tràn ngang | PASS |
| Build cuối đúng mã sẽ commit | mvnw clean test trong export từ Git index riêng | 117 test; failures/errors/skipped đều 0; BUILD SUCCESS | PASS |
| Audit UI toàn repo (bổ sung) | premium audit strict | 90 findings: 20 unresolved + 70 violations; chưa đạt. Native select mới đã ghi trong DESIGN.md nhưng audit vẫn báo ownership; heuristic báo cả nút đóng thông báo/voucher có từ trước. Không coi audit này là PASS | FAIL |
| Migration trên DB thật | Không thực hiện theo yêu cầu | Chưa chạy | NOT TESTED |

## SQL trước/sau và output thô

Thư mục [evidence/phase-c](evidence/phase-c) chứa output đã loại cookie/mật khẩu/CSRF; tất cả khách và đơn bên dưới là dữ liệu thử nghiệm.

```text
migration-before.txt:
shop_verify NULL  -- OBJECT_ID bảng địa chỉ chưa tồn tại
4949 Nguyễn Đà Nẵng 0909499499 Số 12, đường Nguyễn Huệ, Đà Nẵng
migration-first.txt: (1 rows affected)
migration-repeat.txt: (0 rows affected)
migration-after.txt:
1 4949 Nguyễn Đà Nẵng 0909499499 Số 12, đường Nguyễn Huệ, Đà Nẵng 1 1
UQ_dia_chi_customer_slot 1 NULL
UX_dia_chi_customer_default 1 ([la_mac_dinh]=(1))
```

SQL dùng đối chiếu; output đầy đủ `sql-before-after.jsonl` gồm hai lần chạy harness, lần cuối dùng customer 5220 và 5222:

```sql
SELECT id,customer_id,ten_nguoi_nhan,so_dien_thoai,dia_chi,la_mac_dinh,thu_tu
FROM dbo.dia_chi_nhan_hang WHERE customer_id=5220 ORDER BY thu_tu;
SELECT id,address FROM dbo.customers WHERE id=5220;
SELECT id,customer_id,la_mac_dinh,thu_tu FROM dbo.dia_chi_nhan_hang
WHERE customer_id=5222 ORDER BY thu_tu;
```

| Mốc | Trước | Sau SELECT |
|---|---|---|
| Thêm 1/2/3 | Khách 5220: 0 dòng, address NULL | id 32/33/34; slot 1/2/3; default 1/0/0; address cũ = địa chỉ số 1 |
| Đổi mặc định | 32=true, 33=false | 32=false, 33=true; customers.address = Địa chỉ nhận hàng số 2 |
| Xóa mặc định 33 | 3 dòng, default 33 | 2 dòng, default 32; address cũ = Số 12, đường Nguyễn Huệ, Đà Nẵng |
| Tái sử dụng slot | slot 2 trống | id 35 ở slot 2, default vẫn 32 |
| Hai request đồng thời | Khách 5222: id 36/37, slot 1/2 | thêm đúng id 38 slot 3, không có dòng thứ 4 |

INSERT trực tiếp slot=4 với khách đã có 3 bị bắt lỗi (không thêm dòng), `sql-fourth-rejected.json`:

```text
error_number: 547
The INSERT statement conflicted with the CHECK constraint "CK_dia_chi_thu_tu".
The conflict occurred in database "shop_verify", table "dbo.dia_chi_nhan_hang", column 'thu_tu'.
```

Đơn thật từ browser (`browser-orders.json`, `order-sql-comparison.json`, `order-sql-output.txt`):

```sql
SELECT id,receiver_name,phone_number,shipping_address FROM dbo.orders
WHERE id IN (7230,7231,7232,7233) ORDER BY id;
```

| Order | Address ID | Cách nhập | SELECT orders (khớp form) | Kết quả |
|---|---|---|---|---|
| 7230 | 32 | Địa chỉ lưu | Người nhận số 1 / 0901234561 / Số 12, đường Nguyễn Huệ, Đà Nẵng | PASS |
| 7231 | 35 | Địa chỉ lưu | Người nhận số 4 / 0901234564 / Địa chỉ thay thế ở Huế | PASS |
| 7232 | 34 | Địa chỉ lưu | Người nhận số 3 / 0901234563 / Địa chỉ nhận hàng số 3 | PASS |
| 7233 | 32 | Sửa tạm | Người nhận tạm / 0908888999 / Địa chỉ tạm cho riêng đơn này | PASS |

```text
ADDRESS_BOOK_UNCHANGED after temporary checkout edit PASS
```

## Tám kịch bản bảo mật

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| 1. Chưa đăng nhập | GET /profile | 302 đến đăng nhập | PASS |
| 2. Thiếu CSRF | Chủ tài khoản POST lưu | 403, không ghi DB | PASS |
| 3. CSRF sai | Chủ tài khoản POST xóa | 403, không xóa | PASS |
| 4. Sửa của người khác | User khác POST save, id địa chỉ 32 | 404 | PASS |
| 5. Xóa của người khác | User khác POST /addresses/32/delete | 404 | PASS |
| 6. Đặt mặc định của người khác | User khác POST /addresses/32/default | 404 | PASS |
| 7. Xem form của người khác | User khác GET /profile?addressId=32 | 404 | PASS |
| 8. Giả ID checkout | User khác POST checkout shippingAddressId=32, field hợp lệ | 404, không tạo đơn | PASS |

## Crawl chính User/Admin

Base `http://localhost:18085`, tài khoản đúng vai trò. `live-http.json` có 78 request gồm đăng ký/đăng nhập, CRUD, bảo mật và crawl. Bảng 23 URL chính:

| URL | Status | Kết quả |
|---|---|---|
| `/` | 200 | PASS |
| `/products` | 200 | PASS |
| `/category/3127` | 200 | PASS |
| `/products?promotion=true` | 200 | PASS |
| `/products/8055` | 200 | PASS |
| `/cart` | 200 | PASS |
| `/profile` | 200 | PASS |
| `/checkout` | 200 | PASS |
| `/orders` | 200 | PASS |
| `/admin` | 200 | PASS |
| `/admin/products-crud` | 200 | PASS |
| `/admin/materials` | 200 | PASS |
| `/admin/variants` | 200 | PASS |
| `/admin/inventory` | 200 | PASS |
| `/admin/pos` | 200 | PASS |
| `/admin/orders` | 200 | PASS |
| `/admin/invoices` | 200 | PASS |
| `/admin/customers` | 200 | PASS |
| `/admin/employees` | 200 | PASS |
| `/admin/vouchers` | 200 | PASS |
| `/admin/promotions` | 200 | PASS |
| `/admin/statistics` | 200 | PASS |
| `/admin/customers/detail/5220` | 200 | PASS |

## Test cuối và triển khai

Bổ sung 8 test tích hợp SQL Server trong ShippingAddressIntegrationTest, bao phủ giới hạn/DB constraints/concurrency/default/validation/snapshot/ownership/CSRF. Giữ cấu trúc controller/service/repository/entity/dto. Khóa PESSIMISTIC_WRITE trên customer serialize các thao tác sổ; đổi mặc định flush cờ cũ trước cờ mới để phù hợp filtered unique index. customers.address theo địa chỉ mặc định; sửa thông tin cá nhân/admin cũ không ghi đè địa chỉ khi đã có sổ.

Export từ Git index riêng loại các chỉnh sửa ngoài phạm vi để chạy test cuối; không skip, ddl-auto=none. Output nguyên bản `commit-tests.log`, tóm tắt `test-summary.txt`:

```text
PHASE_C concurrency: before=2 requests=2 success=1 rejected=1 after=3
PHASE_C direct SQL: slot4 CHECK rejected; duplicate slot UNIQUE rejected; second default UNIQUE rejected
[INFO] Tests run: 117, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 28.300 s
[INFO] Finished at: 2026-10-24T13:25:30+07:00
```

Ảnh Playwright: [hồ sơ](evidence/phase-c/phase-c-profile-full.png), [mobile](evidence/phase-c/phase-c-profile-mobile.png), [checkout](evidence/phase-c/phase-c-checkout-default.png), [xác nhận xóa](evidence/phase-c/phase-c-delete-confirm.png), [sửa chưa lưu](evidence/phase-c/phase-c-unsaved-confirm.png), [đơn 7230](evidence/phase-c/phase-c-order-0.png), [đơn 7231](evidence/phase-c/phase-c-order-1.png), [đơn 7232](evidence/phase-c/phase-c-order-2.png), [đơn tạm 7233](evidence/phase-c/phase-c-order-3.png). Console và kiểm tra mobile: `browser-verification.txt`. File `ui-audit-final.json` ghi đầy đủ findings audit bổ sung.

## Lệnh migration và test

Từ thư mục DemoDATN, đặt mật khẩu bằng biến môi trường SQLCMDPASSWORD của phiên (không lưu trong source). Lệnh dưới chỉ chọn DB test. Không có migration nào được tự chạy trên DB thật.

```powershell
sqlcmd -S tcp:localhost,1433 -U <tai_khoan_SQL> -C -I -d shop_verify -b -f 65001 -i database/migrations/009_dia_chi_nhan_hang.sql
# Chạy lại cùng lệnh không nhân đôi dữ liệu.
$env:SPRING_DATASOURCE_URL='jdbc:sqlserver://localhost:1433;databaseName=shop_verify;encrypt=true;trustServerCertificate=true'
$env:SPRING_DATASOURCE_USERNAME='<tai_khoan_SQL>'
# Đặt SPRING_DATASOURCE_PASSWORD trong phiên, không ghi vào repository.
$env:SPRING_JPA_HIBERNATE_DDL_AUTO='none'
.\mvnw.cmd clean test
```

Một commit riêng cho C; các thay đổi ngoài phạm vi và báo cáo B giữ nguyên ở working tree. Không push. **Dừng sau C.**
