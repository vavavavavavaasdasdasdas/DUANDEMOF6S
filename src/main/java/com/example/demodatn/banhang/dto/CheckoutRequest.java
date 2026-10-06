package com.example.demodatn.banhang.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Name is required")
    private String customerName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\\\b", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String voucherCode;
    private String email;
}