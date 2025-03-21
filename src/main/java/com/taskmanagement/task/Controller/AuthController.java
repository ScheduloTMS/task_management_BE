package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.ApiResponse;
import com.taskmanagement.task.DTO.LoginRequest;
import com.taskmanagement.task.Service.AuthService;
import com.taskmanagement.task.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest.getUserId(), loginRequest.getPassword());

            String successMessage = "User " + loginRequest.getUserId() + " logged in successfully";

            return ResponseEntity.ok(new ApiResponse(
                    "success",
                    HttpStatus.OK.value(),
                    successMessage,
                    token
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse(
                            "error",
                            HttpStatus.UNAUTHORIZED.value(),
                            e.getMessage(),
                            null
                    )
            );
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                authService.logout(jwt);

                return ResponseEntity.ok(new ApiResponse(
                        "success",
                        HttpStatus.OK.value(),
                        "Logout successful",
                        null
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ApiResponse(
                                "error",
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid token format",
                                null
                        )
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse(
                            "error",
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    )
            );
        }
    }
}