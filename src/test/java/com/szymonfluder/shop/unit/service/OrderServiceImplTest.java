package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.*;
import com.szymonfluder.shop.mapper.OrderItemMapper;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.repository.*;
import com.szymonfluder.shop.service.CartItemService;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.ProductService;
import com.szymonfluder.shop.service.UserService;
import com.szymonfluder.shop.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private static final int USER_ID = 1;
    private static final int CART_ID = 1;
    private static final int ORDER_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final int ORDER_ITEM_ID = 1;
    private static final int PRODUCT_QUANTITY = 1;
    private static final double PRODUCT_PRICE = 10.00;
    private static final double USER_BALANCE = 100.00;
    private static final double CART_TOTAL_GREATER_THAN_BALANCE = 100.01;
    private static final String PRODUCT_NAME = "Test Product";
    private static final String PRODUCT_DESCRIPTION = "Test Product Description";

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private CartService cartService;
    @Mock private ProductService productService;
    @Mock private CartItemService cartItemService;
    @Mock private UserService userService;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;

    @InjectMocks private OrderServiceImpl orderService;

    private CartItemDTO cartItemDTO;
    private ProductDTO productDTO;
    private OrderDTO orderDTO;
    private Order order;
    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        cartItemDTO = new CartItemDTO(ORDER_ITEM_ID, CART_ID, PRODUCT_ID, 4);
        productDTO = new ProductDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, 2);
        orderDTO = new OrderDTO(ORDER_ID, USER_ID, 20.00, LocalDate.now());
        
        order = new Order();
        order.setOrderId(ORDER_ID);
        
        product = new Product();
        product.setProductId(PRODUCT_ID);
        
        user = new User();
        user.setUserId(USER_ID);
    }

    @Nested
    @DisplayName("Checkout Tests")
    class CheckoutTests {

        @Test
        void checkout_shouldCompleteCheckoutSuccessfully() {
            CartItemDTO testCartItem = new CartItemDTO(ORDER_ITEM_ID, CART_ID, PRODUCT_ID, 2);
            ProductDTO testProduct = new ProductDTO(PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, 5);
            Order testOrder = new Order();
            testOrder.setOrderId(ORDER_ID);
            User testUser = new User();
            testUser.setUserId(USER_ID);

            when(cartItemService.getAllCartItemsByCartId(CART_ID)).thenReturn(List.of(testCartItem));
            when(productService.getProductsByIdList(any())).thenReturn(List.of(testProduct));
            when(userService.getUserBalance(USER_ID)).thenReturn(50.00);
            when(cartService.getCartTotal(CART_ID)).thenReturn(50.00);
            when(orderMapper.orderDTOToOrder(any(OrderDTO.class))).thenReturn(testOrder);
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
            when(orderMapper.orderToOrderDTO(any(Order.class))).thenReturn(orderDTO);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            orderService.checkout(USER_ID, CART_ID);
            
            verify(cartItemService).getAllCartItemsByCartId(CART_ID);
            verify(productService).getProductsByIdList(any());
            verify(userService).getUserBalance(USER_ID);
            verify(cartService, times(2)).getCartTotal(CART_ID);
            verify(productService).updateProductsStock(any());
            verify(orderRepository).save(any(Order.class));
            verify(userService).updateUserBalance(USER_ID, 0.0);
            verify(cartItemService).deleteCartItemById(ORDER_ITEM_ID);
            verify(cartRepository).deleteById(CART_ID);
        }

        @Test
        void checkout_shouldCompleteCheckoutWithMultipleProducts() {
            CartItemDTO cartItem1 = new CartItemDTO(1, CART_ID, 1, 2);
            CartItemDTO cartItem2 = new CartItemDTO(2, CART_ID, 2, 3);
            ProductDTO product1 = new ProductDTO(1, "Product 1", "Description 1", 10.0, 5);
            ProductDTO product2 = new ProductDTO(2, "Product 2", "Description 2", 15.0, 10);
            
            Order testOrder = new Order();
            testOrder.setOrderId(ORDER_ID);
            Product productEntity1 = new Product();
            productEntity1.setProductId(1);
            Product productEntity2 = new Product();
            productEntity2.setProductId(2);

            when(cartItemService.getAllCartItemsByCartId(CART_ID)).thenReturn(List.of(cartItem1, cartItem2));
            when(productService.getProductsByIdList(any())).thenReturn(List.of(product1, product2));
            when(userService.getUserBalance(USER_ID)).thenReturn(USER_BALANCE);
            when(cartService.getCartTotal(CART_ID)).thenReturn(65.0);
            when(orderMapper.orderDTOToOrder(any(OrderDTO.class))).thenReturn(testOrder);
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
            when(orderMapper.orderToOrderDTO(any(Order.class))).thenReturn(orderDTO);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(testOrder));
            when(productRepository.findById(1)).thenReturn(Optional.of(productEntity1));
            when(productRepository.findById(2)).thenReturn(Optional.of(productEntity2));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

            orderService.checkout(USER_ID, CART_ID);

            verify(cartItemService).getAllCartItemsByCartId(CART_ID);
            verify(productService).getProductsByIdList(any());
            verify(userService).getUserBalance(USER_ID);
            verify(cartService, times(2)).getCartTotal(CART_ID);
            verify(productService).updateProductsStock(any());
            verify(orderRepository).save(any(Order.class));
            verify(userService).updateUserBalance(USER_ID, 35.0);
            verify(cartItemService).deleteCartItemById(1);
            verify(cartItemService).deleteCartItemById(2);
            verify(cartRepository).deleteById(CART_ID);
        }

        @Test
        void checkout_shouldThrowWhenBalanceIsInsufficient() {
            when(cartItemService.getAllCartItemsByCartId(CART_ID)).thenReturn(List.of(cartItemDTO));
            when(userService.getUserBalance(USER_ID)).thenReturn(USER_BALANCE);
            when(cartService.getCartTotal(CART_ID)).thenReturn(CART_TOTAL_GREATER_THAN_BALANCE);

            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> orderService.checkout(USER_ID, CART_ID));
        assertEquals("Insufficient balance", exception.getMessage());
    }

        @Test
        void checkout_shouldThrowExceptionWhenCartIsEmpty() {
            when(cartItemService.getAllCartItemsByCartId(CART_ID)).thenReturn(List.of());

            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> orderService.checkout(USER_ID, CART_ID));
            assertEquals("Cart is empty", exception.getMessage());
        }

        @Test
        void checkout_shouldThrowExceptionWhenStockIsInsufficient() {
            when(cartItemService.getAllCartItemsByCartId(CART_ID)).thenReturn(List.of(cartItemDTO));
            when(productService.getProductsByIdList(any())).thenReturn(List.of(productDTO));

            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout(USER_ID, CART_ID));
            assertEquals("Not enough products in stock", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Order Management Tests")
    class OrderManagementTests {

        @Test
        void getAllOrders_shouldGetAllOrderDTOs() {
            List<Order> orders = List.of(order);
            List<OrderDTO> expectedOrderDTOs = List.of(orderDTO);

            when(orderRepository.findAll()).thenReturn(orders);
            when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

            List<OrderDTO> result = orderService.getAllOrders();

            assertEquals(expectedOrderDTOs, result);
            verify(orderRepository).findAll();
            verify(orderMapper).orderToOrderDTO(order);
        }

        @Test
        void getOrderById_shouldGetOrderDTO() {
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

            OrderDTO result = orderService.getOrderById(ORDER_ID);

            assertEquals(orderDTO, result);
            verify(orderRepository).findById(ORDER_ID);
            verify(orderMapper).orderToOrderDTO(order);
        }

        @Test
        void getOrderById_shouldThrowExceptionWhenOrderNotFound() {
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> orderService.getOrderById(ORDER_ID));
            assertEquals("Order with given orderId not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Order Item Tests")
    class OrderItemTests {

        @Test
        void getAllOrderItems_shouldGetAllOrderItemDTOs() {
            OrderItem orderItem = new OrderItem();
            OrderItemDTO orderItemDTO = new OrderItemDTO(ORDER_ITEM_ID, ORDER_ID, PRODUCT_QUANTITY, PRODUCT_NAME, PRODUCT_ID, PRODUCT_PRICE);
            List<OrderItem> orderItems = List.of(orderItem);
            List<OrderItemDTO> orderItemDTOs = List.of(orderItemDTO);

            when(orderItemRepository.findAll()).thenReturn(orderItems);
            when(orderItemMapper.orderItemToOrderItemDTO(orderItem)).thenReturn(orderItemDTO);

            List<OrderItemDTO> result = orderService.getAllOrderItems();

            assertEquals(orderItemDTOs, result);
            verify(orderItemRepository).findAll();
            verify(orderItemMapper).orderItemToOrderItemDTO(orderItem);
        }

        @Test
        void getAllOrderItemsByOrderId_shouldGetAllOrderItemDTOsByOrderId() {
            OrderItemDTO orderItemDTO = new OrderItemDTO(ORDER_ITEM_ID, ORDER_ID, PRODUCT_QUANTITY, PRODUCT_NAME, PRODUCT_ID, PRODUCT_PRICE);
            List<OrderItemDTO> expectedOrderItemDTOs = List.of(orderItemDTO);

            when(orderItemRepository.findAllOrderItemsByOrderId(ORDER_ID)).thenReturn(expectedOrderItemDTOs);

            List<OrderItemDTO> result = orderService.getAllOrderItemsByOrderId(ORDER_ID);

            assertEquals(expectedOrderItemDTOs, result);
            verify(orderItemRepository).findAllOrderItemsByOrderId(ORDER_ID);
        }
    }    
}
