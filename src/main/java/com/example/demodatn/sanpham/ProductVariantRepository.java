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
    java.util.List<ProductVariant> findByProduct_Id(Integer productId);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ProductVariant pv SET pv.quantity = pv.quantity - :qty WHERE pv.id = :id AND pv.quantity >= :qty")
    int deductStock(@org.springframework.data.repository.query.Param("id") Integer id, @org.springframework.data.repository.query.Param("qty") Integer qty);
}
