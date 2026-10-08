package com.example.demodatn.sanpham;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service @RequiredArgsConstructor
public class InventoryService {
    private final ProductVariantRepository variants;
    private final InventoryLogRepository logs;
    private final jakarta.persistence.EntityManager entityManager;
    @org.springframework.beans.factory.annotation.Value("${shop.time-zone:Asia/Ho_Chi_Minh}") private String timeZone;

    public ProductVariant lock(Integer id) {
        ProductVariant variant = variants.findForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,com.example.demodatn.core.NhanHienThi.text("ui.043")));
        // A request may have read this entity before the sale transaction acquired its lock.
        entityManager.refresh(variant);
        return variant;
    }
    @Transactional
    public void adjust(InventoryForm form) {
        ProductVariant variant = lock(form.getVariantId());
        if (!"ADD".equals(form.getMode()) && !"SET".equals(form.getMode())) throw new CatalogValidationException("mode","Cách điều chỉnh không hợp lệ.");
        if (form.getQuantity() == null || form.getQuantity() < 0 || ("ADD".equals(form.getMode()) && form.getQuantity() == 0))
            throw new CatalogValidationException("quantity","Số lượng nhập thêm phải > 0; tồn kho mới phải ≥ 0.");
        if (!variant.getQuantity().equals(form.getExpectedQuantity()))
            throw new CatalogValidationException("quantity","Tồn kho đã thay đổi. Tải lại trang rồi điều chỉnh.");
        long target = "ADD".equals(form.getMode()) ? (long) variant.getQuantity() + form.getQuantity() : form.getQuantity();
        if (target == variant.getQuantity()) throw new CatalogValidationException("quantity","Tồn kho không thay đổi. Nhập số lượng khác để ghi nhận điều chỉnh.");
        change(variant, target, form.getReason());
    }
    @Transactional
    public ProductVariant sell(Integer variantId, int quantity, String reference) {
        ProductVariant variant = lock(variantId);
        if (quantity <= 0 || !Boolean.TRUE.equals(variant.getStatus()) || !Boolean.TRUE.equals(variant.getProduct().getStatus()))
            throw new IllegalArgumentException("Sản phẩm đã ngừng bán hoặc số lượng không hợp lệ.");
        if (variant.getQuantity() < quantity) throw new IllegalArgumentException("Không đủ tồn kho: " + variant.getProduct().getName() + " / " + variant.getSize().getSizeValue());
        change(variant, (long) variant.getQuantity() - quantity, "Bán hàng: " + reference);
        return variant;
    }
    @Transactional
    public void restore(Integer variantId, int quantity, String reference) {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng hoàn kho không hợp lệ.");
        ProductVariant variant = lock(variantId);
        change(variant, (long) variant.getQuantity() + quantity, "Hủy đơn: " + reference);
    }
    // Called only with a managed locked variant, or a newly inserted variant, in a transaction.
    public void change(ProductVariant variant, long target, String reason) {
        if (target < 0 || target > Integer.MAX_VALUE) throw new CatalogValidationException("quantity","Tồn kho vượt giới hạn hợp lệ.");
        if (reason == null || reason.isBlank() || reason.trim().length() > 500) throw new CatalogValidationException("reason","Lý do phải có từ 1 đến 500 ký tự.");
        int before = variant.getQuantity();
        if (before == target) return;
        variant.setQuantity((int) target);
        variants.save(variant);
        InventoryLog log = new InventoryLog();
        log.setVariant(variant);
        log.setQuantityBefore(before);
        log.setQuantityAfter((int) target);
        log.setQuantityChange((int) (target - before));
        log.setReason(reason.trim());
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.setActor(auth != null ? auth.getName() : "system");
        log.setCreatedAt(LocalDateTime.now(ZoneId.of(timeZone)));
        logs.save(log);
    }
}

