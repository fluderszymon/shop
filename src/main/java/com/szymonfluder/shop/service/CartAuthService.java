package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.CartItemDTO;

public interface CartAuthService {
    
    void validateCartItemOwnership(int cartItemId);
    void validateCartItemOwnership(CartItemDTO cartItemDTO);
    
}