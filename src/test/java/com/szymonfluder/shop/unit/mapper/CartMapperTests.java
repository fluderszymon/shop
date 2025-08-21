package com.szymonfluder.shop.unit.mapper;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.CartMapper;
import com.szymonfluder.shop.mapper.CartMapperImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CartMapperTests {

    private final int CART_ID = 1;
    private final int USER_ID = 1;

    private final CartMapper cartMapper = new CartMapperImpl();

    @Test
    void cartToCartDTO_shouldMapCartToCartDTO() {
        User user = new User();
        user.setUserId(USER_ID);
        
        Cart cart = new Cart();
        cart.setCartId(CART_ID);
        cart.setUser(user);

        CartDTO result = cartMapper.CartToCartDTO(cart);

        assertThat(result).isNotNull();
        assertThat(result.getCartId()).isEqualTo(CART_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void cartDTOToCart_shouldMapCartDTOToCart() {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(CART_ID);
        cartDTO.setUserId(USER_ID);

        Cart result = cartMapper.CartDTOToCart(cartDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCartId()).isEqualTo(CART_ID);
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void cartToCartDTO_shouldReturnNullWhenCartIsNull() {
        CartDTO result = cartMapper.CartToCartDTO(null);

        assertThat(result).isNull();
    }

    @Test
    void cartDTOToCart_shouldReturnNullWhenCartDTOIsNull() {
        Cart result = cartMapper.CartDTOToCart(null);

        assertThat(result).isNull();
    }
}