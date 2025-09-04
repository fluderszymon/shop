package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.*;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.*;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.ProductService;
import com.szymonfluder.shop.service.impl.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({OrderServiceImpl.class, OrderMapperImpl.class, OrderItemMapperImpl.class,
        UserServiceImpl.class, UserMapperImpl.class, CartServiceImpl.class, 
        CartMapperImpl.class, CartItemMapperImpl.class, ProductServiceImpl.class, 
        ProductMapperImpl.class, CartAuthServiceImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderServiceImplTests {

    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    private UserDTO addUserToDatabaseWithSufficientBalance() {
        User adddedUser = userService.addUser(new UserRegisterDTO("User", "user@outlook.com", "password", "Address"));
        userService.updateUserBalance(adddedUser.getUserId(), 100.00);
        return userService.getUserById(adddedUser.getUserId());
    }

    private User addUserToDatabaseWithInsufficientBalance() {
        return userService.addUser(new UserRegisterDTO("User", "user@outlook.com", "password", "Address"));
    }

    private void addCartItemToDatabase() {
        cartService.addCartItem(new CartItemDTO(0, 1, 1, 10));
    }

    private Product addProductToDatabase() {
        return productService.addProduct(new ProductCreateDTO("Product", "Product Description", 10.00, 10));
    }

    private void addOrderToDatabase() {
        UserDTO addedUserDTO = addUserToDatabaseWithSufficientBalance();
        CartDTO addedCartDTO = cartService.getCartById(1);
        addProductToDatabase();
        addCartItemToDatabase();

        orderService.checkout(addedUserDTO.getUserId(), addedCartDTO.getUserId());
    }

    private OrderDTO getOrderDTOMock() {
        return new OrderDTO(1, 1, 100.00, LocalDate.now());
    }

    private OrderItemDTO getOrderItemDTOMock() {
        return new OrderItemDTO(1, 1, 10, "Product", 1, 10.00);
    }

    @Test
    void checkout_shouldCompleteCheckout() {
        addUserToDatabaseWithSufficientBalance();
        CartDTO cart = cartService.getCartById(1);
        addProductToDatabase();
        addCartItemToDatabase();
        orderService.checkout(1, cart.getCartId());

        assertThat(orderService.getOrderById(1)).isNotNull();
        assertThat(orderService.getAllOrderItemsByOrderId(1)).isNotNull();
        assertThat(userService.getUserBalance(1)).isEqualTo(0.00);
        assertThat(cartService.getAllCartItemsByCartId(1)).isEqualTo(List.of());
    }

    @Test
    void checkout_shouldThrowExceptionWhenCartIsEmpty() {
        UserDTO addedUserDTO = addUserToDatabaseWithSufficientBalance();
        int userId = addedUserDTO.getUserId();
        CartDTO cart = cartService.getCartById(1);
        int cartId = cart.getCartId();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout(userId, cartId));
        assertThat(exception.getMessage()).isEqualTo("Cart is empty");
    }

    @Test
    void checkout_shouldThrowExceptionWhenBalanceIsInsufficient() {
        User addedUser = addUserToDatabaseWithInsufficientBalance();
        int userId = addedUser.getUserId();
        CartDTO cart = cartService.getCartById(1);
        int cartId = cart.getCartId();
        addProductToDatabase();
        addCartItemToDatabase();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout(userId, cartId));
        assertThat(exception.getMessage()).isEqualTo("Insufficient balance");
    }

    @Test
    void checkout_shouldThrowExceptionWhenStockIsInsufficient() {
        User addedUser = addUserToDatabaseWithInsufficientBalance();
        int userId = addedUser.getUserId();
        CartDTO cart = cartService.getCartById(1);
        int cartId = cart.getCartId();
        Product addedProduct = addProductToDatabase();
        addCartItemToDatabase();
        addedProduct.setStock(0);
        productService.updateProduct(addedProduct);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout(userId, cartId));
        assertThat(exception.getMessage()).isEqualTo("Not enough products in stock");
    }

    @Test
    void getAllOrders_shouldGetAllOrderDTOs() {
        addOrderToDatabase();
        List<OrderDTO> actualOrderDTOList = orderService.getAllOrders();
        OrderDTO expectedOrderDTO = getOrderDTOMock();

        assertThat(actualOrderDTOList).isEqualTo(List.of(expectedOrderDTO));
    }

    @Test
    void getOrderById_shouldReturnOrderDTO() {
        addOrderToDatabase();
        OrderDTO actualOrderDTO = orderService.getOrderById(1);
        OrderDTO expectedOrderDTO = getOrderDTOMock();

        assertThat(actualOrderDTO).isEqualTo(expectedOrderDTO);
    }

    @Test
    void getOrderById_shouldThrowExceptionWhenOrderNotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrderById(1));
        assertThat(exception.getMessage()).isEqualTo("Order with given orderId not found");
    }

    @Test
    void getAllOrderItems_shouldGetAllOrderItemDTOs() {
        addOrderToDatabase();
        List<OrderItemDTO> actualOrderItemDTOList = orderService.getAllOrderItems();
        List<OrderItemDTO> expectedOrderItemDTOList = List.of(getOrderItemDTOMock());
        assertThat(actualOrderItemDTOList).isEqualTo(expectedOrderItemDTOList);
    }

    @Test
    void getAllOrderItemsByOrderId_shouldGetAllOrderItemDTOsByOrderId() {
        addOrderToDatabase();
        List<OrderItemDTO> actualOrderItemDTOList = orderService.getAllOrderItemsByOrderId(1);
        List<OrderItemDTO> expectedOrderItemDTOList = List.of(getOrderItemDTOMock());
        assertThat(actualOrderItemDTOList).isEqualTo(expectedOrderItemDTOList);
    }
}