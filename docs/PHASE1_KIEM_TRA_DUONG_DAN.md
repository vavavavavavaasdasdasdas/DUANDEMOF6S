# Phase 1 — Kiểm tra đường dẫn và lỗi

Hoàn tất đóng gói ngày 25/10/2026; kiểm tra thực tế ngày 24/10/2026. Nhánh `hoan-thien-chat-lieu`, nền `c3aff9f`. Hoàn thành phase 1 và dừng trước phase 2/3. Không chạy dọn dữ liệu, không di chuyển file, không merge/push.

## Môi trường và phạm vi

Restore file backup có sẵn `C:\Program Files\Microsoft SQL Server\MSSQL17.MSSQLSERVER\MSSQL\Backup\shopbangiay_release_20261023.bak` (backup_finish_date 23/10/2026 21:59:54) thành **shopbangiay_kt**, với file `shopbangiay_kt.mdf`/`shopbangiay_kt_log.ldf` riêng. Lệnh RESTORE chạy qua master, có guard từ chối ghi đè nếu DB đích đã tồn tại, không dùng WITH REPLACE. Không kết nối hoặc query dữ liệu shopbangiay. Đọc msdb chỉ để tìm metadata backup. Không dùng shop_verify/shopbangiay_thu làm DB thay thế.

App cuối chạy cổng **18086**, từ export Git index của đúng mã sẽ commit; giữ nguyên các thay đổi có sẵn ngoài phạm vi ở working tree. Datasource ghim vào shopbangiay_kt bằng biến môi trường, ddl-auto=none, upload root là thư mục uploads/products hiện có. Job chuyển khoản của app dùng interval P7D trong lần audit để tránh chạy nền giữa crawl. Bộ test vẫn chạy đủ các test job có sẵn; tác động riêng được ghi bên dưới.

Bằng chứng `restore.log`, `app-commit.log`, `commit-tests.log`:

```text
RESTORE DATABASE successfully processed 1193 pages in 0.010 seconds (931.396 MB/sec).
2026-10-24T14:07:35.153+07:00 ... Started DemoDatnApplication in 5.921 seconds
[INFO] Tests run: 121, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 28.516 s
```

Lần trước khi restore đã thất bại do DB chưa tồn tại; báo cáo này thay trạng thái bị chặn trước đó. Log cũ còn trong verify/phase1, không dùng kết quả đó làm PASS.

## Bảng nghiệm thu

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| Restore, chỉ dùng DB test | RESTORE file backup qua master, MOVE hai file riêng; guard DB | shopbangiay_kt restore thành công; không mở shopbangiay | PASS |
| Migration 004–009 | SELECT sys.tables/sys.columns, COUNT mọi bảng, sqlcmd -b từng script | 004 tạo/bù 6 ảnh; 005 thêm 8 chất liệu và khóa biến thể; 006 thêm da_tru_kho; 009 bù 7 địa chỉ. 007/008 đã phù hợp kiểu Unicode nên chạy idempotent, không tăng dòng | PASS |
| Mapping Spring | Export RequestMappingHandlerMapping từ full SpringBootTest context | 108 mapping entries, gồm /error HTML/JSON; method/path/handler trong route-table.csv. Quyền đối chiếu SecurityConfig và 8 ca runtime | PASS |
| Tham chiếu tĩnh | Quét đúng export sẽ commit bằng HTMLParser/regex, đối chiếu route và static | 388 tham chiếu trích xuất, 0 candidate link/method tĩnh sai; 0 literal view thiếu; 27 reference static tồn tại | PASS |
| Link động/form/bộ lọc | Crawl BFS theo mọi link render, phân trang; duyệt từng option GET filter độc lập; kiểm tra form method/action | 710 request; 2103 form instances, 0 action/method không có route; 0 link nội bộ được render dẫn tới 4xx/5xx | PASS |
| HTTP crawl tổng | Raw crawl-http.json và crawl-edges.json | 682×200, 2×302 hợp lệ, 26×404 probe dự kiến; không có 500 hay asset lỗi | PASS |
| ID âm/chữ/không tồn tại | 19 loại đường dẫn × 3 giá trị -1/abc/999999999; Accept:text/html | 57/57 trả 404 và có nội dung tiếng Việt thân thiện; trước sửa 19 ID abc trả 400 | PASS |
| Ảnh thiếu file | SELECT products.image_url/product_images.duong_dan + is_file; HTTP/browser sau sửa | 6 tham chiếu tới 2 file thiếu; resolver dùng ảnh thay thế hiện có, 0 request tài nguyên lỗi trong crawl/browser | PASS |
| Phục hồi ảnh upload gốc | Không có backup thư mục upload tương ứng | Ảnh gốc UUID chưa khôi phục; ghi BUG-DATA-01, không sửa DB/không tạo ảnh giả | NOT TESTED |
| Doanh thu/bán chạy | 5 URL × 3 kênh × 5 khoảng ngày × 4³ lựa chọn ba tham số page | 4800/4800 HTTP 200; 0 trang trắng; không tái hiện lỗi 500 | PASS |
| Playwright 1280/375 | Duyệt 232 navigation với guest/user/admin, console/network/body/canvas, ảnh chụp | 0 pageerror; 0 requestfailed; 0 response tài nguyên lỗi; 0 trang trắng/ảnh vỡ/tràn ngang; 62 trang có canvas đều có Chart instance | PASS |
| Console 404 chủ ý | Cố ý mở /products/abc ở hai viewport | 3 thông báo console HTTP404 được ghi (có một thông báo đến muộn); đều là document probe 404, không có JS exception | PASS |
| 8 bảo mật HTTP | Guest/user/admin; CSRF thiếu/sai; ownership | 8/8 đúng 302/403/404, không submit giao dịch thành công | PASS |
| Build cuối | mvnw clean test, đúng export, datasource shopbangiay_kt | 121 run, 0 failures/errors/skipped; BUILD SUCCESS | PASS |
| Mọi tổ hợp input tùy ý/POST nghiệp vụ | Không gian q/giá/ngày là vô hạn; không submit thao tác bán/xóa thật trong crawl | Ngoài ma trận hữu hạn nêu rõ; các POST nghiệp vụ chỉ kiểm tra mapping/CSRF, không xác nhận toàn bộ nghiệp vụ | NOT TESTED |

