package com.szymonfluder.shop.security;

import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.repository.CartRepository;
import com.szymonfluder.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("resourceAccessService")
public class ResourceAccessService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Autowired
    public ResourceAccessService(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    public boolean isOwnerOrAdmin(int cartId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String username = auth.getName();
        User user = userRepository.findUserByUsername(username);

        if ("ADMIN".equals(user.getRole())) {
            return true;
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return cart.getUser().getUserId() == user.getUserId();
    }
}
