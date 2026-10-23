package com.example.demodatn.sanpham;
import com.example.demodatn.entity.sanpham.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InventoryLogRepository extends JpaRepository<InventoryLog,Long> {
    boolean existsByVariant_Product_Id(Integer productId);
}