## COUNT trước/sau migration và cuối test

`migration-before.json`, `migration-after.json`, `final-counts.json` lưu schema/count thô. Trước/sau migration không xóa bản ghi ở bảng có sẵn.

| Bảng | Trước migration | Sau migration | Cuối test/crawl |
|---|---|---|---|
| categories | 6 | 6 | 6 |
| chat_lieu | 0 | 8 | 8 |
| customers | 14 | 14 | 14 |
| dia_chi_nhan_hang | 0 | 7 | 7 |
| employees | 5 | 5 | 5 |
| inventory_logs | 63 | 63 | 64 |
| invoices | 28 | 28 | 28 |
| order_details | 35 | 35 | 35 |
| orders | 28 | 28 | 28 |
| pos_held_items | 0 | 0 | 0 |
| pos_held_orders | 0 | 0 | 0 |
| product_images | 0 | 6 | 6 |
| product_variants | 20 | 20 | 20 |
| products | 10 | 10 | 10 |
| promotion_details | 7 | 7 | 7 |
| promotions | 5 | 5 | 5 |
| roles | 2 | 2 | 2 |
| sizes | 8 | 8 | 8 |
| users | 12 | 12 | 13 |
| vouchers | 15 | 15 | 15 |

**Hai chênh lệch sau test phải giữ nguyên để xem xét:** users 12→13 do một fixture addr-* lỗi trước khi tạo customer ở lần test đầu; inventory_logs 63→64 do test timeout có sẵn gọi job trên toàn DB. Log mới 966 gắn biến thể 596, lý do “Hoàn kho đơn DH000669 · hủy hoặc giao hàng thất bại”, actor admin. Đơn 669 hiện CANCELLED/UNPAID, da_tru_kho=false, variant 596 hiện quantity=19. Không tự hoàn ngược tồn kho, không xóa log/tài khoản. Ghi BUG-TEST-02 và bằng chứng test-residuals.json, residual-order-state.json. COUNT đơn/chi tiết/hóa đơn/sản phẩm/biến thể vẫn 28/35/28/10/20; số dòng không chứng minh trạng thái/tồn kho không đổi.

## Lỗi tìm được và bản sửa

