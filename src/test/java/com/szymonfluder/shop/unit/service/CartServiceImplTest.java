package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.CartMapper;
import com.szymonfluder.shop.repository.CartRepository;
import com.szymonfluder.shop.service.CartItemService;
import com.szymonfluder.shop.service.ProductService;
import com.szymonfluder.shop.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    private static final int CART_ID = 1;
    private static final int USER_ID = 1;

    @Mock private CartRepository cartRepository;
    @Mock private CartMapper cartMapper;
    @Mock private CartItemService cartItemService;
    @Mock private ProductService productService;

    @InjectMocks private CartServiceImpl cartService;

    private Cart cart;
    private CartDTO cartDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(USER_ID);
        
        cart = new Cart();
        cart.setCartId(CART_ID);
        cart.setUser(user);

        cartDTO = new CartDTO(CART_ID, USER_ID);
    }

    @Test
        void getAllCarts_shouldReturnAllCartDTOs() {
        List<Cart> carts = List.of(cart);
        List<CartDTO> expectedCartDTOs = List.of(cartDTO);

        when(cartRepository.findAll()).thenReturn(carts);
        when(cartMapper.CartToCartDTO(cart)).thenReturn(cartDTO);

        List<CartDTO> result = cartService.getAllCarts();

        assertEquals(expectedCartDTOs, result);
        verify(cartRepository).findAll();
        verify(cartMapper).CartToCartDTO(cart);
    }

    @Test
    void getAllCarts_shouldReturnEmptyListWhenNoCartsExist() {
        when(cartRepository.findAll()).thenReturn(List.of());

        List<CartDTO> result = cartService.getAllCarts();

        assertTrue(result.isEmpty());
        verify(cartRepository).findAll();
        verify(cartMapper, never()).CartToCartDTO(any());
    }

    @Test
    void getCartById_shouldReturnCartDTO() {
        when(cartRepository.findById(CART_ID)).thenReturn(Optional.of(cart));
        when(cartMapper.CartToCartDTO(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.getCartById(CART_ID);

        assertEquals(cartDTO, result);
        verify(cartRepository).findById(CART_ID);
        verify(cartMapper).CartToCartDTO(cart);
    }

    @Test
    void getCartById_shouldThrowExceptionWhenCartNotFound() {
        when(cartRepository.findById(CART_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> cartService.getCartById(CART_ID));
        assertEquals("Cart not found", exception.getMessage());
        verify(cartRepository).findById(CART_ID);
    }

    @Test
    void addCart_shouldAddCart() {
        CartDTO cartDTOToSave = new CartDTO(0, USER_ID);
        User user = new User();
        user.setUserId(USER_ID);
        Cart cartToSave = new Cart(0, user, null);
        CartDTO expectedCartDTO = new CartDTO(1, USER_ID);

        when(cartMapper.CartDTOToCart(cartDTOToSave)).thenReturn(cartToSave);
        when(cartRepository.save(cartToSave)).thenReturn(cartToSave);
        when(cartMapper.CartToCartDTO(cartToSave)).thenReturn(expectedCartDTO);

        CartDTO result = cartService.addCart(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        verify(cartRepository).save(cartToSave);
        verify(cartMapper).CartDTOToCart(cartDTOToSave);
        verify(cartMapper).CartToCartDTO(cartToSave);
    }

    @Test
    void deleteCartById_shouldDeleteCart() {
        doNothing().when(cartRepository).deleteById(CART_ID);

        cartService.deleteCartById(CART_ID);

        verify(cartRepository).deleteById(CART_ID);
    }

    @Test
    void updateCart_shouldUpdateCart() { 
        CartDTO updatedCartDTO = new CartDTO(CART_ID, USER_ID);
        Cart existingCart = new Cart(CART_ID, user, null);
        Cart savedCart = new Cart(CART_ID, user, null);
        CartDTO resultCartDTO = new CartDTO(CART_ID, USER_ID);

        when(cartRepository.findById(CART_ID)).thenReturn(Optional.of(existingCart));
        when(cartMapper.CartDTOToCart(any(CartDTO.class))).thenReturn(savedCart);
        when(cartRepository.save(savedCart)).thenReturn(savedCart);
        when(cartMapper.CartToCartDTO(savedCart)).thenReturn(resultCartDTO);

        CartDTO result = cartService.updateCart(updatedCartDTO);

        assertEquals(resultCartDTO, result);
        verify(cartRepository).findById(CART_ID);
        verify(cartMapper).CartDTOToCart(any(CartDTO.class));
        verify(cartRepository).save(savedCart);
        verify(cartMapper).CartToCartDTO(savedCart);
    }

    @Test
    void getCartTotal_shouldReturnCartTotal() {
        CartItemDTO cartItem1 = new CartItemDTO(1, CART_ID, 1, 2);
        CartItemDTO cartItem2 = new CartItemDTO(2, CART_ID, 2, 1);
        ProductDTO product1 = new ProductDTO(1, "Product 1", "Description 1", 10.0, 10);
        ProductDTO product2 = new ProductDTO(2, "Product 2", "Description 2", 15.0, 10);
        
        when(cartItemService.getAllCartItemsByCartId(CART_ID)).thenReturn(new ArrayList<>(List.of(cartItem1, cartItem2)));
        when(productService.getProductById(1)).thenReturn(product1);
        when(productService.getProductById(2)).thenReturn(product2);

        double result = cartService.getCartTotal(CART_ID);

        assertEquals(35.0, result);
        verify(cartItemService).getAllCartItemsByCartId(CART_ID);
        verify(productService).getProductById(1);
        verify(productService).getProductById(2);
    }
}