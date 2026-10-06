package com.example.demodatn.khuyenmai;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promotion_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