| Mã | Mô tả | File:dòng | Đã sửa |
|---|---|---|---|
| ROUTE-01 | Spring không convert ID chữ/overflow sang Integer/Long, mặc định trả 400 | controller/PathVariableExceptionAdvice.java:15; trước là default binding của Spring | Có: chỉ numeric @PathVariable dùng trang loi/404; query param sai vẫn 400. 57 GET sau sửa 404; test thêm overflow |
| ASSET-01 | URL có prefix hợp lệ nhưng file không tồn tại vẫn được render, gây request 404 | util/ProductMedia.java:5; service/sanpham/ProductImageUrlService.java:18; templates/user/fragments/the-san-pham.html:10 | Có: resolver chung kiểm tra upload-dir/classpath, dùng ảnh thay thế sẵn có; áp dụng 12 template, giữ nguyên dữ liệu DB |
| FIXTURE-01 | Fixture mã khách dài hơn VARCHAR(20); test Unicode chèn NULL vào UNIQUE đã có NULL | src/test/.../ShippingAddressIntegrationTest.java:44; UnicodePersistenceIntegrationTest.java:22 | Có, chỉ sửa dữ liệu fixture: mã ngắn và giá trị unique; không đổi schema/production behavior |
| DEV-01 | Bản sửa thử gọi bean trực tiếp trong th:src bị Thymeleaf chặn, tạo 500 | templates/user/fragments/the-san-pham.html:10; user/trang-chu.html:20 | Đã sửa trước nghiệm thu: tính URL trong th:with, th:src chỉ đọc biến; thêm test render. development-regression.txt giữ Caused by thô |
| BUG-DATA-01 | Thiếu 2 file được 6 bản ghi ảnh tham chiếu | docs/BUG_PHAT_HIEN.md; db-image-files.json | Chưa khôi phục file gốc; giao diện đã fallback |
| BUG-TEST-02 | Test job quét cả dữ liệu cũ trên bản sao, có tác động ngoài fixture | src/test/.../OrderStockIntegrationTest.java:95–98 | Chưa sửa ngoài phạm vi route/resource/500; ghi rõ tác động và không tự sửa tồn |

Stacktrace bản thử DEV-01, đã chạy lại thành công:

```text
Caused by: org.attoparser.ParseException: Instantiation of new objects and access to static classes or parameters is forbidden in this context
(template: "user/fragments/the-san-pham" - line 10, col 14)
Caused by: org.thymeleaf.exceptions.TemplateProcessingException: Instantiation of new objects and access to static classes or parameters is forbidden in this context
```

Không có stacktrace Caused by của trang doanh thu/bán chạy trên bản cuối vì không tái hiện được lỗi: 4800 status=200, final app log không có exception. Không sửa truy vấn/thống kê/luật doanh thu để đoán lỗi.

## URL | vai trò | status | kết quả

Bảng dưới là các URL chính. [url-status.csv](evidence/phase1/url-status.csv) liệt kê từng request URL/vai trò/phương thức/status/kết quả: crawl, ma trận thống kê, ID sai, bảo mật và browser. Có 26 probe 404 dự kiến: 5 sản phẩm đang ẩn × 3 vai trò và 11 đơn ONLINE được cố ý thử vào màn thành công POS. Không có link render thực trỏ tới các probe này; crawl-edges.json có nguồn của mọi link.

| URL | Vai trò | Status | Kết quả |
|---|---|---|---|
| `/` | guest | 200 | PASS |
| `/products` | guest | 200 | PASS |
| `/category/1` | guest | 200 | PASS |
| `/products?promotion=true` | guest | 200 | PASS |
| `/products/2` | guest | 200 | PASS |
| `/profile` | user | 200 | PASS |
| `/cart` | user | 200 | PASS |
| `/checkout` | user | 200 | PASS |
| `/orders` | user | 200 | PASS |
| `/admin` | admin | 200 | PASS |
| `/admin/account` | admin | 200 | PASS |
| `/admin/products-crud` | admin | 200 | PASS |
| `/admin/products-crud/create` | admin | 200 | PASS |
| `/admin/products-crud/view/2` | admin | 200 | PASS |
| `/admin/variants` | admin | 200 | PASS |
| `/admin/inventory` | admin | 200 | PASS |
| `/admin/materials` | admin | 200 | PASS |
| `/admin/customers` | admin | 200 | PASS |
| `/admin/employees` | admin | 200 | PASS |
| `/admin/orders` | admin | 200 | PASS |
| `/admin/invoices` | admin | 200 | PASS |
| `/admin/pos` | admin | 200 | PASS |
| `/admin/statistics` | admin | 200 | PASS |
| `/admin/statistics/revenue` | admin | 200 | PASS |
| `/admin/statistics/best-sellers` | admin | 200 | PASS |
| `/admin/reports/revenue` | admin | 200 | PASS |
| `/admin/reports/best-sellers` | admin | 200 | PASS |


