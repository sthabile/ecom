package com.pc.ecom.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name="categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /*
     *Can specify if this should be an ordered-sequence But must annotate with SequenceGenerator
     */
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long categoryId;

    @NotBlank()
    @Size(min = 5, message = "Category Name must be at least 5 characters")
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}
