package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.ApiResponse;
import com.taskmanagement.task.DTO.UserDTO;
import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserDTO userDTO,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Users currentUser = userService.getUserByEmail(userDetails.getUsername());
        if (!"MENTOR".equals(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse("error", 403, "Only mentors can create users.", null)
            );
        }

        if (userService.isUserExists(userDTO.getUserId(), userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse("error", 409, "User with same ID or email already exists.", null)
            );
        }

        Users createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse("success", 201, "User created successfully.", mapToUserDTO(createdUser))
        );
    }

    @PutMapping("/profile")
    @Transactional
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader("Authorization") String token) throws IOException {

        String currentUserEmail = userDetails.getUsername();
        Users user = userService.getUserByEmail(currentUserEmail);

        boolean passwordChanged = false;

        if (currentPassword != null && newPassword != null && confirmPassword != null) {

            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ApiResponse("error", 400, "New password and confirm password do not match", null)
                );
            }

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ApiResponse("error", 400, "Current password is incorrect", null)
                );
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            passwordChanged = true;

            if (user.isFirstLogin()) {
                user.setFirstLogin(false);
            }
        }


        if (photo != null && !photo.isEmpty()) {
            user.setPhoto(photo.getBytes());
        }

        userService.updateUser(user);

        if (passwordChanged) {
            return ResponseEntity.ok(new ApiResponse(
                    "success",
                    200,
                    user.isFirstLogin()
                            ? "Password changed successfully"
                            : "Password update successful. Please login again.",
                    Map.of("requireReauthentication", !user.isFirstLogin())
            ));
        }

        return ResponseEntity.ok(new ApiResponse(
                "success", 200, "Profile updated successfully", null
        ));
    }


    @DeleteMapping("/{userId}")
    @Transactional
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Users currentUser = userService.getUserByEmail(userDetails.getUsername());
            if (!"MENTOR".equals(currentUser.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ApiResponse("error", 403, "Only mentors can delete users.", null)
                );
            }

            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse(
                    "success", 200, "User deleted successfully.", null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("error", 500, e.getMessage(), null)
            );
        }
    }

    @GetMapping("/profile")
    @Transactional
    public ResponseEntity<ApiResponse> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(
                "success", 200, "User profile retrieved successfully.", mapToUserDTO(user)
        ));
    }

    @GetMapping
    @Transactional

    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllUsers().stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse(
                "success", 200, "All users retrieved successfully.", userDTOs
        ));
    }

    private UserDTO mapToUserDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setPhoto(user.getPhoto());
        return userDTO;
    }
}
