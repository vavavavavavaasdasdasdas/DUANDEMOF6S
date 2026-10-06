package com.example.demodatn.banhang.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {
    private Integer id;
    private String orderCode;
    private String customerName;
    private String orderType;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private LocalDateTime createdAt;
    
    // Additional fields for details
    private String phoneNumber;
    private String shippingAddress;
}
