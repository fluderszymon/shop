package com.szymonfluder.shop.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szymonfluder.shop.controller.UserController;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        List<UserDTO> users = Arrays.asList(
            new UserDTO(1, "user", "user@outlook.com", "USER", 1, "User's Address", 100.0),
            new UserDTO(2, "admin", "admin@outlook.com", "ADMIN", 2, "Admin's Address", 10000.0)
        );
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[0].email").value("user@outlook.com"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[0].cartId").value(1))
                .andExpect(jsonPath("$[0].address").value("User's Address"))
                .andExpect(jsonPath("$[0].balance").value(100.0))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].username").value("admin"))
                .andExpect(jsonPath("$[1].email").value("admin@outlook.com"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].cartId").value(2))
                .andExpect(jsonPath("$[1].address").value("Admin's Address"))
                .andExpect(jsonPath("$[1].balance").value(10000.0));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserByUsername_shouldReturnUser() throws Exception {
        UserDTO userDTO = new UserDTO(1, "user", "user@outlook.com", "USER", 1, "User's Address", 100.0);
        when(userService.getUserByUsername("user")).thenReturn(userDTO);

        mockMvc.perform(get("/users/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@outlook.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.address").value("User's Address"))
                .andExpect(jsonPath("$.balance").value(100.0));

        verify(userService, times(1)).getUserByUsername("user");
    }

    @Test
    void addUser_shouldReturnCreatedUser() throws Exception {
        User user = new User(1, "user", "user@outlook.com", "password", "USER", null, "User's Address", 0.0);
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("user", "user@outlook.com", "password", "User's Address");
        when(userService.addUser(any(UserRegisterDTO.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@outlook.com"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.address").value("User's Address"))
                .andExpect(jsonPath("$.balance").value(0.0));

        verify(userService, times(1)).addUser(any(UserRegisterDTO.class));
    }

    @Test
    void deleteUserById_shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(1);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(1);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        User updatedUser = new User(1, "updateduser", "updateduser@outlook.com", "newpassword", "ADMIN", null, "Updated Address", 500.0);
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updateduser@outlook.com"))
                .andExpect(jsonPath("$.password").value("newpassword"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.address").value("Updated Address"))
                .andExpect(jsonPath("$.balance").value(500.0));

        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    void deleteUserById_shouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/users/invalid"))
                .andExpect(status().isBadRequest());
    }
}