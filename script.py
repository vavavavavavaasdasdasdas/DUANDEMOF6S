import os
import re

base_dir = r"C:\DuAnTNDEMO\DemoDATN\src\main\java\com\example\demodatn"
template_dir = r"C:\DuAnTNDEMO\DemoDATN\src\main\resources\templates\admin"

# 1. Create DTOs

dtos = {
    "sanpham/ProductDto.java": '''package com.example.demodatn.sanpham;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class ProductDto {
    private Integer id;
    @NotBlank(message = "Code is required")
    private String code;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotNull(message = "Category is required")
    private Integer categoryId;
    private String imageUrl;
    private Boolean status = true;
}
''',
    "khachhang/CustomerDto.java": '''package com.example.demodatn.khachhang;
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
''',
    "nhanvien/EmployeeDto.java": '''package com.example.demodatn.nhanvien;
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
''',
    "khuyenmai/VoucherDto.java": '''package com.example.demodatn.khuyenmai;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
@Data
public class VoucherDto {
    private Integer id;
    @NotBlank(message = "Code is required")
    private String code;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Discount Type is required")
    private String discountType;
    @NotNull(message = "Discount Value is required")
    @Min(0)
    private BigDecimal discountValue;
    @Min(0)
    private BigDecimal minOrderValue;
    @Min(0)
    private BigDecimal maxDiscount;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    @Min(1)
    private Integer usageLimit;
    private Integer usedCount = 0;
    private Boolean isPublic = true;
    private Integer customerId;
    private Boolean status = true;
}
''',
    "khuyenmai/PromotionDto.java": '''package com.example.demodatn.khuyenmai;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
@Data
public class PromotionDto {
    private Integer id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Discount type is required")
    private String discountType;
    @NotNull(message = "Discount value is required")
    @Min(0)
    private BigDecimal discountValue;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    private Boolean status = true;
}
'''
}

for path, content in dtos.items():
    with open(os.path.join(base_dir, path), "w") as f:
        f.write(content)

print("DTOs created.")
