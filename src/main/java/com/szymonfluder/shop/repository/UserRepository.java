package com.szymonfluder.shop.repository;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // will fail if there is at least one user without cart
    @Query(value="SELECT new com.szymonfluder.shop.dto.UserDTO(" +
            "u.userId, u.username, u.email, u.role, u.cart.cartId) " +
            "FROM User u LEFT JOIN u.cart")
    List<UserDTO> findAllUserDTO();

    @Query(value="SELECT new com.szymonfluder.shop.dto.UserDTO(" +
            "u.userId, u.username, u.email, u.role, u.cart.cartId) " +
            "FROM User u LEFT JOIN u.cart " +
            "WHERE u.username=?1")
    UserDTO findUserDTOByUsername(String username);

}
