package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.mapper.CartMapper;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.repository.CartRepository;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper, UserService userService, UserMapper userMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        return cartRepository.findAll()
                .stream()
                .map(cartMapper::CartToCartDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO getCartById(int cartId) {
        Cart foundCart = cartRepository.findById(cartId).orElse(new Cart());
        return cartMapper.CartToCartDTO(foundCart);
    }

    @Override
    public Cart addCart(int userId) {
//        CartDTO cartDTO = new CartDTO();
//        cartDTO.setUserId(userId);
//        return cartRepository.save(cartMapper.CartDTOToCart(cartDTO));
        Cart cart = new Cart();
//        cart.setUser(userMapper.userDTOToUser(userService.getUserByUsername(userId)));
        return cartRepository.save(cart);
    }

    @Override
    public void deleteCartById(int cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public Cart updateCart(CartDTO cartDTO) {
        Optional<Cart> tempCart = cartRepository.findById(cartDTO.getCartId());
        CartDTO updatedCartDTO = new CartDTO();
        if(tempCart.isPresent()) {
            updatedCartDTO.setCartId(cartDTO.getCartId());
            updatedCartDTO.setUserId(cartDTO.getUserId());
        }
        return cartRepository.save(cartMapper.CartDTOToCart(updatedCartDTO));
    }
}
