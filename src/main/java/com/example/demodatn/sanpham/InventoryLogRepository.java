package com.example.demodatn.sanpham;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InventoryLogRepository extends JpaRepository<InventoryLog,Long> {
    boolean existsByVariant_Product_Id(Integer productId);
}

