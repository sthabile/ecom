package com.pc.ecom.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private String productDescription;
    private String image;
    private Integer quantity;
    private double specialPrice;
    private double price;
    private double discount;
}
