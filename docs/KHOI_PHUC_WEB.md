# Khôi phục chạy web local — 23/10/2026

## Nguyên nhân và cách sửa

1. Cổng 8080 không có tiến trình lắng nghe; bản kiểm thử trước chạy ở 8081. Trước sửa: `curl.exe http://localhost:8080/` trả HTTP 000, trong khi 8081 trả 200.
2. Sau thay đổi cấu hình ở giai đoạn 6, chạy JAR/nút Run không có biến môi trường bị lỗi: `Driver com.microsoft.sqlserver.jdbc.SQLServerDriver claims to not accept jdbcUrl, ${SPRING_DATASOURCE_URL}`. Đây là lỗi thiết lập chạy local do lần sửa trước, không phải do thiếu class Java.
3. File application-verify.properties riêng của máy tiếp tục ghi đè username/password bằng placeholder chưa có giá trị, dẫn đến `Login failed for user '${SPRING_DATASOURCE_USERNAME}'`, và chọn cổng 8081. Đã cho profile này dùng cấu hình local, bỏ ghi đè username/password, chọn cổng 8080, giữ ddl-auto=none.
4. Hai ảnh fixture của shop_verify nằm trong thư mục tạm của lần kiểm thử trước. Đã sao chép đúng file gốc về verify/uploads, không thay ảnh bằng file giả và không sửa bảng sản phẩm.

Sửa source tối thiểu: application.properties tự import `optional:file:./application-local.properties`. File riêng chứa kết nối shop_verify đã tạo trên máy, được gitignore; không đưa mật khẩu vào commit. Biến môi trường vẫn ưu tiên hơn file local. Cập nhật file mẫu và README để nút Run trong IntelliJ có cách thiết lập rõ ràng.

Không xóa file trong lượt sửa này. Không chỉnh logic nghiệp vụ, migration hay database thật shopbangiay. Không dừng các ứng dụng Java khác trên máy.

## Bằng chứng

Thư mục thô: `C:/Users/ADMIN/AppData/Local/Temp/release-20261023/`.

| Hạng mục | Cách kiểm tra | Kết quả thực tế | Trạng thái |
|---|---|---|---|
| Build | .\mvnw.cmd clean package, shop_verify | Tests run: 101, Failures: 0, Errors: 0, Skipped: 0; BUILD SUCCESS | PASS |
| JAR không có biến datasource | recovery-start.py, profile verify | Started trong 16.159 giây, cổng 8080, databaseName=shop_verify; tiến trình tiếp tục phục vụ request | PASS |
| Chạy mặc định như IntelliJ | java -jar target/DemoDATN-0.0.1-SNAPSHOT.jar --server.port=8082, không profile, không biến datasource | No active profile; Started 14.685 giây; GET / HTTP 200; tiến trình thử 8082 đã dừng | PASS |
| User và Admin | curl.exe cookie jar/CSRF; recovery-crawl.py | 53 URL không 4xx/5xx; 19/19 tài nguyên nội bộ HTTP 200; redirect /admin/products sang products-crud và checkout giỏ rỗng về cart đúng nghiệp vụ | PASS |
| Đăng nhập Admin trên trình duyệt | Playwright, admin/admin123 | Đến /admin; Dashboard, Products, POS, Account, Statistics HTTP 200 | PASS |
| Trang chủ desktop/mobile | Playwright, 1280 và 375 | HTTP 200; scrollWidth lần lượt 1265 và 360, không vượt innerWidth | PASS |
| JavaScript/tài nguyên trình duyệt | Bắt pageerror và response >=400 sau khôi phục ảnh | errors=[], badResponses=[] trên các trang chính đã mở; có một thông tin VERBOSE accessibility của Chrome, không phải error | PASS |
| Bảo mật | Chạy lại final-security.py tại origin 8080 | 8/8: anon redirect, sai mật khẩu, admin login, user login, phân quyền, vô hiệu hóa, CSRF, logout; đọc đơn người khác HTTP 403 | PASS |
| Mua COD/hủy | recovery-checkout.py, SELECT trước/sau | Đơn 6339, variant 5601: tồn 18 → 18 → 18; PENDING/da_tru_kho=0 → CANCELLED/0; Invoice 5487 được tạo | PASS |
| Khôi phục ảnh test | Copy file có sẵn từ evidence uploads về verify/uploads | Hai ảnh từng 404 hiện HTTP 200 | PASS |
| Mọi chức năng CRUD/đồng thời/transaction ngoài lượt smoke | Chưa chạy lại toàn bộ ma trận nghiệp vụ trong lượt này | 101 test tự động đạt; không coi smoke là nghiệm thu lại tất cả nghiệp vụ | NOT TESTED |

