# Cửa hàng giày — hướng dẫn chạy dự án

Dự án Java 17, Spring Boot, Thymeleaf, Spring Security, JPA và SQL Server, phục vụ cửa hàng trực tuyến cùng khu vực quản trị và bán hàng tại quầy (POS).

## Yêu cầu

- JDK 17 và SQL Server có kết nối JDBC.
- Dùng Maven Wrapper trong repository, không cần cài Maven riêng.
- PowerShell trên Windows; dùng `curl.exe` khi kiểm tra HTTP.
- Node.js/Playwright chỉ cần cho kiểm tra trình duyệt, không cần để chạy ứng dụng.

## Database và cấu hình

Đợt dọn dẹp chỉ kiểm thử trên `shop_verify`. Bạn không cần và không nên chạy kiểm thử trên database thật `shopbangiay`.

Nếu cần một database kiểm thử mới, tạo `shop_verify` bằng SSMS hoặc lệnh SQL:

```sql
IF DB_ID(N'shop_verify') IS NULL CREATE DATABASE [shop_verify];
```

Schema và dữ liệu mẫu nằm trong `shopgiay.sql`; các bổ sung nằm trong `database/phase6.sql`, `phase7.sql`, `phase8.sql`, `phase9.sql` và `database/admin-account-pos-hold.sql`. **Bản gốc `shopgiay.sql` có `CREATE DATABASE shopbangiay` và `USE shopbangiay`. Bạn phải mở một bản sao ngoài repository, đổi đúng database đích sang `shop_verify` và kiểm tra toàn bộ script trước khi chạy. Không chạy bản gốc vào SQL Server đang chứa database thật.** Với database đã có dữ liệu, không chạy lại seed hoặc các lệnh tạo bảng; chỉ áp dụng migration còn thiếu sau khi xem xét schema hiện tại.

Không bật tự cập nhật schema để che lỗi thiếu migration. Giữ `spring.jpa.hibernate.ddl-auto=none`. Script database, DataInitializer và dữ liệu mẫu được giữ nguyên trong đợt này; khởi tạo database mới từ đầu chưa được nghiệm thu lại, nên mục đó là **NOT TESTED**.

Đặt cấu hình bằng biến môi trường trong PowerShell. Không ghi mật khẩu thật vào Git:

```powershell
$env:JAVA_HOME = 'DUONG_DAN_JDK_17'
$env:SPRING_DATASOURCE_URL = 'jdbc:sqlserver://localhost:1433;databaseName=shop_verify;encrypt=false;trustServerCertificate=true'
$env:SPRING_DATASOURCE_USERNAME = 'TAI_KHOAN_SQL_SERVER'
$env:SPRING_DATASOURCE_PASSWORD = 'MAT_KHAU_CUA_BAN'
$env:SPRING_PROFILES_ACTIVE = 'verify'
$env:SPRING_CONFIG_ADDITIONAL_LOCATION = 'file:./application-verify.properties'
$env:SPRING_JPA_HIBERNATE_DDL_AUTO = 'none'
```

`application-verify.properties` là cấu hình cục bộ dùng datasource `shop_verify`, cổng 8081 và thư mục upload thử nghiệm. File này không chứa mật khẩu thật trong README; bạn phải kiểm tra cấu hình cục bộ trước khi chạy. Cấu hình gốc không được đợt dọn dẹp sửa đổi.

## Cách chạy và kiểm thử

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Với môi trường verify ở trên, bạn truy cập `http://localhost:8081`; khu vực quản trị là `/admin`, POS là `/admin/pos`. Ở cấu hình chạy thông thường, cổng mặc định là 8080.

Đóng gói khi muốn tạo JAR:

```powershell
.\mvnw.cmd clean package
java -jar target\DemoDATN-0.0.1-SNAPSHOT.jar --spring.profiles.active=verify --spring.config.additional-location=file:./application-verify.properties
```

