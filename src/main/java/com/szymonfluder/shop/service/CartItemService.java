package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.entity.CartItem;

import java.util.List;

public interface CartItemService {

    List<CartItemDTO> getAllCartItems();
    CartItemDTO getCartItemById(int cartItemId);
    CartItem addCartItem(CartItemDTO cartItemDTO);
    void deleteCartItemById(int cartItemId);
    CartItem updateCartItem(CartItem cartItem);
}
