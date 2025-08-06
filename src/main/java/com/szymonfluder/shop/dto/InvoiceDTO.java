package com.szymonfluder.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvoiceDTO {

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private List<OrderItemDTO> orderItemDTOList;
    private double totalPrice;

}
