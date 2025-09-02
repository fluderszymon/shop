package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

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
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CartDTO> getAllCarts() {
        return cartService.getAllCarts();
    }

    @GetMapping("/{cartId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CartDTO getCartById(@PathVariable int cartId) {
        return cartService.getCartById(cartId);
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CartDTO addCart(@PathVariable int userId) {
        return cartService.addCart(userId);
    }

    @DeleteMapping("/{cartId}")
    @PreAuthorize("@resourceAccessService.isOwnerOrAdmin(#cartId)")
    public void deleteCartById(@PathVariable int cartId) {
        cartService.deleteCartById(cartId);
    }

    @PutMapping
    @PreAuthorize("@resourceAccessService.isOwnerOrAdmin(#cartDTO.cartId)")
    public CartDTO updateCart(@RequestBody CartDTO cartDTO) {
        return cartService.updateCart(cartDTO);
    }

    @GetMapping("/{cartId}/items")
    @PreAuthorize("@resourceAccessService.isOwnerOrAdmin(#cartId)")
    public List<CartItemDTO> getCartItemsInCartByCartId(@PathVariable int cartId) {
        return cartService.getAllCartItemsByCartId(cartId);
    }

    @GetMapping("/items/{cartItemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CartItemDTO getCartItemById(@PathVariable int cartItemId) {
        return cartService.getCartItemById(cartItemId);
    }

    @PostMapping("/{cartId}/items")
    @PreAuthorize("@resourceAccessService.isOwnerOrAdmin(#cartId)")
    public CartItemDTO addCartItem(@PathVariable int cartId, @RequestBody CartItemDTO cartItemDTO) {
        return cartService.addCartItem(cartItemDTO);
    }

    @PutMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("@resourceAccessService.isOwnerOrAdmin(#cartId)")
    public CartItemDTO updateCartItem(@PathVariable int cartId, @PathVariable int itemId, @RequestBody CartItemDTO cartItemDTO) {
        return cartService.updateCartItem(cartItemDTO);
    }

    @DeleteMapping("/{cartId}/items/{cartItemId}")
    @PreAuthorize("@resourceAccessService.isOwnerOrAdmin(#cartId)")
    public void deleteCartItem(@PathVariable int cartId, @PathVariable int cartItemId) {
        cartService.deleteCartItemById(cartItemId);
    }

    @GetMapping("/{cartId}/total")
    @PreAuthorize("hasAuthority('ADMIN')")
    public double getCartTotal(@PathVariable int cartId) {
        return cartService.getCartTotal(cartId);
    }
}
