package com.szymonfluder.shop.controller;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserLoginDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User addUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.addUser(userRegisterDTO);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(@PathVariable int userId) {
        userService.deleteUserById(userId);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PostMapping("/register")
    public void register(@RequestBody UserRegisterDTO userRegisterDTO) throws Exception {
        userService.register(userRegisterDTO);
}

    @PostMapping("/login")
    public String login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.verify(userLoginDTO);
    }
}