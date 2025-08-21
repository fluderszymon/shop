package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.dto.InvoiceDTO;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.entity.Order;
import com.szymonfluder.shop.entity.OrderItem;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.OrderItemMapper;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.repository.OrderItemRepository;
import com.szymonfluder.shop.repository.OrderRepository;
import com.szymonfluder.shop.service.OrderService;
import com.szymonfluder.shop.service.UserService;
import com.szymonfluder.shop.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    private static final int ORDER_ID = 1;
    private static final int USER_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final int ORDER_ITEM_ID = 1;
    private static final int QUANTITY = 2;
    private static final double PRICE = 25.00;
    private static final double ORDER_TOTAL = 50.00;
    private static final String USERNAME = "User";
    private static final String EMAIL = "user@outlook.com";
    private static final String PRODUCT_NAME = "Test Product";
    private static final String PRODUCT_DESCRIPTION = "Test Description";

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private OrderService orderService;
    @Mock private UserService userService;

    @InjectMocks private InvoiceServiceImpl invoiceService;

    private Order order;
    private OrderDTO orderDTO;
    private User user;
    private Product product;
    private OrderItem orderItem;
    private OrderItemDTO orderItemDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(USER_ID);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setAddress("Test Address");

        product = new Product();
        product.setProductId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setDescription(PRODUCT_DESCRIPTION);
        product.setPrice(PRICE);

        orderItem = new OrderItem();
        orderItem.setOrderItemId(ORDER_ITEM_ID);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(QUANTITY);

        orderItemDTO = new OrderItemDTO(ORDER_ITEM_ID, ORDER_ID, QUANTITY, PRODUCT_NAME, PRODUCT_ID, PRICE);

        order = new Order();
        order.setOrderId(ORDER_ID);
        order.setUser(user);
        order.setTotalPrice(ORDER_TOTAL);
        order.setOrderDate(LocalDate.now());

        orderDTO = new OrderDTO(ORDER_ID, USER_ID, ORDER_TOTAL, LocalDate.now());
        userDTO = new UserDTO(USER_ID, USERNAME, EMAIL, "USER", -1, "Test Address", 100.0);
    }

    @Test
    void createInvoiceDTO_shouldCreateInvoiceDTO() {
        when(orderService.getOrderById(ORDER_ID)).thenReturn(orderDTO);
        when(orderService.getAllOrderItemsByOrderId(ORDER_ID)).thenReturn(List.of(orderItemDTO));
        when(userService.getUserById(USER_ID)).thenReturn(userDTO);

        InvoiceDTO result = invoiceService.createInvoiceDTO(ORDER_ID);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUserName());
        assertEquals("Test Address", result.getUserAddress());
        assertEquals(ORDER_TOTAL, result.getTotalPrice());
        assertEquals(1, result.getOrderItemDTOList().size());
        assertEquals(orderItemDTO, result.getOrderItemDTOList().get(0));

        verify(orderService).getOrderById(ORDER_ID);
        verify(orderService).getAllOrderItemsByOrderId(ORDER_ID);
        verify(userService).getUserById(USER_ID);
    }

    @Test
    void createInvoiceDTO_shouldCreateInvoiceDTOWithMultipleOrderItems() {
        OrderItemDTO orderItemDTO2 = new OrderItemDTO(2, ORDER_ID, 1, "Product 2", 2, 15.0);
        List<OrderItemDTO> orderItems = List.of(orderItemDTO, orderItemDTO2);

        when(orderService.getOrderById(ORDER_ID)).thenReturn(orderDTO);
        when(orderService.getAllOrderItemsByOrderId(ORDER_ID)).thenReturn(orderItems);
        when(userService.getUserById(USER_ID)).thenReturn(userDTO);

        InvoiceDTO result = invoiceService.createInvoiceDTO(ORDER_ID);

        assertNotNull(result);
        assertEquals(2, result.getOrderItemDTOList().size());
        assertTrue(result.getOrderItemDTOList().contains(orderItemDTO));
        assertTrue(result.getOrderItemDTOList().contains(orderItemDTO2));

        verify(orderService).getOrderById(ORDER_ID);
        verify(orderService).getAllOrderItemsByOrderId(ORDER_ID);
        verify(userService).getUserById(USER_ID);
    }

    @Test
    void generateInvoicePDF_shouldGenerateInvoicePDF() throws IOException {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber("INV_001");
        invoiceDTO.setInvoiceDate(LocalDate.now());
        invoiceDTO.setTotalPrice(ORDER_TOTAL);
        invoiceDTO.setUserName(USERNAME);
        invoiceDTO.setUserAddress("Test Address");
        invoiceDTO.setOrderItemDTOList(new ArrayList<>(List.of(orderItemDTO)));

        ByteArrayOutputStream result = invoiceService.generateInvoicePdf(invoiceDTO);

        assertNotNull(result);
        assertTrue(result.size() > 0);
    }
}
