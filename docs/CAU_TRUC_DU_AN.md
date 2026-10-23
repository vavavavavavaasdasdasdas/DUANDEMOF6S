# Cấu trúc dự án sau giai đoạn 4

Ngày kiểm tra: 23/10/2026. Nhánh: `don-dep`. Gốc package: `com.example.demodatn`.

## Phạm vi và mốc ban đầu

Chuyển package bằng `git mv`, cập nhật package/import và tên kiểu đầy đủ. Giữ tên lớp, route, bảng/cột, tên bean và logic nghiệp vụ. Không sửa template/CSS/JS/cấu hình datasource trong giai đoạn này. Các thay đổi tài nguyên và file đang xóa/stage từ trước được giữ nguyên, không gộp vào commit cấu trúc.

Mốc Java trước khi di chuyển: `ad53aed`. Đây là checkpoint phần Java đang có từ các giai đoạn trước, không phải thay đổi logic mới của giai đoạn sắp xếp.

Lệnh chạy từ PowerShell tại gốc dự án (helper ngoài repository thiết lập profile verify, JDBC chỉ tới shop_verify, JDK 17 và ddl-auto=none):

```powershell
& 'C:/Users/ADMIN/AppData/Local/Temp/packages-20261023/run.ps1' -Mode test
& 'C:/Users/ADMIN/AppData/Local/Temp/packages-20261023/run.ps1' -Mode build
# tương ứng .\mvnw.cmd clean test và .\mvnw.cmd clean package; không bỏ test
```

Mốc đầu tiên chạy đủ 99 bài: `Failures: 1, Errors: 1, Skipped: 0`. Hai lỗi fixture có trước khi di chuyển package:

- `PeopleIntegrationTest.employeeInsertFailureRollsBackTheAlreadyInsertedUser`: dùng tên 101 ký tự để mong DB từ chối, nhưng cột thực tế dài 255. Fixture nay đọc độ dài cột từ INFORMATION_SCHEMA rồi dùng chuỗi vượt giới hạn; vẫn kiểm tra exception và rollback.
- `UserUiIntegrationTest.historyAndOwnedDetailUseSharedLayoutAndResponsiveTables`: trông chờ đơn của tài khoản `user` có sẵn, nhận EmptyResultDataAccess. Fixture nay tự tạo khách/tài khoản/đơn trong transaction rollback và vẫn giữ kiểm tra quyền sở hữu/layout.

Commit fixture riêng `bab4cd6`; sau đó vẫn 99 bài, 0 fail/error/skip. Không bỏ assertion, không giảm số bài, không sửa hành vi ứng dụng để làm test xanh.

## Cây package trước

```text
com.example.demodatn/
  (gốc) [1 lớp]
  banhang [24 lớp]
  banhang/dto [4 lớp]
  config [6 lớp]
  core [8 lớp]
  khachhang [5 lớp]
  khuyenmai [13 lớp]
  nhanvien [13 lớp]
  sanpham [33 lớp]
  security [4 lớp]
  thongke [7 lớp]
```

Tổng: 118 lớp chính, 19 file test. Các package lớn trước đây: banhang=24, sanpham=33.

## Cây package sau

