package com.szymonfluder.shop.unit.mapper;

import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.entity.Order;
import com.szymonfluder.shop.entity.OrderItem;
import com.szymonfluder.shop.entity.Product;
import com.szymonfluder.shop.mapper.OrderItemMapper;
import com.szymonfluder.shop.mapper.OrderItemMapperImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderItemMapperTests {

    private final int ORDER_ITEM_ID = 1;
    private final int ORDER_ID = 1;
    private final int PRODUCT_ID = 1;
    private final String PRODUCT_NAME = "Test Product";
    private final int QUANTITY = 10;
    private final double PRICE_AT_PURCHASE = 10.00;
    
    private final OrderItemMapper orderItemMapper = new OrderItemMapperImpl();

    @Test
    void orderItemToOrderItemDTO_shouldMapOrderItemToOrderItemDTO() {
        Order order = new Order();
        order.setOrderId(ORDER_ID);
        
        Product product = new Product();
        product.setProductId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(ORDER_ITEM_ID);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(QUANTITY);
        orderItem.setPriceAtPurchase(PRICE_AT_PURCHASE);

        OrderItemDTO result = orderItemMapper.orderItemToOrderItemDTO(orderItem);

        assertThat(result).isNotNull();
        assertThat(result.getOrderItemId()).isEqualTo(ORDER_ITEM_ID);
        assertThat(result.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(result.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(result.getProductName()).isEqualTo(PRODUCT_NAME);
        assertThat(result.getQuantity()).isEqualTo(QUANTITY);
        assertThat(result.getPriceAtPurchase()).isEqualTo(PRICE_AT_PURCHASE);
    }

    @Test
    void orderItemDTOToOrderItem_shouldMapOrderItemDTOToOrderItem() {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderItemId(ORDER_ITEM_ID);
        orderItemDTO.setOrderId(ORDER_ID);
        orderItemDTO.setProductId(PRODUCT_ID);
        orderItemDTO.setProductName(PRODUCT_NAME);
        orderItemDTO.setQuantity(QUANTITY);
        orderItemDTO.setPriceAtPurchase(PRICE_AT_PURCHASE);

        OrderItem result = orderItemMapper.orderItemDTOToOrderItem(orderItemDTO);

        assertThat(result).isNotNull();
        assertThat(result.getOrderItemId()).isEqualTo(ORDER_ITEM_ID);
        assertThat(result.getOrder()).isNotNull();
        assertThat(result.getOrder().getOrderId()).isEqualTo(ORDER_ID);
        assertThat(result.getProduct()).isNotNull();
        assertThat(result.getProduct().getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(result.getProduct().getName()).isEqualTo(PRODUCT_NAME);
        assertThat(result.getQuantity()).isEqualTo(QUANTITY);
        assertThat(result.getPriceAtPurchase()).isEqualTo(PRICE_AT_PURCHASE);
    }

    @Test
    void orderItemToOrderItemDTO_shouldReturnNullWhenOrderItemIsNull() {
        OrderItemDTO result = orderItemMapper.orderItemToOrderItemDTO(null);

        assertThat(result).isNull();
    }

    @Test
    void orderItemDTOToOrderItem_shouldReturnNullWhenOrderItemDTOIsNull() {
        OrderItem result = orderItemMapper.orderItemDTOToOrderItem(null);

        assertThat(result).isNull();
    }
}