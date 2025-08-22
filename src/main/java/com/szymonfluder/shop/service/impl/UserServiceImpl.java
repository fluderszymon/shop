package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.repository.UserRepository;
import com.szymonfluder.shop.service.CartService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUserDTO();
    }

    public UserDTO getUserByUsername(String username) {
        return userRepository.findUserDTOByUsername(username);
    }

    @Override
    public UserDTO getUserById(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.userToUserDTO(user);
    }

    @Override
    public double getUserBalance(int userId) {
        UserDTO userDTO = userRepository.findUserDTOById(userId);
        return userDTO.getBalance();
    }

    // password is visible
    public User addUser(UserRegisterDTO userRegisterDTO) {
        User user = userMapper.userRegisterDTOToUser(userRegisterDTO);
        user.setRole("USER");
        return userRepository.save(user);
    }

    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    // password is visible
    public User updateUser(User user) {
        Optional<User> tempUser = userRepository.findById(user.getUserId());
        User updatedUser = new User();
        if (tempUser.isPresent()) {
            updatedUser.setUserId(user.getUserId());
            updatedUser.setUsername(user.getUsername());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setRole(user.getRole());
            updatedUser.setAddress(user.getAddress());
            updatedUser.setBalance(user.getBalance());
        }
        return userRepository.save(updatedUser);
    }

    @Override
    public void updateUserBalance(int userId, double newBalance) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBalance(newBalance);
        userRepository.save(user);
    }
}
