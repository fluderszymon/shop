package com.szymonfluder.shop.service;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserLoginDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();
    UserDTO getUserByUsername(String username);
    UserDTO getUserById(int userId);
    double getUserBalance(int userId);

    User addUser(UserRegisterDTO userRegisterDTO);
    void deleteUserById(int userId);
    User updateUser(User user);
    void updateUserBalance(int userId, double newBalance);

    void register(UserRegisterDTO userRegisterDTO) throws Exception;
    String verify(UserLoginDTO userLoginDTO);
}