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
    Cart addCart(int userId);
    void deleteCartById(int cartId);
    Cart updateCart(CartDTO cartDTO);

}
