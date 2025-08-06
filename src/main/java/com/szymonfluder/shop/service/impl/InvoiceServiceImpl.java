package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.service.InvoiceService;
import com.szymonfluder.shop.service.OrderItemService;
import com.szymonfluder.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Autowired
    public InvoiceServiceImpl(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @Override
    public InvoiceDTO createInvoiceDTO(int orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber("INV_" + orderDTO.getOrderId());
        List<OrderItemDTO> orderItemDTOList = orderItemService.getAllOrderItemsByOrderId(orderId);
        invoiceDTO.setOrderItemDTOList(orderItemDTOList);
        invoiceDTO.setInvoiceDate(orderDTO.getOrderDate());
        double total = 0;
        for (OrderItemDTO orderItemDTO : orderItemDTOList) {
            total += orderItemDTO.getPriceAtPurchase() * orderItemDTO.getQuantity();
        }
        invoiceDTO.setTotalPrice(total);
        return invoiceDTO;
    }
}
