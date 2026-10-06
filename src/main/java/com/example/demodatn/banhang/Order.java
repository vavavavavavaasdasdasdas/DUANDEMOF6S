package com.example.demodatn.banhang;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @Column(nullable = false)
    private String status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
