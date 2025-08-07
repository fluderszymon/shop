package com.szymonfluder.shop.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.entity.Order;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.repository.OrderRepository;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.OrderItemService;
import com.szymonfluder.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemService orderItemService;
    private final CartService cartService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, OrderItemService orderItemService, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderItemService = orderItemService;
        this.cartService = cartService;
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(orderMapper::orderToOrderDTO)
            .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(int orderId) {
        Order foundOrder = orderRepository.findById(orderId).orElse(null);
        return orderMapper.orderToOrderDTO(foundOrder);
    }

    @Override
    public OrderDTO addOrder(int userId, int cartId) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(userId);
        orderDTO.setTotalPrice(cartService.getCartTotal(cartId));
        orderDTO.setOrderDate(LocalDate.now());
        Order savedOrder = orderRepository.save(orderMapper.orderDTOToOrder(orderDTO));
        return orderMapper.orderToOrderDTO(savedOrder);
    }

    @Override
    public double getOrderTotal(int orderId) {
        List<OrderItemDTO> orderItemDTOs = orderItemService.getAllOrderItemsByOrderId(orderId);
        double total = 0;
        for (OrderItemDTO orderItemDTO : orderItemDTOs) {
            total += orderItemDTO.getQuantity() * orderItemDTO.getPriceAtPurchase();
        }
        return total;
    }
}
