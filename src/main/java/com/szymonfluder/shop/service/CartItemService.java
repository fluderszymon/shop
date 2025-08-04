package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.CartItemDTO;

import java.util.List;

public interface CartItemService {

    List<CartItemDTO> getAllCartItems();
    List<CartItemDTO> getAllCartItemsByCartId(int cartId);
    CartItemDTO getCartItemById(int cartItemId);
    CartItemDTO addCartItem(CartItemDTO cartItemDTO);
    void deleteCartItemById(int cartItemId);
    CartItemDTO updateCartItem(CartItemDTO cartItemDTO);
}
