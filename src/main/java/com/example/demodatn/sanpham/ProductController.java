package com.example.demodatn.sanpham;

import com.example.demodatn.banhang.PricingService;
import com.example.demodatn.banhang.PriceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ProductController {

    @org.springframework.beans.factory.annotation.Value("${shop.page-size:12}")
    private int pageSize;

    private final ProductService productService;
    private final PricingService pricingService;
    private final ProductFilterService productFilterService;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;

    @GetMapping({"/products", "/search"})
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer sizeId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean promotion,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page, Model model) {
        
        Page<ProductDto> productPage = productFilterService.filterProducts(keyword, categoryId, sizeId, minPrice, maxPrice, promotion, sort, page, pageSize);
        
        model.addAttribute("productPage", productPage);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sizeId", sizeId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("promotion", promotion);
        model.addAttribute("sort", sort);
        
        return "user/san-pham";
    }
    
    @GetMapping("/category/{id}")
    public String categoryList(@PathVariable Integer id, Model model, jakarta.servlet.http.HttpServletResponse response) {
        if (!categoryRepository.existsById(id)) {
            response.setStatus(404);
            return "loi/404";
        }
        return list(null, id, null, null, null, null, "newest", 0, model);
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Integer id, Model model, jakarta.servlet.http.HttpServletResponse response) {
        Product product = productService.getProductById(id);
        if (product == null || !Boolean.TRUE.equals(product.getStatus())) {
            response.setStatus(404);
            return "loi/404";
        }
        
        List<ProductVariant> variants = productService.getVariantsByProductId(id);
        
        Map<Integer, PriceInfo> variantPrices = variants.stream()
            .collect(Collectors.toMap(ProductVariant::getId, pricingService::calculatePrice));

        List<Product> relatedProducts = productService.getAllProducts().stream()
            .filter(p -> p.getCategory() != null && product.getCategory() != null 
                    && p.getCategory().getId().equals(product.getCategory().getId())
                    && !p.getId().equals(product.getId()))
            .limit(4)
            .collect(Collectors.toList());
            
        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
        model.addAttribute("variantPrices", variantPrices);
        model.addAttribute("relatedProducts", relatedProducts);
        
        return "user/chi-tiet-san-pham";
    }
}