```text
src/main/java/com/example/demodatn/
  DemoDatnApplication.java
  config/ [4 lớp]
    DataInitializer.java
    PosHoldConfig.java
    ProductUploadConfig.java
    StaticResourceConfig.java
  controller/ [4 lớp]
    AdminPageAdvice.java
    GlobalControllerAdvice.java
    PaginationAdvice.java
    ProductUploadExceptionAdvice.java
  controller/admin/donhang/ [3 lớp]
    AdminInvoiceController.java
    AdminOrderController.java
    AdminPosController.java
  controller/admin/khachhang/ [1 lớp]
    AdminCustomerController.java
  controller/admin/khuyenmai/ [2 lớp]
    AdminPromotionController.java
    AdminVoucherController.java
  controller/admin/nhanvien/ [2 lớp]
    AdminEmployeeController.java
    AdminPageController.java
  controller/admin/sanpham/ [4 lớp]
    AdminInventoryController.java
    AdminLookupController.java
    AdminProductController.java
    AdminVariantController.java
  controller/admin/thongke/ [2 lớp]
    AdminController.java
    AdminStatisticsController.java
  controller/auth/ [1 lớp]
    AuthController.java
  controller/user/ [6 lớp]
    CartController.java
    HomeController.java
    OrderController.java
    PolicyController.java
    ProductController.java
    ProfileController.java
  dto/donhang/ [4 lớp]
    CartItemDto.java
    CheckoutRequest.java
    PosCheckoutRequest.java
    PriceInfo.java
  dto/khachhang/ [1 lớp]
    CustomerDto.java
  dto/khuyenmai/ [2 lớp]
    PromotionDto.java
    VoucherDto.java
  dto/nhanvien/ [4 lớp]
    AccountPasswordForm.java
    AccountProfileForm.java
    EmployeeDto.java
    RegisterRequest.java
  dto/sanpham/ [5 lớp]
    AdminProductForm.java
    InventoryForm.java
    LookupForm.java
    ProductDto.java
    VariantForm.java
  entity/donhang/ [5 lớp]
    Invoice.java
    Order.java
    OrderDetail.java
    PosHeldItem.java
    PosHeldOrder.java
  entity/khachhang/ [1 lớp]
    Customer.java
  entity/khuyenmai/ [3 lớp]
    Promotion.java
    PromotionDetail.java
    Voucher.java
  entity/nhanvien/ [3 lớp]
    Employee.java
    Role.java
    User.java
  entity/sanpham/ [7 lớp]
    Category.java
    InventoryLog.java
    Material.java
    Product.java
    ProductImage.java
    ProductVariant.java
    Size.java
  exception/ [3 lớp]
    CatalogValidationException.java
    DiscountValidationException.java
    PeopleValidationException.java
  repository/donhang/ [4 lớp]
    InvoiceRepository.java
    OrderDetailRepository.java
    OrderRepository.java
    PosHeldOrderRepository.java
  repository/khachhang/ [1 lớp]
    CustomerRepository.java
  repository/khuyenmai/ [3 lớp]
    PromotionDetailRepository.java
    PromotionRepository.java
    VoucherRepository.java
  repository/nhanvien/ [3 lớp]
    EmployeeRepository.java
    RoleRepository.java
    UserRepository.java
  repository/sanpham/ [8 lớp]
    AdminCatalogQuery.java
    CategoryRepository.java
    InventoryLogRepository.java
    MaterialRepository.java
    ProductImageRepository.java
    ProductRepository.java
    ProductVariantRepository.java
    SizeRepository.java
  repository/thongke/ [1 lớp]
    DashboardRepository.java
  security/ [4 lớp]
    AccountStatusFilter.java
    CustomUserDetails.java
    SecurityConfig.java
    UserDetailsServiceImpl.java
  service/ [1 lớp]
    PeopleService.java
  service/donhang/ [7 lớp]
    BankTransferExpiryJob.java
    OrderService.java
    OrderStatusService.java
    PosCatalogQuery.java
    PosHoldCleanup.java
    PosHoldService.java
    PricingService.java
  service/khuyenmai/ [2 lớp]
    DiscountAdminService.java
    VoucherService.java
  service/nhanvien/ [2 lớp]
    AdminAccountService.java
    AuthService.java
  service/sanpham/ [6 lớp]
    AdminCatalogService.java
    InventoryService.java
    ProductFilterService.java
    ProductGalleryService.java
    ProductImageStorage.java
    ProductService.java
  service/thongke/ [2 lớp]
    DashboardService.java
    StatisticsService.java
  util/ [3 lớp]
    MoneyFormat.java
    Pagination.java
    ProductMedia.java
```

Tổng: 115 lớp chính; package lớn nhất có 8 lớp (repository/sanpham). Không tạo package enums rỗng vì hiện không có lớp enum riêng cần di chuyển. Nếu bổ sung enum sau này, đặt tại enums theo cấu trúc đích.