Không dùng `-DskipTests` để coi build là thành công. Mốc hiện tại có **75 test, 1 failure, 2 errors**, nên `clean test` đang thất bại và `clean package` chưa được xác nhận thành công. Chi tiết nằm trong [báo cáo lỗi](docs/BUG_PHAT_HIEN.md). Không xóa test hoặc sửa logic trong phạm vi dọn dẹp.

## Tài khoản mẫu

Khi seed/khởi tạo mẫu đúng và tài khoản chưa bị đổi mật khẩu:

| Vai trò | Tên đăng nhập | Mật khẩu mẫu |
|---|---|---|
| Quản trị viên | `admin` | `admin123` |
| Khách hàng | `user` | `user123` |

Đây là tài khoản mẫu, không phải thông tin xác thực SQL Server. Đợt dọn không đổi mật khẩu, vai trò hoặc trạng thái tài khoản. Tài khoản nhân viên tạo qua quản trị phải gắn Employee đúng như nghiệp vụ hiện có.

## Cấu trúc thư mục

```text
src/main/java/com/example/demodatn/
  banhang/       đơn hàng, POS, hóa đơn và hóa đơn chờ
  sanpham/       sản phẩm, biến thể, danh mục, Size và tồn kho
  khachhang/     khách hàng và hồ sơ
  nhanvien/      tài khoản, nhân viên và phân quyền
  khuyenmai/     Voucher và đợt giảm giá
  thongke/       bảng điều khiển, thống kê và điều hướng quản trị
  core/         phần dùng chung
  config/       cấu hình và phục vụ tài nguyên
  security/     xác thực và bảo vệ truy cập
src/main/resources/
  templates/
    user/       cửa hàng và các trang người dùng
    admin/      các thư mục nghiệp vụ có tên tiếng Việt không dấu
    fragments/  phần dùng chung và bo-cuc/
    auth/       dang-nhap.html, dang-ky.html
    loi/        nội dung trang 403, 404, 500
    error/      view cầu nối giữ convention Spring Boot
  static/
    css/        he-thong-thiet-ke.css, quan-tri.css
    js/         hành vi trang và script dùng chung
    anh/        ảnh tĩnh
src/test/       các test hiện có, không xóa
database/       script schema/migration được giữ nguyên
docs/           kế hoạch, báo cáo dọn dẹp và lỗi phát hiện
_cho_xoa/       tệp cách ly cục bộ, được gitignore
verify/         script/bằng chứng còn được giữ theo các mục HỎI
```

Tên class Java, package nghiệp vụ, URL, enum, bảng/cột giữ nguyên. Các URL tài nguyên cũ `/images/**`, tên CSS/JS cũ vẫn được phục vụ tương thích; dữ liệu ảnh lưu trong database không bị sửa. Không có stylesheet riêng cho từng trang mới.

Các mục HỎI chưa được chọn — báo cáo lịch sử, seed, script QA hữu ích, class nghi không dùng và placeholder mồ côi — được giữ nguyên. `_cho_xoa` có thể chứa cookie/token và dữ liệu kiểm thử, không commit hay chia sẻ công khai. Bạn chỉ nên xóa vĩnh viễn sau khi tự kiểm tra và có quyết định riêng.

## Kết quả và giới hạn

Nhóm Việt hóa đã bị revert vì phát sinh lỗi Thymeleaf. Giao diện hiện giữ trạng thái trước nhóm đó; chưa đạt yêu cầu không còn tiếng Anh. Không hạ bảo vệ Thymeleaf, không sửa bug hay logic để hoàn tất dọn dẹp. [Báo cáo dọn dẹp](docs/BAO_CAO_DON_DEP.md) ghi commit từng nhóm, so sánh test/HTTP, từ tiếng Anh còn lại và các mục NOT TESTED.

Bạn nên đọc [kế hoạch đã duyệt](docs/KE_HOACH_DON_DEP.md) trước khi chọn các mục HỎI hoặc triển khai lại Việt hóa. Nhánh `don-dep` chưa được merge.
