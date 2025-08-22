package com.szymonfluder.shop.unit.mapper;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.CartItem;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.CartItemMapper;
import com.szymonfluder.shop.mapper.CartItemMapperImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CartItemMapperTests {

    private final int CART_ITEM_ID = 1;
    private final int CART_ID = 1;
    private final int PRODUCT_ID = 1;
    private final int QUANTITY = 10;

    private final CartItemMapper cartItemMapper = new CartItemMapperImpl();

    private CartItem createCartItem() {
        Cart cart = new Cart();
        cart.setCartId(CART_ID);

        Product product = new Product();
        product.setProductId(PRODUCT_ID);

        return new CartItem(CART_ITEM_ID, cart, product, QUANTITY);
    }

    private CartItemDTO createCartItemDTO() {
        return new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, QUANTITY);
    }

    @Test
    void cartItemToCartItemDTO_shouldMapCartItemToCartItemDTO() {
        CartItem givenCartItem = createCartItem();
        CartItemDTO expectedCartItemDTO = createCartItemDTO();
        CartItemDTO mappedCartItemDTO = cartItemMapper.cartItemToCartItemDTO(givenCartItem);

        assertThat(mappedCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void cartItemDTOToCartItem_shouldMapCartItemDTOToCartItem() {
        CartItemDTO givenCartItemDTO = createCartItemDTO();
        CartItem expectedCartItem = createCartItem();
        CartItem mappedCartItem = cartItemMapper.cartItemDTOToCartItem(givenCartItemDTO);

        assertThat(mappedCartItem).isEqualTo(expectedCartItem);
    }
}