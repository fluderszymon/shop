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

    private OrderItem createOrderItem() {
        Order order = new Order();
        order.setOrderId(ORDER_ID);
        Product product = new Product();
        product.setProductId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);

        return new OrderItem(ORDER_ITEM_ID, order, product, QUANTITY, PRICE_AT_PURCHASE);
    }

    private OrderItemDTO createOrderItemDTO() {
        return new OrderItemDTO(ORDER_ITEM_ID, ORDER_ID, QUANTITY, PRODUCT_NAME, PRODUCT_ID, PRICE_AT_PURCHASE);
    }

    @Test
    void orderItemToOrderItemDTO_shouldMapOrderItemToOrderItemDTO() {
        OrderItem givenOrderItem = createOrderItem();
        OrderItemDTO expectedOrderItemDTO = createOrderItemDTO();
        OrderItemDTO mappedOrderItemDTO = orderItemMapper.orderItemToOrderItemDTO(givenOrderItem);

        assertThat(mappedOrderItemDTO).isEqualTo(expectedOrderItemDTO);
    }

    @Test
    void orderItemDTOToOrderItem_shouldMapOrderItemDTOToOrderItem() {
        OrderItemDTO givenOrderItemDTO = createOrderItemDTO();
        OrderItem expectedOrderItem = createOrderItem();
        OrderItem mappedOrderItem = orderItemMapper.orderItemDTOToOrderItem(givenOrderItemDTO);

        assertThat(mappedOrderItem).isEqualTo(expectedOrderItem);
    }
}