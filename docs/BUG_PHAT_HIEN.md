# Lỗi ngoài phạm vi phase 1 — 24/10/2026

Chỉ kiểm tra trên shopbangiay_kt. Các mục dưới chưa tự sửa dữ liệu/luật nghiệp vụ.

| Mã | Mô tả | Bằng chứng | Trạng thái |
|---|---|---|---|
| BUG-DATA-01 | 6 tham chiếu DB tới 2 file thiếu: /images/placeholder.svg (products 4/5, product_images 4/5), /uploads/products/35e401f9-d0b7-4b5b-99fd-bc011adbd94c.png (product 24, product_image 6). Backup SQL không kèm file upload. | docs/evidence/phase1/db-image-files.json | Giao diện đã fallback; chưa phục hồi ảnh gốc, cần backup file. Không sửa URL DB/không tạo ảnh giả. |
| BUG-TEST-02 | OrderStockIntegrationTest.timeoutJobCancelsOnlyPendingUnpaidOldBankOrders gọi expiry.cancelExpiredOrders() quét toàn DB, không giới hạn fixture. Trên bản sao đã xử lý cả DH000669. | src/test/java/com/example/demodatn/service/donhang/OrderStockIntegrationTest.java:95–98; docs/evidence/phase1/test-residuals.json; residual-order-state.json | Chưa sửa vì ngoài route/resource/500. Log 966, variant 596, tồn hiện tại 19; đơn 669 CANCELLED/UNPAID, da_tru_kho=false. Không tự điều chỉnh lại tồn. |
| BUG-TEST-03 | Lần test đầu fixture customer_code dài hơn VARCHAR(20), owner() lỗi sau khi đã tạo user ở ca không transaction. | docs/evidence/phase1/test-residuals.json; ShippingAddressIntegrationTest.java:41–44 | Đã rút ngắn mã fixture; user thử id 829 còn trong bản sao. Chưa xóa, để giai đoạn dọn dữ liệu xét duyệt. |

COUNT sau migration → cuối test: users 12→13, inventory_logs 63→64. Các bảng khác giữ COUNT như báo cáo phase 1; không suy COUNT bằng nhau là nội dung không đổi. Không xóa log/tài khoản hoặc tự đoán tồn kho. Phase 2/3 chưa bắt đầu.
