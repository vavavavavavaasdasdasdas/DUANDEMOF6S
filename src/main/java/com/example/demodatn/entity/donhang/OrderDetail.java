package com.example.demodatn.entity.donhang;
import com.example.demodatn.entity.sanpham.ProductVariant;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    // Snapshot fields
    @Column(name = "product_name")
    private String productName;

    @Column(name = "size_name")
    private String sizeName;

    @Column(name = "ten_chat_lieu", columnDefinition = "nvarchar(100)")
    private String materialName;

    public String getDisplayMaterialName() { return materialName == null || materialName.isBlank() ? "—" : materialName; }

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
}