## Tám ca bảo mật

| Ca | URL | Status thực tế | Kết quả |
|---|---|---|---|
| 1 guest admin | `/admin` | 302 | PASS |
| 2 guest profile | `/profile` | 302 | PASS |
| 3 user admin | `/admin` | 403 | PASS |
| 4 user admin POST valid CSRF | `/admin/products-crud/save` | 403 | PASS |
| 5 admin missing CSRF | `/admin/products-crud/save` | 403 | PASS |
| 6 admin invalid CSRF | `/admin/products-crud/save` | 403 | PASS |
| 7 user foreign order | `/orders/1` | 403 | PASS |
| 8 user foreign address | `/profile?addressId=1` | 404 | PASS |

## Ảnh DB thiếu file

| Bảng | ID | Đường dẫn | Kết quả |
|---|---|---|---|
| products | 4 | `/images/placeholder.svg` | Thiếu file; render ảnh thay thế |
| products | 5 | `/images/placeholder.svg` | Thiếu file; render ảnh thay thế |
| products | 24 | `/uploads/products/35e401f9-d0b7-4b5b-99fd-bc011adbd94c.png` | Thiếu file; render ảnh thay thế |
| product_images | 4 | `/images/placeholder.svg` | Thiếu file; render ảnh thay thế |
| product_images | 5 | `/images/placeholder.svg` | Thiếu file; render ảnh thay thế |
| product_images | 6 | `/uploads/products/35e401f9-d0b7-4b5b-99fd-bc011adbd94c.png` | Thiếu file; render ảnh thay thế |

Các URL ngoài (Nike/Adidas/Converse) là remote URL, không coi là file phải có trên đĩa; app đã dùng placeholder theo chính sách ProductMedia có sẵn. Không tải ảnh ngoài để giả phục hồi dữ liệu.

## Mapping Spring đầy đủ

