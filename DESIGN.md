# Ngữ cảnh giao diện bộ ảnh sản phẩm

Cửa hàng tiếng Việt, Thymeleaf và Bootstrap. Giữ nguyên giao diện hiện có; không đổi màu thương hiệu trong nhiệm vụ ảnh.

- Token và component chuẩn: `static/css/he-thong-thiet-ke.css`; primary hiện tại `--bs-primary: #b94312`, nền `#f8f9fa`, chữ `#272d2d`, font Inter/system, bo góc `--bs-border-radius`.
- Admin dùng layout quản trị, field/error và confirm hiện có; bộ ảnh dùng input file native nhiều lựa chọn. POST form multipart có CSRF do Thymeleaf/Spring Security.
- Gallery có khung vuông cố định, ảnh object-fit contain, mũi tên tròn trắng và thumbnail cuộn ngang; focus rõ bằng primary. Đổi ảnh không thay kích thước khung.
- Ảnh chính đồng bộ qua `products.image_url` cho các view cũ; bảng product_images là nguồn bộ ảnh, không lưu giá.
- Quản lý ảnh là thay đổi chưa lưu cho tới khi lưu sản phẩm; xóa có confirm. Server kiểm tra lại số lượng, loại thực, dung lượng và chủ sở hữu ảnh, không tin hidden field.
- Bảo mật/transaction thuộc ProductGalleryService và AdminCatalogService. File mới được dọn khi rollback, file bị xóa chỉ dọn sau commit; không xóa file dùng chung còn được ảnh khác tham chiếu.
- Trên mobile gallery dùng vuốt ngang, phím trái/phải khi focus gallery; 1 ảnh thì không render điều hướng/thumbnail.
