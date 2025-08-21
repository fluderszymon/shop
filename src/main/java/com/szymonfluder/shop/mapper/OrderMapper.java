package com.szymonfluder.shop.mapper;

import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface OrderMapper {
    
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source="user.userId", target="userId")
    OrderDTO orderToOrderDTO(Order order);

    @Mapping(source="userId", target="user.userId")
    @Mapping(target="orderItems", ignore = true)
    Order orderDTOToOrder(OrderDTO orderDTO);

}
