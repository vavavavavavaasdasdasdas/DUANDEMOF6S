package com.example.demodatn.dto.nhanvien;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AccountPasswordForm {
    @NotBlank(message="Nhập mật khẩu hiện tại.") @Size(max=72)
    private String currentPassword;
    @NotBlank(message="Nhập mật khẩu mới.") @Size(min=8,max=72,message="Mật khẩu mới phải từ 8 đến 72 ký tự.")
    private String newPassword;
    @NotBlank(message="Xác nhận mật khẩu mới.") @Size(max=72)
    private String confirmPassword;
}
