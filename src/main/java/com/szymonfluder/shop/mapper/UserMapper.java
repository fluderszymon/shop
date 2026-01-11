package com.szymonfluder.shop.mapper;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel="spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "balance", expression = "java(java.math.BigDecimal.valueOf(0.00).setScale(2, java.math.RoundingMode.HALF_UP))")
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "role", ignore = true)
    User userRegisterDTOToUser(UserRegisterDTO userRegisterDTO);

    @Mapping(source="cart.cartId", target="cartId")
    UserDTO userToUserDTO(User user);

}