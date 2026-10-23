package com.example.demodatn.repository.khuyenmai;
import com.example.demodatn.entity.khuyenmai.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {
    java.util.List<PromotionDetail> findByPromotion_IdAndProduct_IdIn(Integer id,java.util.Collection<Integer> products);
    java.util.List<PromotionDetail> findByProductId(Integer productId);
    java.util.List<PromotionDetail> findByPromotion_Id(Integer promotionId);
    void deleteByPromotion_Id(Integer promotionId);
}
