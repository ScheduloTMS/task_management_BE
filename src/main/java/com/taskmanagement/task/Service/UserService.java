package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.UserDTO;
import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.UserRepository;
import com.taskmanagement.task.Util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public boolean isUserExists(String userId, String email) {
        try {
            return userRepository.existsByUserIdOrEmail(userId, email);
        } catch (Exception e) {
            throw new RuntimeException("Error checking user existence");
        }
    }

    @Transactional
    public Users createUser(UserDTO userDTO) {
        try {
            String rolePrefix = userDTO.getRole().equals("MENTOR") ? "MT" : "ST";
            String lastUserId = userRepository.findLastUserIdByRole(rolePrefix);
            int nextId = lastUserId == null ? 1 : Integer.parseInt(lastUserId.substring(2)) + 1;
            String newUserId = String.format("%s%03d", rolePrefix, nextId);

            if (isUserExists(newUserId, userDTO.getEmail())) {
                throw new RuntimeException("User already exists");
            }

            Users user = new Users();
            user.setUserId(newUserId);
            user.setName(userDTO.getName());
            user.setPassword(passwordEncoder.encode("TMS@123"));
            user.setRole(userDTO.getRole());
            user.setEmail(userDTO.getEmail());

            return userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage());
        }
    }

    @Transactional
    public boolean validateAndUpdatePassword(
            String userId,
            String currentPassword,
            String newPassword,
            String token,
            MultipartFile photo) throws IOException {

        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (currentPassword != null && newPassword != null) {
                if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    jwtUtil.blacklistToken(token);
                } else {
                    throw new RuntimeException("Current password is incorrect");
                }
            }

            if (photo != null && !photo.isEmpty()) {
                user.setPhoto(photo.getBytes());
            }

            userRepository.save(user);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error updating password: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteUser(String userId) {
        try {
            Users user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

            if (user.getDeletedAt() != null) {
                throw new RuntimeException("User with ID " + userId + " is already deleted");
            }

            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    public void logout(String token) {
        try {
            jwtUtil.blacklistToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Error during logout: " + e.getMessage());
        }
    }

    @Transactional
    public List<Users> getAllUsers() {
        try {
            return userRepository.findAllActiveUsers();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving users");
        }
    }

    @Transactional
    public Users getUserById(String userId) {
        try {
            return userRepository.findByUserIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> new RuntimeException("User not found or deleted"));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user by ID: " + e.getMessage());
        }
    }

    @Transactional
    public void updateUser(Users user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }
}