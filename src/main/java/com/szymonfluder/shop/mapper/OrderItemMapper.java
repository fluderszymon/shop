package com.szymonfluder.shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.entity.OrderItem;

@Mapper(componentModel="spring")
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    @Mapping(source="orderId", target="order.orderId")
    @Mapping(source="productName", target="product.name")
    OrderItem orderItemDTOToOrderItem(OrderItemDTO orderItemDTO);

    @Mapping(source="order.orderId", target="orderId")
    @Mapping(source="product.name", target="productName")
    OrderItemDTO orderItemToOrderItemDTO(OrderItem orderItem);

}
