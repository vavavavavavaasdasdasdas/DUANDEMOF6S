# Ngữ cảnh giao diện bộ ảnh sản phẩm

Cửa hàng tiếng Việt, Thymeleaf và Bootstrap. Giữ nguyên giao diện hiện có; không đổi màu thương hiệu trong nhiệm vụ ảnh.

- Token và component chuẩn: `static/css/he-thong-thiet-ke.css`; primary hiện tại `--bs-primary: #b94312`, nền `#f8f9fa`, chữ `#272d2d`, font Inter/system, bo góc `--bs-border-radius`.
- Admin dùng layout quản trị, field/error và confirm hiện có; bộ ảnh dùng input file native nhiều lựa chọn. POST form multipart có CSRF do Thymeleaf/Spring Security.
- Gallery có khung vuông cố định, ảnh object-fit contain, mũi tên tròn trắng và thumbnail cuộn ngang; focus rõ bằng primary. Đổi ảnh không thay kích thước khung.
- Ảnh chính đồng bộ qua `products.image_url` cho các view cũ; bảng product_images là nguồn bộ ảnh, không lưu giá.
- Quản lý ảnh là thay đổi chưa lưu cho tới khi lưu sản phẩm; xóa có confirm. Server kiểm tra lại số lượng, loại thực, dung lượng và chủ sở hữu ảnh, không tin hidden field.
- Bảo mật/transaction thuộc ProductGalleryService và AdminCatalogService. File mới được dọn khi rollback, file bị xóa chỉ dọn sau commit; không xóa file dùng chung còn được ảnh khác tham chiếu.
- Trên mobile gallery dùng vuốt ngang, phím trái/phải khi focus gallery; 1 ảnh thì không render điều hướng/thumbnail.

## Phân trang dùng chung

- Nguồn cỡ trang duy nhất: `shop.page-size` trong application.properties. Helper `core/Pagination` tạo PageRequest và chuẩn hóa page; fragment duy nhất `fragments/phan-trang` hiển thị Trước/Sau, trang rút gọn và khoảng bản ghi. Giữ màu/component hiện có.
- URL do PaginationAdvice tạo từ toàn bộ query params, giữ giá trị lặp, Unicode và dấu +; mỗi bảng độc lập dùng tên tham số riêng khi cần. Chỉ render điều hướng khi có hơn một trang.
- Lựa chọn promotion chỉ cập nhật trang đã lưu, không làm mất các trang khác. POS tìm khách phía server có loading/error và giữ khách đã chọn khi đổi trang kết quả.

### Biến thể theo size và chất liệu

Chất liệu là danh mục riêng. Trang chi tiết và thêm nhanh dùng hai thẻ select có nhãn Size và Chất liệu; chỉ kết hợp có biến thể còn hàng mới chọn được. Khi chỉ có một chất liệu, chọn sẵn và vẫn hiển thị tên. POS dùng cùng cách chọn hai trường. Giỏ và kho phân biệt bằng ID biến thể; đơn hàng và hóa đơn dùng tên chất liệu đã chụp tại thời điểm mua, không đọc lại tên danh mục.

### Luồng trạng thái và nhãn tồn kho của đơn

Chi tiết đơn quản trị hiển thị “Đã trừ kho / Chưa trừ kho”. COD chưa xác nhận chưa giữ hàng; khi xác nhận thiếu hàng phải giữ nguyên trạng thái và hiện rõ sản phẩm, size, chất liệu, số cần và số còn. Các bước là Chờ xử lý → Đã xác nhận → Đang chuẩn bị → Đang giao → Hoàn tất. Hủy trước giao và Giao hàng thất bại là các nhánh kết thúc khác nhau. User hủy đơn qua hộp thoại xác nhận, dùng cùng dịch vụ trạng thái với Admin và job quá hạn.

### Thời hạn chuyển khoản

Fragment `fragments/han-chuyen-khoan` dùng alert hiện có, không có CSS riêng. User thấy “Vui lòng chuyển khoản trước HH:mm dd/MM/yyyy” cho đơn online chuyển khoản chưa trả tiền và chưa kết thúc. Admin chỉ thấy “Tự hủy lúc ...” khi đơn còn Chờ xử lý, đúng điều kiện job hiện có. Thời hạn dùng created_at cộng cấu hình timeout, không tính lại từ lúc tải trang; nhãn biến mất khi đã thanh toán/hủy/thất bại.

### Dashboard chưa có dữ liệu

Không render khung canvas cho trạng thái đơn và sản phẩm bán chạy khi danh sách tương ứng rỗng; dùng thông báo trống hiện có. Điều này tránh kích thước canvas mặc định gây tràn ngang trên tablet sau khi dọn dữ liệu thử. Khi có dữ liệu, biểu đồ giữ hành vi cũ.
