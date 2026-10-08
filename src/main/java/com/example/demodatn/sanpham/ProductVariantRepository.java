package com.example.demodatn.sanpham;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import com.example.demodatn.sanpham.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    boolean existsByProduct_IdAndSize_IdAndIdNot(Integer productId, Integer sizeId, Integer id);
    boolean existsBySize_Id(Integer sizeId);
    boolean existsByProduct_Id(Integer productId);
    java.util.List<ProductVariant> findByProduct_IdAndStatusTrue(Integer productId);
    java.util.List<ProductVariant> findByStatusTrueAndProduct_StatusTrue();
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select v from ProductVariant v where v.id=:id")
    java.util.Optional<ProductVariant> findForUpdate(@org.springframework.data.repository.query.Param("id") Integer id);
    java.util.List<ProductVariant> findByProduct_Id(Integer productId);
}
