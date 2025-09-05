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
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public OrderDTO getOrderById(@PathVariable int orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/order-items")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<OrderItemDTO> getAllOrderItems() {
        return orderService.getAllOrderItems();
    }

    @GetMapping("/{orderId}/order-items")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<OrderItemDTO> getOrderItemsInOrderByOrderId(@PathVariable int orderId) {
        return orderService.getAllOrderItemsByOrderId(orderId);
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('USER')")
    public void checkout() {
        orderService.checkout();
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('USER')")
    public List<OrderDTO> getMyOrders() {
        return orderService.getOrdersForCurrentUser();
    }

    @GetMapping("/my-orders/{orderId}")
    @PreAuthorize("hasAuthority('USER')")
    public List<OrderItemDTO> getOrderItemsInMyOrder(@PathVariable int orderId) {
        return orderService.getOrderItemsInOrderByOrderIdForCurrentUser(orderId);
    }

    @GetMapping("/my-orders/order-items")
    @PreAuthorize("hasAuthority('USER')")
    public List<OrderItemDTO> getMyOrderItems() {
        return orderService.getOrderItemsForCurrentUser();
    }
}