package com.example.demodatn.khachhang;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class CustomerDto {
    private Integer id;
    @NotBlank(message = "Code is required")
    private String customerCode;
    @NotBlank(message = "Name is required")
    private String fullName;
    @NotBlank(message = "Phone is required")
    private String phone;
    @Email
    private String email;
    private String address;
    private Boolean status = true;
}