Build đầu bị Windows khóa file JAR vì bản verify cũ còn chạy. Đã dừng đúng PID 26668 rồi build lại thành công, không xóa ép thư mục target khi tiến trình đang dùng.

```text
[INFO] Tests run: 101, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
Tomcat started on port 8080 (http)
Started DemoDatnApplication in 16.159 seconds
```

```sql
-- Chỉ trên shop_verify, có DB_NAME guard trong file bằng chứng.
SELECT id,quantity FROM product_variants WHERE id=5601;
SELECT id,status,da_tru_kho,final_amount FROM orders WHERE id=6339;
SELECT id,order_id FROM invoices WHERE order_id=6339;
```

```text
Trước: 5601 18; orders=39; invoices=39
Đặt: 6339 PENDING 0 230000.00; 5601 18; invoice 5487 → order 6339
Hủy: 6339 CANCELLED 0; 5601 18
```

Log và output: recovery-clean-package.log, recovery-app.log, recovery-default.log, recovery-crawl.json, recovery-checkout.json, recovery-cod-*.sql/.txt, final-security.json, http.json. Ảnh: recovery-home-1280.png, recovery-home-375.png, recovery-admin-1280.png. Không commit cookie, mật khẩu hoặc ảnh chứa dữ liệu test.

## Cách mở và chạy lại

- Web đang chạy nền: http://localhost:8080 ; Admin: http://localhost:8080/admin , admin/admin123. Dùng dữ liệu shop_verify, không phải dữ liệu thật.
- Để tự chạy trong IntelliJ, dừng tiến trình JAR local đang chiếm 8080 rồi Run `com.example.demodatn.DemoDatnApplication`, JDK 17, working directory là gốc dự án. File application-local.properties trên máy đã có kết nối test; không cần gõ lại mật khẩu vào source.
- Hoặc chạy `java -jar target\DemoDATN-0.0.1-SNAPSHOT.jar` từ thư mục gốc sau khi dừng bản đang chạy. Trước clean package phải dừng JAR đang dùng file target, tránh lỗi khóa file của Windows.
- Máy khác: tạo application-local.properties từ application-example.properties rồi điền mật khẩu SQL trên máy, hoặc thiết lập ba biến datasource theo README. Không commit file local.

Không merge; các thay đổi có sẵn từ trước được giữ nguyên.

## Dọn file và dữ liệu kiểm thử sau hoàn thiện

Thực hiện ngày 23/10/2026 theo yêu cầu dọn mới. Web cuối cùng tiếp tục chạy ở http://localhost:8080, JAR profile verify, database shop_verify. **Không có sản phẩm/đơn hàng sau dọn là kết quả chủ đích**, không phải mất dữ liệu thật. Không kết nối hay thay đổi shopbangiay trong lượt này; không xóa backup hoặc bản sao phục vụ quay lại.

### Đã xóa

- Trong project: **80 đường dẫn file sinh ra**, gồm 8 ảnh fixture trong verify/uploads, 6 capture/log trong .playwright-cli, 66 output test trong target/surefire-reports, test-classes, generated-test-sources và metadata testCompile. Output phát sinh lại ở lần build thứ hai cũng đã dọn. Không xóa JAR hoặc main classes.
- Ngoài project: **1.276 file** HTML HTTP capture, header, form POST, cookie jar và bản credential tạm của đợt release-20261023. Không xóa cấu hình local đang được web sử dụng. Các báo cáo cũ có thể dẫn tới capture thô đã dọn theo yêu cầu mới; bảng HTTP/SQL tóm tắt còn trong báo cáo và JSON bằng chứng.
- Database shop_verify: **472 bản ghi fixture** đã được kiểm kê trước. Dọn theo khóa ngoại trong một transaction, có DB_NAME guard và kiểm tra giữ nguyên admin/Employee của admin/roles; có bản backup COPY_ONLY CHECKSUM trước khi dọn và RESTORE VERIFYONLY thành công.

| Bảng | Trước | Sau |
|---|---:|---:|
| products | 31 | 0 |
| product_variants | 32 | 0 |
| product_images | 2 | 0 |
| orders | 40 | 0 |
| order_details | 46 | 0 |
| invoices | 40 | 0 |
| inventory_logs | 52 | 0 |
| pos_held_orders / pos_held_items | 1 / 1 | 0 / 0 |
| customers | 30 | 0 |
| employees | 30 | 1 |
| users | 33 | 1 |
| vouchers / promotions | 30 / 30 | 0 / 0 |
| categories | 30 | 3 |
| sizes | 30 | 3 |
| chat_lieu | 30 | 8 |
| roles | 2 | 2 |
| promotion_details | 0 | 0 |

