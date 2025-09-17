package com.szymonfluder.shop.integration.service;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserLoginDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.exception.EntityNotFoundException;
import com.szymonfluder.shop.exception.UsernameTakenException;
import com.szymonfluder.shop.integration.config.TestConfig;
import com.szymonfluder.shop.mapper.UserMapperImpl;
import com.szymonfluder.shop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapperImpl.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTests extends AbstractServiceTest {

    private final String ROLE = "USER";
    private final String NON_EXISTING_USERNAME = "NonExistingUser";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private UserDTO getUserDTOMock() {
        BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP);
        return new UserDTO(USER_ID, USERNAME, EMAIL, ROLE, USER_ID, ADDRESS, INITIAL_BALANCE);
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() {
        List<UserDTO> actualUserDTOList = userService.getAllUsers();
        assertThat(actualUserDTOList.isEmpty()).isTrue();
    }

    @Test
    void getAllUsers_shouldReturnAllUserDTOs_whenUsersExist() {
        addUserToDatabase();
        List<UserDTO> actualUserDTOList = userService.getAllUsers();
        List<UserDTO> expectedUserDTOList = List.of(getUserDTOMock());

        assertThat(actualUserDTOList).isEqualTo(expectedUserDTOList);
    }

    @Test
    void getUserByUsername_shouldReturnUserDTO_whenUserExists() {
        User addedUser = addUserToDatabase();
        UserDTO actualUserDTO = userService.getUserByUsername(addedUser.getUsername());
        UserDTO expectedUserDTO = getUserDTOMock();

        assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
    }

    @Test
    void getUserByUsername_shouldThrowEntityNotFoundException_whenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByUsername(NON_EXISTING_USERNAME));
    }

    @Test
    void getUserById_shouldReturnUserDTO_whenUserExists() {
        addUserToDatabase();
        UserDTO actualUserDTO = userService.getUserById(USER_ID);
        UserDTO expectedUserDTO = getUserDTOMock();

        assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
    }

    @Test
    void getUserById_shouldThrowEntityNotFoundException_whenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(NON_EXISTING_ID));
    }

    @Test
    void addUser_shouldReturnAddedUser_whenValidDataProvided() {
        User addedUser = addUserToDatabase();

        assertThat(addedUser.getClass()).isEqualTo(User.class);
        assertThat(addedUser.getUserId()).isEqualTo(USER_ID);
        assertThat(addedUser.getUsername()).isEqualTo(USERNAME);
        assertThat(addedUser.getEmail()).isEqualTo(EMAIL);
        assertThat(addedUser.getRole()).isEqualTo(ROLE);
        assertThat(addedUser.getAddress()).isEqualTo(ADDRESS);
        assertThat(addedUser.getBalance()).isEqualTo(BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP));
        assertThat(passwordEncoder.matches(PASSWORD, addedUser.getPassword())).isTrue();
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenUserExists() {
        addUserToDatabase();
        String ADMIN_ROLE = "ADMIN";
        String UPDATED_ADDRESS = "updatedAddress";
        String UPDATED_USERNAME = "UpdatedUsername";
        String UPDATED_EMAIL = "updated@outlook.com";
        String UPDATED_PASSWORD = "updatedPassword";
        User userPassedToUpdateMethod = new User(USER_ID, UPDATED_USERNAME, UPDATED_EMAIL,
                UPDATED_PASSWORD, ADMIN_ROLE, null, UPDATED_ADDRESS, SUFFICIENT_BALANCE);
        User updatedUser = userService.updateUser(userPassedToUpdateMethod);

        assertThat(updatedUser.getClass()).isEqualTo(User.class);
        assertThat(updatedUser.getUserId()).isEqualTo(USER_ID);
        assertThat(updatedUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(updatedUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(updatedUser.getRole()).isEqualTo(ADMIN_ROLE);
        assertThat(updatedUser.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(updatedUser.getBalance()).isEqualTo(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP));
        assertThat(passwordEncoder.matches(UPDATED_PASSWORD, updatedUser.getPassword())).isTrue();
    }

    @Test
    void register_shouldCreateUserSuccessfully_whenValidDataProvided() {
        UserRegisterDTO userRegisterDTO = getUserRegisterDTO();
        userService.register(userRegisterDTO);

        UserDTO savedUser = userService.getUserByUsername(USERNAME);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(USERNAME);
        assertThat(savedUser.getRole()).isEqualTo(ROLE);
    }

    @Test
    void register_shouldThrowUsernameTakenException_whenUsernameAlreadyExists() {
        addUserToDatabase();
        UserRegisterDTO secondUser = getUserRegisterDTO();

        assertThrows(UsernameTakenException.class, () -> userService.register(secondUser));
    }

    @Test
    void verify_shouldNotVerifyUser_whenUsernameDoesNotExist() {
        UserLoginDTO userLoginDTO = new UserLoginDTO(NON_EXISTING_USERNAME, PASSWORD);

        String result = userService.verify(userLoginDTO);
        assertThat(result).isEqualTo("Could not verify user");
    }
}