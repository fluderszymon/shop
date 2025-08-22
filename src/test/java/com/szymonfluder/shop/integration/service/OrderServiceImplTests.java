package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.ProductCreateDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.OrderItemMapperImpl;
import com.szymonfluder.shop.mapper.OrderMapperImpl;
import com.szymonfluder.shop.service.CartItemService;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.ProductService;
import com.szymonfluder.shop.service.UserService;
import com.szymonfluder.shop.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({OrderServiceImpl.class, OrderMapperImpl.class})
public class OrderServiceImplTests {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderMapperImpl orderMapper;

    @Autowired
    private OrderItemMapperImpl orderItemMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartItemService cartItemService;

    private User addUserToDatabase() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
                "User", "user@outlook.com", "password", "Address");
        return userService.addUser(userRegisterDTO);
    }

    private CartDTO addCartToDatabase(int userId) {
        return cartService.addCart(userId);
    }

    private Product addProductToDatabase() {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
                "Product", "Product Description", 10.00, 20);
        return productService.addProduct(productCreateDTO);
    }

    private List<CartItemDTO> addCartItemsToDatabase(int cartId, int productId) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(productId);
        cartItemDTO.setCartId(cartId);
        cartItemDTO.setQuantity(10);
        CartItemDTO addedCartItemDTO = cartItemService.addCartItem(cartItemDTO);
        return List.of(addedCartItemDTO);
    }

    @Test
    void checkout_shouldThrowExceptionWhenBalanceIsInsufficient() {
        User addedUser = addUserToDatabase();
        int userId = addedUser.getUserId();
        CartDTO addedCart = addCartToDatabase(userId);
        int cartId = addedCart.getCartId();
        Product addedProduct = addProductToDatabase();
        int productId = addedProduct.getProductId();
        List<CartItemDTO> addedCartItems = addCartItemsToDatabase(cartId, productId);
        CartItemDTO addedCartItemDTO = addedCartItems.get(0);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout(userId, cartId));

        assertEquals("Insufficient balance", exception.getMessage());
    }

}
