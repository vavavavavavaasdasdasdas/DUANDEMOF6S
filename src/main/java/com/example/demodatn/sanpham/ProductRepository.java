package com.example.demodatn.sanpham;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import com.example.demodatn.sanpham.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Product p where p.id=:id")
    java.util.Optional<Product> lockById(@org.springframework.data.repository.query.Param("id") Integer id);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Integer id);
    boolean existsByCategory_Id(Integer categoryId);
    java.util.List<Product> findByStatusTrue();
}
