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
public class OrderItemDTO {

    @NotNull(message = "Order Item ID is required")
    private int orderItemId;

    @NotNull(message = "Order ID is required")
    private int orderId;

    @NotNull(message = "Quantity is required")
    private int quantity;
    
    @NotBlank(message = "Product Name is required")
    private String productName;

    @NotNull(message = "Product ID is required")
    private int productId;

    @NotNull(message = "Price at Purchase is required")
    private BigDecimal priceAtPurchase;

}