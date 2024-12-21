package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.RegisterRequest;
import com.gfg.userservice.domain.dto.account.ChangePasswordRequest;
import com.gfg.userservice.domain.dto.account.LoginRequest;
import com.gfg.userservice.domain.dto.account.LoginResponse;
import com.gfg.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;
// Đăng ký
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterRequest registerRequest){
        userService.register(registerRequest);
        return ResponseEntity.ok("User registered successfully. Please check your email to activate your account.");
    }
// Xác thực tài khoản
    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        try {
            userService.activateAccount(token); // Gọi method trong service để kích hoạt tài khoản
            return ResponseEntity.ok("Chúc Mừng Bạn Đã Đăng Ký Tài Khoản Thành Công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest){
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok("A new password has been sent to your email address.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.ok("Password updated successfully");
    }
}
