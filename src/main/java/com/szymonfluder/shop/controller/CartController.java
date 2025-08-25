package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
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
    public CartDTO addCart(@PathVariable int userId) {
        return cartService.addCart(userId);
    }

    @DeleteMapping("/{cartId}")
    public void deleteCartById(@PathVariable int cartId) {
        cartService.deleteCartById(cartId);
    }

    @PutMapping
    public CartDTO updateCart(@RequestBody CartDTO cartDTO) {
        return cartService.updateCart(cartDTO);
    }

    @GetMapping("/{cartId}/items")
    public List<CartItemDTO> getCartItemsInCartByCartId(@PathVariable int cartId) {
        return cartService.getAllCartItemsByCartId(cartId);
    }

    @GetMapping("/items/{cartItemId}")
    public CartItemDTO getCartItemById(@PathVariable int cartItemId) {
        return cartService.getCartItemById(cartItemId);
    }

    @PostMapping("/{cartId}/items")
    public CartItemDTO addCartItem(@PathVariable int cartId, @RequestBody CartItemDTO cartItemDTO) {
        return cartService.addCartItem(cartItemDTO);
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public CartItemDTO updateCartItem(@PathVariable int cartId, @PathVariable int itemId, @RequestBody CartItemDTO cartItemDTO) {
        return cartService.updateCartItem(cartItemDTO);
    }

    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public void deleteCartItem(@PathVariable int cartId, @PathVariable int cartItemId) {
        cartService.deleteCartItemById(cartItemId);
    }

    @GetMapping("/{cartId}/total")
    public double getCartTotal(@PathVariable int cartId) {
        return cartService.getCartTotal(cartId);
    }
}
