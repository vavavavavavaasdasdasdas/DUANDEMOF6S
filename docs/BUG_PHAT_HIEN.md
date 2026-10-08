# Lỗi phát hiện trong đợt dọn dẹp

Không sửa logic trong đợt này. Kiểm thử chỉ dùng `shop_verify`.

| Mã | Bằng chứng mốc | Hiện trạng | Xử lý |
|---|---|---|---|
| BUG-01 | `PeopleIntegrationTest.employeeInsertFailureRollsBackTheAlreadyInsertedUser`: kỳ vọng `DataAccessException` nhưng không có exception; `mvnw clean test` | Lỗi có trước đợt dọn, 1 failure | Giữ nguyên, không xóa hoặc bỏ qua test |
| BUG-02 | `UserUiIntegrationTest.historyAndOwnedDetailUseSharedLayoutAndResponsiveTables`: SELECT Order ONLINE của user trả 0 dòng | Lỗi dữ liệu kiểm thử có trước đợt dọn | Giữ nguyên, không tạo đơn trên database thật |
| BUG-03 | `UserUiIntegrationTest.orderDetailAndSuccessRejectOtherCustomersAndMissingIds`: SELECT Order của khách khác trả 0 dòng | Lỗi dữ liệu kiểm thử có trước đợt dọn | Giữ nguyên |

Log mốc: `C:/Users/ADMIN/AppData/Local/Temp/don-dep-20261008/mvn-clean-test.log`. Tổng 75 test, 1 failure, 2 errors, 0 skipped; BUILD FAILURE. Đây không phải kết quả PASS.
