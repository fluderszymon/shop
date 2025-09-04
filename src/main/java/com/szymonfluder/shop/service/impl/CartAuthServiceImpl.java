package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.entity.CartItem;
import com.szymonfluder.shop.repository.CartItemRepository;
import com.szymonfluder.shop.repository.CartRepository;
import com.szymonfluder.shop.service.CartAuthService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class CartAuthServiceImpl implements CartAuthService {

    private final UserService userService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartAuthServiceImpl(UserService userService, CartRepository cartRepository, 
                              CartItemRepository cartItemRepository) {
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void validateCartItemOwnership(int cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        
        validateCartOwnership(cartItem.getCart().getCartId());
    }

    @Override
    public void validateCartItemOwnership(CartItemDTO cartItemDTO) {
        validateCartOwnership(cartItemDTO.getCartId());
    }
    
    private void validateCartOwnership(int cartId) {
        UserDTO currentUser = userService.getCurrentUserDTO();
        boolean isOwner = cartRepository.findCartDTOByUserId(currentUser.getUserId())
                .map(cart -> cart.getCartId() == cartId)
                .orElse(false);
                
        if (!isOwner) {
            throw new AccessDeniedException("You are not allowed to access this cart item");
        }
    }
}