package com.example.demodatn.sanpham;
import com.example.demodatn.banhang.OrderDetailRepository;
import com.example.demodatn.banhang.PriceInfo;
import com.example.demodatn.banhang.PricingService;
import com.example.demodatn.entity.donhang.OrderDetail;
import com.example.demodatn.entity.sanpham.Product;
import com.example.demodatn.entity.sanpham.ProductVariant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Loc / sap xep / phan trang san pham phia User.
 * Gioi han da biet: tai toan bo san pham vao bo nho roi loc theo gia hieu luc (PricingService)
 * va phan trang trong Java. Chap nhan duoc voi quy mo du lieu hien tai.
 */
@Service
@RequiredArgsConstructor
public class ProductFilterService {

    public static final String CANCELLED = "CANCELLED";

    private final ProductRepository productRepository;
    private final com.example.demodatn.core.Pagination pagination;
    private final ProductVariantRepository productVariantRepository;
    private final PricingService pricingService;
    private final OrderDetailRepository orderDetailRepository;

    public Page<ProductDto> filterProducts(String keyword, Integer categoryId, Integer sizeId,
                                           BigDecimal minPrice, BigDecimal maxPrice,
                                           String sort, int page, int size) {
        return filterProducts(keyword, categoryId, sizeId, minPrice, maxPrice, null, sort, page, size);
    }

    public Page<ProductDto> filterProducts(String keyword, Integer categoryId, Integer sizeId,
                                           BigDecimal minPrice, BigDecimal maxPrice, Boolean promotion,
                                           String sort, int page, int size) {
        return filterProducts(keyword,categoryId,sizeId,minPrice,maxPrice,promotion,sort,page,size,null);
    }

    public Page<ProductDto> filterProducts(String keyword, Integer categoryId, Integer sizeId,
            BigDecimal minPrice, BigDecimal maxPrice, Boolean promotion, String sort, int page, int size, Integer materialId) {
        if (size <= 0) size = pagination.getSize();
        if (page < 0) page = 0;

        List<Product> allProducts = productRepository.findAll();

        // best_seller: tong quantity trong OrderDetail cua cac don KHONG bi CANCELLED
        Map<Integer, Long> variantSoldMap = orderDetailRepository.findAll().stream()
                .filter(od -> od.getProductVariant() != null && od.getQuantity() != null)
                .filter(od -> od.getOrder() == null || !CANCELLED.equalsIgnoreCase(od.getOrder().getStatus()))
                .collect(Collectors.groupingBy(od -> od.getProductVariant().getId(),
                        Collectors.summingLong(OrderDetail::getQuantity)));

        List<ProductDto> dtoList = new ArrayList<>();

        for (Product p : allProducts) {
            if (!Boolean.TRUE.equals(p.getStatus())) continue;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.trim().toLowerCase();
                String name = p.getName() == null ? "" : p.getName().toLowerCase();
                String code = p.getCode() == null ? "" : p.getCode().toLowerCase();
                if (!name.contains(kw) && !code.contains(kw)) continue;
            }
            if (categoryId != null && (p.getCategory() == null || !p.getCategory().getId().equals(categoryId))) continue;

            List<ProductVariant> variants = productVariantRepository.findByProduct_IdAndStatusTrue(p.getId());
            if (variants.isEmpty()) continue;

            if (materialId != null) variants = variants.stream().filter(v -> v.getMaterial().getId().equals(materialId)).toList();
            if (variants.isEmpty()) continue;
            if (sizeId != null) {
                boolean hasSize = variants.stream().anyMatch(v -> v.getSize() != null && v.getSize().getId().equals(sizeId));
                if (!hasSize) continue;
            }

            BigDecimal minOrig = null, maxOrig = null, minFinal = null, maxFinal = null;
            int maxDiscount = 0;
            boolean promo = false;
            int stock = 0;
            long sold = 0;
            List<ProductDto.VariantDto> varDtos = new ArrayList<>();

            for (ProductVariant v : variants) {
                int q = v.getQuantity() == null ? 0 : v.getQuantity();
                stock += q;
                sold += variantSoldMap.getOrDefault(v.getId(), 0L);

                ProductDto.VariantDto vDto = new ProductDto.VariantDto();
                vDto.setId(v.getId());
                vDto.setMaterialId(v.getMaterial().getId()); vDto.setMaterialName(v.getMaterial().getName());
                vDto.setSizeId(v.getSize().getId());
                vDto.setSizeValue(v.getSize() != null ? v.getSize().getSizeValue() : "");
                vDto.setQuantity(q);
                varDtos.add(vDto);

                PriceInfo pi = pricingService.calculatePrice(v);
                if (minOrig == null || pi.getOriginalPrice().compareTo(minOrig) < 0) minOrig = pi.getOriginalPrice();
                if (maxOrig == null || pi.getOriginalPrice().compareTo(maxOrig) > 0) maxOrig = pi.getOriginalPrice();
                if (minFinal == null || pi.getFinalPrice().compareTo(minFinal) < 0) minFinal = pi.getFinalPrice();
                if (maxFinal == null || pi.getFinalPrice().compareTo(maxFinal) > 0) maxFinal = pi.getFinalPrice();
                if (pi.getDiscountPercentage() > maxDiscount) maxDiscount = pi.getDiscountPercentage();
                if (pi.isHasPromotion()) promo = true;
            }

            // Loc theo GIA HIEU LUC (sau khuyen mai)
            if (minPrice != null && maxFinal.compareTo(minPrice) < 0) continue;
            if (maxPrice != null && minFinal.compareTo(maxPrice) > 0) continue;
            if (Boolean.TRUE.equals(promotion) && !promo) continue;

            ProductDto dto = new ProductDto();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setCode(p.getCode());
            dto.setImageUrl(p.getImageUrl());
            dto.setCategoryId(p.getCategory() != null ? p.getCategory().getId() : null);
            dto.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);
            dto.setMinOriginalPrice(minOrig);
            dto.setMaxOriginalPrice(maxOrig);
            dto.setMinFinalPrice(minFinal);
            dto.setMaxFinalPrice(maxFinal);
            dto.setDiscountPercentage(maxDiscount);
            dto.setHasPromotion(promo);
            dto.setTotalStock(stock);
            dto.setTotalSold(sold);
            dto.setVariants(varDtos);
            dtoList.add(dto);
        }

        // "Moi nhat" = id lon nhat (bang products khong co cot created_at)
        Comparator<ProductDto> newest = Comparator.comparing(ProductDto::getId).reversed();
        if ("price_asc".equals(sort)) {
            dtoList.sort(Comparator.comparing(ProductDto::getMinFinalPrice).thenComparing(newest));
        } else if ("price_desc".equals(sort)) {
            dtoList.sort(Comparator.comparing(ProductDto::getMinFinalPrice).reversed().thenComparing(newest));
        } else if ("best_seller".equals(sort)) {
            dtoList.sort(Comparator.comparingLong(ProductDto::getTotalSold).reversed().thenComparing(newest));
        } else {
            dtoList.sort(newest);
        }

        return pagination.slice(dtoList,page,size);
    }
}
