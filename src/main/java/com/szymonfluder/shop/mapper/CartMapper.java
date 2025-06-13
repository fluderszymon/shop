package com.szymonfluder.shop.mapper;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(source="userId", target="user.userId")
    Cart CartDTOToCart(CartDTO cartDTO);

    @Mapping(source="user.userId", target="userId")
    CartDTO CartToCartDTO(Cart cart);

}
