package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.UserDTO;
import com.taskmanagement.task.Entity.User;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieve all active users (not soft deleted).
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(user -> new UserDTO(user.getUserId(), user.getName(), user.getEmail(), user.getPhoto()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a user by user ID (only if not soft deleted).
     */
    public Optional<UserDTO> getUserById(String userId) {
        return userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .map(user -> new UserDTO(user.getUserId(), user.getName(), user.getEmail(), user.getPhoto()));
    }

    /**
     * Create a new user with a hashed password.
     */
    public User createUser(String userId, String name, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(userId, name, null, hashedPassword, null);
        return userRepository.save(user);
    }

    /**
     * Update user details including email, password, and profile photo.
     */
    public User updateUser(String userId, String email, String password, MultipartFile photo) throws IOException {
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (StringUtils.hasText(email)) {
            user.setEmail(email);
        }
        if (StringUtils.hasText(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        if (photo != null && !photo.isEmpty()) {
            user.setPhoto(photo.getInputStream().readAllBytes()); // Handle file input efficiently
        }

        return userRepository.save(user);
    }

    /**
     * Soft delete a user by updating the deleted_at timestamp.
     */
    public void deleteUser(String userId) {
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Restore a soft-deleted user (optional feature).
     */
    public void restoreUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getDeletedAt() != null) {
            user.setDeletedAt(null);
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already active");
        }
    }
}
