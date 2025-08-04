package com.szymonfluder.shop.repository;

import com.szymonfluder.shop.dto.CartItemDTO;
import com.szymonfluder.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query(value="SELECT new com.szymonfluder.shop.dto.CartItemDTO(" +
            "ci.cartItemId, ci.cart.cartId, ci.product.productId, ci.quantity) " +
            "FROM CartItem ci " +
            "WHERE ci.cart.cartId=?1")
    List<CartItemDTO> findAllCartItemsByCartId(int cartItemId);

}
