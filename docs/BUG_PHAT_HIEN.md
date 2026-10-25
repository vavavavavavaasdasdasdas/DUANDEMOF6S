# Lỗi cần quyết định nghiệp vụ — kiểm thử 25/10/2026

## BUG-04 — Mật khẩu yếu chưa bị chặn

- Tái hiện Playwright: đăng ký qa_weak_1280, password=123, confirmPassword=123; form chuyển /login và báo thành công.
- Nguồn: src/main/java/com/example/demodatn/dto/nhanvien/RegisterRequest.java (password chỉ NotBlank); service/nhanvien/AuthService.java (không kiểm tra độ mạnh).
- Bằng chứng: verify/full-audit/weak-password-before.json, output/playwright/full-audit-weak-password-before.png.
- Trạng thái: Đã sửa theo quyết định người dùng: ít nhất 8 ký tự, có chữ và số, tối đa 72 byte UTF-8. 127 clean tests PASS; Playwright chặn 123 ở 1280/375px.
- Tài khoản thử chỉ nằm trên shopbangiay_kt_20261025; sẽ được loại bằng phục hồi checkpoint sau nhóm.

## BUG-05 — Nhãn chức năng tiếng Anh

SALE/Size hiển thị trong trang chủ/danh sách/chi tiết/khuyến mãi. Đã Việt hóa GIẢM/Kích cỡ; người dùng chọn giữ tên riêng/thương hiệu. Browser kiểm tra lại không còn nhãn này ở các trang vừa kiểm thử. Bằng chứng: localization-after.json.
