package com.szymonfluder.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartDTO {
    
    @NotNull(message = "Cart ID is required")
    private int cartId;
    
    @NotNull(message = "User ID is required")
    private int userId;

}