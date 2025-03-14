package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.UserDTO;
import com.gfg.userservice.domain.entity.User;
import com.gfg.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    // Lấy thông tin ra
    @GetMapping("/infor")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        String username = principal.getName(); // Lấy username từ token
        UserDTO userDTO = userService.getUserProfile(username);
        return ResponseEntity.ok(userDTO);
    }
    // Update profile
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody @Valid UserDTO userDTO, HttpServletRequest request, Principal principal){
        try {
            String userName = principal.getName();
            // Gọi service để cập nhật thông tin
            userService.updateUserProfile(userName, userDTO);

            return ResponseEntity.ok().body(Map.of("message", "Cập nhật thông tin thành công!", "status", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Lỗi khi cập nhật thông tin", "status", 400));
        }
    }
}
