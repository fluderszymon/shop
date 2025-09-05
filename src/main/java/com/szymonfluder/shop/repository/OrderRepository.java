package com.szymonfluder.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.szymonfluder.shop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value="SELECT o FROM Order o WHERE o.user.userId=?1")
    List<Order> findAllOrdersByUserId(int userId);
    
}