package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.entity.CartItem;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.CartItemMapper;
import com.szymonfluder.shop.repository.CartItemRepository;
import com.szymonfluder.shop.service.ProductService;
import com.szymonfluder.shop.service.impl.CartItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {

    private static final int CART_ITEM_ID = 1;
    private static final int CART_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final int QUANTITY = 10;

    @Mock private CartItemRepository cartItemRepository;
    @Mock private CartItemMapper cartItemMapper;

    @InjectMocks private CartItemServiceImpl cartItemService;

    @Mock private ProductService productService;
    private CartItem cartItem;
    private CartItemDTO cartItemDTO;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(CART_ID);
        
        product = new Product();
        product.setProductId(PRODUCT_ID);
        product.setName("Test Product");
        product.setPrice(10.0);
        
        cartItem = new CartItem();
        cartItem.setCartItemId(CART_ITEM_ID);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(QUANTITY);

        cartItemDTO = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, QUANTITY);
    }

    @Test
    void getAllCartItems_shouldReturnAllCartItemDTOs() {
        List<CartItem> cartItems = List.of(cartItem);
        List<CartItemDTO> expectedCartItemDTOs = List.of(cartItemDTO);

        when(cartItemRepository.findAll()).thenReturn(cartItems);
        when(cartItemMapper.cartItemToCartItemDTO(cartItem)).thenReturn(cartItemDTO);

        List<CartItemDTO> result = cartItemService.getAllCartItems();

        assertEquals(expectedCartItemDTOs, result);
        verify(cartItemRepository, times(1)).findAll();
        verify(cartItemMapper).cartItemToCartItemDTO(cartItem);
    }

    @Test
    void getAllCartItems_shouldReturnEmptyListWhenNoCartItemsExist() {
        when(cartItemRepository.findAll()).thenReturn(List.of());

        List<CartItemDTO> result = cartItemService.getAllCartItems();

        assertTrue(result.isEmpty());
        verify(cartItemRepository, times(1)).findAll();
        verify(cartItemMapper, never()).cartItemToCartItemDTO(any());
    }

    @Test
    void getAllCartItemsByCartId_shouldReturnAllCartItemDTOsByCartId() {
        List<CartItemDTO> expectedCartItemDTOs = List.of(cartItemDTO);

        when(cartItemRepository.findAllCartItemsByCartId(CART_ID)).thenReturn(expectedCartItemDTOs);

        List<CartItemDTO> result = cartItemService.getAllCartItemsByCartId(CART_ID);

        assertEquals(expectedCartItemDTOs, result);
        verify(cartItemRepository, times(1)).findAllCartItemsByCartId(CART_ID);
    }

    @Test
    void getAllCartItemsByCartId_shouldReturnEmptyListWhenCartHasNoItems() {
        when(cartItemRepository.findAllCartItemsByCartId(CART_ID)).thenReturn(List.of());

        List<CartItemDTO> result = cartItemService.getAllCartItemsByCartId(CART_ID);

        assertTrue(result.isEmpty());
        verify(cartItemRepository, times(1)).findAllCartItemsByCartId(CART_ID);
    }
    
    @Test
    void getCartItemById_shouldReturnCartItemDTO() {
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItem));
        when(cartItemMapper.cartItemToCartItemDTO(cartItem)).thenReturn(cartItemDTO);

        CartItemDTO result = cartItemService.getCartItemById(CART_ITEM_ID);

        assertEquals(cartItemDTO, result);
        verify(cartItemRepository, times(1)).findById(CART_ITEM_ID);
        verify(cartItemMapper, times(1)).cartItemToCartItemDTO(cartItem);
    }

    @Test
    void getCartItemById_shouldThrowExceptionWhenCartItemNotFound() {
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> cartItemService.getCartItemById(CART_ITEM_ID));
        assertEquals("CartItem not found", exception.getMessage());
        verify(cartItemRepository, times(1)).findById(CART_ITEM_ID);
    }


    @Test
    void addCartItem_shouldAddCartItemWhenEnoughStock() {
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(QUANTITY);

        when(productService.isEnough(PRODUCT_ID, QUANTITY)).thenReturn(true);
        when(cartItemMapper.cartItemDTOToCartItem(cartItemDTO)).thenReturn(newCartItem);
        when(cartItemRepository.save(newCartItem)).thenReturn(newCartItem);
        when(cartItemMapper.cartItemToCartItemDTO(newCartItem)).thenReturn(cartItemDTO);

        CartItemDTO result = cartItemService.addCartItem(cartItemDTO);

        assertEquals(cartItemDTO, result);
        verify(productService, times(1)).isEnough(PRODUCT_ID, QUANTITY);
        verify(cartItemMapper, times(1)).cartItemDTOToCartItem(cartItemDTO);
        verify(cartItemRepository, times(1)).save(newCartItem);
        verify(cartItemMapper, times(1)).cartItemToCartItemDTO(newCartItem);
    }

    @Test
    void deleteCartItemById_shouldDeleteCartItem() {
        doNothing().when(cartItemRepository).deleteById(CART_ITEM_ID);

        cartItemService.deleteCartItemById(CART_ITEM_ID);

        verify(cartItemRepository).deleteById(CART_ITEM_ID);
    }

    @Test
    void updateCartItem_shouldUpdateCartItem() {
        CartItem existingCartItem = new CartItem(CART_ITEM_ID, cart, product, QUANTITY);
        CartItem updatedCartItem = new CartItem(CART_ITEM_ID, cart, product, 3);
        CartItemDTO updatedCartItemDTO = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, 3);
        CartItemDTO resultCartItemDTO = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, 3);

        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(existingCartItem));
        when(cartItemMapper.cartItemDTOToCartItem(any(CartItemDTO.class))).thenReturn(updatedCartItem);
        when(cartItemRepository.save(updatedCartItem)).thenReturn(updatedCartItem);
        when(cartItemMapper.cartItemToCartItemDTO(updatedCartItem)).thenReturn(resultCartItemDTO);

        CartItemDTO result = cartItemService.updateCartItem(updatedCartItemDTO);

        assertEquals(resultCartItemDTO, result);
        verify(cartItemRepository).findById(CART_ITEM_ID);
        verify(cartItemMapper).cartItemDTOToCartItem(any(CartItemDTO.class));
        verify(cartItemRepository).save(updatedCartItem);
        verify(cartItemMapper).cartItemToCartItemDTO(updatedCartItem);
    }    
}
