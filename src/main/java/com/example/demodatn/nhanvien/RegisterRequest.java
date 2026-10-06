package com.example.demodatn.nhanvien;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
    
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "SĐT không được để trống")
    private String phone;
}
