package com.taskmanagement.task.Service;

import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.UserRepository;
import com.taskmanagement.task.Util.JwtUtil;
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

    public String login(String userId, String password) {
        Optional<Users> user = userRepository.findByUserId(userId);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return jwtUtil.generateToken(userId);
        }
        throw new RuntimeException("Invalid userId or password");
    }
}