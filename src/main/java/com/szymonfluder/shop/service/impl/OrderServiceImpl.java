package com.szymonfluder.shop.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.szymonfluder.shop.dto.*;
import com.szymonfluder.shop.entity.*;
import com.szymonfluder.shop.mapper.OrderItemMapper;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.repository.*;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.OrderService;
import com.szymonfluder.shop.service.ProductService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.szymonfluder.shop.exception.OrderAccessDeniedException;
import com.szymonfluder.shop.exception.EntityNotFoundException;
import com.szymonfluder.shop.exception.EmptyCartException;
import com.szymonfluder.shop.exception.OutOfStockException;
import com.szymonfluder.shop.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
                            OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper,
                            CartService cartService, ProductService productService,
                            UserService userService, ProductRepository productRepository, 
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(orderMapper::orderToOrderDTO)
            .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(int orderId) {
        Order foundOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        return orderMapper.orderToOrderDTO(foundOrder);
    }

    @Override
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findAll()
                .stream()
                .map(orderItemMapper::orderItemToOrderItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemDTO> getAllOrderItemsByOrderId(int orderId) {
        return orderItemRepository.findAllOrderItemsByOrderId(orderId);
    }

    @Transactional
    @Override
    public void checkout() {
        UserDTO currentUser = userService.getCurrentUserDTO();
        int userId = currentUser.getUserId();
        int cartId = currentUser.getCartId();
        List<CartItemDTO> cartItemDTOList = cartService.getAllCartItemsByCartId(cartId);
        validateCartNotEmpty(cartItemDTOList);

        Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap = mapProductsDTOsToCartItemDTOs(cartItemDTOList);
        validateStockAvailability(productDTOCartItemDTOMap);

        BigDecimal userBalance = userService.getUserBalance(userId);
        BigDecimal cartTotal = cartService.getCartTotal(cartId);
        validateUserBalance(userBalance, cartTotal);

        processCheckout(userId, cartId, cartItemDTOList, productDTOCartItemDTOMap, userBalance, cartTotal);

        cleanupCart(userId, cartItemDTOList);
    }

    private void validateCartNotEmpty(List<CartItemDTO> cartItemDTOList) {
        if (cartItemDTOList.isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }
    }

    private void validateStockAvailability(Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap) {
        for (Map.Entry<ProductDTO, CartItemDTO> mapEntry : productDTOCartItemDTOMap.entrySet()) {
            if (mapEntry.getKey().getStock() < mapEntry.getValue().getQuantity()) {
                throw new OutOfStockException("Not enough products in stock");
            }
        }
    }

    private void validateUserBalance(BigDecimal userBalance, BigDecimal cartTotal) {
        if (cartTotal.compareTo(userBalance) > 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }

    @Transactional
    protected void processCheckout(int userId, int cartId, List<CartItemDTO> cartItemDTOList,
                                 Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap,
                                 BigDecimal userBalance, BigDecimal cartTotal) {

        productService.updateProductsStock(productDTOCartItemDTOMap);

        OrderDTO orderDTO = createOrder(userId, cartId);
        createOrderItemsFromCartItems(cartItemDTOList, orderDTO.getOrderId());

        BigDecimal newBalance = userBalance.subtract(cartTotal);
        userService.updateUserBalance(userId, newBalance);
    }

    private OrderDTO createOrder(int userId, int cartId) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(userId);
        orderDTO.setTotalPrice(cartService.getCartTotal(cartId));
        orderDTO.setOrderDate(LocalDate.now());
        Order savedOrder = orderRepository.save(orderMapper.orderDTOToOrder(orderDTO));
        return orderMapper.orderToOrderDTO(savedOrder);
    }

    private void createOrderItemsFromCartItems(List<CartItemDTO> cartItemDTOList, int orderId) {
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            addOrderItemFromCartItem(cartItemDTO, orderId);
        }
    }

    @Transactional
    protected void cleanupCart(int userId, List<CartItemDTO> cartItemDTOList) {
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            cartService.deleteCartItemById(cartItemDTO.getCartItemId());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        user.setCart(null);
    }

    private void addOrderItemFromCartItem(CartItemDTO cartItemDTO, int orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        Product product = productRepository.findById(cartItemDTO.getProductId()).orElseThrow(() -> new EntityNotFoundException("Product", cartItemDTO.getProductId()));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItemDTO.getQuantity());
        orderItem.setPriceAtPurchase(product.getPrice());

        orderItemRepository.save(orderItem);
    }

    private List<Integer> extractProductIdsFromCartItemDTOList(List<CartItemDTO> cartItemDTOList) {
        if (cartItemDTOList.isEmpty()) {throw new EmptyCartException("cartItemDTOList is empty");}
        ArrayList<Integer> productIds = new ArrayList<>();
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            productIds.add(cartItemDTO.getProductId());
        }
        return productIds;
    }

    private Map<ProductDTO, CartItemDTO> mapProductsDTOsToCartItemDTOs(List<CartItemDTO> cartItemDTOList) {
        List<Integer> cartItemProductIds = extractProductIdsFromCartItemDTOList(cartItemDTOList);
        List<ProductDTO> productDTOList = productService.getProductsByIdList(cartItemProductIds);

        Map<Integer, CartItemDTO> cartItemByProductId = new HashMap<>();
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            cartItemByProductId.put(cartItemDTO.getProductId(), cartItemDTO);
        }

        Map<ProductDTO, CartItemDTO> result = new HashMap<>();
        for (ProductDTO productDTO : productDTOList) {
            CartItemDTO cartItemDTO = cartItemByProductId.get(productDTO.getProductId());
            if (cartItemDTO != null) {
                result.put(productDTO, cartItemDTO);
            }
        }
        return result;
    }

    @Override
    public List<OrderDTO> getOrdersForCurrentUser() {
        UserDTO currentUser = userService.getCurrentUserDTO();
        return orderRepository.findAllOrdersByUserId(currentUser.getUserId())
                .stream()
                .map(orderMapper::orderToOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemDTO> getOrderItemsInOrderByOrderIdForCurrentUser(int orderId) {
        validateOrderOwnership(orderId);
        return orderItemRepository.findAllOrderItemsInOrderByOrderId(orderId)
                .stream()
                .map(orderItemMapper::orderItemToOrderItemDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderItemDTO> getOrderItemsForCurrentUser() {
        UserDTO currentUser = userService.getCurrentUserDTO();
        return orderItemRepository.findAllOrderItemsByUserId(currentUser.getUserId())
                .stream()
                .map(orderItemMapper::orderItemToOrderItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void validateOrderOwnership(int orderId) {
        UserDTO currentUser = userService.getCurrentUserDTO();
        boolean isOwner = orderRepository.findAllOrdersByUserId(currentUser.getUserId())
                .stream()
                .anyMatch(order -> order.getOrderId() == orderId);
                
        if (!isOwner) {
            throw new OrderAccessDeniedException("You are not allowed to access this order");
        }
    }
}