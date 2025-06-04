package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.repository.UserRepository;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(int userId) {
        User foundUser = userRepository.findById(userId).orElse(new User());
        return userMapper.userToUserDTO(foundUser);
    }

    public User addUser(UserRegisterDTO userRegisterDTO) {
        User user = userMapper.userRegisterDTOToUser(userRegisterDTO);
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
