package com.szymonfluder.shop.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.entity.OrderItem;
import com.szymonfluder.shop.mapper.OrderItemMapper;
import com.szymonfluder.shop.repository.OrderItemRepository;
import com.szymonfluder.shop.service.OrderItemService;
import com.szymonfluder.shop.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl implements OrderItemService{
    
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final OrderItemMapper orderItemMapper;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, ProductService productService, OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findAll()
            .stream()
            .map(orderItemMapper::orderItemToOrderItemDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemDTO> getAllOrderItemsByOrderId(Integer orderId) {
        return orderItemRepository.findAllOrderItemsByOrderId(orderId);
    }

    public OrderItemDTO getOrderItemById(Integer orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new RuntimeException("Order item not found"));
        return orderItemMapper.orderItemToOrderItemDTO(orderItem);
    }

    @Override
    public OrderItemDTO addOrderItem(OrderItemDTO orderItemDTO) {
        if (productService.isEnough(orderItemDTO.getProductId(), orderItemDTO.getQuantity())) {
            OrderItem savedOrderItem = orderItemRepository.save(orderItemMapper.orderItemDTOToOrderItem(orderItemDTO));
            return orderItemMapper.orderItemToOrderItemDTO(savedOrderItem);
        }
        return null;
    }


}
