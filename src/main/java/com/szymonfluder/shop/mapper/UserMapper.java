package com.szymonfluder.shop.mapper;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserRegisterDTO userToUserRegisterDTO(User user);
    User userRegisterDTOToUser(UserRegisterDTO userRegisterDTO);

    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);

}