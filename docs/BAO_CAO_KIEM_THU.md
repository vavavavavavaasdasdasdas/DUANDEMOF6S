# Báo cáo kiểm thử — đang thực hiện

Ngày 25/10/2026, nhánh hoan-thien-chat-lieu. Theo chỉ dẫn sau của người dùng, dùng **dữ liệu chính qua bản sao mới** từ file shopbangiay_release_20261023.bak đang có, thay vì backup shopbangiay_thu không tồn tại. DB mới: **shopbangiay_kt_20261025**; không ghi đè shopbangiay_kt có sẵn. Không kết nối shopbangiay hoặc shopbangiay_thu. Chỉ đọc metadata master/msdb và file backup khi chuẩn bị restore.

## Tổng hợp hiện tại

| Nhóm | PASS | FAIL | NOT TESTED |
|---|---:|---:|---:|
| Schema, migration, test và phục hồi | 7 | 0 | 0 |
| Playwright chức năng, bảo mật, luật kho và hiệu năng | 0 | 0 | 90 |

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

Chưa đo; cả 8 trang yêu cầu đang NOT TESTED. Chưa sửa tối ưu truy vấn hoặc gán số ước lượng.

## Migration triển khai

Thứ tự: 004_anh_san_pham.sql → 005_chat_lieu.sql → 006_luat_ton_kho.sql → 007_tieng_viet_unicode.sql → 008_nvarchar_con_lai.sql → 009_dia_chi_nhan_hang.sql. Runner: database/migrations/chay_tat_ca.sql, sqlcmd -b -I -d <DB_đích> -i <file>. Script không tự chuyển DB; guard giữ xác nhận session context khi triển khai DB thật. **Chưa chạy bất kỳ migration/lệnh nào trên shopbangiay hoặc shopbangiay_thu.**

Đang chuyển sang nhóm chức năng. Sau mỗi nhóm phá dữ liệu sẽ dừng app, restore checkpoint sạch của bản sao do audit tạo, chạy lại schema/migration nếu cần và xác minh. Cuối toàn đợt sẽ kiểm tra không còn fixture phát sinh.
