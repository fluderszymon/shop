package com.szymonfluder.shop.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.dto.OrderDTO;
import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.dto.ProductDTO;
import com.szymonfluder.shop.entity.Order;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.mapper.ProductMapper;
import com.szymonfluder.shop.repository.OrderRepository;
import com.szymonfluder.shop.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
                            OrderItemService orderItemService, CartService cartService,
                            ProductService productService, CartItemService cartItemService,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderItemService = orderItemService;
        this.cartService = cartService;
        this.productService = productService;
        this.cartItemService = cartItemService;
        this.productRepository = productRepository;
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
        Order foundOrder = orderRepository.findById(orderId).orElse(null);
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

        // get product list by getting product by ids
        ArrayList<Integer> cartItemProductIds = new ArrayList<>();
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            cartItemProductIds.add(cartItemDTO.getProductId());
        }
        List<ProductDTO> productDTOList = productService.getProdutsByIdList(cartItemProductIds);

        // create map containing pairs of ProductDTO and CartItemDTO paired by the same productId
        Map<ProductDTO, CartItemDTO> productDTOCartItemDTOMap = new HashMap<>();
        for (ProductDTO productDTO : productDTOList) {
            for (CartItemDTO cartItemDTO : cartItemDTOList) {
                if (cartItemDTO.getProductId() == productDTO.getProductId()) {
                    productDTOCartItemDTOMap.put(productDTO, cartItemDTO);
                }
            }
        }

        // check if there is enough of stock for each cartItem in user's cart
        boolean isEnoughInProductQuantity = true;
        for (Map.Entry<ProductDTO, CartItemDTO> mapEntry : productDTOCartItemDTOMap.entrySet()) {
            if (mapEntry.getKey().getStock() < mapEntry.getValue().getQuantity()) {
                isEnoughInProductQuantity = false;
                break;
            }
        }




//
//         update products quantities in Product table
        List<Product> updatedProductList = new ArrayList<>();
        for (Map.Entry<ProductDTO, CartItemDTO> mapEntry : productDTOCartItemDTOMap.entrySet()) {
            Product updatedProduct = productRepository.findById(mapEntry.getKey().getProductId()).orElse(new Product());
            updatedProduct.setProductId(mapEntry.getKey().getProductId());
            updatedProduct.setName(mapEntry.getKey().getName());
            updatedProduct.setDescription(mapEntry.getKey().getDescription());
            updatedProduct.setPrice(mapEntry.getKey().getPrice());
            updatedProduct.setStock(mapEntry.getKey().getStock()-mapEntry.getValue().getQuantity());
            updatedProductList.add(updatedProduct);
            productService.updateProduct(updatedProduct);
        }

//        for (Product product : updatedProductList) {
//            productService.updateProduct(product);
//        }


        // add Order
        OrderDTO orderDTO = addOrder(userId, cartId);


        // add orderItems
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            orderItemService.addOrderItemFromCartItem(cartItemDTO, orderDTO.getOrderId());
        }

        // delete cart and cartItems from database
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            cartItemService.deleteCartItemById(cartItemDTO.getCartItemId());
        }
        cartService.deleteCartById(cartId);

    }

    @Override
    public double getOrderTotal(int orderId) {
        List<OrderItemDTO> orderItemDTOs = orderItemService.getAllOrderItemsByOrderId(orderId);
        double total = 0;
        for (OrderItemDTO orderItemDTO : orderItemDTOs) {
            total += orderItemDTO.getQuantity() * orderItemDTO.getPriceAtPurchase();
        }
        return total;
    }
}
