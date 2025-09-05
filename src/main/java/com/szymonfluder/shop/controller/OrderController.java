package com.szymonfluder.shop.controller;

import java.util.List;

import com.szymonfluder.shop.dto.OrderItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDTO getOrderById(@PathVariable int orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/order-items")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderItemDTO> getAllOrderItems() {
        return orderService.getAllOrderItems();
    }

    @GetMapping("/{orderId}/order-items")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderItemDTO> getOrderItemsInOrderByOrderId(@PathVariable int orderId) {
        return orderService.getAllOrderItemsByOrderId(orderId);
    }

    @PostMapping("/checkout/{userId}/{cartId}")
    public void checkout(@PathVariable int userId, @PathVariable int cartId) {
        orderService.checkout(userId, cartId);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public List<OrderDTO> getMyOrders() {
        return orderService.getOrdersForCurrentUser();
    }

    @GetMapping("/my-orders/order-items")
    @PreAuthorize("hasRole('USER')")
    public List<OrderItemDTO> getMyOrderItems() {
        return orderService.getOrderItemsForCurrentUser();
    }
}