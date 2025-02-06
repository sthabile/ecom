package com.pc.ecom.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank()
    @Size(min = 3, max = 50)
    private String productName;

    private String image;

    @Size(min = 0, max = 250)
    private String productDescription;

    @PositiveOrZero
    private Integer quantity;

    @PositiveOrZero
    private double specialPrice;

    @PositiveOrZero
    private double price;

    @PositiveOrZero
    private double discount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
