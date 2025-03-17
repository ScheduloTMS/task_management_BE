package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.ApiResponse;
import com.taskmanagement.task.DTO.UserDTO;
import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserDTO userDTO, @AuthenticationPrincipal UserDetails userDetails) {
        Users currentUser = userService.getUserById(userDetails.getUsername());
        if (currentUser.getRole().equals("MENTOR")) {
            Users createdUser = userService.createUser(userDTO);
            return ResponseEntity.ok(new ApiResponse(
                    HttpStatus.OK.value(),
                    "User created successfully with default password.",
                    mapToUserDTO(createdUser)
            ));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(
                    HttpStatus.FORBIDDEN.value(),
                    "Only mentors can create users.",
                    null
            ));
        }
    }

    @PutMapping("/profile")
    @Transactional
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        String currentUserId = userDetails.getUsername();
        Users user = userService.getUserById(currentUserId);


        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }


        if (photo != null && !photo.isEmpty()) {
            user.setPhoto(photo.getBytes());
        }


        if (currentPassword != null && newPassword != null) {
            if (userService.validateAndUpdatePassword(currentUserId, currentPassword, newPassword)) {
                return ResponseEntity.ok(new ApiResponse(
                        HttpStatus.OK.value(),
                        "Profile and password updated successfully.",
                        mapToUserDTO(user)
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Current password is incorrect.",
                        null
                ));
            }
        }


        Users updatedUser = userService.updateUser(user);

        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.OK.value(),
                "Profile updated successfully.",
                mapToUserDTO(updatedUser)
        ));
    }


    @DeleteMapping("/delete/{userId}")
    @Transactional
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId, @AuthenticationPrincipal UserDetails userDetails) {
        Users currentUser = userService.getUserById(userDetails.getUsername());
        if (currentUser.getRole().equals("MENTOR")) {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse(
                    HttpStatus.OK.value(),
                    "User deleted successfully.",
                    null
            ));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(
                    HttpStatus.FORBIDDEN.value(),
                    "Only mentors can delete users.",
                    null
            ));
        }
    }

    @GetMapping("/profile")
    @Transactional
    public ResponseEntity<ApiResponse> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        Users user = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.OK.value(),
                "User profile retrieved successfully.",
                mapToUserDTO(user)
        ));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse(
                HttpStatus.OK.value(),
                "All users retrieved successfully.",
                userDTOs
        ));
    }


    private UserDTO mapToUserDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoto(user.getPhoto());
        return userDTO;
    }
}