package com.szymonfluder.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDTO {

    @NotNull(message = "Product ID is required")
    private int productId;
    
    @NotBlank(message = "Product Name is required")
    private String name;
    
    @NotBlank(message = "Product Description is required")
    private String description;

    @NotNull(message = "Product Price is required")
    private BigDecimal price;

    @NotNull(message = "Product Stock is required")
    private int stock;

}