Sau chạy lại test/build và smoke đăng ký, tài khoản smoke đã tự xóa. SELECT COUNT_BIG độc lập cuối cùng vẫn đúng bảng trên. Giữ admin hoạt động cùng NV-SYSTEM, 3 thương hiệu nền, size 39/40/41, 8 chất liệu nền và 2 role.

Backup giữ tại `C:\Program Files\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQL\Backup\shop_verify_before_cleanup_20261023.bak`.

### Giữ nguyên và chỉnh sửa tối thiểu

Đã so SHA-256 cho **236 file** thuộc src, .mvn, .idea, pom.xml, wrappers, cấu hình local và script schema. Ở bước dọn, tất cả giữ nguyên. Kiểm tra UI trống sau đó phát hiện canvas rỗng Dashboard gây tràn ngang tablet (scrollWidth=793 tại width=768). Chỉ thêm **hai điều kiện th:if** tại admin/bang-dieu-khien.html để không render hai canvas không có dữ liệu; JavaScript vốn đã bỏ qua hai biểu đồ này khi rỗng. Sau sửa, 235 file bảo vệ khác vẫn giữ nguyên hash; không thay Java, CSS, JS, cấu hình hoặc migration. DESIGN.md ghi lại quy tắc trạng thái trống.

Giữ toàn bộ src/test vì đây là bộ kiểm thử hồi quy bảo vệ dự án; chỉ xóa output do test sinh ra. Giữ .idea, .mvn, mvnw, pom.xml, cấu hình local đang chạy, database scripts (kể cả script cũ chưa chắc được phép bỏ), README và tài liệu cấu trúc/triển khai. Những file đã bị xóa hoặc sửa trước lượt này không bị khôi phục hay gom vào commit.

### Nghiệm thu sau dọn

| Hạng mục | Cách kiểm tra | Kết quả thực tế | Trạng thái |
|---|---|---|---|
| Build trước/sau sửa empty state | mvnw.cmd clean package trên shop_verify, không skip | Cả hai: 101 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS | PASS |
| Khởi động JAR sau dọn output test | recovery-start.py | Cổng 8080, Started 18.255 giây; GET / HTTP 200; tiến trình tiếp tục chạy | PASS |
| Trang trống | 42 URL công khai/Admin bằng curl.exe + cookie + CSRF | Không 4xx/5xx ở route hợp lệ; Dashboard, POS, Statistics, Inventory hoạt động khi không có dữ liệu | PASS |
| Đăng ký/đăng nhập và quyền | Tài khoản cleanup_smoke, xóa sau kiểm tra | Orders/Profile/Cart 200; checkout giỏ trống redirect cart; User vào admin/pos/account 403; POST POS thiếu CSRF 403; logout xong Orders redirect login | PASS |
| Responsive | Playwright, Home/Products/Cart/Login/Dashboard/Inventory/POS/Statistics × 375/768/1280 | 24 lượt đều HTTP 200; scrollWidth=360/753/1265, không vượt innerWidth; lỗi canvas tablet đã sửa và chạy lại toàn bộ lượt browser | PASS |
| Console/tài nguyên | Bắt console error, pageerror và response >=400 trong 8 trang chính | errors=[], failed=[] | PASS |
| Dữ liệu test còn sót | COUNT_BIG 19 bảng sau tất cả test/smoke | Đúng bảng trước/sau; chỉ admin và dữ liệu nền; ảnh fixture đã không còn trong DB/thư mục upload | PASS |
| Không đổi code chính | SHA-256 trước/sau | 235 file giữ nguyên; chỉ 1 template có đúng 2 dòng sửa đã nêu | PASS |
| Mọi luồng có dữ liệu được test lại thủ công | Không tạo lại toàn bộ catalog/đơn hàng sau dọn | Bộ 101 test đã chạy; không coi kiểm tra trạng thái trống là tái nghiệm thu mọi nghiệp vụ | NOT TESTED |

Bằng chứng tóm tắt ngoài project: cleanup-before-counts.*, cleanup-final-actual-counts.*, cleanup-final-proof.json, cleanup-transaction.sql/.txt, cleanup-deleted-files.json, cleanup-deleted-sensitive.json, cleanup-clean-package.log, cleanup-smoke.json. Hash gốc: cleanup-protected-before.json. Không dùng bằng chứng này để cam kết tuyệt đối rằng mọi lỗi chưa kiểm tra đều không tồn tại.
