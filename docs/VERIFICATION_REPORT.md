# Nghiệm thu tổng — giai đoạn 5

Ngày 23/10/2026. Nhánh `don-dep`. Chỉ chạy trên database `shop_verify`, profile `verify`, JDK 17. Không merge.

Tổng các kiểm tra được ghi riêng: 57; PASS=56; FAIL=0; NOT TESTED=1. NOT TESTED là in giấy vật lý; phần hóa đơn PDF/print media đã PASS.

## Môi trường và phạm vi

- Database kiểm thử đã được backup COPY_ONLY/CHECKSUM rồi làm rỗng cả 19 bảng trong transaction. Giữ schema hiện có, không tạo dữ liệu mẫu sản phẩm/khách/đơn; DataInitializer chỉ dựng danh mục cơ bản và admin.
- Dừng JAR trước build; chạy `mvnw clean test` và `mvnw clean package` riêng, không `-DskipTests`; JDBC luôn override tới shop_verify, ddl-auto=none. Chờ log Started trước mọi test HTTP.
- Luồng form dùng curl.exe với cookie jar riêng, lấy CSRF thật; tài khoản đăng ký mới `accept_user`, `accept_other`. Kiểm tra UI chất liệu và hóa đơn in dùng Chrome thật qua Playwright CLI.
- Dữ liệu phân trang được tạo bằng SQL fixture trong shop_verify, được đánh dấu PG5; các đơn này là dữ liệu tổng hợp để kiểm tra danh sách/thống kê, không được coi là bằng chứng đặt hàng qua OrderService.
- Bằng chứng ngoài project: `C:/Users/ADMIN/AppData/Local/Temp/acceptance-20261023/`. Không đưa cookie/password/log HTML vào git.
- Database shop_verify được giữ lại với dữ liệu nghiệm thu để bạn kiểm tra. Dữ liệu verify trước nghiệm thu có backup tại đường dẫn trong `backup-path.txt`.

Các lệnh thực tế chính:

```powershell
& 'C:/Users/ADMIN/AppData/Local/Temp/packages-20261023/run.ps1' -Mode test
& 'C:/Users/ADMIN/AppData/Local/Temp/packages-20261023/run.ps1' -Mode build
& 'C:/Users/ADMIN/.cache/codex-runtimes/codex-primary-runtime/dependencies/python/python.exe' 'C:/Users/ADMIN/AppData/Local/Temp/acceptance-20261023/common.py' start
# start chạy java -jar target/DemoDATN-0.0.1-SNAPSHOT.jar
# --spring.profiles.active=verify --spring.jpa.hibernate.ddl-auto=none --server.port=8081
# scripts: flows.py, metrics-security.py, pagination-check.py, two-products.py, rollback-check.py, final-check.py
```

## Bảng nghiệm thu

