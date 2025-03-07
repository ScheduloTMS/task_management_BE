package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.UserDTO;
import com.taskmanagement.task.Entity.User;
import com.taskmanagement.task.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Users retrieved successfully",
                "body", users
        ));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String userId) {
        Optional<UserDTO> user = userService.getUserById(userId);
        return user.map(u -> ResponseEntity.ok(Map.of(
                        "status", 200,
                        "message", "User retrieved successfully",
                        "body", u
                )))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of(
                        "status", 404,
                        "message", "User not found"
                )));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user.getUserId(), user.getName(), user.getPassword());
        return ResponseEntity.ok(Map.of(
                "status", 201,
                "message", "User created successfully",
                "body", createdUser
        ));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String userId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) MultipartFile photo) {
        try {
            User updatedUser = userService.updateUser(userId, email, password, photo);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "User updated successfully",
                    "body", updatedUser
            ));
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "User deleted successfully"
        ));
    }
}
