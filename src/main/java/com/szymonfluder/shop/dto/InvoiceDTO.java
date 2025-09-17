package com.szymonfluder.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvoiceDTO {

    @NotBlank(message = "Invoice Number is required")
    private String invoiceNumber;

    @NotNull(message = "Invoice Date is required")
    private LocalDate invoiceDate;

    @NotNull(message = "Order Item List is required")
    private List<OrderItemDTO> orderItemDTOList;

    @NotNull(message = "Total Price is required")
    private BigDecimal totalPrice;

    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "User Address is required")
    private String address;

}