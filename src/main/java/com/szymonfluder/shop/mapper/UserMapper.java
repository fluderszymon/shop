package com.szymonfluder.shop.mapper;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "role", ignore = true)
    User userRegisterDTOToUser(UserRegisterDTO userRegisterDTO);

    @Mapping(source="cart.cartId", target="cartId")
    UserDTO userToUserDTO(User user);

}