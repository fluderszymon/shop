package com.szymonfluder.shop.service.impl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.szymonfluder.shop.dto.*;
import com.szymonfluder.shop.entity.*;
import com.szymonfluder.shop.mapper.OrderItemMapper;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.repository.*;
import com.szymonfluder.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
                            OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper,
                            CartService cartService, ProductService productService,
                            CartItemService cartItemService, UserService userService,
                            ProductRepository productRepository, UserRepository userRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.cartService = cartService;
        this.productService = productService;
        this.cartItemService = cartItemService;
        this.userService = userService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
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
                .orElseThrow(() -> new RuntimeException("Order with given orderId not found"));
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
    public void checkout(int userId, int cartId) {
        List<CartItemDTO> cartItemDTOList = cartItemService.getAllCartItemsByCartId(cartId);
        validateCartNotEmpty(cartItemDTOList);

        Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap = mapProductsDTOsToCartItemDTOs(cartItemDTOList);
        validateStockAvailability(productDTOCartItemDTOMap);

        double userBalance = userService.getUserBalance(userId);
        double cartTotal = cartService.getCartTotal(cartId);
        validateUserBalance(userBalance, cartTotal);

        processCheckout(userId, cartId, cartItemDTOList, productDTOCartItemDTOMap, userBalance, cartTotal);

        cleanupCart(cartId, userId, cartItemDTOList);
    }

    private void validateCartNotEmpty(List<CartItemDTO> cartItemDTOList) {
        if (cartItemDTOList.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
    }

    private void validateStockAvailability(Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap) {
        for (Map.Entry<ProductDTO, CartItemDTO> mapEntry : productDTOCartItemDTOMap.entrySet()) {
            if (mapEntry.getKey().getStock() < mapEntry.getValue().getQuantity()) {
                throw new RuntimeException("Not enough products in stock");
            }
        }
    }

    private void validateUserBalance(double userBalance, double cartTotal) {
        if (cartTotal > userBalance) {
            throw new RuntimeException("Insufficient balance");
        }
    }

    @Transactional
    public void processCheckout(int userId, int cartId, List<CartItemDTO> cartItemDTOList,
                                 Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap,
                                 double userBalance, double cartTotal) {

        productService.updateProducts(productDTOCartItemDTOMap);

        OrderDTO orderDTO = createOrder(userId, cartId);
        createOrderItemsFromCartItems(cartItemDTOList, orderDTO.getOrderId());

        double newBalance = userBalance - cartTotal;
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
    public void cleanupCart(int cartId, int userId, List<CartItemDTO> cartItemDTOList) {
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            cartItemService.deleteCartItemById(cartItemDTO.getCartItemId());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setCart(null);
        cartRepository.deleteById(cartId);
    }

    private void addOrderItemFromCartItem(CartItemDTO cartItemDTO, int orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Product product = productRepository.findById(cartItemDTO.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItemDTO.getQuantity());
        orderItem.setPriceAtPurchase(product.getPrice());

        orderItemRepository.save(orderItem);
    }

    private List<Integer> extractProductIdsFromCartItemDTOList(List<CartItemDTO> cartItemDTOList) {
        if (cartItemDTOList.isEmpty()) {throw new RuntimeException("cartItemDTOList is empty");}
        ArrayList<Integer> productIds = new ArrayList<>();
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            productIds.add(cartItemDTO.getProductId());
        }
        return productIds;
    }

    private Map<ProductDTO, CartItemDTO> mapProductsDTOsToCartItemDTOs(List<CartItemDTO> cartItemDTOList) {

        List<Integer> cartItemProductIds = extractProductIdsFromCartItemDTOList(cartItemDTOList);
        List<ProductDTO> productDTOList = productService.getProdutsByIdList(cartItemProductIds);

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
}
