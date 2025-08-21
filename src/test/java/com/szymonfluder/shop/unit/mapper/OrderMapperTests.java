package com.szymonfluder.shop.unit.mapper;

import java.time.LocalDate;
import java.util.ArrayList;

import com.szymonfluder.shop.entity.Order;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.mapper.OrderMapperImpl;
import com.szymonfluder.shop.dto.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderMapperTests {

    private final int USER_ID = 1;
    private final int ORDER_ID = 1;
    private final double TOTAL_PRICE = 100.0;
    private final LocalDate ORDER_DATE = LocalDate.of(2020, 1, 13);
    
    private final OrderMapper orderMapper = new OrderMapperImpl();
    
    private User user;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(USER_ID);

        order = new Order(ORDER_ID, user, new ArrayList<>(), TOTAL_PRICE, ORDER_DATE);

        orderDTO = new OrderDTO(ORDER_ID, USER_ID, TOTAL_PRICE, ORDER_DATE);
    }

    @Test
    void orderToOrderDTO_shouldMapOrderToOrderDTO() {   
        OrderDTO result = orderMapper.orderToOrderDTO(order);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderDTO.getOrderId());
        assertThat(result.getUserId()).isEqualTo(orderDTO.getUserId());
        assertThat(result.getTotalPrice()).isEqualTo(orderDTO.getTotalPrice());
        assertThat(result.getOrderDate()).isEqualTo(orderDTO.getOrderDate());
    }

    @Test
    void orderToOrderDTO_shouldReturnNullWhenOrderIsNull() {
        OrderDTO result = orderMapper.orderToOrderDTO(null);

        assertThat(result).isNull();
    }

    @Test
    void orderDTOToOrder_shouldMapOrderDTOToOrder() {
        Order result = orderMapper.orderDTOToOrder(orderDTO);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getUserId()).isEqualTo(order.getUser().getUserId());
        assertThat(result.getTotalPrice()).isEqualTo(order.getTotalPrice());
        assertThat(result.getOrderDate()).isEqualTo(order.getOrderDate());
    }

    @Test
    void orderDTOToOrder_shouldReturnNullWhenOrderDTOIsNull() {
        Order result = orderMapper.orderDTOToOrder(null);

        assertThat(result).isNull();
    }
}