package com.szymonfluder.shop.controller;

import java.util.List;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Autowired
    public OrderController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping("{userId}")
    public OrderDTO addOrder(@PathVariable int userId) {
        return orderService.addOrder(userId);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItemDTO> getOrderItemsInOrderByOrderId(@PathVariable int orderId) {
        return orderItemService.getAllOrderItemsByOrderId(orderId);
    }

    @PostMapping("{orderId}/items")
    public OrderItemDTO addOrderItem(@PathVariable int orderId, @RequestBody OrderItemDTO orderItemDTO) {
        return orderItemService.addOrderItem(orderItemDTO);
    }
    
}
