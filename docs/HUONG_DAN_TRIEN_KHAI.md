# Hướng dẫn triển khai và quay lại

## Điều kiện bắt đầu

Không triển khai khi diễn tập trên bản sao chưa đạt. Giai đoạn này đã kiểm tra `shop_verify` và schema mới `shop_fresh`; trạng thái diễn tập `shopbangiay_thu` được ghi rõ trong [báo cáo](BAO_CAO_CHOT_TRIEN_KHAI.md). Không dùng kết quả trên database test thay cho kết quả trên bản sao thật.

Các lệnh migration có `shopbangiay` dưới đây **chỉ là hướng dẫn để bạn tự chạy sau khi sao lưu**, không được agent chạy. Ngoại lệ bạn cấp riêng trong phiên này: BACKUP COPY_ONLY nguồn và restore thành shopbangiay_thu, không sửa dữ liệu nguồn. Dừng ứng dụng trước khi thay schema; cất JAR cũ, cấu hình môi trường, thư mục upload và bản sao lưu database cùng thời điểm.

## 1. Sao lưu và kiểm tra khả năng phục hồi

Chọn một đường dẫn mới mà tài khoản dịch vụ SQL Server có quyền ghi; không ghi đè bản backup cũ. Nhập mật khẩu SQL qua biến môi trường `SQLCMDPASSWORD` hoặc dùng Windows Authentication `-E`; không đưa mật khẩu vào Git/lịch sử lệnh.

```powershell
$taskSecret = Read-Host 'Mật khẩu SQL Server' -AsSecureString
$env:SQLCMDPASSWORD = [System.Net.NetworkCredential]::new('', $taskSecret).Password
sqlcmd -S 'localhost,1433' -U '<tai_khoan_SQL>' -C -I -b -d shopbangiay -Q "IF DB_NAME()<>N'shopbangiay' THROW 51000,N'Sai database',1; BACKUP DATABASE [shopbangiay] TO DISK=N'D:\SQLBackups\shopbangiay_truoc_trien_khai_YYYYMMDD_HHMM.bak' WITH COPY_ONLY,CHECKSUM; RESTORE VERIFYONLY FROM DISK=N'D:\SQLBackups\shopbangiay_truoc_trien_khai_YYYYMMDD_HHMM.bak' WITH CHECKSUM;"
if ($LASTEXITCODE -ne 0) { throw 'Sao lưu/kiểm tra thất bại; không chạy migration.' }
```

Restore backup đó thành **shopbangiay_thu**, dùng tên/file dữ liệu riêng và `WITH MOVE` theo `RESTORE FILELISTONLY`; không ghi đè file MDF/LDF đang dùng. Kiểm tra trên bản sao: migration, đăng nhập, crawl 40 URL, COD đặt → xác nhận → hủy và SQL kho trước/sau. Có thể dùng `lpc:localhost\MSSQLSERVER01` cho kết nối local Shared Memory nếu SQL Browser tắt; JDBC cần TCP/IP của đúng instance. Không đổi cổng/protocol của dịch vụ đang chạy thật chỉ để diễn tập.

## 2. Kiểm kê và thứ tự migration

`database/kiem_tra_trien_khai.sql` in bảng/cột cần có, kiểu văn bản và **COUNT_BIG thực tế của từng bảng**. Lưu output trước và sau; mọi bảng đang có phải không mất bản ghi. 004 có thể thêm dòng ảnh và 005 có thể thêm danh mục chất liệu, nên một số COUNT được phép tăng.

Thứ tự: **004 → 005 → 006 → 007 → 008**. Các script tự kiểm tra và chạy lại được; chỉ bỏ qua khi đã kiểm chứng schema và dữ liệu tương ứng. Không chạy lại các phase cũ sau 005 vì unique `(product,size)` cũ không còn phù hợp với chất liệu.

| Script | Kết quả cần kiểm tra |
|---|---|
| 004_anh_san_pham.sql | product_images, ảnh chính cũ được chuyển, không xóa sản phẩm |
| 005_chat_lieu.sql | chat_lieu, product_variants.chat_lieu_id NOT NULL, unique bộ ba, snapshot order_details.ten_chat_lieu |
| 006_luat_ton_kho.sql | orders.da_tru_kho; chỉ bù cờ ở lần thêm cột đầu; inventory_logs.reason NVARCHAR |
| 007_tieng_viet_unicode.sql | Tên/địa chỉ/mô tả/snapshot NVARCHAR |
| 008_nvarchar_con_lai.sql | Văn bản còn lại NVARCHAR, giữ các mã/enum/username/password/actor kỹ thuật; index phụ thuộc được dựng lại trong cùng transaction |

**Không chạy 003 để triển khai.** 003 là dọn/xóa dữ liệu khách, đơn, sản phẩm, không phải cập nhật schema.

## 3. Chạy trên database thật — cần bật guard chủ động

Migration mặc định chỉ cho phép `shop_verify`, `shop_fresh`, `shopbangiay_thu`. Với `shopbangiay`, bắt buộc cả đúng DB_NAME() và session context `shop.allow-production-migration=1`; context NULL bị chặn. Không thêm cờ này vào ứng dụng hoặc script nghiệm thu.

Sau khi diễn tập đạt, bạn lưu nội dung dưới đây vào file **cục bộ không commit** `deploy-local.sql` ở gốc project và chạy từ gốc project:

