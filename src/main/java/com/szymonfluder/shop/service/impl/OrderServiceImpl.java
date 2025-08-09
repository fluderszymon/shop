package com.szymonfluder.shop.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.entity.Order;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.repository.OrderRepository;
import com.szymonfluder.shop.repository.ProductRepository;
import com.szymonfluder.shop.repository.UserRepository;
import com.szymonfluder.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemService orderItemService;
    private final CartService cartService;
    private final ProductService productService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
                            OrderItemService orderItemService, CartService cartService,
                            ProductService productService, CartItemService cartItemService,
                            UserService userService, UserMapper userMapper, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderItemService = orderItemService;
        this.cartService = cartService;
        this.productService = productService;
        this.cartItemService = cartItemService;
        this.userService = userService;
        this.userMapper = userMapper;
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
                .orElseThrow(() -> new RuntimeException("Order with given orderId not found"));
        return orderMapper.orderToOrderDTO(foundOrder);
    }

    @Override
    public OrderDTO addOrder(int userId, int cartId) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(userId);
        orderDTO.setTotalPrice(cartService.getCartTotal(cartId));
        orderDTO.setOrderDate(LocalDate.now());
        Order savedOrder = orderRepository.save(orderMapper.orderDTOToOrder(orderDTO));
        return orderMapper.orderToOrderDTO(savedOrder);
    }

    @Transactional
    @Override
    public void checkout(int userId, int cartId) {
        List<CartItemDTO> cartItemDTOList = cartItemService.getAllCartItemsByCartId(cartId);
        List<Integer> cartItemProductIds = getCartItemProductIds(cartItemDTOList);
        List<ProductDTO> productDTOList = productService.getProdutsByIdList(cartItemProductIds);
        Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap = getProductDTOCartItemDTOMap(productDTOList, cartItemDTOList);
        boolean isEnoughInProductQuantity = isEnoughInStock(productDTOCartItemDTOMap);
        double userBalance = userService.getUserById(userId).getBalance();
        double cartTotal = cartService.getCartTotal(cartId);
        boolean isAbleToPay = cartTotal <= userBalance;

        if (isEnoughInProductQuantity && isAbleToPay) {
            productService.updateProducts(productDTOCartItemDTOMap);
            OrderDTO orderDTO = addOrder(userId, cartId);
            for (CartItemDTO cartItemDTO : cartItemDTOList) {
                orderItemService.addOrderItemFromCartItem(cartItemDTO, orderDTO.getOrderId());
            }
            for (CartItemDTO cartItemDTO : cartItemDTOList) {
                cartItemService.deleteCartItemById(cartItemDTO.getCartItemId());
            }

            double newBalance = userBalance - cartTotal;
            userService.updateUserBalance(userId, newBalance);

            cartService.deleteCartById(cartId);
        } else {
            throw new RuntimeException("Not enough product in stock");
        }
    }

    private Map<ProductDTO, CartItemDTO> getProductDTOCartItemDTOMap(List<ProductDTO> productDTOList, List<CartItemDTO> cartItemDTOList) {
        Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap = new HashMap<>();
        for (ProductDTO productDTO : productDTOList) {
            for (CartItemDTO cartItemDTO : cartItemDTOList) {
                if (cartItemDTO.getProductId() == productDTO.getProductId()) {
                    productDTOCartItemDTOMap.put(productDTO, cartItemDTO);
                }
            }
        }
        return productDTOCartItemDTOMap;
    }

    private List<Integer> getCartItemProductIds(List<CartItemDTO> cartItemDTOList) {
        ArrayList<Integer> cartItemProductIds = new ArrayList<>();
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            cartItemProductIds.add(cartItemDTO.getProductId());
        }
        return cartItemProductIds;
    }

    private boolean isEnoughInStock(Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap) {
        boolean isEnoughInStock = true;
        for (Map.Entry<ProductDTO, CartItemDTO> mapEntry : productDTOCartItemDTOMap.entrySet()) {
            if (mapEntry.getKey().getStock() < mapEntry.getValue().getQuantity()) {
                isEnoughInStock = false;
                break;
            }
        }
        return isEnoughInStock;
    }
}
