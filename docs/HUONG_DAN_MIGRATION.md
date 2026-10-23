# Migration database

Lệnh có guard, sao lưu và quay lại: [HUONG_DAN_TRIEN_KHAI.md](HUONG_DAN_TRIEN_KHAI.md).

- Database mới: chọn database thử đã tạo rồi chạy `shopgiay.sql`; script từ chối schema đã có bảng users.
- Database đang dùng: kiểm kê, sao lưu và diễn tập trên bản sao; chạy **004 → 005 → 006 → 007 → 008**.
- **003_don_du_lieu_kiem_thu.sql xóa dữ liệu nghiệp vụ**; không chạy trong nâng cấp thông thường.
- Không chạy lại phase cũ sau khi đổi unique variant sang sản phẩm + size + chất liệu.
- Với database thật, guard chỉ mở khi bạn chủ động đặt SESSION_CONTEXT theo hướng dẫn; không bỏ guard.
- Agent chỉ chạy migration trên database thử/bản sao. Ngoại lệ duy nhất với nguồn thật trong giai đoạn 6 là BACKUP COPY_ONLY được bạn cấp quyền riêng.
