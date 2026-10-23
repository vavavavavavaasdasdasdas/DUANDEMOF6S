package com.example.demodatn.banhang;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Integer productId;
    private Integer variantId;
    private String productName;
    private String size;
    private BigDecimal price;
    private Integer quantity;
    
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }
}
