package com.example.demodatn.dto.khachhang;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerDto {
    private Integer id;
    @NotBlank(message="Nhập mã.")
    @Pattern(regexp="[A-Za-z0-9_-]{1,20}",message="Mã gồm tối đa 20 chữ, số, dấu - hoặc _.")
    private String customerCode;
    @NotBlank(message="Nhập họ tên.") @Size(max=100,message="Họ tên tối đa 100 ký tự.")
    private String fullName;
    @NotBlank(message="Nhập số điện thoại.")
    @Pattern(regexp="(?:0[0-9]{9}|\\+84[0-9]{9})",message="SĐT gồm 10 số bắt đầu bằng 0 hoặc +84 và 9 số.")
    private String phone;
    @NotBlank(message="Nhập email.") @Email(message="Email không hợp lệ.") @Size(max=100,message="Email tối đa 100 ký tự.")
    private String email;
    @Size(max=255,message="Địa chỉ tối đa 255 ký tự.") private String address;
}
