package com.taskmanagement.task.Service;

import com.taskmanagement.task.DTO.UserDTO;
import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Users createUser(UserDTO userDTO) {

        String rolePrefix = userDTO.getRole().equals("MENTOR") ? "MT" : "ST";
        String lastUserId = userRepository.findLastUserIdByRole(rolePrefix);
        int nextId = lastUserId == null ? 1 : Integer.parseInt(lastUserId.substring(2)) + 1;
        String newUserId = String.format("%s%03d", rolePrefix, nextId);


        Users user = new Users();
        user.setUserId(newUserId);
        user.setName(userDTO.getName());
        user.setPassword(passwordEncoder.encode("TMS@123"));
        user.setRole(userDTO.getRole());
        user.setEmail(userDTO.getEmail());
        return userRepository.save(user);
    }


    public Users updateUser(Users user) {
        Users existingUser = userRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPhoto() != null) {
            existingUser.setPhoto(user.getPhoto());
        }

        return userRepository.save(existingUser);
    }


    public void deleteUser(String userId) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }


    public Users getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void updatePassword(String userId, String currentPassword, String newPassword) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Current password is incorrect");
        }
    }
}