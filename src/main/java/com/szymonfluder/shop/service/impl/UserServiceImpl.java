package com.szymonfluder.shop.service.impl;

import com.szymonfluder.shop.dto.UserDTO;
import com.szymonfluder.shop.dto.UserLoginDTO;
import com.szymonfluder.shop.dto.UserRegisterDTO;
import com.szymonfluder.shop.entity.User;
import com.szymonfluder.shop.entity.Cart;
import com.szymonfluder.shop.exception.UsernameTakenException;
import com.szymonfluder.shop.mapper.UserMapper;
import com.szymonfluder.shop.repository.UserRepository;
import com.szymonfluder.shop.security.JWTService;
import com.szymonfluder.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.szymonfluder.shop.repository.CartRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final CartRepository cartRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           JWTService jwtService, AuthenticationManager authenticationManager, 
                           CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.cartRepository = cartRepository;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUserDTO();
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findUserDTOByUsername(username);
    }

    @Override
    public UserDTO getUserById(int userId) {
        return userRepository.findUserDTOById(userId);
    }

    @Override
    public double getUserBalance(int userId) {
        UserDTO userDTO = userRepository.findUserDTOById(userId);
        return userDTO.getBalance();
    }

    @Override
    public User addUser(UserRegisterDTO userRegisterDTO) {
        User user = userMapper.userRegisterDTOToUser(userRegisterDTO);
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole("USER");
        User savedUser = userRepository.save(user);
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);
        return savedUser;
    }

    @Override
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User user) {
        Optional<User> tempUser = userRepository.findById(user.getUserId());
        User updatedUser = new User();
        if (tempUser.isPresent()) {
            updatedUser.setUserId(user.getUserId());
            updatedUser.setUsername(user.getUsername());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            updatedUser.setRole(user.getRole());
            updatedUser.setAddress(user.getAddress());
            updatedUser.setBalance(user.getBalance());
        }
        return userRepository.save(updatedUser);
    }

    @Override
    public void updateUserBalance(int userId, double newBalance) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBalance(newBalance);
        userRepository.save(user);
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) throws UsernameTakenException {
        if (userRepository.findUserDTOByUsername(userRegisterDTO.getUsername()) == null) {
            User userToAdd = userMapper.userRegisterDTOToUser(userRegisterDTO);
            userToAdd.setPassword(bCryptPasswordEncoder.encode(userRegisterDTO.getPassword()));
            userToAdd.setRole("USER");
            User savedUser = userRepository.save(userToAdd);

            Cart cart = new Cart();
            cart.setUser(savedUser);
            cartRepository.save(cart);

        } else {
            throw new UsernameTakenException(userRegisterDTO.getUsername());
        }
    }

    @Override
    public String verify(UserLoginDTO userLoginDTO) {
        if (userRepository.findUserDTOByUsername(userLoginDTO.getUsername()) != null) {
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
        return userRepository.findUserDTOByUsername(username);
    }
}