# Cửa hàng giày

Ứng dụng Java 17, Spring Boot, Thymeleaf, Spring Security, JPA và SQL Server; gồm cửa hàng trực tuyến, quản trị, POS và thống kê.

## Chuẩn bị

JDK 17, Maven Wrapper trong repository, SQL Server và tài khoản có quyền trên database đích. Trên Windows dùng PowerShell và `curl.exe`. Node/Playwright chỉ phục vụ kiểm thử trình duyệt.

Không chạy kiểm thử trên `shopbangiay`. Dùng `shop_verify`; diễn tập migration trên bản sao riêng. Sao lưu database và thư mục upload trước triển khai.

## Khởi tạo database thử mới

Tạo `shop_verify` bằng SSMS. Chạy `shopgiay.sql` với database đích đã chọn; script chứa schema hiện tại và chặn database ngoài danh sách cho phép. Không chạy trên database đã có bảng `users`. Script không seed khách, sản phẩm, đơn hàng mẫu; ứng dụng khởi tạo vai trò, quản trị viên và danh mục cơ bản khi chưa tồn tại.

```powershell
sqlcmd -S 'localhost,1433' -E -C -I -b -d shop_verify -i shopgiay.sql
```

Với database hiện có, làm theo [hướng dẫn triển khai](docs/HUONG_DAN_TRIEN_KHAI.md): kiểm kê trước, migration **004 → 005 → 006 → 007 → 008**, đối chiếu dữ liệu sau. **003 xóa dữ liệu nghiệp vụ**, không thuộc quy trình nâng cấp thông thường.

## Cấu hình và chạy

Xem `application-example.properties`. Ba biến datasource bắt buộc; không có mật khẩu thật mặc định trong cấu hình nguồn. Nhập bí mật qua môi trường, không commit file cấu hình cục bộ.

```powershell
$env:JAVA_HOME = 'DUONG_DAN_JDK_17'
$env:SPRING_DATASOURCE_URL = 'jdbc:sqlserver://localhost:1433;databaseName=shop_verify;encrypt=false;trustServerCertificate=true'
$env:SPRING_DATASOURCE_USERNAME = 'TAI_KHOAN_SQL_SERVER'
$taskSecret = Read-Host 'Mật khẩu SQL Server' -AsSecureString
$env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', $taskSecret).Password
$env:SPRING_PROFILES_ACTIVE = 'verify'
$env:SPRING_JPA_HIBERNATE_DDL_AUTO = 'none'
.\mvnw.cmd clean test
.\mvnw.cmd clean package
java -jar target\DemoDATN-0.0.1-SNAPSHOT.jar --spring.profiles.active=verify --server.port=8081
```

Giữ `ddl-auto=none` với database đã chuẩn bị schema. `update` chỉ dùng nghiệm thu schema mới trên database tạm. Cổng thường là 8080; lệnh trên dùng 8081. Chờ log `Started DemoDatnApplication in ...` trước kiểm tra.

Tài khoản khởi tạo khi chưa có: `admin/admin123`. Đổi mật khẩu trước đưa lên môi trường thật. Khách hàng tự đăng ký tại `/register`; không có cam kết tài khoản mẫu `user/user123` trên database mới.

## Cấu trúc

```text
src/main/java/com/example/demodatn/
  DemoDatnApplication.java
  config/ security/ controller/ service/ repository/
  entity/ dto/ enums/ exception/ util/
src/main/resources/
  templates/     user, admin, auth, loi và fragments
  static/        css, js, anh
src/test/java/   kiểm thử theo lớp và nghiệp vụ
database/        schema bổ sung, migrations, kiểm kê triển khai
docs/            báo cáo và hướng dẫn
```

Xem [cấu trúc dự án](docs/CAU_TRUC_DU_AN.md) và [báo cáo chốt triển khai](docs/BAO_CAO_CHOT_TRIEN_KHAI.md) để biết kết quả thực chạy và giới hạn nghiệm thu. Giá trị enum trong database giữ nguyên; nhãn giao diện dùng tiếng Việt. Văn bản nghiệp vụ mới lưu NVARCHAR; dữ liệu đã mất dấu trước đó cần nhập lại từ nguồn đúng.

Thư mục upload thật, `target/`, `verify/`, log và cấu hình bí mật cục bộ không đưa vào Git. Mật khẩu từng có trong lịch sử Git phải được thay mới; bỏ khỏi file hiện tại không xóa được lịch sử.
