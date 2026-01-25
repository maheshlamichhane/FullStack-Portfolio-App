//package com.portfolio.app.controller;
//
//
//import com.portfolio.app.dto.*;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
//@Tag(name = "Authentication", description = "User authentication and authorization APIs")
//@Slf4j
//public class AuthController {
//
//    private final AuthService authService;
//
//    @PostMapping("/register")
//    @Operation(summary = "Register a new user")
//    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
//        UserResponseDTO user = authService.register(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(user);
//    }
//
//    @PostMapping("/login")
//    @Operation(summary = "User login")
//    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
//        AuthResponseDTO response = authService.login(request);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/refresh-token")
//    @Operation(summary = "Refresh authentication token")
//    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
//        AuthResponseDTO response = authService.refreshToken(request);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/logout")
//    @Operation(summary = "User logout")
//    public ResponseEntity<Map<String, String>> logout(@RequestBody LogoutRequestDTO request) {
//        authService.logout(request);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Logged out successfully");
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/forgot-password")
//    @Operation(summary = "Request password reset")
//    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
//        authService.forgotPassword(request);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Password reset instructions sent to email");
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/reset-password")
//    @Operation(summary = "Reset password")
//    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
//        authService.resetPassword(request);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Password reset successfully");
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/profile")
//    @Operation(summary = "Get current user profile")
//    public ResponseEntity<UserResponseDTO> getCurrentUser() {
//        UserResponseDTO user = authService.getCurrentUser();
//        return ResponseEntity.ok(user);
//    }
//
//    @PutMapping("/profile")
//    @Operation(summary = "Update user profile")
//    public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UpdateProfileRequestDTO request) {
//        UserResponseDTO user = authService.updateProfile(request);
//        return ResponseEntity.ok(user);
//    }
//
//    @PostMapping("/change-password")
//    @Operation(summary = "Change password")
//    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
//        authService.changePassword(request);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Password changed successfully");
//
//        return ResponseEntity.ok(response);
//    }
//}
