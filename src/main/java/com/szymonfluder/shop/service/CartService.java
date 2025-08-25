package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;

import java.util.List;

public interface CartService {

    List<CartDTO> getAllCarts();
    CartDTO getCartById(int cartId);
    CartDTO addCart(int userId);
    void deleteCartById(int cartId);
    CartDTO updateCart(CartDTO cartDTO);

    double getCartTotal(int cartId);

    List<CartItemDTO> getAllCartItems();
    List<CartItemDTO> getAllCartItemsByCartId(int cartId);
    CartItemDTO getCartItemById(int cartItemId);
    CartItemDTO addCartItem(CartItemDTO cartItemDTO);
    void deleteCartItemById(int cartItemId);
    CartItemDTO updateCartItem(CartItemDTO cartItemDTO);

}
