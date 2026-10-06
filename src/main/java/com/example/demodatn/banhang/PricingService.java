package com.example.demodatn.banhang;

import com.example.demodatn.khuyenmai.Promotion;
import com.example.demodatn.khuyenmai.PromotionDetail;
import com.example.demodatn.khuyenmai.PromotionDetailRepository;
import com.example.demodatn.sanpham.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PromotionDetailRepository promotionDetailRepository;

    public PriceInfo calculatePrice(ProductVariant variant) {
        BigDecimal originalPrice = variant.getPrice();
        if (originalPrice == null) originalPrice = BigDecimal.ZERO;
        
        List<PromotionDetail> details = promotionDetailRepository.findByProductId(variant.getProduct().getId());
        
        BigDecimal maxDiscountValue = BigDecimal.ZERO;
        int maxDiscountPercent = 0;
        boolean hasActivePromo = false;
        
        LocalDateTime now = LocalDateTime.now();
        
        for (PromotionDetail detail : details) {
            Promotion promo = detail.getPromotion();
            if (promo != null && Boolean.TRUE.equals(promo.getStatus()) &&
                !promo.getStartDate().isAfter(now) && !promo.getEndDate().isBefore(now)) {
                
                BigDecimal discount = BigDecimal.ZERO;
                int percent = 0;
                
                if ("PERCENT".equalsIgnoreCase(promo.getDiscountType())) {
                    discount = originalPrice.multiply(promo.getDiscountValue()).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP);
                    percent = promo.getDiscountValue().intValue();
                } else if ("AMOUNT".equalsIgnoreCase(promo.getDiscountType())) {
                    discount = promo.getDiscountValue();
                    if (originalPrice.compareTo(BigDecimal.ZERO) > 0) {
                        percent = discount.multiply(new BigDecimal(100)).divide(originalPrice, 0, RoundingMode.HALF_UP).intValue();
                    }
                }
                
                if (discount.compareTo(maxDiscountValue) > 0) {
                    maxDiscountValue = discount;
                    maxDiscountPercent = percent;
                    hasActivePromo = true;
                }
            }
        }
        
        BigDecimal finalPrice = originalPrice.subtract(maxDiscountValue);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }
        
        return new PriceInfo(originalPrice, finalPrice, maxDiscountPercent, hasActivePromo);
    }
}
