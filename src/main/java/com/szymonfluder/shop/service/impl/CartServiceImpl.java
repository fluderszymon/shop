package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.mapper.CartMapper;
import com.szymonfluder.shop.repository.CartRepository;
import com.szymonfluder.shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
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
        Cart foundCart = cartRepository.findById(cartId ).orElse(null);
        return cartMapper.CartToCartDTO(foundCart);
    }

    @Override
    public CartDTO addCart(int userId) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId(userId);
        Cart savedCart = cartRepository.save(cartMapper.CartDTOToCart(cartDTO));
        return cartMapper.CartToCartDTO(savedCart);
    }

    @Override
    public void deleteCartById(int cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public CartDTO updateCart(CartDTO cartDTO) {
        Optional<Cart> tempCart = cartRepository.findById(cartDTO.getCartId());
        CartDTO updatedCartDTO = new CartDTO();
        if(tempCart.isPresent()) {
            updatedCartDTO.setCartId(cartDTO.getCartId());
            updatedCartDTO.setUserId(cartDTO.getUserId());
        }
        Cart updatedCart = cartRepository.save(cartMapper.CartDTOToCart(updatedCartDTO));
        return cartMapper.CartToCartDTO(updatedCart);
    }
}
