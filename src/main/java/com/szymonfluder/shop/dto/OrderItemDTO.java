package com.szymonfluder.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemDTO {

    private int orderItemId;
    private int orderId;
    private int quantity;
    private String productName;
    private int productId;
    private double priceAtPurchase;

}
