package com.example.demodatn.banhang.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank(message = "{ui.058}")
    private String receiverName;

    @NotBlank(message = "{ui.059}")
    private String phone;

    @NotBlank(message = "{ui.060}")
    private String email;

    @NotBlank(message = "{ui.061}")
    private String shippingAddress;

    @NotBlank(message = "{ui.062}")
    private String paymentMethod;

    private String voucherCode;
}
