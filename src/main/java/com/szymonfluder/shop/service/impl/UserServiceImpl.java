package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.repository.UserRepository;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(new User());
    }

    public User addUser(UserRegisterDTO userRegisterDTO) {
        User user = userMapper.UserRegisterDTOToUser(userRegisterDTO);
        user.setRole("USER");
        return userRepository.save(user);
    }

    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    public User updateUser(User user) {
        Optional<User> tempUser = userRepository.findById(user.getUserId());
        User updatedUser = new User();
        if (tempUser.isPresent()) {
            updatedUser.setUserId(user.getUserId());
            updatedUser.setUsername(user.getUsername());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setRole(user.getRole());
        }
        return userRepository.save(updatedUser);
    }
}
