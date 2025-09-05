package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.*;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.*;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.service.impl.CartServiceImpl;
import com.szymonfluder.shop.service.impl.ProductServiceImpl;
import com.szymonfluder.shop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import org.springframework.security.access.AccessDeniedException;

@DataJpaTest
@Import({CartServiceImpl.class, CartMapperImpl.class,
        CartItemMapperImpl.class, ProductServiceImpl.class, ProductMapperImpl.class,
        UserServiceImpl.class, UserMapperImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CartServiceImplTests {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JWTService jwtService;

    private CartDTO addCartToDatabase() {
        userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        return cartService.getCartById(1);
    }

    private CartDTO getCartMock() {
        return new CartDTO(1, 1);
    }

    @Test
    void getAllCarts_shouldReturnAllCartDTOs() {
        addCartToDatabase();
        List<CartDTO> actualProductDTOList = cartService.getAllCarts();
        List<CartDTO> expectedProductDTOList = List.of(getCartMock());

        assertThat(actualProductDTOList).isEqualTo(expectedProductDTOList);
    }

    @Test
    void getAllCarts_shouldReturnReturnEmptyList() {
        List<CartDTO> actualProductDTOList = cartService.getAllCarts();
        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getCartById_shouldReturnCartDTO() {
        addCartToDatabase();
        CartDTO actualCart = cartService.getCartById(1);
        CartDTO expectedCart = getCartMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void getCartById_shouldThrowExceptionWhenCartNotFound() {
        int cartId = 1;

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartService.getCartById(cartId));

        assertThat(exception.getMessage()).isEqualTo("Cart not found");
    }

    @Test
    void addCart_shouldAddCart() {
        addCartToDatabase();
        CartDTO actualCart = cartService.getCartById(1);
        CartDTO expectedCart = getCartMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void deleteCartById_shouldDeleteCart() {
        CartDTO addedCartDTO = addCartToDatabase();
        int cartId = addedCartDTO.getCartId();
        assertThat(cartService.getCartById(cartId)).isNotNull();

        cartService.deleteCartById(cartId);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartService.getCartById(cartId));
        assertThat(exception.getMessage()).isEqualTo("Cart not found");
    }

    @Test
    void updateCart_shouldUpdateCart() {
        CartDTO addedCartDTO = addCartToDatabase();
        int cartId = addedCartDTO.getCartId();

        CartDTO cartDTOPassedToUpdateMethod = new CartDTO(cartId, 2);

        CartDTO updatedCartDTO = cartService.updateCart(cartDTOPassedToUpdateMethod);

        assertThat(updatedCartDTO).isEqualTo(cartDTOPassedToUpdateMethod);
    }

    @Test
    void getCartTotal_shouldReturnCartTotal() {
        addCartItemToDatabase();
        int cartId = 1;

        double cartTotal = cartService.getCartTotal(cartId);
        assertThat(cartTotal).isEqualTo(100.0);
    }

    private CartItemDTO addCartItemToDatabase() {
        userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        Product product = productService.addProduct(new ProductCreateDTO("Product", "Product Description", 10.00, 100));
        return cartService.addCartItem(new CartItemDTO(0, 1, product.getProductId(), 10));
    }

    private CartItemDTO getCartItemDTOMock() {
        return new CartItemDTO(1, 1, 1, 10);
    }

    @Test
    void getAllCartItems_shouldReturnAllCartItemDTOs() {
        addCartItemToDatabase();
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItems();
        List<CartItemDTO> expectedCartItemDTOList = List.of(getCartItemDTOMock());

        assertThat(actualCartItemDTOList).isEqualTo(expectedCartItemDTOList);
    }

    @Test
    void getAllCartItems_shouldReturnEmptyList() {
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItems();
        assertThat(actualCartItemDTOList.isEmpty()).isTrue();
    }

    @Test
    void getAllCartItemsByCartId_shouldReturnAllCartItemDTOsByCartId() {
        CartItemDTO addedCartITemDTO = addCartItemToDatabase();
        int cartId = addedCartITemDTO.getCartId();
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItemsByCartId(cartId);
        List<CartItemDTO> expectedCartItemDTOList = List.of(getCartItemDTOMock());

        assertThat(actualCartItemDTOList).isEqualTo(expectedCartItemDTOList);
    }

    @Test
    void getAllCartItemsByCartId_shouldReturnEmptyListWhenCartHasNoItems() {
        addCartItemToDatabase();
        int emptyCartId = 99;
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItemsByCartId(emptyCartId);

        assertThat(actualCartItemDTOList.isEmpty()).isTrue();
    }

    @Test
    void getCartItemById_shouldReturnCartItemDTO() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartId();
        CartItemDTO actualCartItemDTO = cartService.getCartItemById(cartItemId);
        CartItemDTO expectedCartItemDTO = getCartItemDTOMock();

        assertThat(actualCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void getCartItemById_shouldThrowExceptionWhenCartItemNotFound() {
        int nonExistingCartItemId = 1;

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartService.getCartItemById(nonExistingCartItemId));

        assertThat(exception.getMessage()).isEqualTo("CartItem not found");
    }

    @Test
    void addCartItem_shouldAddCartItem() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        CartItemDTO expectedCartItemDTO = getCartItemDTOMock();

        assertThat(addedCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void deleteCartItemById_shouldDeleteCartItem() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartId();
        assertThat(cartService.getCartItemById(cartItemId)).isNotNull();

        cartService.deleteCartItemById(cartItemId);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartService.getCartItemById(cartItemId));
        assertThat(exception.getMessage()).isEqualTo("CartItem not found");
    }

    @Test
    void updateCartItem_shouldUpdateCartItem() {
        addCartItemToDatabase();
        CartItemDTO cartItemDTOPassedToUpdateMethod = new CartItemDTO(1, 1, 1, 99);

        CartItemDTO updatedCartItemDTO = cartService.updateCartItem(cartItemDTOPassedToUpdateMethod);

        assertThat(updatedCartItemDTO).isEqualTo(cartItemDTOPassedToUpdateMethod);
    }

    @Test
    void addCartItem_shouldThrowExceptionWhenInsufficientStock() {
        userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        Product product = productService.addProduct(new ProductCreateDTO("Product", "Product Description", 10.00, 5));
        
        CartItemDTO cartItemDTO = new CartItemDTO(0, 1, product.getProductId(), 10);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartService.addCartItem(cartItemDTO));

        assertThat(exception.getMessage()).isEqualTo("Not enough products in stock");
    }

    @Test
    void getCartTotalForCurrentUser_shouldReturnCartTotal() {
        addCartItemToDatabase();
        Product product2 = productService.addProduct(new ProductCreateDTO("Product", "Product Description", 15.00, 100));
        CartItemDTO cartItem2 = new CartItemDTO(0, 1, product2.getProductId(), 2);
        cartService.addCartItem(cartItem2);

        when(jwtService.getCurrentUsername()).thenReturn("Username");
        double cartTotal = cartService.getCartTotalForCurrentUser();
        assertThat(cartTotal).isEqualTo(130.0);
    }

    @Test
    void getCartDTOForCurrentUser_shouldReturnCartDTO() {
        addCartToDatabase();
        when(jwtService.getCurrentUsername()).thenReturn("Username");
        CartDTO actualCart = cartService.getCartDTOForCurrentUser();
        CartDTO expectedCart = getCartMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void getCartDTOForCurrentUser_shouldThrowExceptionWhenCartNotFound() {
        userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        cartService.deleteCartById(1);

        when(jwtService.getCurrentUsername()).thenReturn("Username");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartService.getCartDTOForCurrentUser());

        assertThat(exception.getMessage()).isEqualTo("Cart not found for current user");
    }

    @Test
    void getCartItemsInCartForCurrentUser_shouldReturnCartItems() {
        addCartItemToDatabase();

        when(jwtService.getCurrentUsername()).thenReturn("Username");
        List<CartItemDTO> actualCartItems = cartService.getCartItemsInCartForCurrentUser();
        List<CartItemDTO> expectedCartItems = List.of(getCartItemDTOMock());

        assertThat(actualCartItems).isEqualTo(expectedCartItems);
    }

    @Test
    void addCartItemToCartForCurrentUser_shouldAddCartItem() {
        userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        Product product = productService.addProduct(new ProductCreateDTO("Product", "Product Description", 10.00, 100));
        CartItemDTO cartItemDTO = new CartItemDTO(0, 1, product.getProductId(), 5);

        when(jwtService.getCurrentUsername()).thenReturn("Username");
        CartItemDTO addedCartItemDTO = cartService.addCartItemToCartForCurrentUser(cartItemDTO);
        CartItemDTO expectedCartItemDTO = new CartItemDTO(1, 1, product.getProductId(), 5);

        assertThat(addedCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void updateCartItemInCartForCurrentUser_shouldUpdateCartItem() {
        addCartItemToDatabase();
        CartItemDTO cartItemDTOPassedToUpdateMethod = new CartItemDTO(1, 1, 1, 99);

        when(jwtService.getCurrentUsername()).thenReturn("Username");
        CartItemDTO updatedCartItemDTO = cartService.updateCartItemInCartForCurrentUser(cartItemDTOPassedToUpdateMethod);

        assertThat(updatedCartItemDTO).isEqualTo(cartItemDTOPassedToUpdateMethod);
    }

    @Test
    void deleteCartItemFromCartForCurrentUser_shouldDeleteCartItem() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();
        assertThat(cartService.getCartItemById(cartItemId)).isNotNull();

        when(jwtService.getCurrentUsername()).thenReturn("Username");
        cartService.deleteCartItemFromCartForCurrentUser(cartItemId);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartService.getCartItemById(cartItemId));
        assertThat(exception.getMessage()).isEqualTo("CartItem not found");
    }

    @Test
    void getCartItemDTOForCurrentUserByCartItemId_shouldReturnCartItemDTO() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();

        when(jwtService.getCurrentUsername()).thenReturn("Username");
        CartItemDTO actualCartItemDTO = cartService.getCartItemDTOForCurrentUserByCartItemId(cartItemId);
        CartItemDTO expectedCartItemDTO = getCartItemDTOMock();

        assertThat(actualCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void getCartItemDTOForCurrentUserByCartItemId_shouldThrowExceptionWhenCartItemNotFound() {
        addCartToDatabase();
        int nonExistingCartItemId = 99;

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartService.getCartItemDTOForCurrentUserByCartItemId(nonExistingCartItemId));

        assertThat(exception.getMessage()).isEqualTo("CartItem not found");
    }

    @Test
    void addCartItemToCartForCurrentUser_shouldThrowExceptionWhenAccessDenied() {
        userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        userService.addUser(new UserRegisterDTO("OtherUser", "otheruser@outlook.com", "password", "Address"));

        Product product = productService.addProduct(new ProductCreateDTO("Product", "Product Description", 10.00, 100));
        CartItemDTO cartItemDTO = new CartItemDTO(0, 2, product.getProductId(), 5);

        when(jwtService.getCurrentUsername()).thenReturn("Username");
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> cartService.addCartItemToCartForCurrentUser(cartItemDTO));

        assertThat(exception.getMessage()).isEqualTo("You are not allowed to access this cart item");
    }   
}