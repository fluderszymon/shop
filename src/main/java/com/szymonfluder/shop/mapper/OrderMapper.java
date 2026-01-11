package com.szymonfluder.shop.mapper;

import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface OrderMapper {

    @Mapping(source="user.userId", target="userId")
    OrderDTO orderToOrderDTO(Order order);

    @Mapping(source="userId", target="user.userId")
    @Mapping(target="orderItems", ignore = true)
    Order orderDTOToOrder(OrderDTO orderDTO);

}