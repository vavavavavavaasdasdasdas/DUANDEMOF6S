package com.example.demodatn.sanpham;
import com.example.demodatn.banhang.OrderDetailRepository;
import com.example.demodatn.khuyenmai.PromotionDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service @RequiredArgsConstructor
public class AdminCatalogService {
    private final ProductRepository products;
    private final ProductVariantRepository variants;
    private final CategoryRepository categories;
    private final SizeRepository sizes;
    private final OrderDetailRepository details;
    private final PromotionDetailRepository promotions;
    private final InventoryLogRepository inventoryLogs;
    private final InventoryService inventory;
    private final ProductImageStorage images;

    public Product product(Integer id) {
        return products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy sản phẩm."));
    }
    public ProductVariant variant(Integer id) {
        return variants.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,com.example.demodatn.core.NhanHienThi.text("ui.043")));
    }
    @Transactional
    public Product saveProduct(AdminProductForm form) {
        String code = form.getCode().trim();
        if (products.existsByCodeIgnoreCaseAndIdNot(code, form.getId() == null ? -1 : form.getId()))
            throw new CatalogValidationException("code","Mã sản phẩm đã tồn tại.");
        Category category = categories.findById(form.getCategoryId()).orElseThrow(() -> new CatalogValidationException("categoryId","Danh mục không tồn tại."));
        Product product = form.getId() == null ? new Product() : product(form.getId());
        product.setCode(code); product.setName(form.getName().trim()); product.setDescription(form.getDescription());
        product.setCategory(category); product.setStatus(form.getStatus());
        String image = images.store(form.getImage());
        if (image != null) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCompletion(int status) { if (status != STATUS_COMMITTED) images.removeNewFile(image); }
            });
            product.setImageUrl(image);
        }
        return products.saveAndFlush(product);
    }
    @Transactional
    public String deleteProduct(Integer id) {
        Product product = product(id);
        if (details.existsByProductVariant_Product_Id(id)) {
            product.setStatus(false);
            return "Sản phẩm đã có trong đơn hàng: đã chuyển sang ngừng bán, không xóa dữ liệu đơn cũ.";
        }
        if (variants.existsByProduct_Id(id) || inventoryLogs.existsByVariant_Product_Id(id) || !promotions.findByProductId(id).isEmpty()) {
            product.setStatus(false);
            return com.example.demodatn.core.NhanHienThi.text("ui.044");
        }
        products.delete(product);
        products.flush();
        return "Đã xóa sản phẩm chưa có dữ liệu liên quan.";
    }
    @Transactional
    public ProductVariant saveVariant(VariantForm form) {
        Product product = product(form.getProductId());
        Size size = sizes.findById(form.getSizeId()).orElseThrow(() -> new CatalogValidationException("sizeId","Size không tồn tại."));
        if (variants.existsByProduct_IdAndSize_IdAndIdNot(product.getId(),size.getId(),form.getId()==null ? -1 : form.getId()))
            throw new CatalogValidationException("sizeId",com.example.demodatn.core.NhanHienThi.text("ui.045"));
        ProductVariant variant = form.getId() == null ? new ProductVariant() : inventory.lock(form.getId());
        if (form.getId() != null && !variant.getProduct().getId().equals(product.getId()))
            throw new CatalogValidationException("productId",com.example.demodatn.core.NhanHienThi.text("ui.046"));
        int before = variant.getQuantity();
        if (form.getId() != null && !java.util.Objects.equals(form.getExpectedQuantity(),before))
            throw new CatalogValidationException("quantity","Tồn kho đã thay đổi. Tải lại trang trước khi lưu.");
        if (form.getId() != null && before != form.getQuantity() && (form.getReason()==null || form.getReason().isBlank()))
            throw new CatalogValidationException("reason","Nhập lý do khi thay đổi số lượng.");
        variant.setProduct(product); variant.setSize(size); variant.setPrice(form.getPrice()); variant.setStatus(form.getStatus());
        variants.saveAndFlush(variant);
        inventory.change(variant, form.getQuantity(), form.getId() == null ? com.example.demodatn.core.NhanHienThi.text("ui.047") : (form.getReason()==null || form.getReason().isBlank() ? com.example.demodatn.core.NhanHienThi.text("ui.048") : form.getReason()));
        return variant;
    }
    @Transactional
    public void saveLookup(String kind, LookupForm form) {
        String value = form.getValue().trim();
        if ("sizes".equals(kind)) {
            if (value.length()>10) throw new CatalogValidationException("value","Size tối đa 10 ký tự.");
            if (sizes.existsBySizeValueIgnoreCaseAndIdNot(value,form.getId()==null ? -1 : form.getId()))
                throw new CatalogValidationException("value","Size đã tồn tại.");
            Size size = form.getId()==null ? new Size() : sizes.findById(form.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            size.setSizeValue(value); sizes.saveAndFlush(size);
        } else {
            if (categories.existsByNameIgnoreCaseAndIdNot(value,form.getId()==null ? -1 : form.getId()))
                throw new CatalogValidationException("value","Danh mục đã tồn tại.");
            Category category = form.getId()==null ? new Category() : categories.findById(form.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            category.setName(value); category.setStatus(Boolean.TRUE.equals(form.getStatus())); categories.saveAndFlush(category);
        }
    }
    @Transactional
    public String deleteLookup(String kind, Integer id) {
        if ("sizes".equals(kind)) {
            if (variants.existsBySize_Id(id)) return com.example.demodatn.core.NhanHienThi.text("ui.049");
            sizes.deleteById(id); sizes.flush(); return "Đã xóa size.";
        }
        if (products.existsByCategory_Id(id)) return "Không thể xóa danh mục đang được sản phẩm tham chiếu.";
        categories.deleteById(id); categories.flush(); return "Đã xóa danh mục.";
    }
}

