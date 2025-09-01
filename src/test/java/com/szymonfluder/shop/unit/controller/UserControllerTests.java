package com.szymonfluder.shop.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szymonfluder.shop.controller.UserController;
import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserLoginDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.security.SecurityConfig;
import com.szymonfluder.shop.security.UserDetailsServiceImpl;
import com.szymonfluder.shop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        setupJwtMocksWithTokenExtraction(jwtService, userDetailsService);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        List<UserDTO> users = List.of(new UserDTO(1, "user", "user@outlook.com", "USER", 1, "User's Address", 100.0));
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("user"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserByUsername_shouldReturnUser() throws Exception {
        UserDTO userDTO = new UserDTO(1, "user", "user@outlook.com", "USER", 1, "User's Address", 100.0);
        when(userService.getUserByUsername("user")).thenReturn(userDTO);

        mockMvc.perform(get("/users/user")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("user"));

        verify(userService, times(1)).getUserByUsername("user");
    }

    @Test
    void addUser_shouldReturnCreatedUser() throws Exception {
        User user = new User(1, "user", "user@outlook.com", "password", "USER", null, "User's Address", 0.0);
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("user", "user@outlook.com", "password", "User's Address");
        when(userService.addUser(any(UserRegisterDTO.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("user"));

        verify(userService, times(1)).addUser(any(UserRegisterDTO.class));
    }

    @Test
    void deleteUserById_shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(1);

        mockMvc.perform(delete("/users/1")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(1);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        User updatedUser = new User(1, "updatedUser", "updated_user@outlook.com", "updatedPassword", "ADMIN", null, "Updated Address", 500.0);
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("updatedUser"));

        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    void deleteUserById_shouldHandleInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/users/invalid")
                .header("Authorization", AUTH_HEADER))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldRegisterUser() throws Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("newUser", "newuser@outlook.com", "password", "New Address");
        doNothing().when(userService).register(any(UserRegisterDTO.class));

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)))
                .andExpect(status().isOk());

        verify(userService, times(1)).register(any(UserRegisterDTO.class));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("user", "password");
        String expectedToken = "jwt.token.response";
        when(userService.verify(any(UserLoginDTO.class))).thenReturn(expectedToken);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));

        verify(userService, times(1)).verify(any(UserLoginDTO.class));
    }
}