package com.example.demodatn.khuyenmai;

import com.example.demodatn.khachhang.Customer;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public static class VoucherResult {
        public boolean valid;
        public String message;
        public Voucher voucher;
        public BigDecimal discountAmount = BigDecimal.ZERO;
    }

    public VoucherResult validateAndCalculate(String code, Customer customer, BigDecimal subtotal) {
        VoucherResult result = new VoucherResult();
        if (code == null || code.trim().isEmpty()) {
            result.valid = false;
            result.message = "Mã voucher trống";
            return result;
        }

        Voucher voucher = voucherRepository.findByCode(code).orElse(null);
        if (voucher == null || voucher.getStatus() == null || !voucher.getStatus()) {
            result.valid = false;
            result.message = "Voucher không tồn tại hoặc đã bị khóa";
            return result;
        }

        LocalDateTime now = LocalDateTime.now();
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            result.valid = false;
            result.message = "Voucher chưa tới ngày sử dụng";
            return result;
        }

        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            result.valid = false;
            result.message = "Voucher đã hết hạn";
            return result;
        }

        if (voucher.getUsageLimit() != null && voucher.getUsedCount() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            result.valid = false;
            result.message = "Voucher đã hết lượt sử dụng";
            return result;
        }

        if (voucher.getCustomer() != null && (customer == null || !voucher.getCustomer().getId().equals(customer.getId()))) {
            result.valid = false;
            result.message = "Voucher không dành cho tài khoản này";
            return result;
        }

        if (voucher.getMinOrderValue() != null && subtotal.compareTo(voucher.getMinOrderValue()) < 0) {
            result.valid = false;
            result.message = "Đơn hàng chưa đạt giá trị tối thiểu " + String.format("%,d đ", voucher.getMinOrderValue().longValue());
            return result;
        }

        // Tính giảm giá
        BigDecimal discount = BigDecimal.ZERO;
        if ("PERCENT".equals(voucher.getDiscountType())) {
            discount = subtotal.multiply(voucher.getDiscountValue()).divide(new BigDecimal("100"));
            if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
                discount = voucher.getMaxDiscount();
            }
        } else if ("FIXED".equals(voucher.getDiscountType())) {
            discount = voucher.getDiscountValue();
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        result.valid = true;
        result.voucher = voucher;
        result.discountAmount = discount;
        result.message = "Áp dụng thành công";
        return result;
    }
}