package com.example.demodatn.sanpham;
import com.example.demodatn.entity.sanpham.Product;
import com.example.demodatn.entity.sanpham.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public List<Product> getAllProducts() {
        return productRepository.findByStatusTrue();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<ProductVariant> getVariantsByProductId(Integer productId) {
        // We might need a custom query, let's just fetch all and filter for simplicity if repo method is missing
        return productVariantRepository.findByProduct_IdAndStatusTrue(productId);
    }

    public ProductVariant getVariantById(Integer variantId) {
        return productVariantRepository.findById(variantId).orElse(null);
    }
}
