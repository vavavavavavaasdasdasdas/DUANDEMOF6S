package com.example.demodatn.banhang.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PosCheckoutRequest {
    private Integer customerId;
    private Integer employeeId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String voucherCode;
}