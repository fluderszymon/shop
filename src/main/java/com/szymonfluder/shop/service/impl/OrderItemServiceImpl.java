package com.szymonfluder.shop.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, ProductService productService,
                                OrderItemMapper orderItemMapper, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
        this.orderItemMapper = orderItemMapper;
        this.productRepository = productRepository;
    }

    @Override
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findAll()
            .stream()
            .map(orderItemMapper::orderItemToOrderItemDTO)
            .collect(Collectors.toList());
    }

    @Override
    public OrderItemDTO getOrderItemById(int orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));
        return orderItemMapper.orderItemToOrderItemDTO(orderItem);
    }

    @Override
    public List<OrderItemDTO> getAllOrderItemsByOrderId(int orderId) {
        return orderItemRepository.findAllOrderItemsByOrderId(orderId);
    }

    @Override
    public OrderItemDTO addOrderItem(OrderItemDTO orderItemDTO) {
        if (productService.isEnough(orderItemDTO.getProductId(), orderItemDTO.getQuantity())) {
            OrderItem savedOrderItem = orderItemRepository.save(orderItemMapper.orderItemDTOToOrderItem(orderItemDTO));
            return orderItemMapper.orderItemToOrderItemDTO(savedOrderItem);
        }
        return null;
    }

    public OrderItemDTO addOrderItemFromCartItem(CartItemDTO cartItemDTO, int orderId) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderId(orderId);
        orderItemDTO.setQuantity(cartItemDTO.getQuantity());
        Product product = productRepository.findById(cartItemDTO.getProductId()).orElse(new Product());
        orderItemDTO.setProductName(product.getName());
        orderItemDTO.setProductId(cartItemDTO.getProductId());
        orderItemDTO.setPriceAtPurchase(product.getPrice());
        OrderItem savedOrderItem = orderItemMapper.orderItemDTOToOrderItem(orderItemDTO);
        savedOrderItem.setProduct(product);
        orderItemRepository.save(savedOrderItem);
        return orderItemMapper.orderItemToOrderItemDTO(savedOrderItem);
    }
}
