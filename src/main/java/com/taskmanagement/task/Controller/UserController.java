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
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        Optional<UserDTO> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user.getUserId(), user.getName(), user.getPassword()));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
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
            return ResponseEntity.badRequest().body(Map.of("status", 400, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}