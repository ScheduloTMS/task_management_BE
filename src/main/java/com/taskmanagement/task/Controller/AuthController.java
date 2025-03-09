package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.LoginRequest;
import com.taskmanagement.task.DTO.LoginResponse;
import com.taskmanagement.task.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        try {
            String token = authService.login(loginRequest.getUserId(), loginRequest.getPassword());
            loginResponse.setStatusCode(HttpStatus.OK.value());
            loginResponse.setMessage("Login successful");
            loginResponse.setResponse(token);
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            loginResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            loginResponse.setMessage("Login failed");
            loginResponse.setResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
    }
}