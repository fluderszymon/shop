package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.exception.EntityNotFoundException;
import com.szymonfluder.shop.exception.OutOfStockException;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({CartServiceImpl.class, CartMapperImpl.class,
        CartItemMapperImpl.class, ProductServiceImpl.class, ProductMapperImpl.class,
        UserServiceImpl.class, UserMapperImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CartServiceImplTests extends AbstractServiceTest {

    private final int UPDATED_QUANTITY = 99;
    private final int SMALL_QUANTITY = 5;
    private final BigDecimal CART_TOTAL = BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP);

    private CartDTO addCartToDatabase() {
        userService.addUser(getUserRegisterDTO());
        return cartService.getCartById(CART_ID);
    }

    private CartDTO getCartDTOMock() {
        return new CartDTO(CART_ID, USER_ID);
    }

    @Test
    void getAllCarts_shouldReturnAllCartDTOs_whenCartsExist() {
        addCartToDatabase();
        List<CartDTO> actualProductDTOList = cartService.getAllCarts();
        List<CartDTO> expectedProductDTOList = List.of(getCartDTOMock());

        assertThat(actualProductDTOList).isEqualTo(expectedProductDTOList);
    }

    @Test
    void getAllCarts_shouldReturnEmptyList_whenNoCartsExist() {
        List<CartDTO> actualProductDTOList = cartService.getAllCarts();
        assertThat(actualProductDTOList.isEmpty()).isTrue();
    }

    @Test
    void getCartById_shouldReturnCartDTO_whenCartExists() {
        addCartToDatabase();
        CartDTO actualCart = cartService.getCartById(CART_ID);
        CartDTO expectedCart = getCartDTOMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void getCartById_shouldThrowEntityNotFoundException_whenCartNotFound() {
        assertThrows(EntityNotFoundException.class, () -> cartService.getCartById(CART_ID));
    }

    @Test
    void addCart_shouldAddCart_whenValidDataProvided() {
        addCartToDatabase();
        CartDTO actualCart = cartService.getCartById(CART_ID);
        CartDTO expectedCart = getCartDTOMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void deleteCartById_shouldDeleteCart_whenCartExists() {
        CartDTO addedCartDTO = addCartToDatabase();
        int cartId = addedCartDTO.getCartId();
        assertThat(cartService.getCartById(cartId)).isNotNull();

        cartService.deleteCartById(cartId);

        assertThrows(EntityNotFoundException.class, () -> cartService.getCartById(cartId));
    }

    @Test
    void updateCart_shouldUpdateCart_whenCartExists() {
        addCartToDatabase();
        CartDTO cartDTOPassedToUpdateMethod = new CartDTO(CART_ID, (USER_ID + 1));

        CartDTO updatedCartDTO = cartService.updateCart(cartDTOPassedToUpdateMethod);

        assertThat(updatedCartDTO).isEqualTo(cartDTOPassedToUpdateMethod);
    }

    @Test
    void getCartTotal_shouldReturnCartTotal_whenCartHasItems() {
        addCartItemToDatabase();
        BigDecimal cartTotal = cartService.getCartTotal(CART_ID);
        assertThat(cartTotal).isEqualTo(CART_TOTAL);
    }

    @Test
    void getAllCartItems_shouldReturnAllCartItemDTOs_whenCartItemsExist() {
        addCartItemToDatabase();
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItems();
        List<CartItemDTO> expectedCartItemDTOList = List.of(getCartItemDTOMock());

        assertThat(actualCartItemDTOList).isEqualTo(expectedCartItemDTOList);
    }

    @Test
    void getAllCartItems_shouldReturnEmptyList_whenNoCartItemsExist() {
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItems();
        assertThat(actualCartItemDTOList.isEmpty()).isTrue();
    }

    @Test
    void getAllCartItemsByCartId_shouldReturnAllCartItemDTOsByCartId_whenCartHasItems() {
        CartItemDTO addedCartITemDTO = addCartItemToDatabase();
        int cartId = addedCartITemDTO.getCartId();
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItemsByCartId(cartId);
        List<CartItemDTO> expectedCartItemDTOList = List.of(getCartItemDTOMock());

        assertThat(actualCartItemDTOList).isEqualTo(expectedCartItemDTOList);
    }

    @Test
    void getAllCartItemsByCartId_shouldReturnEmptyList_whenCartHasNoItems() {
        addCartItemToDatabase();
        List<CartItemDTO> actualCartItemDTOList = cartService.getAllCartItemsByCartId(NON_EXISTING_ID);

        assertThat(actualCartItemDTOList.isEmpty()).isTrue();
    }

    @Test
    void getCartItemById_shouldReturnCartItemDTO_whenCartItemExists() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();
        CartItemDTO actualCartItemDTO = cartService.getCartItemById(cartItemId);
        CartItemDTO expectedCartItemDTO = getCartItemDTOMock();

        assertThat(actualCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void getCartItemById_shouldThrowEntityNotFoundException_whenCartItemNotFound() {
        assertThrows(EntityNotFoundException.class, () -> cartService.getCartItemById(NON_EXISTING_ID));
    }

    @Test
    void addCartItem_shouldAddCartItem_whenValidDataProvided() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        CartItemDTO expectedCartItemDTO = getCartItemDTOMock();

        assertThat(addedCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void deleteCartItemById_shouldDeleteCartItem_whenCartItemExists() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();
        assertThat(cartService.getCartItemById(cartItemId)).isNotNull();

        cartService.deleteCartItemById(cartItemId);

        assertThrows(EntityNotFoundException.class, () -> cartService.getCartItemById(cartItemId));
    }

    @Test
    void updateCartItem_shouldUpdateCartItem_whenCartItemExists() {
        addCartItemToDatabase();
        CartItemDTO cartItemDTOPassedToUpdateMethod = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, UPDATED_QUANTITY);

        CartItemDTO updatedCartItemDTO = cartService.updateCartItem(cartItemDTOPassedToUpdateMethod);

        assertThat(updatedCartItemDTO).isEqualTo(cartItemDTOPassedToUpdateMethod);
    }

    @Test
    void addCartItem_shouldThrowOutOfStockException_whenInsufficientStock() {
        addUserToDatabase();
        addProductToDatabase();
        
        CartItemDTO cartItemDTO = new CartItemDTO(0, CART_ID, PRODUCT_ID, DEFAULT_STOCK+1);

        assertThrows(OutOfStockException.class, () -> cartService.addCartItem(cartItemDTO));
    }

    @Test
    void getCartTotalForCurrentUser_shouldReturnCartTotal_whenUserIsAuthenticated() {
        addCartItemToDatabase();

        authenticateUser(USERNAME);
        BigDecimal cartTotal = cartService.getCartTotalForCurrentUser();
        assertThat(cartTotal).isEqualTo(CART_TOTAL);
    }

    @Test
    void getCartDTOForCurrentUser_shouldReturnCartDTO_whenUserIsAuthenticated() {
        addCartToDatabase();
        authenticateUser(USERNAME);
        CartDTO actualCart = cartService.getCartDTOForCurrentUser();
        CartDTO expectedCart = getCartDTOMock();

        assertThat(actualCart).isEqualTo(expectedCart);
    }

    @Test
    void getCartDTOForCurrentUser_shouldThrowEntityNotFoundException_whenCartNotFound() {
        addUserToDatabase();
        cartService.deleteCartById(CART_ID);

        authenticateUser(USERNAME);
        assertThrows(EntityNotFoundException.class, () -> cartService.getCartDTOForCurrentUser());
    }

    @Test
    void getCartItemsInCartForCurrentUser_shouldReturnCartItems_whenUserIsAuthenticated() {
        addCartItemToDatabase();

        authenticateUser(USERNAME);
        List<CartItemDTO> actualCartItems = cartService.getCartItemsInCartForCurrentUser();
        List<CartItemDTO> expectedCartItems = List.of(getCartItemDTOMock());

        assertThat(actualCartItems).isEqualTo(expectedCartItems);
    }

    @Test
    void addCartItemToCartForCurrentUser_shouldAddCartItem_whenUserIsAuthenticated() {
        addUserToDatabase();
        addProductToDatabase();
        CartItemDTO cartItemDTO = new CartItemDTO(0, CART_ID, PRODUCT_ID, SMALL_QUANTITY);

        authenticateUser(USERNAME);
        CartItemDTO addedCartItemDTO = cartService.addCartItemToCartForCurrentUser(cartItemDTO);
        CartItemDTO expectedCartItemDTO = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, SMALL_QUANTITY);

        assertThat(addedCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void addCartItemToCartForCurrentUser_shouldThrowException_whenAccessDenied() {
        userService.addUser(new UserRegisterDTO(USERNAME, EMAIL, PASSWORD, ADDRESS));
        userService.addUser(new UserRegisterDTO(OTHER_USERNAME, OTHER_EMAIL, PASSWORD, ADDRESS));

        addProductToDatabase();
        CartItemDTO cartItemDTO = new CartItemDTO(0, (USER_ID + 1), PRODUCT_ID, SMALL_QUANTITY);

        authenticateUser(USERNAME);
        assertCartAccessDeniedException(() -> cartService.addCartItemToCartForCurrentUser(cartItemDTO));
    }   

    @Test
    void updateCartItemInCartForCurrentUser_shouldUpdateCartItem_whenUserIsAuthenticated() {
        addCartItemToDatabase();
        CartItemDTO cartItemDTOPassedToUpdateMethod = new CartItemDTO(CART_ITEM_ID, CART_ID, PRODUCT_ID, UPDATED_QUANTITY);

        authenticateUser(USERNAME);
        CartItemDTO updatedCartItemDTO = cartService.updateCartItemInCartForCurrentUser(cartItemDTOPassedToUpdateMethod);

        assertThat(updatedCartItemDTO).isEqualTo(cartItemDTOPassedToUpdateMethod);
    }

    @Test
    void deleteCartItemFromCartForCurrentUser_shouldDeleteCartItem_whenUserIsAuthenticated() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();
        assertThat(cartService.getCartItemById(cartItemId)).isNotNull();

        authenticateUser(USERNAME);
        cartService.deleteCartItemFromCartForCurrentUser(cartItemId);

        assertThrows(EntityNotFoundException.class, () -> cartService.getCartItemById(cartItemId));
    }

    @Test
    void getCartItemDTOForCurrentUserByCartItemId_shouldReturnCartItemDTO_whenUserIsAuthenticated() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartItemId();

        authenticateUser(USERNAME);
        CartItemDTO actualCartItemDTO = cartService.getCartItemDTOForCurrentUserByCartItemId(cartItemId);
        CartItemDTO expectedCartItemDTO = getCartItemDTOMock();

        assertThat(actualCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void getCartItemDTOForCurrentUserByCartItemId_shouldThrowEntityNotFoundException_whenCartItemNotFound() {
        addCartToDatabase();

        assertThrows(EntityNotFoundException.class, () -> cartService.getCartItemDTOForCurrentUserByCartItemId(NON_EXISTING_ID));
    }
}