Test đã di chuyển tương ứng:

```text
src/test/java/com/example/demodatn/
  ./
    DemoDatnApplicationTests.java
  config/
    DataInitializerTest.java
  controller/
    PaginationIntegrationTest.java
  controller/user/
    UserUiIntegrationTest.java
  security/
    StandaloneAccountTest.java
  service/
    PeopleIntegrationTest.java
  service/donhang/
    OrderRollbackIntegrationTest.java
    OrderStockIntegrationTest.java
    PosHoldServiceTest.java
    PosInvoiceIntegrationTest.java
    PricingServiceTest.java
  service/khuyenmai/
    DiscountIntegrationTest.java
  service/nhanvien/
    AdminAccountServiceTest.java
  service/sanpham/
    CatalogIntegrationTest.java
    MaterialIntegrationTest.java
    ProductGalleryIntegrationTest.java
  service/thongke/
    DashboardIntegrationTest.java
    StatisticsIntegrationTest.java
  util/
    PaginationTest.java
```

## Trách nhiệm package

| Package | Trách nhiệm |
|---|---|
| gốc | Điểm khởi động Spring Boot; quét component/entity/repository từ com.example.demodatn. |
| config | Cấu hình tài nguyên/upload, Clock/scheduling và DataInitializer. |
| security | Cấu hình phân quyền, UserDetails và kiểm tra trạng thái tài khoản. |
| controller | Advice dùng chung cho model, phân trang và lỗi upload. |
| controller/admin/donhang | Đơn hàng, hóa đơn, POS của quản trị. |
| controller/admin/khachhang | Quản lý khách hàng. |
| controller/admin/nhanvien | Quản lý nhân viên và tài khoản quản trị. |
| controller/admin/sanpham | Sản phẩm, biến thể, danh mục/size/chất liệu và tồn kho. |
| controller/admin/khuyenmai | Voucher và đợt giảm giá. |
| controller/admin/thongke | Dashboard và thống kê. |
| controller/user | Trang cửa hàng, sản phẩm, giỏ, đặt hàng, hồ sơ và chính sách. |
| controller/auth | Đăng nhập, đăng ký và xác thực. |
| service | PeopleService dùng chung nghiệp vụ khách hàng/nhân viên. |
| service/donhang | Đặt hàng, chuyển trạng thái, giá, POS/hold và job hết hạn. |
| service/sanpham | Danh mục sản phẩm, lọc, ảnh và trừ/hoàn kho. |
| service/nhanvien | Xác thực, hồ sơ và mật khẩu. |
| service/khuyenmai | Voucher và quản trị giảm giá. |
| service/thongke | Dữ liệu Dashboard và thống kê. |
| repository/donhang | Truy cập đơn, chi tiết, hóa đơn và hóa đơn chờ. |
| repository/sanpham | Repository sản phẩm/biến thể/ảnh/danh mục và truy vấn quản trị. |
| repository/khachhang | Truy cập khách hàng. |
| repository/nhanvien | Truy cập tài khoản, nhân viên và vai trò. |
| repository/khuyenmai | Truy cập voucher, promotion và sản phẩm áp dụng. |
| repository/thongke | Truy vấn Dashboard. |
| entity/donhang | Entity đơn, chi tiết, hóa đơn và hóa đơn chờ. |
| entity/sanpham | Entity sản phẩm, ảnh, biến thể, danh mục, size, chất liệu và log kho. |
| entity/khachhang | Entity khách hàng. |
| entity/nhanvien | Entity User, Employee, Role. |
| entity/khuyenmai | Entity Voucher, Promotion, PromotionDetail. |
| dto/donhang | Dữ liệu giỏ hàng, checkout, POS và thông tin giá. |
| dto/sanpham | Form/DTO sản phẩm, biến thể, tồn kho và danh mục. |
| dto/khachhang | DTO khách hàng. |
| dto/nhanvien | Form tài khoản, đăng ký và DTO nhân viên. |
| dto/khuyenmai | DTO voucher và promotion. |
| exception | Lỗi validation nghiệp vụ dùng chung. |
| util | Phân trang, định dạng tiền và đường dẫn ảnh. |

