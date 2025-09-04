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

    // "/carts" endpoints for admin
    
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteCartById(@PathVariable int cartId) {
        cartService.deleteCartById(cartId);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public CartDTO updateCart(@RequestBody CartDTO cartDTO) {
        return cartService.updateCart(cartDTO);
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

    @PostMapping("/{cartId}/items")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CartItemDTO addCartItem(@PathVariable int cartId, @RequestBody CartItemDTO cartItemDTO) {
        return cartService.addCartItem(cartItemDTO);
    }

    @PutMapping("/{cartId}/items/{cartItemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CartItemDTO updateCartItem(@PathVariable int cartId, @PathVariable int cartItemId, @RequestBody CartItemDTO cartItemDTO) {
        return cartService.updateCartItem(cartItemDTO);
    }

    @DeleteMapping("/{cartId}/items/{cartItemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteCartItem(@PathVariable int cartId, @PathVariable int cartItemId) {
        cartService.deleteCartItemById(cartItemId);
    }

    @GetMapping("/{cartId}/total")
    @PreAuthorize("hasAuthority('ADMIN')")
    public double getCartTotal(@PathVariable int cartId) {
        return cartService.getCartTotal(cartId);
    }

    // "/my-cart" endpoints which verify that the user changes only his own cart

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
    public CartItemDTO addCartItemToMyCart(@RequestBody CartItemDTO cartItemDTO) {
        return cartService.addCartItemToCartForCurrentUser(cartItemDTO);
    }

    @PutMapping("/my-cart/items")
    @PreAuthorize("hasAuthority('USER')")
    public CartItemDTO updateCartItemInMyCart(@RequestBody CartItemDTO cartItemDTO) {
        return cartService.updateCartItemInCartForCurrentUser(cartItemDTO);
    }

    @DeleteMapping("/my-cart/items/{cartItemId}")
    @PreAuthorize("hasAuthority('USER')")
    public void deleteCartItemFromMyCart(@PathVariable int cartItemId) {
        cartService.deleteCartItemFromCartForCurrentUser(cartItemId);
    }

    @GetMapping("/my-cart/total")
    @PreAuthorize("hasAuthority('USER')")
    public double getMyCartTotal() {
        return cartService.getCartTotalForCurrentUser();
    }
}