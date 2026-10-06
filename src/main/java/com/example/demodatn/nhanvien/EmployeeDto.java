package com.example.demodatn.nhanvien;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class EmployeeDto {
    private Integer id;
    @NotBlank(message = "Code is required")
    private String employeeCode;
    @NotBlank(message = "Name is required")
    private String fullName;
    @NotBlank(message = "Phone is required")
    private String phone;
    @Email
    private String email;
    private String address;
    private String position;
    private Integer userId;
    private Boolean status = true;
}
