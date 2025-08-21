package com.szymonfluder.shop.unit.service;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.repository.UserRepository;
import com.szymonfluder.shop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// @DataJpaTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final int USER_ID = 1;
    private static final int CART_ID = 1;
    private static final String USERNAME = "user";
    private static final String EMAIL = "user@outlook.com";
    private static final String PASSWORD = "password";
    private static final String ROLE = "USER";
    private static final String ADDRESS = "Test Address";
    private static final double INITIAL_BALANCE = 100.00;
    private static final double NEW_BALANCE = 150.00;

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        Cart cart = new Cart(CART_ID, null, new ArrayList<>());
        user = new User(USER_ID, USERNAME, EMAIL, PASSWORD, ROLE, cart, ADDRESS, INITIAL_BALANCE);
        userDTO = new UserDTO(USER_ID, USERNAME, EMAIL, ROLE, CART_ID, ADDRESS, INITIAL_BALANCE);
    }

    // private User addUser() {
    //     UserRegisterDTO userRegisterDTO = new UserRegisterDTO(USERNAME, EMAIL, PASSWORD, ADDRESS);
    //     return userService.addUser(userRegisterDTO);
    // }

    // @Test
    // void getAllUsers_shouldSuccessfullyReturnAllUserDTOs() {
    //     User addedUser = addUser();
    //     List<UserDTO> result = userService.getAllUsers();
    //     assertTrue(result.get(0).equals(userMapper.userToUserDTO(addedUser)));
    // }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.findAllUserDTO()).thenReturn(List.of());

        List<UserDTO> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(userRepository).findAllUserDTO();
    }

    @Test
    void getUserByUsername_shouldSuccessfullyReturnUserDTO() {
        when(userRepository.findUserDTOByUsername(USERNAME)).thenReturn(userDTO);

        UserDTO result = userService.getUserByUsername(USERNAME);

        assertEquals(userDTO, result);
        verify(userRepository).findUserDTOByUsername(USERNAME);
    }

    @Test
    void getUserByUsername_shouldReturnNullWhenUsernameNotFound() {
        when(userRepository.findUserDTOByUsername(USERNAME)).thenReturn(null);

        UserDTO result = userService.getUserByUsername(USERNAME);

        assertNull(result);
        verify(userRepository).findUserDTOByUsername(USERNAME);
    }

    @Test
    void getUserById_shouldSuccessfullyReturnUserDTO() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(USER_ID);

        assertEquals(userDTO, result);
        verify(userRepository).findById(USER_ID);
        verify(userMapper).userToUserDTO(user);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.getUserById(USER_ID));
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void getUserBalance_shouldSuccessfullyReturnUserBalance() {
        when(userRepository.findUserDTOById(USER_ID)).thenReturn(userDTO);

        double result = userService.getUserBalance(USER_ID);

        assertEquals(INITIAL_BALANCE, result);
        verify(userRepository).findUserDTOById(USER_ID);
    }

    @Test
    void addUser_shouldSuccessfullyAddUser() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(USERNAME, EMAIL, PASSWORD, "Test Address");
        User userMappedFromUserRegisterDTO = new User(0, USERNAME, EMAIL, PASSWORD, null, null, "Test Address", 0.0);
        User userSaved = new User(USER_ID, USERNAME, EMAIL, PASSWORD, ROLE, null, "Test Address", 0.0);

        when(userMapper.userRegisterDTOToUser(userRegisterDTO)).thenReturn(userMappedFromUserRegisterDTO);
        when(userRepository.save(userMappedFromUserRegisterDTO)).thenReturn(userSaved);

        User result = userService.addUser(userRegisterDTO);

        assertNotNull(result);
        assertEquals(userSaved, result);
        verify(userMapper).userRegisterDTOToUser(userRegisterDTO);
        verify(userRepository).save(any(User.class));
        }

    @Test
    void deleteUserById_shouldSuccessfullyDeleteUser() {
        doNothing().when(userRepository).deleteById(USER_ID);

        userService.deleteUserById(USER_ID);

        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void updateUser_shouldSuccessfullyUpdateUser() {
        User existingUser = new User(USER_ID, "User", "user@outlook.com", "password", "USER", null, "Test Address", 200.0);
        User updatedUser = new User(USER_ID, "Updated User", "updated_user@outlook.com", "updated_password", "USER", null, "Updated Test Address", 200.0);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(updatedUser);

        assertEquals(updatedUser, result);
        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(updatedUser);
    }

    @Test
    void updateUserBalance_shouldSuccessfullyUpdateUserBalance() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUserBalance(USER_ID, NEW_BALANCE);

        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserBalance_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.updateUserBalance(USER_ID, NEW_BALANCE));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(USER_ID);
    }
}