| Hạng mục | Cách kiểm tra / bằng chứng | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| Đăng ký + Admin tạo chất liệu/sản phẩm/2 biến thể | results.json và SQL/output trong mục chi tiết | {"material":933,"material2":851,"category":3127,"size":3127,"product":5868,"variants":[5601,5602]} | PASS |
| User COD đặt/hủy | flows-after-fix.log + SQL cùng tên | {"order":4945,"before":{"5601":20,"5602":20},"placed":{"5601":20,"5602":20},"after":{"5601":20,"5602":20},"materialSnapshots":true} | PASS |
| User BANK_TRANSFER đặt/hủy | flows-after-fix.log + SQL cùng tên | {"order":4946,"before":{"5601":20,"5602":20},"placed":{"5601":19,"5602":19},"after":{"5601":20,"5602":20},"materialSnapshots":true} | PASS |
| Admin COD xác nhận -> hoàn tất | flows-after-fix.log + SQL cùng tên | {"order":4947,"stock":{"5601":19,"5602":19},"status":{"id":4947,"status":"DELIVERED","payment_status":"PAID","da_tru_kho":true,"final_amount":500000.0}} | PASS |
| Giao hàng thất bại hoàn kho | flows-after-fix.log + SQL cùng tên | {"order":4948,"before":{"5601":19,"5602":19},"after":{"5601":19,"5602":19},"status":{"id":4948,"status":"DELIVERY_FAILED","payment_status":"UNPAID","da_tru_kho":false,"final_amount":500000.0}} | PASS |
| POS có chất liệu + Order/Invoice/kho | flows-after-fix.log + SQL cùng tên | {"order":4949,"invoice":4268,"before":{"5601":19,"5602":19},"after":{"5601":18,"5602":19},"final":200000.0} | PASS |
| Dashboard 6 card khớp SQL | metrics-security-final.log + http.json | {"html":{"todayRevenue":"700000.00","monthRevenue":"700000.00","orders":"37","pending":"0","products":"30","customers":"30"},"sql":{"todayRevenue":700000.0,"monthRevenue":700000.0,"orders":37,"pending":0,"products":30,"customers":30}} | PASS |
| Thống kê doanh thu/số đơn/trung bình/top/bộ lọc | metrics-security-final.log + http.json | {"channels":[{"channel":"ALL","html":{"revenue":"700000","orders":"2","average":"350000"},"sql":{"revenue":700000.0,"orders":2,"average":350000.0}},{"channel":"ONLINE","html":{"revenue":"500000","orders":"1","average":"500000"},"sql":{"revenue":500000.0,"orders":1,"average":500000.0}},{"channel":"POS","html":{"revenue":"200000","orders":"1","average":"200000"},"sql":{"revenue":200000.0,"orders":1,"average":200000.0}}],"top":[{"name":"Giày nghiệm thu Đà Nẵng","quantity":3,"amount":700000.0}],"empty":true} | PASS |
| SEC-01 anonymous | metrics-security-final.log + http.json | {"login":200,"orders":302,"admin":302} | PASS |
| SEC-02 sai mật khẩu | metrics-security-final.log + http.json | {"id":548,"role":"bad","url":"/login","method":"POST","status":302,"location":"http://localhost:8081/login?error","body":"0548-bad.html"} | PASS |
| SEC-03 admin login/dashboard | metrics-security-final.log + http.json | {"login":{"id":550,"role":"freshadmin","url":"/login","method":"POST","status":302,"location":"http://localhost:8081/admin","body":"0550-freshadmin.html"},"dashboard":{"id":551,"role":"freshadmin","url":"/admin","method":"GET","status":200,"location":"","body":"0551-freshadmin.html"}} | PASS |
| SEC-04 user login/orders | metrics-security-final.log + http.json | {"login":{"id":553,"role":"freshuser","url":"/login","method":"POST","status":302,"location":"http://localhost:8081/","body":"0553-freshuser.html"},"orders":{"id":554,"role":"freshuser","url":"/orders","method":"GET","status":200,"location":"","body":"0554-freshuser.html"}} | PASS |
| SEC-05 user bị chặn admin | metrics-security-final.log + http.json | [{"url":"/admin","status":403},{"url":"/admin/account","status":403},{"url":"/admin/pos","status":403},{"url":"/admin/products-crud","status":403},{"url":"/admin/materials","status":403},{"url":"/admin/inventory","status":403},{"url":"/admin/orders","status":403},{"url":"/admin/invoices","status":403},{"url":"/admin/statistics","status":403},{"url":"/admin/anything/deep","status":403}] | PASS |
| SEC-06 vô hiệu hóa không đăng nhập/không giữ session | metrics-security-final.log + http.json | {"login":{"id":566,"role":"disabled","url":"/login","method":"POST","status":302,"location":"http://localhost:8081/login?error","body":"0566-disabled.html"},"existingSession":{"id":567,"role":"other","url":"/orders","method":"GET","status":302,"location":"http://localhost:8081/login","body":"0567-other.html"}} | PASS |
| SEC-07 POST thiếu CSRF | metrics-security-final.log + http.json | {"userCart":403,"adminPos":403,"adminStatus":403} | PASS |
| SEC-08 logout chặn truy cập lại | metrics-security-final.log + http.json | {"logout":{"id":572,"role":"freshuser","url":"/logout","method":"POST","status":302,"location":"http://localhost:8081/login?logout","body":"0572-freshuser.html"},"ordersAfter":{"id":573,"role":"freshuser","url":"/orders","method":"GET","status":302,"location":"http://localhost:8081/login","body":"0573-freshuser.html"}} | PASS |
| Không đọc/hủy đơn người khác | metrics-security-final.log + http.json | {"detail":403,"success":403,"cancel":403,"stockUnchanged":true} | PASS |
| Hoàn tất không thể hủy | results.json và SQL/output trong mục chi tiết | {"status":{"id":4947,"status":"DELIVERED","payment_status":"PAID","da_tru_kho":true,"final_amount":500000.0},"stock":{"5601":18,"5602":19}} | PASS |
| Tiếng Việt ghi -> SQL -> HTML | unicode-only.log / unicode-roundtrip-verified.sql/.txt | {"rows":[{"name":"Giày nghiệm thu Đà Nẵng","description":"Mô tả tiếng Việt: bền, nhẹ, êm ái.","ten":"Vải dệt kiểm thử Đà Nẵng","full_name":"Nguyễn Thị Ánh","receiver_name":"Nguyễn Thị Ánh","shipping_address":"Số 12, đường Nguyễn Huệ, phường Bến Nghé, Hồ Chí Minh","ten_chat_lieu":"Vải dệt kiểm thử Đà Nẵng"},{"name":"Giày nghiệm thu Đà Nẵng","description":"Mô tả tiếng Việt: bền, nhẹ, êm ái.","ten":"Da thật","full_name":"Nguyễn Thị Ánh","receiver_name":"Nguyễn Thị Ánh","shipping_address":"Số 12, đường Nguyễn Huệ, phường Bến Nghé, Hồ Chí Minh","ten_chat_lieu":"Da thật"}],"note":{"id":31,"note":"Ghi chú: khách Nguyễn muốn nhận tại Đà Nẵng"},"htmlD… (chi tiết trong log/JSON) | PASS |
| Phân trang: Sản phẩm Admin | HTTP /admin/products-crud?status=true&categoryId=3127&sort=name; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Sản phẩm User | HTTP /products?categoryId=3127&materialId=933&sort=price_asc; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Danh mục User | HTTP /category/3127?sort=price_asc; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Tìm kiếm User | HTTP /search?keyword=Gi%C3%A0y&sort=price_asc; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Khách hàng | HTTP /admin/customers; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Nhân viên | HTTP /admin/employees; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Đơn Admin | HTTP /admin/orders?q=PG5&status=DELIVERED; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Đơn User | HTTP /orders; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Hóa đơn | HTTP /admin/invoices?q=PG5&from=2025-01-01&to=2025-01-30; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Danh mục | HTTP /admin/categories; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Size | HTTP /admin/sizes; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Chất liệu | HTTP /admin/materials; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Voucher | HTTP /admin/vouchers; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Đợt giảm giá | HTTP /admin/promotions; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Chọn sản phẩm promotion | HTTP /admin/promotions/3491/products; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Biến thể | HTTP /admin/variants?materialId=933; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Tồn kho | HTTP /admin/inventory?materialId=933&stock=all; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Lịch sử kho | HTTP /admin/inventory?variantId=5631; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=1 (dòng log không hiển thị id; xác nhận ID riêng bằng SQL bên dưới) | PASS |
| Phân trang: Lưới POS | HTTP /admin/pos; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Tìm khách POS | HTTP /admin/pos/customers?customerQuery=; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Thống kê theo ngày | HTTP /admin/statistics?from=2025-01-01&to=2025-01-30&channel=ONLINE; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Top số lượng | HTTP /admin/statistics/best-sellers?from=2025-01-01&to=2025-01-30&channel=ONLINE; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Phân trang: Top doanh thu | HTTP /admin/statistics/best-sellers?from=2025-01-01&to=2025-01-30&channel=ONLINE; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| User hai sản phẩm khác nhau/khác chất liệu: COD | two-products.log + two-products-*.sql/.txt | {"variants":[5602,5603],"order":4980,"before":{"5602":19,"5603":20},"placed":{"5602":19,"5603":20},"after":{"5602":19,"5603":20}} | PASS |
| User hai sản phẩm khác nhau/khác chất liệu: BANK_TRANSFER | two-products.log + two-products-*.sql/.txt | {"variants":[5602,5603],"order":4981,"before":{"5602":19,"5603":20},"placed":{"5602":18,"5603":19},"after":{"5602":19,"5603":20}} | PASS |
| Phân trang: Lịch sử đơn khách | HTTP /admin/customers/detail/3500; pagination-results.json | [12, 12, 6]; HTTP 200; -1/abc→12, 99999→6; giữ lọc; số dòng phân biệt=30 | PASS |
| Checkout thiếu hàng không tạo dữ liệu | rollback-check.log + rollback-*.sql/.txt | {"http":{"id":484,"role":"user","url":"/checkout/place","method":"POST","status":302,"location":"http://localhost:8081/cart","body":"0484-user.html"},"countersBefore":{"orders":37,"details":43,"invoices":37,"logs":47},"countersAfter":{"orders":37,"details":43,"invoices":37,"logs":47},"stockRestored":{"5601":18,"5602":19}} | PASS |
| POS thiếu hàng rollback đầy đủ | rollback-check.log + rollback-*.sql/.txt | {"http":{"id":497,"role":"admin","url":"/admin/pos/checkout","method":"POST","status":302,"location":"http://localhost:8081/admin/pos","body":"0497-admin.html"},"countersBefore":{"orders":37,"details":43,"invoices":37,"logs":47},"countersAfter":{"orders":37,"details":43,"invoices":37,"logs":47},"stockRestored":{"5601":18,"5602":19}} | PASS |
| Đúng12 bản ghi không hiện phân trang | results.json và SQL/output trong mục chi tiết | {"url":"/admin/products-crud?keyword=%C4%90%C3%9ANG12","count":12,"paginationVisible":false} | PASS |
| JAR cuối + crawl + Invoice 1-1 | final-check.log / final-crawl.json | {"urls":31,"http500":0,"invoiceMismatches":0,"profile":"verify","database":"shop_verify"} | PASS |
| Lịch sử kho ID SQL không trùng trang | results.json và SQL/output trong mục chi tiết | {"pages":[[5139,5138,5137,5136,5135,5134,5133,5132,5131,5130,5129,5128],[5127,5126,5125,5124,5123,5122,5121,5120,5119,5118,5117,5116],[5115,5114,5113,5112,5111,5110]],"counts":[12,12,6],"unique":30} | PASS |
| Database kiểm thử khởi đầu rỗng | results.json và SQL/output trong mục chi tiết | {"tables":19,"allCounts":0,"evidence":"zero.json / reset.sql / backup.txt","database":"shop_verify"} | PASS |
| Hóa đơn in qua Chrome/PDF | material-ui.log / print-check.log / console-*.log | {"desktop":"hoa-don-desktop.png","print":"hoa-don-print.png","pdf":"hoa-don-in.pdf","dimensions":{"width":1280,"scroll":1265},"material":true} | PASS |
| UI chọn chất liệu + responsive sản phẩm | material-ui.log / print-check.log / console-*.log | {"cloth":{"variant":5601,"price":200000,"stock":18},"leather":{"variant":5602,"price":300000,"stock":19},"width1280":1265,"width375":360,"consoleErrors":0} | PASS |
| Maven clean test cuối | final-clean-test.log / final-clean-package.log | {"tests":100,"failures":0,"errors":0,"skipped":0,"log":"final-clean-test.log"} | PASS |
| Maven clean package cuối | final-clean-test.log / final-clean-package.log | {"tests":100,"failures":0,"errors":0,"skipped":0,"log":"final-clean-package.log"} | PASS |
| Migration 007 idempotent | results.json và SQL/output trong mục chi tiết | {"runs":2,"errors":0,"files":["migration007-first.txt","migration007-second.txt"]} | PASS |
| In ra máy in vật lý | results.json và SQL/output trong mục chi tiết | "Không có máy in được cung cấp; đã nghiệm thu PDF và print media bằng Chrome." | NOT TESTED |

## Lỗi phát hiện, sửa và chạy lại

### DEF-NT5-01 — HIGH — mất dấu khi ghi database

Các cột products.name/description, customers.full_name/address, orders.receiver_name/shipping_address, tên snapshot/hóa đơn còn VARCHAR. Chất liệu NVARCHAR vẫn đúng, nên có bằng chứng phân biệt lỗi kiểu cột với lỗi giải mã request.

Output trước sửa (`before-fix/unicode-roundtrip.txt`):

```text
[{"name":"Giày nghi?m thu Ðà N?ng","description":"Mô t? ti?ng Vi?t: b?n, nh?, êm ái.","ten":"Vải dệt kiểm thử Đà Nẵng","full_name":"Nguy?n Th? Ánh","receiver_name":"Nguy?n Th? Ánh","shipping_address":"S? 12, du?ng Nguy?n Hu?, phu?ng B?n Nghé, H? Chí Minh","ten_chat_lieu":"Vải dệt kiểm thử Đà Nẵng"},{"name":"Giày nghi?m thu Ðà N?ng","description":"Mô t? ti?ng Vi?t: b?n, nh?, êm ái.","ten":"Da thật","full_name":"Nguy?n Th? Ánh","receiver_name":"Nguy?n Th? Ánh","shipping_address":"S? 12, du?ng Nguy?n Hu?, phu?ng B?n Nghé, H? Chí Minh","ten_chat_lieu":"Da thật"}]
```

Sửa `database/migrations/007_tieng_viet_unicode.sql:1`: đổi các cột văn bản hiển thị còn VARCHAR sang NVARCHAR, giữ số ký tự và NULL/NOT NULL; không đổi mã, enum, route, giá, kho. Migration được chạy hai lần trên shop_verify; lần hai không lỗi. Dữ liệu đã mất dấu không tự phục hồi, nên nghiệm thu được chạy lại với dữ liệu mới sau migration.

Thêm `src/test/java/com/example/demodatn/entity/UnicodePersistenceIntegrationTest.java:20`: EntityManager persist/flush/clear/find tên, địa chỉ, mô tả rồi assert chuỗi gốc. Không chỉ kiểm tra code tồn tại.

### DEF-NT5-02 — MEDIUM — fixture phụ thuộc dữ liệu mẫu

Build trên DB rỗng lỗi 5 test: 3 DiscountIntegrationTest lấy customer đầu tiên, DashboardIntegrationTest lấy variant có sẵn, StatisticsIntegrationTest lấy hai variant của hai sản phẩm có sẵn. Chỉ sửa fixture; giữ các assertion nghiệp vụ và test count cũ, thêm 1 regression Unicode nên tổng tăng từ 99 thành 100.

Vị trí sửa:

- `src/test/java/com/example/demodatn/service/khuyenmai/DiscountIntegrationTest.java:62`: tự tạo khách trong transaction rollback.
- `src/test/java/com/example/demodatn/service/thongke/DashboardIntegrationTest.java:47`: tự tạo sản phẩm/variant cho test ngưỡng tồn.
- `src/test/java/com/example/demodatn/service/thongke/StatisticsIntegrationTest.java:30`: tự tạo hai variant ở hai sản phẩm.

Output trước sửa fixture:

```text
[ERROR]   DiscountIntegrationTest.checkoutAndPosConsumeSamePercentageFormulaAndReferencedDeleteLocksVoucher:111 � IndexOutOfBounds Index 0 out of bounds for length 0
[ERROR]   DiscountIntegrationTest.promotionSelectionAndExpirationRepriceCartAndOrderSnapshotsRemain:103 � IndexOutOfBounds Index 0 out of bounds for length 0
[ERROR]   DiscountIntegrationTest.voucherCasesHaveSpecificReasonsAndCorrectCapsWithoutConsuming:81 � IndexOutOfBounds Index 0 out of bounds for length 0
[ERROR]   DashboardIntegrationTest.lowStockIncludesThresholdAndZeroButNotInactiveVariants:47 � EmptyResultDataAccess Incorrect result size: expected 1, actual 0
[ERROR]   StatisticsIntegrationTest.metricsRankingsBoundariesChannelsAndStatusesUseSnapshotMoney:39 � IndexOutOfBounds Index 0 out of bounds for length 0
[ERROR] Tests run: 100, Failures: 0, Errors: 5, Skipped: 0
```

Sau sửa: clean package trên dữ liệu trống đạt 100/0/0/0; khởi động lại JAR và chạy lại COD, chuyển khoản, trạng thái Admin, POS, SQL và tiếng Việt. Cuối cùng chạy riêng clean test + clean package, JAR mới và bảo mật/metrics/crawl lần nữa.

### Lỗi của script nghiệm thu, không phải lỗi ứng dụng

Đã điều chỉnh encoding stdout Python UTF-8; sửa tên cột fixture sizes không có status; bỏ tên biến che hàm all; so ghi chú bằng HTML đã giải mã entity; lấy CSRF từ cart khi checkout thiếu hàng redirect. Không sửa ứng dụng để né các lỗi script. Tồn kho ép về 0 đã được phục hồi đúng số SQL trước test.

## User và Admin: SQL trước/sau

Hai biến thể cùng sản phẩm, cùng size 39, khác chất liệu: 5601 (Vải dệt kiểm thử Đà Nẵng, 200000), 5602 (Da thật, 300000), ban đầu 20/20. Checkout có hai dòng; tổng 500000, miễn phí giao hàng theo cấu hình. SQL được dùng trong từng snapshot:

```sql
SELECT id,quantity FROM product_variants WHERE id IN (5601,5602) ORDER BY id; SELECT id,order_code,order_type,status,payment_method,payment_status,da_tru_kho,total_amount,shipping_fee,discount_amount,final_amount FROM orders ORDER BY id; SELECT id,order_id,product_variant_id,ten_chat_lieu,quantity,price,total_price FROM order_details ORDER BY id; SELECT id,order_id FROM invoices ORDER BY id; SELECT * FROM inventory_logs ORDER BY id;
```

Trích nguyên dòng tồn kho và dòng đơn tương ứng từ output đã lưu (đủ output gốc nằm trong file .txt cùng tên):

**cod_cancel-before.txt**

```text
5601 20
5602 20
```

**cod_cancel-placed.txt**

```text
5601 20
5602 20
4945 DH004945 ONLINE PENDING COD UNPAID 0 500000.00 .00 .00 500000.00
```

**cod_cancel-cancelled.txt**

```text
5601 20
5602 20
4945 DH004945 ONLINE CANCELLED COD UNPAID 0 500000.00 .00 .00 500000.00
```

**bank_cancel-before.txt**

```text
5601 20
5602 20
```

**bank_cancel-placed.txt**

```text
5601 19
5602 19
4946 DH004946 ONLINE PENDING BANK_TRANSFER UNPAID 1 500000.00 .00 .00 500000.00
```

**bank_cancel-cancelled.txt**

```text
5601 20
5602 20
4946 DH004946 ONLINE CANCELLED BANK_TRANSFER UNPAID 0 500000.00 .00 .00 500000.00
```

**admin-cod-pending.txt**

```text
5601 20
5602 20
4947 DH004947 ONLINE PENDING COD UNPAID 0 500000.00 .00 .00 500000.00
```

**admin-cod-confirmed.txt**

```text
5601 19
5602 19
4947 DH004947 ONLINE CONFIRMED COD UNPAID 1 500000.00 .00 .00 500000.00
```

**admin-cod-delivered.txt**

```text
5601 19
5602 19
4947 DH004947 ONLINE DELIVERED COD PAID 1 500000.00 .00 .00 500000.00
```

**admin-delivery-failed.txt**

```text
5601 19
5602 19
4948 DH004948 ONLINE DELIVERY_FAILED COD UNPAID 0 500000.00 .00 .00 500000.00
```

**pos-before.txt**

```text
5601 19
5602 19
```

**pos-after.txt**

```text
5601 18
5602 19
4949 DH004949 POS CONFIRMED CASH PAID 1 200000.00 .00 .00 200000.00
```

Confirm lần hai được gửi thật, stock vẫn 19/19. Luồng giao thất bại đi đầy đủ CONFIRMED → PREPARING → SHIPPING → DELIVERY_FAILED; không nhảy trực tiếp từ CONFIRMED. DELIVERED → CANCELLED bị từ chối, trạng thái/kho giữ nguyên.

Log kho nguyên văn (`pos-after.txt`; schema output SELECT * đặt quantity_after trước quantity_before/quantity_change):

```text
5099 accept_user 2026-10-23 20:31:40.5116427 19 20 -1 Trừ kho đơn DH004946 · ONLINE / BANK_TRANSFER 5601
5100 accept_user 2026-10-23 20:31:40.5146432 19 20 -1 Trừ kho đơn DH004946 · ONLINE / BANK_TRANSFER 5602
5101 accept_user 2026-10-23 20:31:41.6676435 20 19 1 Hoàn kho đơn DH004946 · hủy hoặc giao hàng thất bại 5601
5102 accept_user 2026-10-23 20:31:41.6736435 20 19 1 Hoàn kho đơn DH004946 · hủy hoặc giao hàng thất bại 5602
5103 admin 2026-10-23 20:31:43.5212720 19 20 -1 Trừ kho đơn DH004947 · ONLINE / COD 5601
5104 admin 2026-10-23 20:31:43.5252714 19 20 -1 Trừ kho đơn DH004947 · ONLINE / COD 5602
5105 admin 2026-10-23 20:31:46.7747247 18 19 -1 Trừ kho đơn DH004948 · ONLINE / COD 5601
5106 admin 2026-10-23 20:31:46.7777257 18 19 -1 Trừ kho đơn DH004948 · ONLINE / COD 5602
5107 admin 2026-10-23 20:31:47.6028153 19 18 1 Hoàn kho đơn DH004948 · hủy hoặc giao hàng thất bại 5601
5108 admin 2026-10-23 20:31:47.6058159 19 18 1 Hoàn kho đơn DH004948 · hủy hoặc giao hàng thất bại 5602
5109 admin 2026-10-23 20:31:49.2900802 18 19 -1 Trừ kho đơn DH004949 · POS / CASH 5601
```

Bổ sung đúng hai sản phẩm khác nhau, khác chất liệu: variants 5602 và 5603. COD không đổi kho; BANK_TRANSFER từ 19/20 → 18/19 → hủy về 19/20. Mã đơn 4980 và 4981; HTTP success/detail 200, POST hủy 302. Bằng chứng `two-products-*.sql/.txt`, `two-products.log`.

POS đơn 4949: tiền hàng=200000, voucher=0, vận chuyển=0, tổng=200000; khách đưa=500000, tiền thừa=300000. Order/Detail/Invoice tạo đủ; hóa đơn id=4268. Kiểm tra sau cùng mọi Order có đúng 1 Invoice: SELECT HAVING COUNT(i.id)<>1 trả 0 dòng (`final-invoices-integrity.sql/.txt`).

## Dashboard và Thống kê đối chiếu SELECT độc lập

Quy tắc đang dùng: doanh thu = SUM(final_amount) của PAID và status<>CANCELLED, theo ngày tạo; gồm phí giao hàng. Thống kê trạng thái gồm cả chưa thanh toán/hủy. Top sản phẩm cộng giá dòng snapshot trước voucher và phí. Trang có ghi rõ quy tắc; không thay đổi quy tắc trong giai đoạn này.

Dashboard sau fixture phân trang và các đơn User hủy:

```sql
SELECT (SELECT COALESCE(SUM(final_amount),0) FROM orders WHERE payment_status='PAID' AND status<>'CANCELLED' AND CAST(created_at AS date)=CAST(SYSDATETIME() AS date)) todayRevenue,(SELECT COALESCE(SUM(final_amount),0) FROM orders WHERE payment_status='PAID' AND status<>'CANCELLED' AND YEAR(created_at)=YEAR(SYSDATETIME()) AND MONTH(created_at)=MONTH(SYSDATETIME())) monthRevenue,(SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM orders WHERE status='PENDING') pending,(SELECT COUNT(*) FROM products) products,(SELECT COUNT(*) FROM customers) customers
```

```text
[{"todayRevenue":700000.00,"monthRevenue":700000.00,"orders":37,"pending":0,"products":30,"customers":30}]
```

HTML thực tế: `{"todayRevenue": "700000.00", "monthRevenue": "700000.00", "orders": "37", "pending": "0", "products": "30", "customers": "30"}`. Assert từng khóa bằng kết quả SELECT, không suy ra từ code.

**Kênh ALL**

```sql
SELECT COALESCE(SUM(final_amount),0) revenue,COUNT_BIG(*) orders,CAST(COALESCE(AVG(final_amount),0) AS decimal(18,2)) average FROM orders WHERE payment_status='PAID' AND status<>'CANCELLED' AND created_at>=DATEADD(day,-29,CAST(CAST(SYSDATETIME() AS date) AS datetime2)) AND created_at<DATEADD(day,1,CAST(CAST(SYSDATETIME() AS date) AS datetime2))
```

```text
[{"revenue":700000.00,"orders":2,"average":350000.00}]
```

**Kênh ONLINE**

```sql
SELECT COALESCE(SUM(final_amount),0) revenue,COUNT_BIG(*) orders,CAST(COALESCE(AVG(final_amount),0) AS decimal(18,2)) average FROM orders WHERE payment_status='PAID' AND status<>'CANCELLED' AND created_at>=DATEADD(day,-29,CAST(CAST(SYSDATETIME() AS date) AS datetime2)) AND created_at<DATEADD(day,1,CAST(CAST(SYSDATETIME() AS date) AS datetime2)) AND order_type='ONLINE'
```

```text
[{"revenue":500000.00,"orders":1,"average":500000.00}]
```

**Kênh POS**

```sql
SELECT COALESCE(SUM(final_amount),0) revenue,COUNT_BIG(*) orders,CAST(COALESCE(AVG(final_amount),0) AS decimal(18,2)) average FROM orders WHERE payment_status='PAID' AND status<>'CANCELLED' AND created_at>=DATEADD(day,-29,CAST(CAST(SYSDATETIME() AS date) AS datetime2)) AND created_at<DATEADD(day,1,CAST(CAST(SYSDATETIME() AS date) AS datetime2)) AND order_type='POS'
```

```text
[{"revenue":200000.00,"orders":1,"average":200000.00}]
```

HTML: ALL=700000 / 2 / 350000; ONLINE=500000 / 1 / 500000; POS=200000 / 1 / 200000 (doanh thu / số đơn / trung bình). Top: 3 món, 700000 tiền dòng. Bộ lọc năm 1999 có empty state, không hiện biểu đồ doanh thu rỗng.

```text
[{"name":"Giày nghiệm thu Đà Nẵng","quantity":3,"amount":700000.00}]
```

## Phân trang

Fixture có đúng 30 sản phẩm, 30 khách, 30 đơn PG5; còn giữ 5 đơn nghiệp vụ và 2 đơn hủy bổ sung. Do đó danh sách Admin dùng q=PG5, User dùng tài khoản page_user riêng có đúng 30 đơn. Tổng mọi đơn không được nói là 30. Tạo thêm 30 danh mục/size/chất liệu/nhân viên/voucher/promotion hoặc đủ tổng 30 để kiểm tra từng danh sách; bộ lọc chất liệu cho variants/inventory trả đúng 30. SQL dữ liệu: pagination-seed.sql; chỉ shop_verify.

```text
[{"products":30,"customers":30,"orders":30,"variants":30,"logs":30,"categories":30,"sizes":30,"materials":30,"employees":30,"vouchers":30,"promotions":30}]
```

24 danh sách gồm User products/category/search/orders; Admin products/customer/employee/orders/customer history/invoices/category/size/material/voucher/promotion/promotion product selection/variants/inventory/history/POS grid/customer search/statistics daily/top quantity/top revenue. Mỗi trang 12/12/6, không trùng dữ liệu nhận diện giữa trang. Riêng lịch sử kho không hiển thị log ID nên dùng SELECT độc lập để chứng minh ID; các dòng có thể giống nội dung. -1 và abc về đầu, 99999 về cuối, đều HTTP 200. Link giữ mọi tham số hiện có. Đúng 12 bản ghi không render fragment phân trang (rollback-check.log).

Ngoại lệ có chủ đích theo yêu cầu giai đoạn 1: Home/Dashboard khối cố định, sản phẩm liên quan, tối đa 5 hóa đơn chờ; không tính vào 12/12/6.

## Bảo mật

| Mã | Kịch bản | Output thật |
|---|---|---|
| SEC-01 | Anonymous | /login 200; /orders và /admin 302 → /login |
| SEC-02 | Admin sai mật khẩu | POST /login 302 → /login?error |
| SEC-03 | Admin đúng mật khẩu | POST /login 302 → /admin; GET /admin 200 |
| SEC-04 | User đúng mật khẩu | POST /login 302 → /; /orders 200 |
| SEC-05 | User vào 10 route admin, kể cả route sâu không tồn tại | 403 cho từng route |
| SEC-06 | Tài khoản bị vô hiệu hóa | Login 302 ?error; session cũ 302 /login?disabled; đã bật lại tài khoản fixture |
| SEC-07 | POST không CSRF | /cart/add, /admin/pos/add, /admin/orders/4947/status đều 403 |
| SEC-08 | Logout POST có CSRF | 302 /login?logout; sau đó /orders 302 /login |

accept_other truy cập /orders/4947, /orders/success/4947, POST /orders/4947/cancel có CSRF: đều 403; kho không đổi. SQL employee/User mồ côi cuối cùng: `0 0`. Xem metrics-security-final.log, ownership-before/after.sql/.txt, final-account-integrity.sql/.txt; http.json lưu từng method/status/Location.

## Tiếng Việt và bản in

Output SQL sau migration và chạy lại form:

```text
[{"name":"Giày nghiệm thu Đà Nẵng","description":"Mô tả tiếng Việt: bền, nhẹ, êm ái.","ten":"Vải dệt kiểm thử Đà Nẵng","full_name":"Nguyễn Thị Ánh","receiver_name":"Nguyễn Thị Ánh","shipping_address":"Số 12, đường Nguyễn Huệ, phường Bến Nghé, Hồ Chí Minh","ten_chat_lieu":"Vải dệt kiểm thử Đà Nẵng"},{"name":"Giày nghiệm thu Đà Nẵng","description":"Mô tả tiếng Việt: bền, nhẹ, êm ái.","ten":"Da thật","full_name":"Nguyễn Thị Ánh","receiver_name":"Nguyễn Thị Ánh","shipping_address":"Số 12, đường Nguyễn Huệ, phường Bến Nghé, Hồ Chí Minh","ten_chat_lieu":"Da thật"}]
```

```text
[{"id":31,"note":"Ghi chú: khách Nguyễn muốn nhận tại Đà Nẵng"}]
```

Ghi chú trong textarea có entity như &uacute;/&agrave; được trình duyệt giải mã đúng; script dùng html.unescape khi đối chiếu chuỗi. Không nhầm entity HTML hợp lệ với mojibake.

Playwright trên Chrome sẵn có (Chromium bundled chưa được cài; đã dùng --browser chrome thành công). Đã chọn hai chất liệu và size bằng combobox thật:

```text
5601: Vải dệt kiểm thử Đà Nẵng -> 200.000 ₫ -> Còn 18 sản phẩm
5602: Da thật -> 300.000 ₫ -> Còn 19 sản phẩm
1280px: scrollWidth=1265 <= innerWidth=1280
375px: scrollWidth=360 <= innerWidth=375
Console invoice: Total messages: 0 (Errors: 0, Warnings: 0)
Console product: Total messages: 0 (Errors: 0, Warnings: 0)
```

Ảnh/PDF ngoài project:

- [Sản phẩm desktop](C:/Users/ADMIN/AppData/Local/Temp/acceptance-20261023/san-pham-1280.png)
- [Sản phẩm mobile](C:/Users/ADMIN/AppData/Local/Temp/acceptance-20261023/san-pham-375.png)
- [Hóa đơn desktop](C:/Users/ADMIN/AppData/Local/Temp/acceptance-20261023/hoa-don-desktop.png)
- [Hóa đơn print media](C:/Users/ADMIN/AppData/Local/Temp/acceptance-20261023/hoa-don-print.png)
- [PDF hóa đơn](C:/Users/ADMIN/AppData/Local/Temp/acceptance-20261023/hoa-don-in.pdf)

Đã xem ảnh desktop và print: các cột sản phẩm/size/chất liệu/số lượng/giá/tổng đều có, không chồng chữ; print không hiện sidebar/topbar. Chưa test máy in vật lý vì không có thiết bị.

## Thiếu hàng và rollback

Ép một variant về quantity=0 trong shop_verify sau khi thêm vào giỏ, dùng CSRF hợp lệ. Checkout bị chặn ở bước kiểm tra giá/tồn trước lưu; đây là bằng chứng không tạo dữ liệu, không phải fault injection giữa transaction. POS cũng bị từ chối, giữ giỏ; không đổi Order/Detail/Invoice/InventoryLog và dòng còn hàng không bị trừ. Bộ test OrderRollbackIntegrationTest trong 100 bài còn kiểm tra exception giữa transaction; log Maven có kết quả bài.

Output đếm trước/sau hai luồng:

```text
{"http": {"id": 484, "role": "user", "url": "/checkout/place", "method": "POST", "status": 302, "location": "http://localhost:8081/cart", "body": "0484-user.html"}, "countersBefore": {"orders": 37, "details": 43, "invoices": 37, "logs": 47}, "countersAfter": {"orders": 37, "details": 43, "invoices": 37, "logs": 47}, "stockRestored": {"5601": 18, "5602": 19}}
```

```text
{"http": {"id": 497, "role": "admin", "url": "/admin/pos/checkout", "method": "POST", "status": 302, "location": "http://localhost:8081/admin/pos", "body": "0497-admin.html"}, "countersBefore": {"orders": 37, "details": 43, "invoices": 37, "logs": 47}, "countersAfter": {"orders": 37, "details": 43, "invoices": 37, "logs": 47}, "stockRestored": {"5601": 18, "5602": 19}}
```

SQL mô phỏng tồn=0 chỉ là fixture, không đi qua service; sau test đã phục hồi về 18/19. Có file force/restore-*.sql; không che giấu thao tác trực tiếp này như thay đổi của ứng dụng.

## Build cuối và khởi động từ JAR

**final-clean-test.log**

```text
[INFO] Tests run: 100, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  26.794 s
```

**final-clean-package.log**

```text
[INFO] Tests run: 100, Failures: 0, Errors: 0, Skipped: 0
[INFO] Building jar: C:\DuAnTNDEMO\DemoDATN\target\DemoDATN-0.0.1-SNAPSHOT.jar
[INFO] BUILD SUCCESS
[INFO] Total time:  26.876 s
```

```text
2026-10-23T20:43:49.611+07:00  INFO 26332 --- [           main] c.example.demodatn.DemoDatnApplication   : Started DemoDatnApplication in 7.027 seconds (process running for 7.412)
```

Sau JAR cuối: crawl 31 URL chính (final-crawl.json), tất cả 200/302 hợp lệ, không 500. Chạy lại toàn bộ 8 kịch bản bảo mật và đối chiếu Dashboard/Statistics trên JAR này. Bảng URL:

| Vai trò | URL | Status | Location |
|---|---|---:|---|
| user | `/` | 200 | — |
| user | `/products` | 200 | — |
| user | `/products/5868` | 200 | — |
| user | `/cart` | 200 | — |
| user | `/checkout` | 302 | http://localhost:8081/cart |
| user | `/orders` | 200 | — |
| user | `/orders/4947` | 200 | — |
| user | `/profile` | 200 | — |
| anon | `/login` | 200 | — |
| anon | `/register` | 200 | — |
| user | `/policy` | 200 | — |
| admin | `/admin` | 200 | — |
| admin | `/admin/account` | 200 | — |
| admin | `/admin/products` | 302 | http://localhost:8081/admin/products-crud |
| admin | `/admin/products-crud` | 200 | — |
| admin | `/admin/variants` | 200 | — |
| admin | `/admin/categories` | 200 | — |
| admin | `/admin/sizes` | 200 | — |
| admin | `/admin/materials` | 200 | — |
| admin | `/admin/inventory` | 200 | — |
| admin | `/admin/customers` | 200 | — |
| admin | `/admin/employees` | 200 | — |
| admin | `/admin/vouchers` | 200 | — |
| admin | `/admin/promotions` | 200 | — |
| admin | `/admin/orders` | 200 | — |
| admin | `/admin/orders/4947` | 200 | — |
| admin | `/admin/invoices` | 200 | — |
| admin | `/admin/invoices/4268` | 200 | — |
| admin | `/admin/pos` | 200 | — |
| admin | `/admin/statistics` | 200 | — |
| admin | `/admin/statistics/best-sellers` | 200 | — |

## File thay đổi và kết thúc

- `database/migrations/007_tieng_viet_unicode.sql`: sửa lỗi lưu tiếng Việt.
- `src/test/java/com/example/demodatn/entity/UnicodePersistenceIntegrationTest.java`: regression Unicode mới.
- Ba file fixture DiscountIntegrationTest, DashboardIntegrationTest, StatisticsIntegrationTest nêu trên.
- `VERIFICATION_REPORT.md`: báo cáo này.
- `docs/HUONG_DAN_MIGRATION.md`: hướng dẫn bạn tự chạy sau khi sao lưu.

Không sửa Java production, route, logic kho/doanh thu, template/CSS/JS hoặc application.properties trong giai đoạn 5. Các thay đổi và file đang stage/xóa từ trước được giữ nguyên. Không chạy hoặc kết nối shopbangiay; mọi query trong helper có guard DB_NAME()=shop_verify.

Hướng dẫn migration hiện tại: [HUONG_DAN_TRIEN_KHAI.md](HUONG_DAN_TRIEN_KHAI.md). Thứ tự 004 → 005 → 006 → 007 → 008; kiểm kê bản sao trước khi quyết định script còn thiếu. 003 xóa dữ liệu và chỉ chạy khi bạn chủ động muốn dọn, không phải bước mặc định.

JAR kiểm thử và phiên Chrome nghiệm thu đã được dừng. Giới hạn: không nghiệm thu in giấy vật lý; không tuyên bố đã crawl toàn bộ link hoặc mọi trang ở cả ba độ rộng trong giai đoạn này. Không merge. Dừng theo yêu cầu.
