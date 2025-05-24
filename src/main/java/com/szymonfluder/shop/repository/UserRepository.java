package com.szymonfluder.shop.repository;

import com.szymonfluder.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

//    @Transactional
//    @Query("UPDATE User u SET u.username = :user.user_id")
//    User updateUser(User user);

}
