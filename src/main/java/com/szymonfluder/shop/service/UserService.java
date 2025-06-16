package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();
    UserDTO getUserByUsername(String username);
    User addUser(UserRegisterDTO userRegisterDTO);
    void deleteUserById(int userId);
    User updateUser(User user);

}
