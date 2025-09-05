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

    List<CartItemDTO> getAllCartItems();
    List<CartItemDTO> getAllCartItemsByCartId(int cartId);
    CartItemDTO getCartItemById(int cartItemId);
    CartItemDTO addCartItem(CartItemDTO cartItemDTO);
    void deleteCartItemById(int cartItemId);
    CartItemDTO updateCartItem(CartItemDTO cartItemDTO);

    double getCartTotal(int cartId);

    // methods for "/my-cart" endpoints
    CartDTO getCartDTOForCurrentUser();
    CartItemDTO getCartItemDTOForCurrentUserByCartItemId(int cartItemId);
    List<CartItemDTO> getCartItemsInCartForCurrentUser();
    CartItemDTO addCartItemToCartForCurrentUser(CartItemDTO cartItemDTO);
    void deleteCartItemFromCartForCurrentUser(int cartItemId);
    CartItemDTO updateCartItemInCartForCurrentUser(CartItemDTO cartItemDTO);

    double getCartTotalForCurrentUser();
}