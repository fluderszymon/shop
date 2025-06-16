package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.entity.CartItem;
import com.szymonfluder.shop.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;

    @Autowired
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public List<CartItemDTO> getAllCartItems() {
        return cartItemService.getAllCartItems();
    }

    @GetMapping("/cart/{cartId}")
    public List<CartItemDTO> getCartItemsByCartId(@PathVariable int cartId) {
        return cartItemService.getAllCartItemsByCartId(cartId);
    }

    @GetMapping("/{cartItemId}")
    public CartItemDTO getCartItemById(@PathVariable int cartItemId) {
        return cartItemService.getCartItemById(cartItemId);
    }

    @PostMapping
    public CartItem addCartItem(@RequestBody CartItemDTO cartItemDTO) {
        return cartItemService.addCartItem(cartItemDTO);
    }

    @DeleteMapping("/{cartItemId}")
    public void deleteCartItemById(@PathVariable int cartItemId) {
        cartItemService.deleteCartItemById(cartItemId);
    }

    @PutMapping
    public CartItem updateCart(@RequestBody CartItem cartItem) {
        return cartItemService.updateCartItem(cartItem);
    }

}
