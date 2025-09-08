package com.szymonfluder.shop.repository;

import com.szymonfluder.shop.dto.CartDTO;
import com.szymonfluder.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query(value="SELECT new com.szymonfluder.shop.dto.CartDTO(" +
            "c.cartId, c.user.userId) " +
            "FROM Cart c " +
            "WHERE c.user.userId=?1")
    Optional<CartDTO> findCartDTOByUserId(int userId);
}
