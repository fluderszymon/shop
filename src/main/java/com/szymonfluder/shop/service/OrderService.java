package com.szymonfluder.shop.service;

import java.util.List;

import com.szymonfluder.shop.dto.OrderDTO;

public interface OrderService {
    
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(int id);
    OrderDTO addOrder(int userId, int cartId);
    void checkout(int userId, int cartId);
    double getOrderTotal(int cartId);
}
