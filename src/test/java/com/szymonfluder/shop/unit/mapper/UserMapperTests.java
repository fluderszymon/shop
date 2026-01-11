package com.szymonfluder.shop.unit.mapper;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.mapper.UserMapperImpl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UserMapperTests {

    private final String USERNAME = "username";
    private final String EMAIL = "user@outlook.com";
    private final String PASSWORD = "password";
    private final String ADDRESS = "address";

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void userRegisterDTOToUser_shouldMapUserRegisterDTOtoUser_whenValidDataProvided() {
        UserRegisterDTO givenUserRegisterDTO = new UserRegisterDTO(USERNAME, EMAIL, PASSWORD, ADDRESS);
        User expectedUser = new User(0, USERNAME, EMAIL, PASSWORD, null, null, ADDRESS, BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP));
        User mappedUser = userMapper.userRegisterDTOToUser(givenUserRegisterDTO);

        assertThat(mappedUser).isEqualTo(expectedUser);
    }

    @Test
    void userToUserDTO_shouldMapUserToUserDTO_whenValidDataProvided() {
        User givenUser = new User(1, USERNAME, EMAIL, PASSWORD, "USER", null, ADDRESS, BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP));
        Cart givenCart = new Cart(1, givenUser, null);
        givenUser.setCart(givenCart);
        UserDTO expectedUserDTO = new UserDTO(1, USERNAME, EMAIL, "USER", 1, ADDRESS, BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP));
        UserDTO mappedUserDTO = userMapper.userToUserDTO(givenUser);

        assertThat(mappedUserDTO).isEqualTo(expectedUserDTO);
    }
}