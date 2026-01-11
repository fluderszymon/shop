package com.szymonfluder.shop.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szymonfluder.shop.controller.CartController;
import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.security.RateLimitService;
import com.szymonfluder.shop.security.SecurityConfig;
import com.szymonfluder.shop.security.UserDetailsServiceImpl;
import com.szymonfluder.shop.service.CartService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
public class CartControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        setupJwtMocksWithTokenExtraction(jwtService, userDetailsService);
        setupRateLimitMocks(rateLimitService);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getAllCarts_shouldReturnAllCarts_whenCartsExist() throws Exception {
        List<CartDTO> carts = List.of(new CartDTO(1, 1));
        when(cartService.getAllCarts()).thenReturn(carts);

        mockMvc.perform(get("/carts")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cartId").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(cartService, times(1)).getAllCarts();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getAllCarts_shouldReturnEmptyList_whenNoCartsExist() throws Exception {
        when(cartService.getAllCarts()).thenReturn(List.of());

        mockMvc.perform(get("/carts")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(cartService, times(1)).getAllCarts();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getCartById_shouldReturnCart_whenCartExists() throws Exception {
        CartDTO cartDTO = new CartDTO(1, 1);
        when(cartService.getCartById(1)).thenReturn(cartDTO);

        mockMvc.perform(get("/carts/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        verify(cartService, times(1)).getCartById(1);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getCartItemsInCartByCartId_shouldReturnCartItems_whenCartHasItems() throws Exception {
        List<CartItemDTO> cartItems = List.of(new CartItemDTO(1, 1, 1, 2));
        when(cartService.getAllCartItemsByCartId(1)).thenReturn(cartItems);

        mockMvc.perform(get("/carts/1/items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cartItemId").value(1))
                .andExpect(jsonPath("$[0].cartId").value(1));

        verify(cartService, times(1)).getAllCartItemsByCartId(1);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getCartItemsInCartByCartId_shouldReturnEmptyList_whenCartHasNoItems() throws Exception {
        when(cartService.getAllCartItemsByCartId(1)).thenReturn(List.of());

        mockMvc.perform(get("/carts/1/items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(cartService, times(1)).getAllCartItemsByCartId(1);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getCartItemById_shouldReturnCartItem_whenCartItemExists() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1, 1, 1, 2);
        when(cartService.getCartItemById(1)).thenReturn(cartItemDTO);

        mockMvc.perform(get("/carts/items/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.cartId").value(1));

        verify(cartService, times(1)).getCartItemById(1);
    }

    @Test
    void getMyCart_shouldReturnUserCart_whenUserIsAuthenticated() throws Exception {
        CartDTO cartDTO = new CartDTO(1, 1);
        when(cartService.getCartDTOForCurrentUser()).thenReturn(cartDTO);

        mockMvc.perform(get("/carts/my-cart")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        verify(cartService, times(1)).getCartDTOForCurrentUser();
    }

    @Test
    void getMyCartItems_shouldReturnUserCartItems_whenUserIsAuthenticated() throws Exception {
        List<CartItemDTO> cartItems = List.of(new CartItemDTO(1, 1, 1, 2));
        when(cartService.getCartItemsInCartForCurrentUser()).thenReturn(cartItems);

        mockMvc.perform(get("/carts/my-cart/items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cartItemId").value(1))
                .andExpect(jsonPath("$[0].cartId").value(1));

        verify(cartService, times(1)).getCartItemsInCartForCurrentUser();
    }

    @Test
    void getMyCartItems_shouldReturnEmptyList_whenCartHasNoItems() throws Exception {
        when(cartService.getCartItemsInCartForCurrentUser()).thenReturn(List.of());

        mockMvc.perform(get("/carts/my-cart/items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(cartService, times(1)).getCartItemsInCartForCurrentUser();
    }

    @Test
    void getCartItemByCartItemId_shouldReturnCartItem_whenCartItemExists() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1, 1, 1, 2);
        when(cartService.getCartItemDTOForCurrentUserByCartItemId(1)).thenReturn(cartItemDTO);

        mockMvc.perform(get("/carts/my-cart/items/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.cartId").value(1));

        verify(cartService, times(1)).getCartItemDTOForCurrentUserByCartItemId(1);
    }

    @Test
    void addCartItemToMyCart_shouldReturnCreatedCartItem_whenValidDataProvided() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1, 1, 1, 2);
        when(cartService.addCartItemToCartForCurrentUser(any(CartItemDTO.class))).thenReturn(cartItemDTO);

        mockMvc.perform(post("/carts/my-cart/items")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.cartId").value(1));

        verify(cartService, times(1)).addCartItemToCartForCurrentUser(any(CartItemDTO.class));
    }

    @Test
    void updateCartItemInMyCart_shouldReturnUpdatedCartItem_whenValidDataProvided() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1, 1, 1, 3);
        when(cartService.updateCartItemInCartForCurrentUser(any(CartItemDTO.class))).thenReturn(cartItemDTO);

        mockMvc.perform(put("/carts/my-cart/items")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cartItemId").value(1))
                .andExpect(jsonPath("$.cartId").value(1));

        verify(cartService, times(1)).updateCartItemInCartForCurrentUser(any(CartItemDTO.class));
    }

    @Test
    void deleteCartItemFromMyCart_shouldDeleteCartItem_whenCartItemExists() throws Exception {
        doNothing().when(cartService).deleteCartItemFromCartForCurrentUser(1);

        mockMvc.perform(delete("/carts/my-cart/items/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk());

        verify(cartService, times(1)).deleteCartItemFromCartForCurrentUser(1);
    }

    @Test
    void getMyCartTotal_shouldReturnTotal_whenUserIsAuthenticated() throws Exception {
        when(cartService.getCartTotalForCurrentUser()).thenReturn(BigDecimal.valueOf(99.99));

        mockMvc.perform(get("/carts/my-cart/total")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().string("99.99"));

        verify(cartService, times(1)).getCartTotalForCurrentUser();
    }
}