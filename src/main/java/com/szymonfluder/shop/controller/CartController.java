package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.service.CartService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
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

    @GetMapping("/{cartId}/items")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CartItemDTO> getCartItemsInCartByCartId(@PathVariable int cartId) {
        return cartService.getAllCartItemsByCartId(cartId);
    }

    @GetMapping("/items/{cartItemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CartItemDTO getCartItemById(@PathVariable int cartItemId) {
        return cartService.getCartItemById(cartItemId);
    }

    @GetMapping("/my-cart")
    @PreAuthorize("hasAuthority('USER')")
    public CartDTO getMyCart() {
        return cartService.getCartDTOForCurrentUser();
    }

    @GetMapping("/my-cart/items/{cartItemId}")
    @PreAuthorize("hasAuthority('USER')")
    public CartItemDTO getCartItemByCartItemId(@PathVariable int cartItemId) {
        return cartService.getCartItemDTOForCurrentUserByCartItemId(cartItemId);
    }

    @GetMapping("/my-cart/items")
    @PreAuthorize("hasAuthority('USER')")
    public List<CartItemDTO> getMyCartItems() {
        return cartService.getCartItemsInCartForCurrentUser();
    }

    @PostMapping("/my-cart/items")
    @PreAuthorize("hasAuthority('USER')")
    public CartItemDTO addCartItemToMyCart(@Valid @RequestBody CartItemDTO cartItemDTO) {
        return cartService.addCartItemToCartForCurrentUser(cartItemDTO);
    }

    @PutMapping("/my-cart/items")
    @PreAuthorize("hasAuthority('USER')")
    public CartItemDTO updateCartItemInMyCart(@Valid @RequestBody CartItemDTO cartItemDTO) {
        return cartService.updateCartItemInCartForCurrentUser(cartItemDTO);
    }

    @DeleteMapping("/my-cart/items/{cartItemId}")
    @PreAuthorize("hasAuthority('USER')")
    public void deleteCartItemFromMyCart(@PathVariable int cartItemId) {
        cartService.deleteCartItemFromCartForCurrentUser(cartItemId);
    }

    @GetMapping("/my-cart/total")
    @PreAuthorize("hasAuthority('USER')")
    public BigDecimal getMyCartTotal() {
        return cartService.getCartTotalForCurrentUser();
    }
}