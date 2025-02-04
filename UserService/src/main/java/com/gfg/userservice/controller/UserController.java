package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.UserDTO;
import com.gfg.userservice.domain.dto.UserFullNameDTO;
import com.gfg.userservice.domain.entity.User;
import com.gfg.userservice.repository.UserRepository;
import com.gfg.userservice.response.ResponseCollectionDTO;
import com.gfg.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;
    private final UserRepository userRepository;


    @GetMapping("/test")
    public String test() {
        return "Service is running";
    }

    @GetMapping
    public ResponseEntity<ResponseCollectionDTO<UserDTO>> findAll() {
        log.info("UserDto List, controller; fetch all users");
        return ResponseEntity.ok(new ResponseCollectionDTO<>(this.userService.findAll()));

    }

    @PostMapping
    public ResponseEntity<UserDTO> save(@RequestBody
                                        @NotNull(message = "Input must not null")
                                        @Valid final UserDTO userDTO) {
        log.info("*** UserDto, save the user *");
        return ResponseEntity.ok(this.userService.save(userDTO));

    }

    @PutMapping
    public ResponseEntity<UserDTO> update(
            @RequestBody
            @NotNull(message = "Input must not null")
            @Valid final UserDTO userDTO
    ) {
        log.info("*** UserDto, resource; update user *");
        return ResponseEntity.ok(this.userService.update(userDTO));

    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> update(
            @PathVariable("userId")
            @NotBlank(message = "Input must not blank") final String userId,
            @RequestBody
            @NotNull(message = "Input must not NULL")
            @Valid final UserDTO userDto) {
        log.info("*** UserDto, resource; update user with userId *");
        return ResponseEntity.ok(this.userService.update(Integer.parseInt(userId.strip()), userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("userId") @NotBlank(message = "Input must not blank") @Valid final String userId) {
        log.info("*** Boolean, resource; delete user by id *");
        this.userService.deleteById(Integer.parseInt(userId));
        return ResponseEntity.ok(true);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> findByUsername(
            @PathVariable("username")
            @NotBlank(message = "Input must not blank")
            @Valid final String username) {
        return ResponseEntity.ok(this.userService.findByUsername(username));
    }

    //API đếm số lượng người đăng ký
    @GetMapping("/countUser")
    public ResponseEntity<Map<String, Object>> getCountUser() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUserCount());
        return ResponseEntity.ok(stats);
    }

    // Lấy fullName người dùng
    @GetMapping("fullName/{userId}")
    public ResponseEntity<UserFullNameDTO> getUserById(@PathVariable Integer userId) {
        User user = userRepository.findUserByUserId(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Chuyển đổi từ User sang UserSummaryDTO
        UserFullNameDTO userFullName = new UserFullNameDTO(user.getUserId(), user.getFullName());
        return ResponseEntity.ok(userFullName);
    }

    // Lấy list all user in admin
    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

}
