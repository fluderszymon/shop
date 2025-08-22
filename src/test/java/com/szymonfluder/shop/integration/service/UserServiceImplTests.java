package com.szymonfluder.shop.integration.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapperImpl;
import com.szymonfluder.shop.service.impl.UserServiceImpl;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapperImpl.class})
public class UserServiceImplTests {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapperImpl userMapper;

    private User addUserToDatabase() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            "User", "user@outlook.com", "password", "Address");
        return userService.addUser(userRegisterDTO);
    }

    private UserDTO prepareMockUserDTO(int userId) {
        return new UserDTO(userId, "User", "user@outlook.com", "USER", -1, "Address", 0.00);
    }

    @Test
    void getAllUsers_shouldReturnEmptyList() {
        List<UserDTO> users = userService.getAllUsers();
        assertThat(users.isEmpty()).isTrue();
    }

    @Test
    void getAllUsers_shouldReturnAllUserDTOs() {
        User addedUser = addUserToDatabase();
        List<UserDTO> userDTOList = userService.getAllUsers();
        UserDTO expectedUserDTO = prepareMockUserDTO(addedUser.getUserId());

        assertThat(userDTOList.size()).isEqualTo(1);
        assertThat(userDTOList.contains(expectedUserDTO)).isTrue();
    }

    @Test
    void getUserByUsername_shouldReturnUserDTO() {
        User addedUser = addUserToDatabase();
        UserDTO userDTO = userService.getUserByUsername(addedUser.getUsername());
        UserDTO expectedUserDTO = prepareMockUserDTO(addedUser.getUserId());

        assertThat(userDTO).isEqualTo(expectedUserDTO);
    }

    @Test
    void getUserByUsername_shouldReturnNullUserDTOWhenUserNotFound() {
        addUserToDatabase();
        String username = "nonExistingUsername";

        UserDTO userDTO = userService.getUserByUsername(username);

        assertThat(userDTO).isNull();
    }

    @Test
    void getUserById_shouldReturnUserDTO() {
        User addedUser = addUserToDatabase();
        UserDTO userDTO = userService.getUserById(addedUser.getUserId());
        UserDTO expectedUserDTO = userMapper.userToUserDTO(addedUser);

        assertEquals(expectedUserDTO, userDTO);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserWithGivenIdIsNotPresent() {
        int userId = 1;

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> userService.getUserById(userId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addUser_shouldReturnAddedUser() {
        User addedUser = addUserToDatabase();
        UserDTO addedUserDTO = userMapper.userToUserDTO(addedUser);
        UserDTO result = userService.getUserById(addedUser.getUserId());

        assertEquals(User.class, addedUser.getClass());
        assertThat(addedUserDTO).isEqualTo(result);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        User addedUser = addUserToDatabase();
        int userId = addedUser.getUserId();

        User userPassedToUpdateMethod = new User(userId, "UpdatedUsername", "updated@outlook.com",
                "updatedPassword", "ADMIN", null, "updatedAddress", 100.00);

        User updatedUser = userService.updateUser(userPassedToUpdateMethod);

        assertEquals(userPassedToUpdateMethod, updatedUser);
    }

}