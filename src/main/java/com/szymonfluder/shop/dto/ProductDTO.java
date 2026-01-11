package com.szymonfluder.shop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.szymonfluder.shop.validation.ValidPrice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDTO {

    @NotNull(message = "Product ID is required")
    private int productId;
    
    @NotBlank(message = "Product Name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Product Description is required")
    @Size(min = 5, max = 255, message = "Description must be between 5 and 255 characters")
    private String description;

    @NotNull(message = "Product Price is required")
    @ValidPrice
    private BigDecimal price;

    @NotNull(message = "Product Stock is required")
    @Min(value = 0, message = "Stock must be non-negative")
    @Max(value = 999999, message = "Stock must not exceed 999999")
    private int stock;

}