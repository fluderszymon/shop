package com.szymonfluder.shop.unit.mapper;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.CartMapper;
import com.szymonfluder.shop.mapper.CartMapperImpl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CartMapperTests {

    private final int CART_ID = 1;
    private final int USER_ID = 1;

    private final CartMapper cartMapper = new CartMapperImpl();

    private Cart createCart() {
        User user = new User();
        user.setUserId(USER_ID);

        return new Cart(CART_ID, user, null);
    };

    private CartDTO createCartDTO() {
        return new CartDTO(CART_ID, USER_ID);
    }

    @Test
    void cartToCartDTO_shouldMapCartToCartDTO() {
        Cart givenCart = createCart();
        CartDTO expectedCartDTO = createCartDTO();
        CartDTO mappedCartDTO = cartMapper.CartToCartDTO(givenCart);

        assertThat(mappedCartDTO).isEqualTo(expectedCartDTO);
    }

    @Test
    void cartDTOToCart_shouldMapCartDTOToCart() {
        CartDTO givenCartDTO = createCartDTO();
        Cart expectedCart = createCart();
        Cart mappedCart = cartMapper.CartDTOToCart(givenCartDTO);

        assertThat(mappedCart).isEqualTo(expectedCart);
    }
}