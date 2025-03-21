package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.UserRepository;
import com.taskmanagement.task.Util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public String login(String userId, String password) {
        try {
            Optional<Users> user = userRepository.findByUserId(userId);

            if (user.isEmpty() || user.get().getDeletedAt() != null) {
                throw new RuntimeException("User does not exist or has been deleted");
            }

            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return jwtUtil.generateToken(userId);
            }

            throw new RuntimeException("Invalid userId or password");
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public void logout(String token) {
        try {
            jwtUtil.blacklistToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }
}