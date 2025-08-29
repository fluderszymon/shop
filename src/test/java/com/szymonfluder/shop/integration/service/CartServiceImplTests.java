package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.*;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.*;
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

    private CartDTO addCartToDatabase() {
        User addedUser = userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        return cartService.addCart(addedUser.getUserId());
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
        CartDTO addedCartDTO = addCartToDatabase();
        int cartId = addedCartDTO.getCartId();
        ProductCreateDTO product1 = new ProductCreateDTO("Product 1", "Description 1", 10.0, 10);
        ProductCreateDTO product2 = new ProductCreateDTO("Product 2", "Description 2", 15.0, 10);
        productService.addProduct(product1);
        productService.addProduct(product2);
        CartItemDTO cartItem1 = new CartItemDTO(0, 1, 1, 2);
        CartItemDTO cartItem2 = new CartItemDTO(0, 1, 2, 1);
        cartService.addCartItem(cartItem1);
        cartService.addCartItem(cartItem2);

        double cartTotal = cartService.getCartTotal(cartId);
        assertThat(cartTotal).isEqualTo(35.0);
    }

    private CartItemDTO addCartItemToDatabase() {
        User addedUser = userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        CartDTO cartDTO = cartService.addCart(addedUser.getUserId());
        Product product = productService.addProduct(new ProductCreateDTO("Product", "Product Description", 10.00, 100));
        return cartService.addCartItem(new CartItemDTO(0, cartDTO.getCartId(), product.getProductId(), 10));
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
}