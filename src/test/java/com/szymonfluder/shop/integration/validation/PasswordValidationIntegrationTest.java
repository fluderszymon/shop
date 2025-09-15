package com.szymonfluder.shop.integration.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szymonfluder.shop.dto.UserRegisterDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class PasswordValidationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private UserRegisterDTO createUserWithPassword(String password) {
        return new UserRegisterDTO(
            "User",
            "user@outlook.com",
            password,
            "123 Main St"
        );
    }

    private void performRegistrationAndExpectBadRequest(UserRegisterDTO user) throws Exception {
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    private void performRegistrationAndExpectOk(UserRegisterDTO user) throws Exception {
        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    void register_shouldReturn400_whenPasswordIsTooWeak() throws Exception {
        UserRegisterDTO user = createUserWithPassword("weak");
        performRegistrationAndExpectBadRequest(user);
    }

    @Test
    void register_shouldReturn400_whenPasswordMissingSpecialChar() throws Exception {
        UserRegisterDTO user = createUserWithPassword("MyPassword1");
        performRegistrationAndExpectBadRequest(user);
    }

    @Test
    void register_shouldReturn400_whenPasswordHasWhitespace() throws Exception {
        UserRegisterDTO user = createUserWithPassword("My Password1!");
        performRegistrationAndExpectBadRequest(user);
    }

    @Test
    void register_shouldReturn400_whenPasswordMissingUppercase() throws Exception {
        UserRegisterDTO user = createUserWithPassword("mypassword1!");
        performRegistrationAndExpectBadRequest(user);
    }

    @Test
    void register_shouldReturn200_whenPasswordIsValid() throws Exception {
        UserRegisterDTO user = createUserWithPassword("MyPassword1!");
        performRegistrationAndExpectOk(user);
    }
}