package com.szymonfluder.shop.service;

import java.util.List;

import com.szymonfluder.shop.dto.OrderItemDTO;

public interface OrderItemService {

    List<OrderItemDTO> getAllOrderItems();
    List<OrderItemDTO> getAllOrderItemsByOrderId(Integer orderId);
    OrderItemDTO getOrderItemById(Integer orderItemId);
    OrderItemDTO addOrderItem(OrderItemDTO orderItemDTO);

}
