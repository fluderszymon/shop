package com.szymonfluder.shop.unit.mapper;

import java.time.LocalDate;
import java.util.List;

import com.szymonfluder.shop.entity.Order;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.OrderMapper;
import com.szymonfluder.shop.mapper.OrderMapperImpl;
import com.szymonfluder.shop.dto.OrderDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderMapperTests {

    private final int USER_ID = 1;
    private final int ORDER_ID = 1;
    private final double TOTAL_PRICE = 100.0;
    private final LocalDate ORDER_DATE = LocalDate.of(2020, 1, 13);
    
    private final OrderMapper orderMapper = new OrderMapperImpl();

    private Order createOrder() {
        User user = new User();
        user.setUserId(USER_ID);
        return new Order(ORDER_ID, user, null, TOTAL_PRICE, ORDER_DATE);
    }

    private OrderDTO createOrderDTO() {
        return new OrderDTO(ORDER_ID, USER_ID, TOTAL_PRICE, ORDER_DATE);
    }

    @Test
    void orderToOrderDTO_shouldMapOrderToOrderDTO() {
        Order givenOrder = createOrder();
        OrderDTO expectedOrderDTO = createOrderDTO();
        OrderDTO mappedOrderDTO = orderMapper.orderToOrderDTO(givenOrder);

        assertThat(mappedOrderDTO).isEqualTo(expectedOrderDTO);
    }

    @Test
    void orderDTOToOrder_shouldMapOrderDTOToOrder() {
        OrderDTO givenOrderDTO = createOrderDTO();
        Order expectedOrder = createOrder();
        Order mappedOrder = orderMapper.orderDTOToOrder(givenOrderDTO);

        assertThat(mappedOrder).isEqualTo(expectedOrder);
    }
}