package com.szymonfluder.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import com.szymonfluder.shop.validation.ValidUsername;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {

    @NotNull(message = "User ID is required")
    private int userId;
    
    @NotBlank(message = "Username is required")
    @ValidUsername
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Role is required")
    private String role;

    @NotNull(message = "Cart ID is required")
    private int cartId;
    
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotNull(message = "Balance is required")
    private BigDecimal balance;

}