```sql
:ON ERROR EXIT
IF DB_NAME()<>N'shopbangiay' THROW 51000,N'Sai database triển khai',1;
EXEC sys.sp_set_session_context @key=N'shop.allow-production-migration',@value=1;
GO
:out before-deploy.log
:r database/kiem_tra_trien_khai.sql
:out migrations-deploy.log
:r database/migrations/004_anh_san_pham.sql
GO
:r database/migrations/005_chat_lieu.sql
GO
:r database/migrations/006_luat_ton_kho.sql
GO
:r database/migrations/007_tieng_viet_unicode.sql
GO
:r database/migrations/008_nvarchar_con_lai.sql
GO
:out after-deploy.log
:r database/kiem_tra_trien_khai.sql
GO
EXEC sys.sp_set_session_context @key=N'shop.allow-production-migration',@value=NULL;
GO
```

```powershell
sqlcmd -S 'localhost,1433' -U '<tai_khoan_SQL>' -C -I -b -f 65001 `
    -d shopbangiay -i deploy-local.sql
if ($LASTEXITCODE -ne 0) { throw 'Migration lỗi; giữ app dừng và kiểm tra log.' }
```

Với bản sao, dùng `-d shopbangiay_thu`, đổi guard đầu wrapper thành đúng `shopbangiay_thu`, bỏ thao tác bật production context. Không chạy `shopgiay.sql` trên database đang có dữ liệu: file đó chỉ khởi tạo schema mới và đã chặn schema tồn tại.

## 4. Cấu hình và chạy JAR

`src/main/resources/application.properties` chỉ tham chiếu biến môi trường. Cần cung cấp cả URL, username và password:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:sqlserver://localhost:1433;databaseName=shopbangiay;encrypt=false;trustServerCertificate=true'
$env:SPRING_DATASOURCE_USERNAME = '<tai_khoan_SQL>'
$env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Mật khẩu ứng dụng' -AsSecureString)).Password
$env:SPRING_JPA_HIBERNATE_DDL_AUTO = 'none'
$env:SHOP_UPLOAD_DIR = 'DUONG_DAN_UPLOAD_THAT'
java -jar target\DemoDATN-0.0.1-SNAPSHOT.jar
```

Không dùng `ddl-auto=update` trên database thật. @Nationalized bảo đảm schema mới, nhưng database cũ vẫn phải chạy migration. Mật khẩu SQL từng nằm trong file được Git track: cấu hình hiện tại đã bỏ giá trị thật, nhưng lịch sử Git có thể còn; đổi mật khẩu SQL theo quy trình vận hành trước triển khai, không tự rewrite lịch sử repository.

## 5. Kiểm tra sau chạy

- Chờ log Started, admin đăng nhập và mở Dashboard; user đăng ký/mở sản phẩm/giỏ/checkout/chi tiết đơn.
- Chuỗi “Nguyễn Thị Ánh - Đà Nẵng - Hủy đơn” đọc lại đúng từ form → SQL → HTML. Dữ liệu cũ đã mất dấu thành `?` phải phục hồi từ backup có chữ đúng hoặc nhập lại; đổi NVARCHAR không tự khôi phục được.
- COD: đặt không trừ, xác nhận trừ đúng một lần, hủy hoàn. Chuyển khoản: trừ ngay; hạn hiển thị bằng created_at + timeout. Đã thanh toán/giao thất bại không hiển thị lời nhắc sai.
- Doanh thu/bán chạy chỉ PAID và không CANCELLED/DELIVERY_FAILED. SELECT độc lập khớp Dashboard/Thống kê/Home.
- Ảnh upload còn truy cập được; POST thiếu CSRF=403; user không vào admin/đọc đơn của người khác.
- Giữ các log trước/sau ngoài Git; không chạy bộ integration test có tạo/xóa dữ liệu lên shopbangiay.

## 6. Quay lại

Dừng app mới, giữ nguyên log và backup, phục hồi đồng bộ **JAR cũ + database backup trước migration + upload/cấu hình cùng thời điểm**. Không quay lui bằng cách ép NVARCHAR về VARCHAR vì có thể tiếp tục mất chữ.

Ví dụ chỉ dành cho người vận hành đã kiểm tra tên DB/file backup:

```sql
USE master;
IF DB_NAME()<>N'master' THROW 51000,N'Sai context phục hồi',1;
ALTER DATABASE [shopbangiay] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
BEGIN TRY
    RESTORE DATABASE [shopbangiay]
      FROM DISK=N'D:\SQLBackups\shopbangiay_truoc_trien_khai_YYYYMMDD_HHMM.bak'
      WITH REPLACE,CHECKSUM;
    ALTER DATABASE [shopbangiay] SET MULTI_USER;
END TRY
BEGIN CATCH
    ALTER DATABASE [shopbangiay] SET MULTI_USER;
    THROW;
END CATCH;
```

Lần phục hồi này ghi đè database đích; phải dùng đúng backup đã xác nhận và lưu riêng dữ liệu phát sinh sau thời điểm backup nếu có. Khởi động JAR cũ, kiểm tra lại đăng nhập/đơn/kho/ảnh. Kết thúc phiên PowerShell thì xóa biến mật khẩu:

```powershell
Remove-Item Env:SQLCMDPASSWORD -ErrorAction SilentlyContinue
Remove-Item Env:SPRING_DATASOURCE_PASSWORD -ErrorAction SilentlyContinue
```