## Commit và kiểm tra sau từng lớp

| Nhóm | Commit | mvnw clean test | Khởi động verify | Crawl / khác mốc |
|---|---|---|---|---|
| Fixture độc lập | bab4cd6 | 99 / 0 fail / 0 error / 0 skip | Started | 40 / 0 |
| Entity (19 file chuyển) | b698245 | 99 / 0 fail / 0 error / 0 skip | Started | 40 / 0 |
| Repository (20 file chuyển) | 4a47dde | 99 / 0 fail / 0 error / 0 skip | Started | 40 / 0 |
| Service (20 file chuyển) | a6ea476 | 99 / 0 fail / 0 error / 0 skip | Started | 40 / 0 |
| Controller (25 file chuyển) | a91e4e7 | 99 / 0 fail / 0 error / 0 skip | Started | 40 / 0 |
| DTO/util/exception/test (38 file chuyển) và dọn lớp chết | 53df9e2 | 99 / 0 fail / 0 error / 0 skip | Started | 40 / 0 |

Không có nhóm phải revert. Tổng 122 lần git mv (bao gồm test). Kiểm tra phần thân lớp Java chính sau khi chuẩn hóa package/import/tên kiểu đầy đủ và bỏ println được yêu cầu: `Business body differences: []`.

Output build JAR:

```text
Tests run: 99, Failures: 0, Errors: 0, Skipped: 0
Building jar: C:\DuAnTNDEMO\DemoDATN\target\DemoDATN-0.0.1-SNAPSHOT.jar
BUILD SUCCESS
```

Bằng chứng thô ở `C:/Users/ADMIN/AppData/Local/Temp/packages-20261023/`: mỗi nhóm có `<nhom>-test.log`, `<nhom>-app.log`, `<nhom>-crawl.json`; thêm `baseline-test.log`, `baseline-crawl.json`, `package-test.log`, `crawl-comparison.json`, `mapping.json`, `semantic-differences.json`.

## HTTP trước/sau

Crawl dùng curl.exe, đăng nhập thật bằng cookie/CSRF; không follow redirect để ghi nguyên status. So sánh cả role, URL, status và Location; đủ 15 URL User + 25 URL Admin. Bảng sau là mốc đầu và sau nhóm cuối:

| Vai trò | URL | Trước | Sau | Location nếu redirect |
|---|---|---:|---:|---|
| user | `/` | 200 | 200 | — |
| user | `/products` | 200 | 200 | — |
| user | `/products?page=1` | 200 | 200 | — |
| user | `/search?keyword=Giay` | 200 | 200 | — |
| user | `/category/1` | 200 | 200 | — |
| user | `/products/466` | 200 | 200 | — |
| user | `/cart` | 200 | 200 | — |
| user | `/checkout` | 302 | 302 | http://localhost:8081/cart |
| user | `/orders` | 200 | 200 | — |
| user | `/orders/386` | 200 | 200 | — |
| user | `/orders/success/386` | 200 | 200 | — |
| user | `/profile` | 200 | 200 | — |
| user | `/login` | 200 | 200 | — |
| user | `/register` | 200 | 200 | — |
| user | `/policy` | 200 | 200 | — |
| admin | `/admin` | 200 | 200 | — |
| admin | `/admin/account` | 200 | 200 | — |
| admin | `/admin/products` | 302 | 302 | http://localhost:8081/admin/products-crud |
| admin | `/admin/products-crud` | 200 | 200 | — |
| admin | `/admin/products-crud/create` | 200 | 200 | — |
| admin | `/admin/products-crud/edit/466` | 200 | 200 | — |
| admin | `/admin/products-crud/466/variants` | 200 | 200 | — |
| admin | `/admin/variants` | 200 | 200 | — |
| admin | `/admin/variants/create` | 200 | 200 | — |
| admin | `/admin/categories` | 200 | 200 | — |
| admin | `/admin/sizes` | 200 | 200 | — |
| admin | `/admin/inventory` | 200 | 200 | — |
| admin | `/admin/inventory/adjust/332` | 200 | 200 | — |
| admin | `/admin/customers` | 200 | 200 | — |
| admin | `/admin/customers/detail/38` | 200 | 200 | — |
| admin | `/admin/employees` | 200 | 200 | — |
| admin | `/admin/vouchers` | 200 | 200 | — |
| admin | `/admin/promotions` | 200 | 200 | — |
| admin | `/admin/promotions/1/products` | 200 | 200 | — |
| admin | `/admin/orders` | 200 | 200 | — |
| admin | `/admin/orders/356` | 200 | 200 | — |
| admin | `/admin/invoices` | 200 | 200 | — |
| admin | `/admin/invoices/161` | 200 | 200 | — |
| admin | `/admin/pos` | 200 | 200 | — |
| admin | `/admin/statistics` | 200 | 200 | — |