108 entries từ context đầy đủ của **bản sẽ commit**; lượt offline ban đầu trên working tree có các thay đổi sẵn của người dùng cho 118 patterns, không dùng con số đó thay kết quả cuối. GET có HEAD implicit; OPTIONS của framework không phải controller mapping riêng. POST /login và /logout do Spring Security filter xử lý, cần CSRF. Static /css/**,/js/**,/images/**,/anh/** và upload handler /uploads/products/** ghi riêng trong báo cáo; không nhầm chúng với controller mappings. Quyền dưới đây lấy từ SecurityConfig, kiểm tra truy cập đại diện bằng 8 ca thực tế.

| Method | URL | Quyền |
|---|---|---|
| GET | `/` | PUBLIC |
| GET | `/admin` | ROLE_ADMIN |
| GET | `/admin/` | ROLE_ADMIN |
| GET | `/admin/account` | ROLE_ADMIN |
| POST | `/admin/account/password` | ROLE_ADMIN |
| POST | `/admin/account/profile` | ROLE_ADMIN |
| GET | `/admin/customers` | ROLE_ADMIN |
| GET | `/admin/customers/create` | ROLE_ADMIN |
| POST | `/admin/customers/delete/{id}` | ROLE_ADMIN |
| GET | `/admin/customers/detail/{id}` | ROLE_ADMIN |
| GET | `/admin/customers/edit/{id}` | ROLE_ADMIN |
| POST | `/admin/customers/save` | ROLE_ADMIN |
| POST | `/admin/customers/status/{id}` | ROLE_ADMIN |
| GET | `/admin/dashboard` | ROLE_ADMIN |
| GET | `/admin/employees` | ROLE_ADMIN |
| GET | `/admin/employees/create` | ROLE_ADMIN |
| POST | `/admin/employees/delete/{id}` | ROLE_ADMIN |
| GET | `/admin/employees/detail/{id}` | ROLE_ADMIN |
| GET | `/admin/employees/edit/{id}` | ROLE_ADMIN |
| POST | `/admin/employees/save` | ROLE_ADMIN |
| POST | `/admin/employees/status/{id}` | ROLE_ADMIN |
| GET | `/admin/inventory` | ROLE_ADMIN |
| POST | `/admin/inventory/adjust` | ROLE_ADMIN |
| GET | `/admin/inventory/adjust/{id}` | ROLE_ADMIN |
| GET | `/admin/invoices` | ROLE_ADMIN |
| GET | `/admin/invoices/{id}` | ROLE_ADMIN |
| GET | `/admin/orders` | ROLE_ADMIN |
| GET | `/admin/orders/detail/{id}` | ROLE_ADMIN |
| POST | `/admin/orders/update-status/{id}` | ROLE_ADMIN |
| GET | `/admin/orders/{id}` | ROLE_ADMIN |
| POST | `/admin/orders/{id}/confirm-payment` | ROLE_ADMIN |
| POST | `/admin/orders/{id}/status` | ROLE_ADMIN |
| GET | `/admin/pos` | ROLE_ADMIN |
| POST | `/admin/pos/add` | ROLE_ADMIN |
| POST | `/admin/pos/checkout` | ROLE_ADMIN |
| GET | `/admin/pos/customers` | ROLE_ADMIN |
| POST | `/admin/pos/holds` | ROLE_ADMIN |
| POST | `/admin/pos/holds/review` | ROLE_ADMIN |
| POST | `/admin/pos/holds/{id}/delete` | ROLE_ADMIN |
| POST | `/admin/pos/holds/{id}/open` | ROLE_ADMIN |
| POST | `/admin/pos/new` | ROLE_ADMIN |
| POST | `/admin/pos/quantity` | ROLE_ADMIN |
| POST | `/admin/pos/remove` | ROLE_ADMIN |
| GET | `/admin/pos/success/{id}` | ROLE_ADMIN |
| POST | `/admin/pos/validate-voucher` | ROLE_ADMIN |
| GET | `/admin/products` | ROLE_ADMIN |
| GET | `/admin/products-crud` | ROLE_ADMIN |
| GET | `/admin/products-crud/create` | ROLE_ADMIN |
| POST | `/admin/products-crud/delete/{id}` | ROLE_ADMIN |
| GET | `/admin/products-crud/edit/{id}` | ROLE_ADMIN |
| POST | `/admin/products-crud/save` | ROLE_ADMIN |
| GET | `/admin/products-crud/view/{id}` | ROLE_ADMIN |
| GET | `/admin/products-crud/{productId}/variants` | ROLE_ADMIN |
| GET | `/admin/promotions` | ROLE_ADMIN |
| GET | `/admin/promotions/create` | ROLE_ADMIN |
| POST | `/admin/promotions/delete/{id}` | ROLE_ADMIN |
| GET | `/admin/promotions/edit/{id}` | ROLE_ADMIN |
| POST | `/admin/promotions/save` | ROLE_ADMIN |
| POST | `/admin/promotions/{id}/products` | ROLE_ADMIN |
| GET | `/admin/promotions/{id}/products` | ROLE_ADMIN |
| GET | `/admin/reports/best-sellers` | ROLE_ADMIN |
| GET | `/admin/reports/revenue` | ROLE_ADMIN |
| GET | `/admin/statistics` | ROLE_ADMIN |
| GET | `/admin/statistics/best-sellers` | ROLE_ADMIN |
| GET | `/admin/statistics/revenue` | ROLE_ADMIN |
| GET | `/admin/variants` | ROLE_ADMIN |
| GET | `/admin/variants/create` | ROLE_ADMIN |
| GET | `/admin/variants/edit/{id}` | ROLE_ADMIN |
| POST | `/admin/variants/save` | ROLE_ADMIN |
| GET | `/admin/vouchers` | ROLE_ADMIN |
| GET | `/admin/vouchers/create` | ROLE_ADMIN |
| POST | `/admin/vouchers/delete/{id}` | ROLE_ADMIN |
| GET | `/admin/vouchers/edit/{id}` | ROLE_ADMIN |
| POST | `/admin/vouchers/save` | ROLE_ADMIN |
| GET | `/admin/{kind:categories|sizes|materials}` | ROLE_ADMIN |
| GET | `/admin/{kind:categories|sizes|materials}/create` | ROLE_ADMIN |
| POST | `/admin/{kind:categories|sizes|materials}/delete/{id}` | ROLE_ADMIN |
| GET | `/admin/{kind:categories|sizes|materials}/edit/{id}` | ROLE_ADMIN |
| POST | `/admin/{kind:categories|sizes|materials}/save` | ROLE_ADMIN |
| POST | `/admin/{kind:categories|sizes|materials}/toggle-status/{id}` | ROLE_ADMIN |
| GET | `/cart` | PUBLIC |
| POST | `/cart/add` | PUBLIC |
| POST | `/cart/add-ajax` | PUBLIC |
| POST | `/cart/remove-ajax` | PUBLIC |
| POST | `/cart/update-ajax` | PUBLIC |
| GET | `/category/{id}` | PUBLIC |
| GET | `/checkout` | AUTHENTICATED |
| POST | `/checkout/place` | AUTHENTICATED |
| POST | `/checkout/validate-voucher-ajax` | AUTHENTICATED |
| ANY | `/error` | PUBLIC |
| ANY | `/error` | PUBLIC |
| GET | `/login` | PUBLIC |
| GET | `/orders` | AUTHENTICATED |
| GET | `/orders/success/{id}` | AUTHENTICATED |
| GET | `/orders/{id}` | AUTHENTICATED |
| POST | `/orders/{id}/cancel` | AUTHENTICATED |
| GET | `/policy` | PUBLIC |
| GET | `/products` | PUBLIC |
| GET | `/products/{id}` | PUBLIC |
| GET | `/profile` | AUTHENTICATED |
| POST | `/profile/addresses/save` | AUTHENTICATED |
| POST | `/profile/addresses/{id}/default` | AUTHENTICATED |
| POST | `/profile/addresses/{id}/delete` | AUTHENTICATED |
| POST | `/profile/change-password` | AUTHENTICATED |
| POST | `/profile/update` | AUTHENTICATED |
| POST | `/register` | PUBLIC |
| GET | `/register` | PUBLIC |
| GET | `/search` | PUBLIC |

## Ma trận thống kê và Playwright

Kênh ALL/ONLINE/POS; ngày mặc định, from>to (24/10→01/10/2026), quá dài (1900→2026), rỗng (01→02/01/1900), có dữ liệu (2026). Mỗi bộ ngày/kênh chạy toàn bộ 64 kết hợp page/quantityPage/revenuePage thuộc {0,-1,abc,99999}. Năm URL: /admin/statistics, /admin/statistics/revenue, /admin/statistics/best-sellers, /admin/reports/revenue, /admin/reports/best-sellers. HTTP 4800/4800 200. Browser chạy 232 lượt với hai viewport, gồm 150 lượt cho năm URL/ba kênh/năm khoảng ngày, cùng trang chính và chi tiết thật. 62 canvas pages có Chart instance, không lỗi Chart.js.

Ảnh: [Doanh thu 375](evidence/phase1/phase1-revenue-375.png), [Doanh thu 1280](evidence/phase1/phase1-revenue-1280.png), [Bán chạy 375](evidence/phase1/phase1-best-sellers-375.png), [Sản phẩm 375](evidence/phase1/phase1-products-375.png), [404 thân thiện](evidence/phase1/phase1-404-375.png). Giữ cả ảnh tương ứng viewport còn lại trong evidence. Không commit cookie, token, HTML form riêng hoặc script nhúng browser state.

## Lệnh chạy lại

Cấp SQLCMDPASSWORD và SPRING_DATASOURCE_PASSWORD qua môi trường, không lưu trong source. Chạy từ thư mục DemoDATN:

```powershell
# Chỉ DB test; các script đã thêm shopbangiay_kt vào whitelist, không bỏ guard.
sqlcmd -S tcp:localhost,1433 -U <tai_khoan_SQL> -C -I -d shopbangiay_kt -b -f 65001 -i database/migrations/004_anh_san_pham.sql
# Tương tự 005_chat_lieu.sql, 006_luat_ton_kho.sql, 007_tieng_viet_unicode.sql,
# 008_nvarchar_con_lai.sql, 009_dia_chi_nhan_hang.sql theo thứ tự khi schema cần.
$env:SPRING_DATASOURCE_URL='jdbc:sqlserver://localhost:1433;databaseName=shopbangiay_kt;encrypt=true;trustServerCertificate=true'
$env:SPRING_DATASOURCE_USERNAME='<tai_khoan_SQL>'
$env:SPRING_JPA_HIBERNATE_DDL_AUTO='none'
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=18086'
```

Lưu ý BUG-TEST-02 trước khi chạy lại bộ test trên bản sao có dữ liệu cũ. Một commit riêng cho phase 1, không đưa các thay đổi có sẵn ngoài phạm vi vào commit. **DỪNG trước phase 2/3.**
