package com.szymonfluder.shop.service;

import java.util.List;

import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;

public interface OrderService {
    
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(int orderId);

    List<OrderItemDTO> getAllOrderItems();
    List<OrderItemDTO> getAllOrderItemsByOrderId(int orderId);

    void checkout(int userId, int cartId);

}
