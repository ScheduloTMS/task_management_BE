package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.UserDTO;
import com.taskmanagement.task.Entity.User;
import com.taskmanagement.task.Repository.UserRepository;
import jakarta.transaction.Transactional;
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


    @Transactional
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setUserId(user.getUserId());
                    userDTO.setName(user.getName());
                    userDTO.setEmail(user.getEmail());

                    if (user.getPhoto() != null) {
                        userDTO.setPhoto(user.getPhoto().clone());
                    }

                    return userDTO;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public Optional<UserDTO> getUserById(String userId) {
        return userRepository.findById(userId).map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());


            if (user.getPhoto() != null) {
                userDTO.setPhoto(user.getPhoto().clone());
            }

            return userDTO;
        });
    }



    public User createUser(String userId, String name, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(userId, name, null, hashedPassword, null);
        return userRepository.save(user);
    }


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


    public void deleteUser(String userId) {
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }


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
