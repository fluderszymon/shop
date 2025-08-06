package com.szymonfluder.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDTO {

    private int orderId;
    private int userId;
    private double totalPrice;
    private LocalDate orderDate;

}
