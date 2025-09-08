package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.CartItemMapperImpl;
import com.szymonfluder.shop.mapper.CartMapperImpl;
import com.szymonfluder.shop.mapper.ProductMapperImpl;
import com.szymonfluder.shop.mapper.UserMapperImpl;
import com.szymonfluder.shop.service.impl.CartServiceImpl;
import com.szymonfluder.shop.service.impl.ProductServiceImpl;
import com.szymonfluder.shop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({CartServiceImpl.class, CartMapperImpl.class,
        CartItemMapperImpl.class, ProductServiceImpl.class, ProductMapperImpl.class,
        UserServiceImpl.class, UserMapperImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CartServiceImplTests extends AbstractServiceTest {

    private final int UPDATED_QUANTITY = 99;
    private final int SMALL_QUANTITY = 5;
    private final double CART_TOTAL = 100.0;

    private CartDTO addCartToDatabase() {
        userService.addUser(getUserRegisterDTO());
        return cartService.getCartById(CART_ID);
    }

    private CartDTO getCartDTOMock() {
        return new CartDTO(CART_ID, USER_ID);
    }

    @Test
    void getAllCarts_shouldReturnAllCartDTOs() {
        addCartToDatabase();
        List<CartDTO> actualProductDTOList = cartService.getAllCarts();
        List<CartDTO> expectedProductDTOList = List.of(getCartDTOMock());

        assertThat(actualProductDTOList).isEqualTo(expectedProductDTOList);
    }

    @Test
    void getAllCarts_shouldReturnEmptyList() {
        List<CartDTO> actualProductDTOList = cartService.getAllCarts();
        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getCartById_shouldReturnCartDTO() {
        addCartToDatabase();
        CartDTO actualCart = cartService.getCartById(CART_ID);
        CartDTO expectedCart = getCartDTOMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void getCartById_shouldThrowExceptionWhenCartNotFound() {
        assertRuntimeExceptionWithMessage(() -> cartService.getCartById(CART_ID), "Cart not found");
    }

    @Test
    void addCart_shouldAddCart() {
        addCartToDatabase();
        CartDTO actualCart = cartService.getCartById(CART_ID);
        CartDTO expectedCart = getCartDTOMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void deleteCartById_shouldDeleteCart() {
        CartDTO addedCartDTO = addCartToDatabase();
        int cartId = addedCartDTO.getCartId();
        assertThat(cartService.getCartById(cartId)).isNotNull();

        cartService.deleteCartById(cartId);

        assertRuntimeExceptionWithMessage(() -> cartService.getCartById(cartId), "Cart not found");
    }

    @Test
    void updateCart_shouldUpdateCart() {
        addCartToDatabase();
        CartDTO cartDTOPassedToUpdateMethod = new CartDTO(CART_ID, (USER_ID + 1));

        CartDTO updatedCartDTO = cartService.updateCart(cartDTOPassedToUpdateMethod);

        assertThat(updatedCartDTO).isEqualTo(cartDTOPassedToUpdateMethod);
    }

    @Test
    void getCartTotal_shouldReturnCartTotal() {
        addCartItemToDatabase();
        double cartTotal = cartService.getCartTotal(CART_ID);
        assertThat(cartTotal).isEqualTo(CART_TOTAL);
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
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItemsByCartId(NON_EXISTING_ID);

        assertThat(actualCartItemDTOList.isEmpty()).isTrue();
    }

    @Test
    void getCartItemById_shouldReturnCartItemDTO() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();
        CartItemDTO actualCartItemDTO = cartService.getCartItemById(cartItemId);
        CartItemDTO expectedCartItemDTO = getCartItemDTOMock();

        assertThat(actualCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void getCartItemById_shouldThrowExceptionWhenCartItemNotFound() {
        assertRuntimeExceptionWithMessage(() -> cartService.getCartItemById(NON_EXISTING_ID), "CartItem not found");
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
        int cartItemId = addedCartItemDTO.getCartItemId();
        assertThat(cartService.getCartItemById(cartItemId)).isNotNull();

        cartService.deleteCartItemById(cartItemId);

        assertRuntimeExceptionWithMessage(() -> cartService.getCartItemById(cartItemId), "CartItem not found");
    }

    @Test
    void updateCartItem_shouldUpdateCartItem() {
        addCartItemToDatabase();
        CartItemDTO cartItemDTOPassedToUpdateMethod = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, UPDATED_QUANTITY);

        CartItemDTO updatedCartItemDTO = cartService.updateCartItem(cartItemDTOPassedToUpdateMethod);

        assertThat(updatedCartItemDTO).isEqualTo(cartItemDTOPassedToUpdateMethod);
    }

    @Test
    void addCartItem_shouldThrowExceptionWhenInsufficientStock() {
        addUserToDatabase();
        addProductToDatabase();
        
        CartItemDTO cartItemDTO = new CartItemDTO(0, CART_ID, PRODUCT_ID, DEFAULT_STOCK+1);

        assertRuntimeExceptionWithMessage(() -> cartService.addCartItem(cartItemDTO), "Not enough products in stock");
    }

    @Test
    void getCartTotalForCurrentUser_shouldReturnCartTotal() {
        addCartItemToDatabase();

        mockJwtService_getCurrentUsername();
        double cartTotal = cartService.getCartTotalForCurrentUser();
        assertThat(cartTotal).isEqualTo(CART_TOTAL);
    }

    @Test
    void getCartDTOForCurrentUser_shouldReturnCartDTO() {
        addCartToDatabase();
        mockJwtService_getCurrentUsername();
        CartDTO actualCart = cartService.getCartDTOForCurrentUser();
        CartDTO expectedCart = getCartDTOMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void getCartDTOForCurrentUser_shouldThrowExceptionWhenCartNotFound() {
        addUserToDatabase();
        cartService.deleteCartById(CART_ID);

        mockJwtService_getCurrentUsername();
        assertRuntimeExceptionWithMessage(() -> cartService.getCartDTOForCurrentUser(), "Cart not found for current user");
    }

    @Test
    void getCartItemsInCartForCurrentUser_shouldReturnCartItems() {
        addCartItemToDatabase();

        mockJwtService_getCurrentUsername();
        List<CartItemDTO> actualCartItems = cartService.getCartItemsInCartForCurrentUser();
        List<CartItemDTO> expectedCartItems = List.of(getCartItemDTOMock());

        assertThat(actualCartItems).isEqualTo(expectedCartItems);
    }

    @Test
    void addCartItemToCartForCurrentUser_shouldAddCartItem() {
        addUserToDatabase();
        addProductToDatabase();
        CartItemDTO cartItemDTO = new CartItemDTO(0, CART_ID, PRODUCT_ID, SMALL_QUANTITY);

        mockJwtService_getCurrentUsername();
        CartItemDTO addedCartItemDTO = cartService.addCartItemToCartForCurrentUser(cartItemDTO);
        CartItemDTO expectedCartItemDTO = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, SMALL_QUANTITY);

        assertThat(addedCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void updateCartItemInCartForCurrentUser_shouldUpdateCartItem() {
        addCartItemToDatabase();
        CartItemDTO cartItemDTOPassedToUpdateMethod = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, UPDATED_QUANTITY);

        mockJwtService_getCurrentUsername();
        CartItemDTO updatedCartItemDTO = cartService.updateCartItemInCartForCurrentUser(cartItemDTOPassedToUpdateMethod);

        assertThat(updatedCartItemDTO).isEqualTo(cartItemDTOPassedToUpdateMethod);
    }

    @Test
    void deleteCartItemFromCartForCurrentUser_shouldDeleteCartItem() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();
        assertThat(cartService.getCartItemById(cartItemId)).isNotNull();

        mockJwtService_getCurrentUsername();
        cartService.deleteCartItemFromCartForCurrentUser(cartItemId);

        assertRuntimeExceptionWithMessage(() -> cartService.getCartItemById(cartItemId), "CartItem not found");
    }

    @Test
    void getCartItemDTOForCurrentUserByCartItemId_shouldReturnCartItemDTO() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();

        mockJwtService_getCurrentUsername();
        CartItemDTO actualCartItemDTO = cartService.getCartItemDTOForCurrentUserByCartItemId(cartItemId);
        CartItemDTO expectedCartItemDTO = getCartItemDTOMock();

        assertThat(actualCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void getCartItemDTOForCurrentUserByCartItemId_shouldThrowExceptionWhenCartItemNotFound() {
        addCartToDatabase();

        assertRuntimeExceptionWithMessage(() -> cartService.getCartItemDTOForCurrentUserByCartItemId(NON_EXISTING_ID), "CartItem not found");
    }

    @Test
    void addCartItemToCartForCurrentUser_shouldThrowExceptionWhenAccessDenied() {
        userService.addUser(new UserRegisterDTO(USERNAME, EMAIL, PASSWORD, ADDRESS));
        userService.addUser(new UserRegisterDTO(OTHER_USERNAME, OTHER_EMAIL, PASSWORD, ADDRESS));

        addProductToDatabase();
        CartItemDTO cartItemDTO = new CartItemDTO(0, (USER_ID + 1), PRODUCT_ID, SMALL_QUANTITY);

        mockJwtService_getCurrentUsername();
        assertAccessDeniedException(() -> cartService.addCartItemToCartForCurrentUser(cartItemDTO), 
                "You are not allowed to access this cart item");
    }   
}