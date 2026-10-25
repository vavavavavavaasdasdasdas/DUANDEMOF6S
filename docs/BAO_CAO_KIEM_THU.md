# Báo cáo kiểm thử — đang thực hiện

Ngày 25/10/2026, nhánh hoan-thien-chat-lieu. Theo chỉ dẫn sau của người dùng, dùng **dữ liệu chính qua bản sao mới** từ file shopbangiay_release_20261023.bak đang có, thay vì backup shopbangiay_thu không tồn tại. DB mới: **shopbangiay_kt_20261025**; không ghi đè shopbangiay_kt có sẵn. Không kết nối shopbangiay hoặc shopbangiay_thu. Chỉ đọc metadata master/msdb và file backup khi chuẩn bị restore.

## Tổng hợp hiện tại

| Nhóm | PASS | FAIL | NOT TESTED |
|---|---:|---:|---:|
| Schema, migration, test và phục hồi | 7 | 0 | 0 |
| Khách/đăng ký/đăng nhập — Playwright hai viewport | 19 | 0 | 0 |
| User/Admin/bảo mật/luật kho/hiệu năng còn lại | 0 | 0 | 71 |

File [cases.json](evidence/kiem-thu/cases.json) có từng Mã / Chức năng / Vai trò / Các bước / Kết quả / Bằng chứng. Chưa dùng các kết quả cũ để đánh PASS cho đợt này. Đây chưa phải báo cáo hoàn tất toàn bộ nhiệm vụ.

## Bảng lỗi

| Mã | Mô tả | File:dòng | Trạng thái |
|---|---|---|---|
| BUG-01 | DB thiếu bảng dia_chi_nhan_hang, gây checkout lỗi theo báo cáo người dùng | OrderController.java:81; SchemaCheck.java; migration 009 | Đã bổ sung runner 004–009 và bootstrap 009, chạy hai lần; checkout có giỏ trả 200 |
| BUG-02 | Không có preflight liệt kê đầy đủ schema thiếu trước khi phục vụ | config/SchemaCheck.java | Đã thêm kiểm tra chỉ đọc trước EntityManagerFactory; liệt kê tất cả bảng/cột thiếu và migration, ngăn startup khi thiếu |
| BUG-03 | Chạy lại 008 sau 009 có thể đổi so_dien_thoai từ VARCHAR sang NVARCHAR | migrations/008_nvarchar_con_lai.sql | Đã loại cột VARCHAR(20) theo đặc tả khỏi phép đổi Unicode; schema hai lần giống nhau |

Validate ban đầu dừng ở missing table chat_lieu. SchemaCheck liệt kê đủ: chat_lieu (005), dia_chi_nhan_hang (009), product_images (004), order_details.ten_chat_lieu (005), orders.da_tru_kho (006), product_variants.chat_lieu_id (005). Validate sau migration đã sạch, log Started app cổng 18100. Kiểm tra kiểu do Hibernate validate; SchemaCheck tập trung liệt kê phần thiếu.

## Bằng chứng thô nhóm schema

Trong [evidence/kiem-thu/schema](evidence/kiem-thu/schema): restore, validate trước/sau, log migration hai lần, COUNT/schema trước/sau, HTTP checkout, clean test và restore sau test. Các log preflight đầu tiên bị charset console Windows làm mất dấu trong thông báo; tên bảng/cột/migration vẫn rõ. JVM sau dùng UTF-8; dữ liệu Unicode ứng dụng chưa được đánh PASS ở nhóm chức năng.

