package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.User;

import java.util.List;

public interface CartService {

    List<CartDTO> getAllCarts();
    CartDTO getCartById(int cartId);
    CartDTO addCart(int userId);
    void deleteCartById(int cartId);
    CartDTO updateCart(CartDTO cartDTO);

    double getCartTotal(int cartId);

}
