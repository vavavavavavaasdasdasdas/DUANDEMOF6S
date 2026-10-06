# Hướng dẫn chạy Project SHOP GIÀY (Spring Boot + SQL Server)

Dự án này là một website thương mại điện tử bán giày đầy đủ chức năng với 2 phân hệ: Quản trị (ADMIN) và Khách hàng (USER).

## 1. Yêu cầu hệ thống
- **JDK:** Phiên bản 17 (hoặc tương thích)
- **Công cụ build:** Maven (project đã có sẵn Maven wrapper `mvnw`)
- **Hệ quản trị cơ sở dữ liệu:** Microsoft SQL Server
- **Công cụ quản lý DB:** SQL Server Management Studio (SSMS) hoặc Azure Data Studio

## 2. Khởi tạo Cơ sở dữ liệu (Database)
Project đi kèm với script SQL để tạo cấu trúc và dữ liệu mẫu.

1. Mở file `shopgiay.sql` nằm ở thư mục gốc của project (cùng cấp với `pom.xml`).
2. Mở SQL Server Management Studio (SSMS).
3. Kết nối vào SQL Server (`localhost` hoặc `127.0.0.1`, cổng `1433`).
4. Mở file `shopgiay.sql` trong SSMS và nhấn **Execute** để chạy script.
   - Script sẽ tạo database `shopbangiay` và chèn các dữ liệu mẫu (sản phẩm, tài khoản, v.v.).

*Lưu ý: Nếu bạn có mật khẩu/tài khoản SQL Server khác với thông tin mặc định, vui lòng cập nhật lại trong file `src/main/resources/application.properties`.*

## 3. Kiểm tra thông tin kết nối SQL Server
Mặc định project được cấu hình trong `application.properties` như sau:
- **Username:** sa
- **Password:** 123456789
- **Port:** 1433
- **Database:** shopbangiay

## 4. Chạy project
Có thể sử dụng Maven Wrapper để chạy project:

**Trên Windows:**
```cmd
mvnw spring-boot:run
```

Hoặc bạn có thể mở project bằng IDE (IntelliJ IDEA, Eclipse, VS Code) và chạy file `DemoDatnApplication.java`.

## 5. Truy cập ứng dụng
Sau khi ứng dụng khởi động thành công (thông báo trên console), mở trình duyệt và truy cập:

- **Website Khách hàng & Trang chủ:** [http://localhost:8080](http://localhost:8080)
- **Trang Quản trị (Admin):** [http://localhost:8080/admin](http://localhost:8080/admin)

## 6. Tài khoản đăng nhập
Project đã được khởi tạo sẵn các tài khoản sau để bạn test:

**Tài khoản ADMIN:**
- Username: `admin`
- Password: `admin123`

**Tài khoản USER:**
- Username: `user`
- Password: `user123`

## 7. Hướng dẫn test các chức năng
- **Chức năng USER:** Hãy thử đăng nhập bằng tài khoản `user`, xem danh sách sản phẩm, thêm vào giỏ hàng và tiến hành đặt hàng trực tuyến (Checkout). Sử dụng mã giảm giá (Voucher) có sẵn như `WELCOME10` để test giảm giá.
- **Chức năng ADMIN:** Đăng nhập bằng `admin`, truy cập mục `/admin` để xem Dashboard thống kê. Từ đây, bạn có thể quản lý sản phẩm, đơn hàng, hóa đơn, người dùng.
- **Bán hàng tại quầy (POS):** Trong màn hình Admin, vào chức năng Bán hàng tại quầy, chọn sản phẩm, kích cỡ và khách hàng để tạo đơn hàng trực tiếp, hệ thống sẽ tự động trừ số lượng tồn kho.
