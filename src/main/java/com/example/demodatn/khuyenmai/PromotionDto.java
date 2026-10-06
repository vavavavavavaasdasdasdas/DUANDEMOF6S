package com.example.demodatn.khuyenmai;
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
