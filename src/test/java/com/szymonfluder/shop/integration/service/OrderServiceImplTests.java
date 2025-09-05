package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.*;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.*;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.ProductService;
import com.szymonfluder.shop.service.impl.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({OrderServiceImpl.class, OrderMapperImpl.class, OrderItemMapperImpl.class,
        UserServiceImpl.class, UserMapperImpl.class, CartServiceImpl.class, 
        CartMapperImpl.class, CartItemMapperImpl.class, ProductServiceImpl.class, 
        ProductMapperImpl.class, TestConfig.class})
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
    @Autowired
    private JWTService jwtService;
    
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
        addUserToDatabaseWithSufficientBalance();
        addProductToDatabase();
        addCartItemToDatabase();

        when(jwtService.getCurrentUsername()).thenReturn("User");
        orderService.checkout();
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
        addProductToDatabase();
        addCartItemToDatabase();

        when(jwtService.getCurrentUsername()).thenReturn("User");
        orderService.checkout();

        assertThat(orderService.getOrderById(1)).isNotNull();
        assertThat(orderService.getAllOrderItemsByOrderId(1)).isNotNull();
        assertThat(userService.getUserBalance(1)).isEqualTo(0.00);
        assertThat(cartService.getAllCartItemsByCartId(1)).isEqualTo(List.of());
    }

    @Test
    void checkout_shouldThrowExceptionWhenCartIsEmpty() {
        addUserToDatabaseWithSufficientBalance();
        
        when(jwtService.getCurrentUsername()).thenReturn("User");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout());
        assertThat(exception.getMessage()).isEqualTo("Cart is empty");
    }

    @Test
    void checkout_shouldThrowExceptionWhenBalanceIsInsufficient() {
        addUserToDatabaseWithInsufficientBalance();
        addProductToDatabase();
        addCartItemToDatabase();

        when(jwtService.getCurrentUsername()).thenReturn("User");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout());
        assertThat(exception.getMessage()).isEqualTo("Insufficient balance");
    }

    @Test
    void checkout_shouldThrowExceptionWhenStockIsInsufficient() {
        addUserToDatabaseWithInsufficientBalance();
        Product addedProduct = addProductToDatabase();
        addCartItemToDatabase();
        addedProduct.setStock(0);
        productService.updateProduct(addedProduct);

        when(jwtService.getCurrentUsername()).thenReturn("User");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout());
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

    @Test
    void getOrdersForCurrentUser_shouldGetAllOrderDTOsForCurrentUser() {
        when(jwtService.getCurrentUsername()).thenReturn("User");
        addOrderToDatabase();
        List<OrderDTO> actualOrderDTOList = orderService.getOrdersForCurrentUser();
        List<OrderDTO> expectedOrderDTOList = List.of(getOrderDTOMock());

        assertThat(actualOrderDTOList).isEqualTo(expectedOrderDTOList);
    }

    @Test
    void getOrderItemsForCurrentUser_shouldGetAllOrderItemDTOsForCurrentUser() {
        when(jwtService.getCurrentUsername()).thenReturn("User");
        addOrderToDatabase();
        List<OrderItemDTO> actualOrderItemDTOList = orderService.getOrderItemsForCurrentUser();
        List<OrderItemDTO> expectedOrderItemDTOList = List.of(getOrderItemDTOMock());

        assertThat(actualOrderItemDTOList).isEqualTo(expectedOrderItemDTOList);
    }

    @Test
    void getOrderItemsInOrderByOrderIdForCurrentUser_shouldGetAllOrderItemDTOsForCurrentUser() {
        addOrderToDatabase();
        when(jwtService.getCurrentUsername()).thenReturn("User");
        List<OrderItemDTO> actualOrderItemDTOList = orderService.getOrderItemsInOrderByOrderIdForCurrentUser(1);
        List<OrderItemDTO> expectedOrderItemDTOList = List.of(getOrderItemDTOMock());

        assertThat(actualOrderItemDTOList).isEqualTo(expectedOrderItemDTOList);
    }

    @Test
    void getOrderItemsInOrderByOrderIdForCurrentUser_shouldThrowAccessDeniedExceptionWhenNotOwner() {
        addOrderToDatabase();
        userService.addUser(new UserRegisterDTO("OtherUser", "other@outlook.com", "password", "Address"));
        
        when(jwtService.getCurrentUsername()).thenReturn("OtherUser");
        assertThrows(AccessDeniedException.class,
                () -> orderService.getOrderItemsInOrderByOrderIdForCurrentUser(1));
    }
}