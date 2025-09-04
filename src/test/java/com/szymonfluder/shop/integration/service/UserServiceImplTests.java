package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.integration.config.TestConfig;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.mapper.UserMapperImpl;
import com.szymonfluder.shop.service.impl.UserServiceImpl;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapperImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTests {

    @Autowired
    private UserServiceImpl userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private User addUserToDatabase() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            "User", "user@outlook.com", "password", "Address");
        return userService.addUser(userRegisterDTO);
    }

    private UserDTO getUserDTOMock() {
        return new UserDTO(1, "User", "user@outlook.com", "USER", 1, "Address", 0.00);
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
        UserDTO expectedUserDTO = getUserDTOMock();

        assertThat(actualUserDTOList.contains(expectedUserDTO)).isTrue();
    }

    @Test
    void getUserByUsername_shouldReturnUserDTO() {
        User addedUser = addUserToDatabase();
        UserDTO actualUserDTO = userService.getUserByUsername(addedUser.getUsername());
        UserDTO expectedUserDTO = getUserDTOMock();

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
        UserDTO expectedUserDTO = getUserDTOMock();

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

        assertThat(addedUser.getClass()).isEqualTo(User.class);
        assertThat(addedUser.getUserId()).isEqualTo(1);
        assertThat(addedUser.getUsername()).isEqualTo("User");
        assertThat(addedUser.getEmail()).isEqualTo("user@outlook.com");
        assertThat(addedUser.getRole()).isEqualTo("USER");
        assertThat(addedUser.getAddress()).isEqualTo("Address");
        assertThat(addedUser.getBalance()).isEqualTo(0.0);
        
        assertThat(passwordEncoder.matches("password", addedUser.getPassword())).isTrue();
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        User addedUser = addUserToDatabase();
        int userId = addedUser.getUserId();

        User userPassedToUpdateMethod = new User(userId, "UpdatedUsername", "updated@outlook.com",
                "updatedPassword", "ADMIN", null, "updatedAddress", 100.00);

        User updatedUser = userService.updateUser(userPassedToUpdateMethod);

        assertThat(updatedUser.getClass()).isEqualTo(User.class);
        assertThat(updatedUser.getUserId()).isEqualTo(userId);
        assertThat(updatedUser.getUsername()).isEqualTo("UpdatedUsername");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@outlook.com");
        assertThat(updatedUser.getRole()).isEqualTo("ADMIN");
        assertThat(updatedUser.getAddress()).isEqualTo("updatedAddress");
        assertThat(updatedUser.getBalance()).isEqualTo(100.00);
        
        assertThat(passwordEncoder.matches("updatedPassword", updatedUser.getPassword())).isTrue();
    }
}