```text
Schema validation: missing table [chat_lieu]
SCHEMA_CHECK: ... dbo.dia_chi_nhan_hang ... 009_dia_chi_nhan_hang.sql
Started DemoDatnApplication in 15.556 seconds
Tests run: 123, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

COUNT trước migration: 17 bảng; sau: 20 bảng (chat_lieu=8, product_images=6, dia_chi_nhan_hang=7). Schema và COUNT sau lần 1 == sau lần 2. Sau full suite đã restore checkpoint sạch của chính DB audit; COUNT/schema lại trùng checkpoint. Không giữ lại tác động của test job lên đơn/kho hay fixture thử.

## URL | vai trò | status

| URL | Vai trò | Status | Kết quả |
|---|---|---:|---|
| /login | User/Admin | 200 | PASS |
| POST /login | User/Admin | 302 | PASS |
| /products/2 | User | 200 | PASS |
| POST /cart/add | User | 302 | PASS (chỉ giỏ session) |
| GET /checkout có giỏ | User | 200 | PASS, có combo địa chỉ |
| Các luồng Playwright và mọi route khác | Các vai trò | — | NOT TESTED trong đợt mới |

## SQL mỗi trang trước/sau

Đo bằng proxy DataSource đếm prepareStatement, qua MockMvc render đủ Thymeleaf và Spring Security, với 14 sản phẩm fixture bổ sung. Có tính cả JPA và JdbcTemplate.

| URL | Trước | Sau | Kết quả |
|---|---:|---:|---|
| / | 159 | 14 | PASS |
| /products | 63 | 8 | PASS |
| /products/949 | 22 | 22 | PASS |
| /category/1 | 41 | 9 | PASS |
| /cart (có một dòng) | 3 | 3 | PASS |
| /admin/pos | 50 | 9 | PASS |
| /admin/products-crud | 6 | 6 | PASS |
| /admin/inventory | 6 | 6 | PASS |

BUG-06: ProductFilterService truy vấn biến thể từng sản phẩm và PricingService truy vấn khuyến mãi mỗi biến thể; POS tính cùng giá hai lần. Đã nạp gộp theo danh sách ID và dùng lại bản đồ giá trong một lần render. HTML cả 8 trang giống từng ký tự sau khi thay riêng token CSRF và checkoutToken ngẫu nhiên bằng placeholder; sản phẩm/giá/thứ tự không đổi. Bằng chứng SQL, HTML đã bỏ token và phép so sánh: evidence/kiem-thu/performance. Mặc định show-sql=false, profile debug bật lại. Không thêm cache qua request.

## Migration triển khai

Thứ tự: 004_anh_san_pham.sql → 005_chat_lieu.sql → 006_luat_ton_kho.sql → 007_tieng_viet_unicode.sql → 008_nvarchar_con_lai.sql → 009_dia_chi_nhan_hang.sql. Runner: database/migrations/chay_tat_ca.sql, sqlcmd -b -I -d <DB_đích> -i <file>. Script không tự chuyển DB; guard giữ xác nhận session context khi triển khai DB thật. **Chưa chạy bất kỳ migration/lệnh nào trên shopbangiay hoặc shopbangiay_thu.**

Đang chuyển sang nhóm chức năng. Sau mỗi nhóm phá dữ liệu sẽ dừng app, restore checkpoint sạch của bản sao do audit tạo, chạy lại schema/migration nếu cần và xác minh. Cuối toàn đợt sẽ kiểm tra không còn fixture phát sinh.

## Nhóm Khách, mật khẩu và ngôn ngữ — hoàn tất

- 19 mục Khách chạy ở hai viewport: 38/38 PASS. Các FAIL do locator có icon và isDisabled trên option được lưu trong guest-results-first.json, đã xác minh DOM và chạy lại đúng 8 ca; không sửa ứng dụng theo lỗi script.
- BUG-04: form đăng ký chấp nhận password=123. Người dùng quyết định thống nhất ít nhất 8 ký tự, có chữ và số cho đăng ký/đổi mật khẩu User/Admin/tạo tài khoản nhân viên. PasswordPolicy dùng chung, giữ trần 72 byte UTF-8 của BCrypt. DTO và service/controller đều chặn; regression test gồm các entry point. Thêm hướng dẫn cạnh trường.
- BUG-05: nhãn SALE và Size được xác nhận hiển thị trong browser; đã Việt hóa GIẢM/Kích cỡ. Người dùng xác nhận giữ tên riêng/thương hiệu. Kiểm tra lại 10 trang ở hai viewport không còn những nhãn này, không tràn ngang. Các khu vực Admin/User khác vẫn chờ kiểm thử riêng.
- Clean test trên đúng bản commit nhóm: 127 run, 0 failures/errors/skipped; BUILD SUCCESS. Lần test mới đầu bị một assert JDBC đọc trước flush trong transaction test, đã sửa fixture kiểm tra bằng flush; không phải lỗi lưu mật khẩu production.
- Ledger ghi đúng hai tài khoản thử mới, tên tiếng Việt đọc lại đúng. Sau nhóm đã restore checkpoint sạch; schema và COUNT bằng sau migration, không giữ lại tài khoản/sản phẩm/khuyến mãi thử hoặc tác động test job.
- Bằng chứng: evidence/kiem-thu/guest-password. Nhóm User/Admin đang tiếp tục; chưa kết luận toàn bộ chức năng PASS.

## Nhóm User và bảo mật — hoàn tất

23 mục User ở hai viewport: 46/46 PASS. Hồ sơ/mật khẩu, giới hạn địa chỉ, mặc định, Unicode, giỏ, địa chỉ checkout, voucher hợp lệ và bảy lý do từ chối, COD/chuyển khoản, thành công, danh sách/chi tiết/hủy đơn và ownership đều có kết quả browser. SQL xác nhận sửa tạm tại checkout không sửa sổ địa chỉ.

Luật kho cho từng viewport: COD 20→20 khi đặt→19 khi xác nhận→20 khi hủy→20 khi hủy lại; chuyển khoản 20→19 khi đặt→20 khi hủy. Thiếu hàng không đổi đơn/voucher/log/kho. Hai request xác nhận đồng thời khi còn một đôi: đúng một đơn được xác nhận, một log trừ, kho bằng 0.

Ma trận GET và CSRF thiếu/sai mọi POST có 639 kết quả PASS; thêm 96 POST Admin có CSRF hợp lệ kiểm tra khách 302/User 403. Năm kiểm tra ownership địa chỉ/checkout trả 404, không đổi dữ liệu; đơn người khác trả 403. Không ghi nhận pageerror hoặc tràn ngang trong 46 ca User. Một lần mobile thiếu fixture stock do ca đồng thời trước đó đã bán hết; đã đặt lại tồn fixture và chạy lại đúng ca, không sửa ứng dụng theo lỗi chuẩn bị dữ liệu.

Bằng chứng: evidence/kiem-thu/user-security. Sau nhóm đã dừng app, restore checkpoint đúng DB audit, COUNT/schema bằng baseline. Nhóm Admin/POS và kiểm tra chất lượng toàn bộ vẫn đang chạy; chưa kết luận hoàn tất toàn đợt.
