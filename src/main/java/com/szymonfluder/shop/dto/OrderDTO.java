package com.szymonfluder.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDTO {

    @NotNull(message = "Order ID is required")
    private int orderId;

    @NotNull(message = "User ID is required")
    private int userId;

    @NotNull(message = "Total Price is required")
    private BigDecimal totalPrice;

    @NotNull(message = "Order Date is required")
    private LocalDate orderDate;

}