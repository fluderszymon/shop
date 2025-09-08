package com.szymonfluder.shop.unit.controller;

import com.szymonfluder.shop.controller.OrderController;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.security.RateLimitService;
import com.szymonfluder.shop.security.SecurityConfig;
import com.szymonfluder.shop.security.UserDetailsServiceImpl;
import com.szymonfluder.shop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        setupJwtMocksWithTokenExtraction(jwtService, userDetailsService);
        setupRateLimitMocks(rateLimitService);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getAllOrders_shouldReturnAllOrders() throws Exception {
        List<OrderDTO> orders = List.of(new OrderDTO(1, 1, 99.99, LocalDate.of(2024, 1, 15)));
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getAllOrders_shouldReturnEmptyList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of());

        mockMvc.perform(get("/orders")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getOrderById_shouldReturnOrder() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, 1, 99.99, LocalDate.of(2024, 1, 15));
        when(orderService.getOrderById(1)).thenReturn(orderDTO);

        mockMvc.perform(get("/orders/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        verify(orderService, times(1)).getOrderById(1);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getAllOrderItems_shouldReturnAllOrderItems() throws Exception {
        List<OrderItemDTO> orderItems = List.of(new OrderItemDTO(1, 2, 1, "Product", 2, 29.99));
        when(orderService.getAllOrderItems()).thenReturn(orderItems);

        mockMvc.perform(get("/orders/order-items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderItemId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(2));

        verify(orderService, times(1)).getAllOrderItems();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getAllOrderItems_shouldReturnEmptyList() throws Exception {
        when(orderService.getAllOrderItems()).thenReturn(List.of());

        mockMvc.perform(get("/orders/order-items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getAllOrderItems();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getOrderItemsInOrderByOrderId_shouldReturnOrderItems() throws Exception {
        List<OrderItemDTO> orderItems = List.of(new OrderItemDTO(1, 1, 2, "Product 1", 1, 19.99));
        when(orderService.getAllOrderItemsByOrderId(1)).thenReturn(orderItems);

        mockMvc.perform(get("/orders/1/order-items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderItemId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1));

        verify(orderService, times(1)).getAllOrderItemsByOrderId(1);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void getOrderItemsInOrderByOrderId_shouldReturnEmptyList() throws Exception {
        when(orderService.getAllOrderItemsByOrderId(1)).thenReturn(List.of());

        mockMvc.perform(get("/orders/1/order-items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getAllOrderItemsByOrderId(1);
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void checkout_shouldProcessCheckout() throws Exception {
        doNothing().when(orderService).checkout();

        mockMvc.perform(post("/orders/checkout")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk());

        verify(orderService, times(1)).checkout();
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void getMyOrders_shouldReturnUserOrders() throws Exception {
        List<OrderDTO> orders = List.of(new OrderDTO(1, 1, 99.99, LocalDate.of(2024, 1, 15)));
        when(orderService.getOrdersForCurrentUser()).thenReturn(orders);

        mockMvc.perform(get("/orders/my-orders")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(orderService, times(1)).getOrdersForCurrentUser();
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void getOrderItemsInMyOrder_shouldReturnOrderItems() throws Exception {
        List<OrderItemDTO> orderItems = List.of(new OrderItemDTO(1, 1, 2, "Product 1", 1, 19.99));
        when(orderService.getOrderItemsInOrderByOrderIdForCurrentUser(1)).thenReturn(orderItems);

        mockMvc.perform(get("/orders/my-orders/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderItemId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1));

        verify(orderService, times(1)).getOrderItemsInOrderByOrderIdForCurrentUser(1);
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void getMyOrderItems_shouldReturnUserOrderItems() throws Exception {
        List<OrderItemDTO> orderItems = List.of(new OrderItemDTO(1, 1, 2, "Product 1", 1, 19.99));
        when(orderService.getOrderItemsForCurrentUser()).thenReturn(orderItems);

        mockMvc.perform(get("/orders/my-orders/order-items")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderItemId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1));

        verify(orderService, times(1)).getOrderItemsForCurrentUser();
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void getOrderItemsInMyOrder_shouldThrowAccessDeniedException() throws Exception {
        when(orderService.getOrderItemsInOrderByOrderIdForCurrentUser(999))
                .thenThrow(new org.springframework.security.access.AccessDeniedException("You are not allowed to access this order"));

        mockMvc.perform(get("/orders/my-orders/999")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isForbidden());

        verify(orderService, times(1)).getOrderItemsInOrderByOrderIdForCurrentUser(999);
    }
}