**Kết quả: số URL khác status/Location = 0/40; 38 trả 200, 2 trả 302 hợp lệ như mốc.** Đây là kiểm tra hồi quy route/khởi động, không thay thế toàn bộ nghiệm thu giao diện hoặc mọi hành vi nghiệp vụ.

## JAR chạy trên database kiểm thử rỗng

Chỉ dùng shop_verify. Sao lưu COPY_ONLY/CHECKSUM trước, xóa dữ liệu theo thứ tự khóa ngoại trong transaction (không sửa schema), khởi động bằng `java -jar target/DemoDATN-0.0.1-SNAPSHOT.jar --spring.profiles.active=verify --spring.jpa.hibernate.ddl-auto=none --server.port=8081`, rồi dừng và RESTORE chính shop_verify trong finally. JDBC được override qua biến môi trường, không dùng datasource mặc định.

Tất cả 19 bảng đều COUNT=0 trước khi JAR khởi động. DataInitializer sau khởi động chỉ tạo danh mục và admin: products=0, product_variants=0, customers=0, orders=0, order_details=0, invoices=0; roles=2, users=1, employees=1, categories=3, sizes=3, chat_lieu=8.

Lần thử đầu helper PowerShell bị giữ pipe và timeout 90 giây; đã dừng đúng tiến trình verify, finally restore thành công. Thay cách khởi chạy helper bằng tiến trình Java nền trực tiếp (không sửa ứng dụng), chạy lại thành công. Bằng chứng lần chạy lại: `empty-verify.log`, `empty-zero.json`, `empty-jar-app.log`, `empty-crawl.json`.

```text
2026-10-23T16:55:59.467+07:00  INFO 26636 --- [           main] c.example.demodatn.DemoDatnApplication   : Started DemoDatnApplication in 5.832 seconds (process running for 6.236)
POST /login (admin/admin123) -> 302 /admin
shop_verify restored; all table counts match backup
```

| URL | Status trên dữ liệu rỗng |
|---|---:|
| `/` | 200 |
| `/products` | 200 |
| `/search?keyword=none` | 200 |
| `/cart` | 200 |
| `/admin` | 200 |
| `/admin/dashboard` | 200 |
| `/admin/products` | 302 |
| `/admin/products-crud` | 200 |
| `/admin/variants` | 200 |
| `/admin/categories` | 200 |
| `/admin/sizes` | 200 |
| `/admin/materials` | 200 |
| `/admin/inventory` | 200 |
| `/admin/orders` | 200 |
| `/admin/invoices` | 200 |
| `/admin/pos` | 200 |
| `/admin/statistics` | 200 |
| `/admin/account` | 200 |

Số bản ghi trước / sau restore (khớp cả 19 bảng):

