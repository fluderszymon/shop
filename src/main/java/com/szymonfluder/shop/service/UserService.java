package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User getUserById(int id);
    User addUser(UserRegisterDTO userRegisterDTO);
    void deleteUserById(int userId);
    User updateUser(User user);

}
