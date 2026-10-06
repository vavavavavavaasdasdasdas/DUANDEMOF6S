package com.example.demodatn.khuyenmai;
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
