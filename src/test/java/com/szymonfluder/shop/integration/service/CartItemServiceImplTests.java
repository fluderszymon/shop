package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.CartItemMapperImpl;
import com.szymonfluder.shop.mapper.CartMapperImpl;
import com.szymonfluder.shop.mapper.ProductMapperImpl;
import com.szymonfluder.shop.mapper.UserMapperImpl;
import com.szymonfluder.shop.service.impl.CartItemServiceImpl;
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
@Import({CartItemServiceImpl.class, CartItemMapperImpl.class,
        ProductServiceImpl.class, ProductMapperImpl.class,
        UserServiceImpl.class, UserMapperImpl.class,
        CartServiceImpl.class, CartMapperImpl.class,})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CartItemServiceImplTests {

    @Autowired
    private CartItemServiceImpl cartItemService;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CartServiceImpl cartService;

    private CartItemDTO addCartItemToDatabase() {
        User addedUser = userService.addUser(new UserRegisterDTO("Username", "user@outlook.com", "password", "Address"));
        CartDTO cartDTO = cartService.addCart(addedUser.getUserId());
        Product product = productService.addProduct(new ProductCreateDTO("Product", "Product Description", 10.00, 100));
        return cartItemService.addCartItem(new CartItemDTO(0, cartDTO.getCartId(), product.getProductId(), 10));
    }

    private CartItemDTO getCartItemDTO() {
        return new CartItemDTO(1, 1, 1, 10);
    }

    @Test
    void getAllCartItems_shouldReturnAllCartItemDTOs() {
        addCartItemToDatabase();
        List<CartItemDTO> actualCartItemDTOList = cartItemService.getAllCartItems();
        List<CartItemDTO> expectedCartItemDTOList = List.of(getCartItemDTO());

        assertThat(actualCartItemDTOList).isEqualTo(expectedCartItemDTOList);
    }

    @Test
    void getAllCartItems_shouldReturnEmptyList() {
        List<CartItemDTO> actualCartItemDTOList = cartItemService.getAllCartItems();
        assertThat(actualCartItemDTOList.isEmpty()).isTrue();
    }

    @Test
    void getAllCartItemsByCartId_shouldReturnAllCartItemDTOsByCartId() {
        CartItemDTO addedCartITemDTO = addCartItemToDatabase();
        int cartId = addedCartITemDTO.getCartId();
        List<CartItemDTO> actualCartItemDTOList = cartItemService.getAllCartItemsByCartId(cartId);
        List<CartItemDTO> expectedCartItemDTOList = List.of(getCartItemDTO());

        assertThat(actualCartItemDTOList).isEqualTo(expectedCartItemDTOList);
    }

    @Test
    void getAllCartItemsByCartId_shouldReturnEmptyListWhenCartHasNoItems() {
        CartItemDTO addedCartITemDTO = addCartItemToDatabase();
        int emptyCartId = (addedCartITemDTO.getCartId() + 1);
        List<CartItemDTO> actualCartItemDTOList = cartItemService.getAllCartItemsByCartId(emptyCartId);

        assertThat(actualCartItemDTOList.isEmpty()).isTrue();
    }

    @Test
    void getCartItemById_shouldReturnCartItemDTO() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartId();
        CartItemDTO actualCartItemDTO = cartItemService.getCartItemById(cartItemId);
        CartItemDTO expectedCartItemDTO = getCartItemDTO();

        assertThat(actualCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void getCartItemById_shouldThrowExceptionWhenCartItemNotFound() {
        int nonExistingCartItemId = 1;

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartItemService.getCartItemById(nonExistingCartItemId));

        assertThat(exception.getMessage()).isEqualTo("CartItem not found");
    }

    @Test
    void addCartItem_shouldAddCartItem() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        CartItemDTO expectedCartItemDTO = getCartItemDTO();

        assertThat(addedCartItemDTO).isEqualTo(expectedCartItemDTO);
    }

    @Test
    void deleteCartItemById_shouldDeleteCartItem() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        int cartItemId = addedCartItemDTO.getCartId();
        assertThat(cartItemService.getCartItemById(cartItemId)).isNotNull();

        cartItemService.deleteCartItemById(cartItemId);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cartItemService.getCartItemById(cartItemId));
        assertThat(exception.getMessage()).isEqualTo("CartItem not found");
    }

    @Test
    void updateCartItem_shouldUpdateCartItem() {
        CartItemDTO addedCartItemDTO = addCartItemToDatabase();
        CartItemDTO cartItemDTOPassedToUpdateMethod = new CartItemDTO(1, 1, 1, 99);

        CartItemDTO updatedCartItemDTO = cartItemService.updateCartItem(cartItemDTOPassedToUpdateMethod);

        assertThat(updatedCartItemDTO).isEqualTo(cartItemDTOPassedToUpdateMethod);
    }
}