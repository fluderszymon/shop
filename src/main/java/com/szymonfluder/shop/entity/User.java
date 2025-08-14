package com.szymonfluder.shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private int userId;

    @NotBlank
    @Column(name="username", nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(name="email", nullable = false)
    private String email;

    @NotBlank
    @Column(name="password", nullable = false)
    private String password;

    @NotBlank
    @Column(name="role", nullable = false)
    private String role;

    @OneToOne(mappedBy="user")
    private Cart cart;

    @Column(name="address")
    private String address;

    @Column(name="balance")
    private double balance;

}
