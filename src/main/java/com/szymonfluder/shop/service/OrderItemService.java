package com.szymonfluder.shop.service;

import java.util.List;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;

public interface OrderItemService {

    List<OrderItemDTO> getAllOrderItems();
    List<OrderItemDTO> getAllOrderItemsByOrderId(int orderId);
    OrderItemDTO getOrderItemById(int orderItemId);
    OrderItemDTO addOrderItem(OrderItemDTO orderItemDTO);

    OrderItemDTO addOrderItemFromCartItem(CartItemDTO cartItemDTO, int orderId);

}