| Bảng | Trước | Sau restore |
|---|---:|---:|
| product_images | 7 | 7 |
| chat_lieu | 12 | 12 |
| categories | 3 | 3 |
| customers | 1 | 1 |
| employees | 24 | 24 |
| inventory_logs | 45 | 45 |
| invoices | 25 | 25 |
| order_details | 28 | 28 |
| orders | 25 | 25 |
| pos_held_items | 0 | 0 |
| pos_held_orders | 0 | 0 |
| product_variants | 10 | 10 |
| products | 7 | 7 |
| promotion_details | 0 | 0 |
| promotions | 1 | 1 |
| roles | 2 | 2 |
| sizes | 3 | 3 |
| users | 26 | 26 |
| vouchers | 3 | 3 |

SQL/đầu ra: `empty-delete.sql`, `empty-before.json`, `empty-zero.json`, `empty-seeded.json`, `empty-restored.json`, `empty-verify.log`. Backup nằm tại đường dẫn SQL Server lưu trong `empty-backup-path.txt` (ngoài project). Dữ liệu shop_verify ban đầu đã được phục hồi; không kết nối hoặc chạy lệnh lên shopbangiay.

## Code thừa đã xóa và bằng chứng

Lệnh trước khi xóa (sau khi DTO dùng thật đã được chuyển):

```powershell
rg -n '\b(CartItem|OrderResponseDto|InsufficientStockException)\b' src pom.xml application-verify.properties
```

Output toàn bộ:

```text
src/main/java/com/example/demodatn/banhang/InsufficientStockException.java:3:public class InsufficientStockException extends RuntimeException {
src/main/java/com/example/demodatn/banhang/InsufficientStockException.java:4:    public InsufficientStockException(String message) {
src/main/java/com/example/demodatn/banhang/dto/OrderResponseDto.java:8:public class OrderResponseDto {
src/main/java/com/example/demodatn/banhang/CartItem.java:11:public class CartItem {
```

| Lớp xóa bằng git rm | Bằng chứng/quyết định |
|---|---|
| CartItem | Chỉ có khai báo, không entity/bean; luồng giỏ dùng CartItemDto. |
| OrderResponseDto | Chỉ có khai báo DTO, không nơi dùng trong main/template/test/cấu hình. |
| InsufficientStockException | Chỉ có khai báo/constructor, không throw/catch/handler tham chiếu. |

Giữ các lớp không có tham chiếu Java trực tiếp nhưng Spring dùng qua annotation/scan, ví dụ DataInitializer, advice, controller, job, cấu hình, bean `money`. Không có lớp chưa chắc nào bị xóa. Không đổi tên bean hoặc tên class main.

Đã bỏ import tường minh không dùng, thay wildcard import nội bộ bằng import cụ thể và xóa System.out.println trong main/test. Không tìm thấy khối code bị comment có đủ bằng chứng để xóa; giữ comment tài liệu/nghiệp vụ.

Kiểm tra cuối:

```text
rg System.out.println src/main/java src/test/java -> 0 match
rg com.example.demodatn.(banhang|sanpham|nhanvien|khachhang|khuyenmai|thongke|core) src pom.xml application-verify.properties -> 0 tham chiếu package cũ
src/main/java empty packages []
src/test/java empty packages []
Business body differences: []
```

Không có @EntityScan/@EnableJpaRepositories/@ComponentScan tùy chỉnh hoặc JPQL select new chứa package cũ cần sửa. Main vẫn ở gốc; Spring khởi động và repository/entity được phát hiện trong tất cả lần chạy. Bean money giữ tên `@Component("money")`; template gọi bean không cần đổi.

## Giới hạn và trạng thái kết thúc

- Không thay đổi datasource/schema/route hoặc sửa nghiệp vụ. Không chạy gì trên shopbangiay.
- application.properties và tài nguyên có thay đổi từ trước nhiệm vụ; giai đoạn này không ghi các file đó. Commit cấu trúc chỉ chứa Java/test và tài liệu này.
- Đây là sắp xếp package và kiểm tra hồi quy build/route. Các kiểm thử giao diện bằng trình duyệt, ảnh chụp và toàn bộ luồng nghiệp vụ ngoài 99 bài hiện có: NOT TESTED trong giai đoạn này.
- Không merge. Dừng sau giai đoạn 4.
