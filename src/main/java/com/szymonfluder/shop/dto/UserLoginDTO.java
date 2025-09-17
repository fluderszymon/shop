package com.szymonfluder.shop.dto;

import com.szymonfluder.shop.validation.StrongPassword;
import com.szymonfluder.shop.validation.ValidUsername;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLoginDTO {

    @NotNull(message = "Username is required")
    @ValidUsername
    private String username;

    @NotNull(message = "Password is required")
    @StrongPassword
    private String password;

}