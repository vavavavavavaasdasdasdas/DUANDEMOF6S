package com.example.demodatn.khuyenmai;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import com.example.demodatn.khuyenmai.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {
    java.util.List<PromotionDetail> findByProductId(Integer productId);
    java.util.List<PromotionDetail> findByPromotion_Id(Integer promotionId);
    void deleteByPromotion_Id(Integer promotionId);
}
