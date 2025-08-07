package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.CartDTO;

import java.util.List;

public interface CartService {

    List<CartDTO> getAllCarts();
    CartDTO getCartById(int cartId);
    CartDTO addCart(int userId);
    void deleteCartById(int cartId);
    CartDTO updateCart(CartDTO cartDTO);

    double getCartTotal(int cartId);

}
