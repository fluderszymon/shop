package com.szymonfluder.shop.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szymonfluder.shop.controller.CartController;
import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.security.SecurityConfig;
import com.szymonfluder.shop.security.UserDetailsServiceImpl;
import com.szymonfluder.shop.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
public class CartControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String validToken = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("username")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("USER")))
                .build();

        when(jwtService.extractUsername(validToken)).thenReturn("username");
        when(jwtService.validateToken(validToken, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails);
    }

    @Test
    void getAllCarts_shouldReturnAllCarts() throws Exception {
        List<CartDTO> carts = List.of(new CartDTO(1, 1));
        when(cartService.getAllCarts()).thenReturn(carts);

        mockMvc.perform(get("/carts")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cartId").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(cartService, times(1)).getAllCarts();
    }

    @Test
    void getAllCarts_shouldReturnEmptyList() throws Exception {
        when(cartService.getAllCarts()).thenReturn(List.of());

        mockMvc.perform(get("/carts")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(cartService, times(1)).getAllCarts();
    }

    @Test
    void getCartById_shouldReturnCart() throws Exception {
        CartDTO cartDTO = new CartDTO(1, 1);
        when(cartService.getCartById(1)).thenReturn(cartDTO);

        mockMvc.perform(get("/carts/1")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        verify(cartService, times(1)).getCartById(1);
    }

    @Test
    void addCart_shouldReturnCreatedCart() throws Exception {
        CartDTO cartDTO = new CartDTO(1, 1);
        when(cartService.addCart(1)).thenReturn(cartDTO);

        mockMvc.perform(post("/carts/1")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        verify(cartService, times(1)).addCart(1);
    }

    @Test
    void deleteCartById_shouldDeleteCart() throws Exception {
        doNothing().when(cartService).deleteCartById(1);

        mockMvc.perform(delete("/carts/1")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());

        verify(cartService, times(1)).deleteCartById(1);
    }

    @Test
    void updateCart_shouldReturnUpdatedCart() throws Exception {
        CartDTO cartDTO = new CartDTO(1, 2);
        when(cartService.updateCart(any(CartDTO.class))).thenReturn(cartDTO);

        mockMvc.perform(put("/carts")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.userId").value(2));

        verify(cartService, times(1)).updateCart(any(CartDTO.class));
    }

    @Test
    void getCartItemsInCartByCartId_shouldReturnCartItems() throws Exception {
        List<CartItemDTO> cartItems = List.of(new CartItemDTO(1, 1, 1, 2));
        when(cartService.getAllCartItemsByCartId(1)).thenReturn(cartItems);

        mockMvc.perform(get("/carts/1/items")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cartItemId").value(1))
                .andExpect(jsonPath("$[0].cartId").value(1));

        verify(cartService, times(1)).getAllCartItemsByCartId(1);
    }

    @Test
    void getCartItemsInCartByCartId_shouldReturnEmptyList() throws Exception {
        when(cartService.getAllCartItemsByCartId(1)).thenReturn(List.of());

        mockMvc.perform(get("/carts/1/items")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(cartService, times(1)).getAllCartItemsByCartId(1);
    }

    @Test
    void getCartItemById_shouldReturnCartItem() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1, 1, 1, 2);
        when(cartService.getCartItemById(1)).thenReturn(cartItemDTO);

        mockMvc.perform(get("/carts/items/1")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.cartId").value(1));

        verify(cartService, times(1)).getCartItemById(1);
    }

    @Test
    void addCartItem_shouldReturnCreatedCartItem() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1, 1, 1, 2);
        when(cartService.addCartItem(any(CartItemDTO.class))).thenReturn(cartItemDTO);

        mockMvc.perform(post("/carts/1/items")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.cartId").value(1));

        verify(cartService, times(1)).addCartItem(any(CartItemDTO.class));
    }

    @Test
    void updateCartItem_shouldReturnUpdatedCartItem() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1, 1, 1, 3);
        when(cartService.updateCartItem(any(CartItemDTO.class))).thenReturn(cartItemDTO);

        mockMvc.perform(put("/carts/1/items/1")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.cartId").value(1));

        verify(cartService, times(1)).updateCartItem(any(CartItemDTO.class));
    }

    @Test
    void deleteCartItem_shouldDeleteCartItem() throws Exception {
        doNothing().when(cartService).deleteCartItemById(1);

        mockMvc.perform(delete("/carts/1/items/1")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());

        verify(cartService, times(1)).deleteCartItemById(1);
    }

    @Test
    void getCartTotal_shouldReturnTotal() throws Exception {
        when(cartService.getCartTotal(1)).thenReturn(59.97);

        mockMvc.perform(get("/carts/1/total")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("59.97"));

        verify(cartService, times(1)).getCartTotal(1);
    }
}