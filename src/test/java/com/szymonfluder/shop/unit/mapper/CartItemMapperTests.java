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

    @Test
    void cartItemToCartItemDTO_shouldMapCartItemToCartItemDTO() {
        Cart cart = new Cart();
        cart.setCartId(CART_ID);
        
        Product product = new Product();
        product.setProductId(PRODUCT_ID);
        
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(CART_ITEM_ID);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(QUANTITY);

        CartItemDTO result = cartItemMapper.cartItemToCartItemDTO(cartItem);

        assertThat(result).isNotNull();
        assertThat(result.getCartItemId()).isEqualTo(CART_ITEM_ID);
        assertThat(result.getCartId()).isEqualTo(CART_ID);
        assertThat(result.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(result.getQuantity()).isEqualTo(QUANTITY);
    }

    @Test
    void cartItemDTOToCartItem_shouldMapCartItemDTOToCartItem() {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setCartItemId(CART_ITEM_ID);
        cartItemDTO.setCartId(CART_ID);
        cartItemDTO.setProductId(PRODUCT_ID);
        cartItemDTO.setQuantity(QUANTITY);

        CartItem result = cartItemMapper.cartItemDTOToCartItem(cartItemDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCartItemId()).isEqualTo(CART_ITEM_ID);
        assertThat(result.getCart()).isNotNull();
        assertThat(result.getCart().getCartId()).isEqualTo(CART_ID);
        assertThat(result.getProduct()).isNotNull();
        assertThat(result.getProduct().getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(result.getQuantity()).isEqualTo(QUANTITY);
    }

    @Test
    void cartItemToCartItemDTO_shouldReturnNullWhenCartItemIsNull() {
        CartItemDTO result = cartItemMapper.cartItemToCartItemDTO(null);

        assertThat(result).isNull();
    }

    @Test
    void cartItemDTOToCartItem_shouldReturnNullWhenCartItemDTOIsNull() {
        CartItem result = cartItemMapper.cartItemDTOToCartItem(null);

        assertThat(result).isNull();
    }
}