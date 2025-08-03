package com.szymonfluder.shop.repository;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // set cartId to -1 if there is no cart present for user
    @Query(value="SELECT new com.szymonfluder.shop.dto.UserDTO(" +
            "u.userId, u.username, u.email, u.role, COALESCE(u.cart.cartId, '-1')) " +
            "FROM User u LEFT JOIN u.cart")
    List<UserDTO> findAllUserDTO();

    @Query(value="SELECT new com.szymonfluder.shop.dto.UserDTO(" +
            "u.userId, u.username, u.email, u.role, COALESCE(u.cart.cartId, '-1')) " +
            "FROM User u LEFT JOIN u.cart " +
            "WHERE u.username=?1")
    UserDTO findUserDTOByUsername(String username);

}
