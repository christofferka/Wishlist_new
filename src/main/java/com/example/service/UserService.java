package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String password) {
        if (username == null || email == null || password == null) return null;
        username = username.trim();
        email = email.trim().toLowerCase();
        if (username.isEmpty() || email.isEmpty() || password.length() < 4) return null;

        if (userRepository.findByEmail(email) != null) {
            return null;
        }
        User user = new User(username, email, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        if (email == null || password == null) return null;
        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
