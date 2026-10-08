# Lỗi phát hiện trong đợt dọn dẹp

Không sửa logic trong đợt này. Kiểm thử chỉ dùng `shop_verify`.

| Mã | Bằng chứng mốc | Hiện trạng | Xử lý |
|---|---|---|---|
| BUG-01 | `PeopleIntegrationTest.employeeInsertFailureRollsBackTheAlreadyInsertedUser`: kỳ vọng `DataAccessException` nhưng không có exception; `mvnw clean test` | Lỗi có trước đợt dọn, 1 failure | Giữ nguyên, không xóa hoặc bỏ qua test |
| BUG-02 | `UserUiIntegrationTest.historyAndOwnedDetailUseSharedLayoutAndResponsiveTables`: SELECT Order ONLINE của user trả 0 dòng | Lỗi dữ liệu kiểm thử có trước đợt dọn | Giữ nguyên, không tạo đơn trên database thật |
| BUG-03 | `UserUiIntegrationTest.orderDetailAndSuccessRejectOtherCustomersAndMissingIds`: SELECT Order của khách khác trả 0 dòng | Lỗi dữ liệu kiểm thử có trước đợt dọn | Giữ nguyên |

Log mốc: `C:/Users/ADMIN/AppData/Local/Temp/don-dep-20261008/mvn-clean-test.log`. Tổng 75 test, 1 failure, 2 errors, 0 skipped; BUILD FAILURE. Đây không phải kết quả PASS.

## BUG-04 — nhóm Việt hóa gây lỗi render, đã revert

Mức độ: HIGH, lỗi mới từ nhóm (d), không tồn tại ở mốc và nhóm (a)–(c).

Commit phát sinh: `edc396d`. Bằng chứng `d-test.log`: 76 test, 1 failure, 9 errors, 0 skipped. Bảy errors mới là `TemplateInputException` tại view giỏ hàng, trang chủ, biến thể, hóa đơn và thống kê. Test UTF-8 nhãn riêng chạy thành công nhưng không chứng minh trang render được.

Nguyên nhân thô tại `d-test.log:304`:

```text
Instantiation of new objects and access to static classes or parameters is forbidden in this context
(template: "fragments/bo-cuc/bo-cuc-quan-tri" - line 23, col 32)
```

Nhóm này dùng method static của `NhanHienThi` trong biểu thức thuộc tính Thymeleaf có hạn chế truy cập static. Bằng chứng tương tự ở `fragments/dau-trang:7`. Không hạ hạn chế bảo mật Thymeleaf, không sửa hoặc giữ lại một phần nhóm. Đã chạy `git revert --no-edit edc396d` theo luật nghiệm thu.

Crawl nhóm lỗi lưu ngoài project tại `d-fail/crawl.json`; POST đăng nhập/giỏ hàng không lấy được CSRF từ trang đăng nhập lỗi được ghi NOT TESTED, không bịa status. Các GET còn lại vẫn được curl để ghi status thực tế.

Việt hóa chưa hoàn thành sau revert; không tuyên bố số từ tiếng Anh còn lại bằng 0. Nếu triển khai lại cần được chọn sửa riêng; đợt này chỉ ghi lỗi và phục hồi.

## BUG-05 — dữ liệu mô tả sản phẩm bị lỗi mã hóa từ trước

Mức độ: MEDIUM. Không phải lỗi mới do đổi vị trí template.

Bằng chứng thô cùng URL `/products/1`, anon, HTTP 200:

- Mốc `0018-anon.html:196` trong thư mục bằng chứng ngoài project.
- Cuối `toan-site-cuoi/0018-anon.html:196`.

```text
<p>GiÃƒÂ y thÃ¡Â»Æ’ thao Nike Air Force 1 trÃ¡ÂºÂ¯ng</p>
```

Hai lần đều có chuỗi lỗi này. Bộ quét cuối phát hiện 17 lượt nghi vấn trên HTML, có lặp dữ liệu qua các trang; đây không phải 17 lỗi độc lập. Trang Tài khoản vẫn render đúng `Tài khoản`, `Số điện thoại`, không có U+FFFD. Chưa kết luận nguồn hỏng nằm trong seed hay database nếu chưa SELECT đối chiếu. Không sửa dữ liệu, DataInitializer hoặc template để che lỗi trong phạm vi dọn dẹp.
