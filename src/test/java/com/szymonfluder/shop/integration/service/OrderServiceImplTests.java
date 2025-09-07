package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.CartItemMapperImpl;
import com.szymonfluder.shop.mapper.CartMapperImpl;
import com.szymonfluder.shop.mapper.OrderItemMapperImpl;
import com.szymonfluder.shop.mapper.OrderMapperImpl;
import com.szymonfluder.shop.mapper.ProductMapperImpl;
import com.szymonfluder.shop.mapper.UserMapperImpl;
import com.szymonfluder.shop.service.impl.CartServiceImpl;
import com.szymonfluder.shop.service.impl.OrderServiceImpl;
import com.szymonfluder.shop.service.impl.ProductServiceImpl;
import com.szymonfluder.shop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@DataJpaTest
@Import({OrderServiceImpl.class, OrderMapperImpl.class, OrderItemMapperImpl.class,
        UserServiceImpl.class, UserMapperImpl.class, CartServiceImpl.class, 
        CartMapperImpl.class, CartItemMapperImpl.class, ProductServiceImpl.class, 
        ProductMapperImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderServiceImplTests extends AbstractServiceTest {

    @Test
    void checkout_shouldCompleteCheckout() {
        setupCompleteCartScenario();
        orderService.checkout();

        assertThat(orderService.getOrderById(USER_ID)).isNotNull();
        assertThat(orderService.getAllOrderItemsByOrderId(USER_ID)).isNotNull();
        assertThat(userService.getUserBalance(USER_ID)).isEqualTo(0.00);
        assertThat(cartService.getAllCartItemsByCartId(USER_ID)).isEqualTo(List.of());
    }

    @Test
    void checkout_shouldThrowExceptionWhenCartIsEmpty() {
        setupEmptyCartScenario();
        assertRuntimeExceptionWithMessage(() -> orderService.checkout(), "Cart is empty");
    }

    @Test
    void checkout_shouldThrowExceptionWhenBalanceIsInsufficient() {
        setupInsufficientBalanceScenario();
        assertRuntimeExceptionWithMessage(() -> orderService.checkout(), "Insufficient balance");
    }

    @Test
    void checkout_shouldThrowExceptionWhenStockIsInsufficient() {
        setupCartWithNotEnoughStockInProducts();
        assertRuntimeExceptionWithMessage(() -> orderService.checkout(), "Not enough products in stock");
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
        OrderDTO actualOrderDTO = orderService.getOrderById(ORDER_ID);
        OrderDTO expectedOrderDTO = getOrderDTOMock();

        assertThat(actualOrderDTO).isEqualTo(expectedOrderDTO);
    }

    @Test
    void getOrderById_shouldThrowExceptionWhenOrderNotFound() {
        assertRuntimeExceptionWithMessage(() -> orderService.getOrderById(ORDER_ID), "Order with given orderId not found");
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
        List<OrderItemDTO> actualOrderItemDTOList = orderService.getAllOrderItemsByOrderId(ORDER_ID);
        List<OrderItemDTO> expectedOrderItemDTOList = List.of(getOrderItemDTOMock());
        assertThat(actualOrderItemDTOList).isEqualTo(expectedOrderItemDTOList);
    }

    @Test
    void getOrdersForCurrentUser_shouldGetAllOrderDTOsForCurrentUser() {
        mockJwtService_getCurrentUsername();
        addOrderToDatabase();
        List<OrderDTO> actualOrderDTOList = orderService.getOrdersForCurrentUser();
        List<OrderDTO> expectedOrderDTOList = List.of(getOrderDTOMock());

        assertThat(actualOrderDTOList).isEqualTo(expectedOrderDTOList);
    }

    @Test
    void getOrderItemsForCurrentUser_shouldGetAllOrderItemDTOsForCurrentUser() {
        mockJwtService_getCurrentUsername();
        addOrderToDatabase();
        List<OrderItemDTO> actualOrderItemDTOList = orderService.getOrderItemsForCurrentUser();
        List<OrderItemDTO> expectedOrderItemDTOList = List.of(getOrderItemDTOMock());

        assertThat(actualOrderItemDTOList).isEqualTo(expectedOrderItemDTOList);
    }

    @Test
    void getOrderItemsInOrderByOrderIdForCurrentUser_shouldGetAllOrderItemDTOsForCurrentUser() {
        addOrderToDatabase();
        mockJwtService_getCurrentUsername();
        List<OrderItemDTO> actualOrderItemDTOList = orderService.getOrderItemsInOrderByOrderIdForCurrentUser(ORDER_ID);
        List<OrderItemDTO> expectedOrderItemDTOList = List.of(getOrderItemDTOMock());

        assertThat(actualOrderItemDTOList).isEqualTo(expectedOrderItemDTOList);
    }

    @Test
    void getOrderItemsInOrderByOrderIdForCurrentUser_shouldThrowAccessDeniedExceptionWhenNotOwner() {
        addOrderToDatabase();
        userService.addUser(new UserRegisterDTO(OTHER_USERNAME, EMAIL, PASSWORD, ADDRESS));
        
        mockJwtService_getCurrentUsername_withWrongUsername();
        assertAccessDeniedException(() -> orderService.getOrderItemsInOrderByOrderIdForCurrentUser(USER_ID), 
                "You are not allowed to access this order");
    }

    @Test
    void validateOrderOwnership_shouldAllowAccessWhenUserOwnsOrder() {
        addOrderToDatabase();
        mockJwtService_getCurrentUsername();
        assertThatCode(() -> orderService.validateOrderOwnership(ORDER_ID))
                .doesNotThrowAnyException();
    }

    @Test
    void validateOrderOwnership_shouldThrowAccessDeniedExceptionWhenUserDoesNotOwnOrder() {
        addOrderToDatabase();
        
        mockJwtService_getCurrentUsername_withWrongUsername();
        userService.addUser(new UserRegisterDTO(OTHER_USERNAME, OTHER_EMAIL, PASSWORD, ADDRESS));
        
        assertAccessDeniedException(() -> orderService.validateOrderOwnership(USER_ID), 
                "You are not allowed to access this order");
    }
}