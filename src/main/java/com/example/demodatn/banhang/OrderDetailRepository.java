package com.example.demodatn.banhang;
import com.example.demodatn.entity.donhang.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    boolean existsByProductVariant_Product_Id(Integer productId);
}
