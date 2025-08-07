package com.szymonfluder.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.szymonfluder.shop.dto.OrderItemDTO;
import com.szymonfluder.shop.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    @Query(value="SELECT new com.szymonfluder.shop.dto.OrderItemDTO(" +
            "oi.orderItemId, oi.order.orderId, oi.quantity, oi.product.name, oi.product.productId, oi.product.price) " +
            "FROM OrderItem oi " +
            "WHERE oi.order.orderId=?1")
    List<OrderItemDTO> findAllOrderItemsByOrderId(int orderId);

    @Modifying
    @Query(value="INSERT INTO OrderItem (order, order_item_id, ) VALUES ", nativeQuery = true)
    void insertOrderItem(OrderItemDTO orderItemDTO);

}
