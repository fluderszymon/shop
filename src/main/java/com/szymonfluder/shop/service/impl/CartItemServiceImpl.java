package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.entity.CartItem;
import com.szymonfluder.shop.mapper.CartItemMapper;
import com.szymonfluder.shop.repository.CartItemRepository;
import com.szymonfluder.shop.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;


    @Autowired
    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    public List<CartItemDTO> getAllCartItems() {
        return cartItemRepository.findAll()
                .stream()
                .map(cartItemMapper::cartItemToCartItemDTO)
                .collect(Collectors.toList());
    }

    public List<CartItemDTO> getAllCartItemsByCartId(int cartId) {
        return cartItemRepository.findAllCartItemsByCartItemId(cartId);
    }

    @Override
    public CartItemDTO getCartItemById(int cartItemId) {
        CartItem foundCartItem = cartItemRepository.findById(cartItemId).orElse(new CartItem());
        return cartItemMapper.cartItemToCartItemDTO(foundCartItem);
    }

    @Override
    public CartItem addCartItem(CartItemDTO cartItemDTO) {
        return cartItemRepository.save(cartItemMapper.cartItemDTOToCartItem(cartItemDTO));
    }

    @Override
    public void deleteCartItemById(int cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public CartItem updateCartItem(CartItem cartItem) {
        Optional<CartItem> tempCartItem = cartItemRepository.findById(cartItem.getCartItemId());
        CartItemDTO updatedCartItemDTO = new CartItemDTO();
        if(tempCartItem.isPresent()) {
            updatedCartItemDTO.setCartItemId(tempCartItem.get().getCartItemId());
            updatedCartItemDTO.setQuantity(tempCartItem.get().getQuantity());
            updatedCartItemDTO.setCartId(updatedCartItemDTO.getCartId());
            updatedCartItemDTO.setProductId(updatedCartItemDTO.getProductId());
        }
        return cartItemRepository.save(cartItemMapper.cartItemDTOToCartItem(updatedCartItemDTO));
    }
}
