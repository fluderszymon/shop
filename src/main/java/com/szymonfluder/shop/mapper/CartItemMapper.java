package com.szymonfluder.shop.mapper;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface CartItemMapper {

    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    @Mapping(source="cartId", target="cart.cartId")
    @Mapping(source="productId", target="product.productId")
    CartItem cartItemDTOToCartItem(CartItemDTO cartItemDTO);

    @Mapping(source="cart.cartId", target="cartId")
    @Mapping(source="product.productId", target="productId")
    CartItemDTO cartItemToCartItemDTO(CartItem cartItem);

}
