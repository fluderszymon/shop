package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartDTO> getAllCarts() {
        return cartService.getAllCarts();
    }

    @GetMapping("/{cartId}")
    public CartDTO getCartById(@PathVariable int cartId) {
        return cartService.getCartById(cartId);
    }

    @PostMapping("/{userId}")
    public Cart addCart(@PathVariable int userId) {
        return cartService.addCart(userId);
    }

    @DeleteMapping("/{cartId}")
    public void deleteCartById(@PathVariable int cartId) {
        cartService.deleteCartById(cartId);
    }

    @PutMapping
    public Cart updateCart(@RequestBody CartDTO cartDTO) {
        return cartService.updateCart(cartDTO);
    }
}
