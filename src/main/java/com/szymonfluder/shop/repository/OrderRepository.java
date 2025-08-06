package com.szymonfluder.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.szymonfluder.shop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
}
