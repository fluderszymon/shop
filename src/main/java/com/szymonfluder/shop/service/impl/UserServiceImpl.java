package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserLoginDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.exception.UsernameTakenException;
import com.szymonfluder.shop.exception.EntityNotFoundException;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.repository.UserRepository;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           JWTService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::userToUserDTO)
                .orElseThrow(() -> new EntityNotFoundException("User", username));
        }

    @Override
    public UserDTO getUserById(int userId) {
        return userRepository.findById(userId)
                .map(userMapper::userToUserDTO)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    @Override
    public BigDecimal getUserBalance(int userId) {
        return getUserById(userId).getBalance();
    }

    @Override
    public User addUser(UserRegisterDTO userRegisterDTO) {
        User user = User.builder()
                .username(userRegisterDTO.getUsername())
                .email(userRegisterDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(userRegisterDTO.getPassword()))
                .role("USER")
                .address(userRegisterDTO.getAddress())
                .balance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .build();
        
        Cart cart = Cart.builder()
                .user(user)
                .build();
        
        user.setCart(cart);
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User user) {
        userRepository.findById(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", user.getUserId()));
        
        User updatedUser = User.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .role(user.getRole())
                .address(user.getAddress())
                .balance(user.getBalance())
                .build();
        
        return userRepository.save(updatedUser);
    }

    @Override
    public void updateUserBalance(int userId, BigDecimal newBalance) {
        User user = userRepository.findById(userId).
            orElseThrow(() -> new EntityNotFoundException("User", userId));
        user.setBalance(newBalance);
        userRepository.save(user);
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) throws UsernameTakenException {
        String username = userRegisterDTO.getUsername();
        if (userExists(username)) {
            throw new UsernameTakenException(username);
        }
        addUser(userRegisterDTO);
    }

    @Override
    public String verify(UserLoginDTO userLoginDTO) {
        if (userRepository.findByUsername(userLoginDTO.getUsername()).isPresent()) {
            Authentication auth = authenticationManager.
                    authenticate(new UsernamePasswordAuthenticationToken(
                            userLoginDTO.getUsername(), userLoginDTO.getPassword()));
            if (auth.isAuthenticated()) {
                return jwtService.generateToken(userLoginDTO.getUsername());
            }
        }
        return "Could not verify user";
    }

    @Override
    public UserDTO getCurrentUserDTO() {
        String username = jwtService.getCurrentUsername();
        return userRepository.findByUsername(username)
                .map(userMapper::userToUserDTO)
                .orElseThrow(() -> new EntityNotFoundException("User", username));
    }

    private boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}