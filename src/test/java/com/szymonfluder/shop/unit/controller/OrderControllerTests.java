package com.szymonfluder.shop.unit.controller;

import com.szymonfluder.shop.controller.OrderController;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getAllOrders_shouldReturnAllOrders() throws Exception {
        List<OrderDTO> orders = Arrays.asList(
            new OrderDTO(1, 1, 99.99, LocalDate.of(2024, 1, 15)),
            new OrderDTO(2, 2, 149.99, LocalDate.of(2024, 1, 16))
        );
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].totalPrice").value(99.99))
                .andExpect(jsonPath("$[0].orderDate").value("2024-01-15"))
                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].totalPrice").value(149.99))
                .andExpect(jsonPath("$[1].orderDate").value("2024-01-16"));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getAllOrders_shouldReturnEmptyList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getOrderById_shouldReturnOrder() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, 1, 99.99, LocalDate.of(2024, 1, 15));
        when(orderService.getOrderById(1)).thenReturn(orderDTO);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalPrice").value(99.99))
                .andExpect(jsonPath("$.orderDate").value("2024-01-15"));

        verify(orderService, times(1)).getOrderById(1);
    }

    @Test
    void getAllOrderItems_shouldReturnAllOrderItems() throws Exception {
        List<OrderItemDTO> orderItems = Arrays.asList(
            new OrderItemDTO(1, 1, 2, "Product 1", 1, 19.99),
            new OrderItemDTO(2, 1, 1, "Product 2", 2, 29.99)
        );
        when(orderService.getAllOrderItems()).thenReturn(orderItems);

        mockMvc.perform(get("/orders/order-items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderItemId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].productName").value("Product 1"))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].priceAtPurchase").value(19.99))
                .andExpect(jsonPath("$[1].orderItemId").value(2))
                .andExpect(jsonPath("$[1].orderId").value(1))
                .andExpect(jsonPath("$[1].quantity").value(1))
                .andExpect(jsonPath("$[1].productName").value("Product 2"))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].priceAtPurchase").value(29.99));

        verify(orderService, times(1)).getAllOrderItems();
    }

    @Test
    void getAllOrderItems_shouldReturnEmptyList() throws Exception {
        when(orderService.getAllOrderItems()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/orders/order-items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getAllOrderItems();
    }

    @Test
    void getOrderItemsInOrderByOrderId_shouldReturnOrderItems() throws Exception {
        List<OrderItemDTO> orderItems = Arrays.asList(
            new OrderItemDTO(1, 1, 2, "Product 1", 1, 19.99),
            new OrderItemDTO(2, 1, 1, "Product 2", 2, 29.99)
        );
        when(orderService.getAllOrderItemsByOrderId(1)).thenReturn(orderItems);

        mockMvc.perform(get("/orders/1/order-items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderItemId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].productName").value("Product 1"))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].priceAtPurchase").value(19.99))
                .andExpect(jsonPath("$[1].orderItemId").value(2))
                .andExpect(jsonPath("$[1].orderId").value(1))
                .andExpect(jsonPath("$[1].quantity").value(1))
                .andExpect(jsonPath("$[1].productName").value("Product 2"))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].priceAtPurchase").value(29.99));

        verify(orderService, times(1)).getAllOrderItemsByOrderId(1);
    }

    @Test
    void getOrderItemsInOrderByOrderId_shouldReturnEmptyList() throws Exception {
        when(orderService.getAllOrderItemsByOrderId(1)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/orders/1/order-items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getAllOrderItemsByOrderId(1);
    }

    @Test
    void checkout_shouldProcessCheckout() throws Exception {
        doNothing().when(orderService).checkout(1, 1);

        mockMvc.perform(post("/orders/checkout/1/1"))
                .andExpect(status().isOk());

        verify(orderService, times(1)).checkout(1, 1);
    }
}
