package com.szymonfluder.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartItemDTO {

    @NotNull(message = "Cart Item ID is required")
    private int cartItemId;
    
    @NotNull(message = "Cart ID is required")
    private int cartId;

    @NotNull(message = "Product ID is required")
    private int productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 999, message = "Quantity must not exceed 999")
    private int quantity;

}