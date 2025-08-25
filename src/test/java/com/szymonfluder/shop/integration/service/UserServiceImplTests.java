package com.szymonfluder.shop.integration.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapperImpl;
import com.szymonfluder.shop.service.impl.UserServiceImpl;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapperImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTests {

    @Autowired
    private UserServiceImpl userService;

    private User addUserToDatabase() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            "User", "user@outlook.com", "password", "Address");
        return userService.addUser(userRegisterDTO);
    }

    private UserDTO getUserDTO() {
        return new UserDTO(1, "User", "user@outlook.com", "USER", -1, "Address", 0.00);
    }

    @Test
    void getAllUsers_shouldReturnEmptyList() {
        List<UserDTO> actualUserDTOList = userService.getAllUsers();
        assertThat(actualUserDTOList.isEmpty()).isTrue();
    }

    @Test
    void getAllUsers_shouldReturnAllUserDTOs() {
        addUserToDatabase();
        List<UserDTO> actualUserDTOList = userService.getAllUsers();
        UserDTO expectedUserDTO = getUserDTO();

        assertThat(actualUserDTOList.contains(expectedUserDTO)).isTrue();
    }

    @Test
    void getUserByUsername_shouldReturnUserDTO() {
        User addedUser = addUserToDatabase();
        UserDTO actualUserDTO = userService.getUserByUsername(addedUser.getUsername());
        UserDTO expectedUserDTO = getUserDTO();

        assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
    }

    @Test
    void getUserByUsername_shouldReturnNullUserDTOWhenUserNotFound() {
        addUserToDatabase();
        String username = "nonExistingUsername";

        UserDTO actualUserDTO = userService.getUserByUsername(username);

        assertThat(actualUserDTO).isNull();
    }

    @Test
    void getUserById_shouldReturnUserDTO() {
        User addedUser = addUserToDatabase();
        UserDTO actualUserDTO = userService.getUserById(addedUser.getUserId());
        UserDTO expectedUserDTO = getUserDTO();

        assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserWithGivenIdIsNotPresent() {
        int userId = 1;
        UserDTO actualUserDTO = userService.getUserById(userId);

        assertThat(actualUserDTO).isNull();
    }

    @Test
    void addUser_shouldReturnAddedUser() {
        User addedUser = addUserToDatabase();
        User expectedUser = new User(1, "User", "user@outlook.com", "password", "USER", null, "Address", 0.0);

        assertThat(addedUser.getClass()).isEqualTo(User.class);
        assertThat(addedUser).isEqualTo(expectedUser);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        User addedUser = addUserToDatabase();
        int userId = addedUser.getUserId();

        User userPassedToUpdateMethod = new User(userId, "UpdatedUsername", "updated@outlook.com",
                "updatedPassword", "ADMIN", null, "updatedAddress", 100.00);

        User updatedUser = userService.updateUser(userPassedToUpdateMethod);

        assertThat(updatedUser.getClass()).isEqualTo(User.class);
        assertThat(updatedUser).isEqualTo(userPassedToUpdateMethod);
    }
}