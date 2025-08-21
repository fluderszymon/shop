package com.szymonfluder.shop.unit.mapper;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.mapper.UserMapperImpl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserMapperTests {

    private final String USERNAME = "john";
    private final String EMAIL = "john.doe@outlook.com";
    private final String PASSWORD = "password";
    private final String ADDRESS = "7 Spruce Street San Diego, CA 92117";
    private final String ROLE = "USER";
    private final double BALANCE = 100.00;

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void userRegisterDTOToUser_shouldMapUserRegisterDTOtoUser() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(USERNAME, EMAIL, PASSWORD, ADDRESS);
        User user = new User(0, USERNAME, EMAIL, PASSWORD, null, null, ADDRESS, 0.0);
        
        assertThat(userMapper.userRegisterDTOToUser(userRegisterDTO)).isEqualTo(user);
    }

    @Test
    void userRegisterDTOToUser_shouldReturnNullWhenUserRegisterDTOIsNull() {
        assertThat(userMapper.userRegisterDTOToUser(null)).isNull();
    }

    @Test
    void userToUserDTO_shouldMapUserToUserDTO() {
        User user = new User(2, USERNAME, EMAIL, PASSWORD, ROLE, null, ADDRESS, BALANCE);
        Cart cart = new Cart(1, user, null);
        user.setCart(cart);

        UserDTO userDTO = new UserDTO(2, USERNAME, EMAIL, ROLE, 1, ADDRESS, BALANCE);

        assertThat(userMapper.userToUserDTO(user)).isEqualTo(userDTO);
    }

    @Test
    void userToUserDTO_shouldReturnNullWhenUserIsNull() {
        assertThat(userMapper.userToUserDTO(null)).isNull();
    }
}