package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.domain.dto.RegisterRequest;
import com.gfg.userservice.domain.dto.UserDTO;
import com.gfg.userservice.domain.dto.account.ChangePasswordRequest;
import com.gfg.userservice.domain.dto.account.LoginRequest;
import com.gfg.userservice.domain.dto.account.LoginResponse;
import com.gfg.userservice.domain.entity.Credential;
import com.gfg.userservice.domain.entity.User;
import com.gfg.userservice.domain.enums.RoleBasedAuthority;
import com.gfg.userservice.exceptions.CredentialNotFoundException;
import com.gfg.userservice.exceptions.UserObjectNotFoundException;
import com.gfg.userservice.helperClass.UserMapping;
import com.gfg.userservice.repository.CredentialRepository;
import com.gfg.userservice.repository.UserRepository;
import com.gfg.userservice.security.JwtUtil;
import com.gfg.userservice.service.EmailService;
import com.gfg.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> findAll() {
        log.info("*****find all user*****");

        return this.userRepository.findAll()
                .stream().map(UserMapping::map)
                .distinct().collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Integer userId) {
        log.info("*****find by id*******");
        return this.userRepository.findById(userId)
                .map(UserMapping::map)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User not found with is id: %d",userId)));
    }


//    @Override
//    public UserDTO save(final UserDTO userDto) {
//        log.info("*** UserDto, service; save user *");
//        return UserMapping.map(this.userRepository.save(UserMapping.map(userDto)));
//    }


    @Override
    public UserDTO save(final UserDTO userDto) {
        log.info("*** UserDto, service; save user *");
        return UserMapping.map(this.userRepository.save(UserMapping.map(userDto)));
    }



    @Override
    public UserDTO update(UserDTO userDTO) {
        log.info("************update the user******************");
        return UserMapping.map(this.userRepository.save(UserMapping.map(userDTO)));

    }

    @Override
    public UserDTO update(Integer userId, UserDTO userDTO) {
        log.info("*** UserDto, service; update user with userId *");
        return UserMapping.map(this.userRepository.save(
                UserMapping.map(this.findById(userId))));
    }

    @Override
    public void deleteById(Integer userId) {
        log.info("*** Void, service; delete user by id *");
        this.userRepository.deleteById(userId);
    }

    @Override
    public UserDTO findByUsername(String username) {
        log.info("*** UserDto, service; fetch user with username *");
        return UserMapping.map(this.userRepository.findByCredentialUsername(username)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with username: %s not found", username))));
    }
// Kích hoạt tài khoản
    @Override
    public void activateAccount(String token) {
        String email = jwtUtil.extractUsername(token); // Sử dụng email để xác thực người dùng từ token
        if (email == null || !jwtUtil.validateToken(token, email)) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserObjectNotFoundException("User not found."));

        Credential credential = user.getCredential();
        if (credential.getIsEnabled()) {
            throw new IllegalArgumentException("The account is already activated.");
        }

        credential.setIsEnabled(true); // Kích hoạt tài khoản
        credential.setIsAccountNonLocked(true); // Tài Khoản được mở Khóa
        credentialRepository.save(credential);
    }
    // tạo password ngẫu nhiên
    private String generateRandomPassword() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Tạo số ngẫu nhiên từ 000000 đến 999999
    }
    @Override
    public void forgotPassword(String email) {
        // Kiểm tra email tồn tại
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email address not found."));

        // Tạo mật khẩu ngẫu nhiên 6 chữ số
        String newPassword = generateRandomPassword();

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(newPassword);

        // Cập nhật mật khẩu trong cơ sở dữ liệu
        Credential credential = user.getCredential();
        credential.setPassword(encodedPassword);
        userRepository.save(user);

        // Gửi email thông báo
        String emailContent = "Dear " + user.getCredential().getUsername() + ",\n\n"
                + "Your password has been reset. Here is your new password: " + newPassword + "\n\n"
                + "Please log in and change your password immediately for security purposes.\n\n"
                + "Best regards,\nYour App Team";
        emailService.sendSimpleMessage(email, "Password Reset XanhShop", emailContent);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        validateResetToken(token);
        updatePassword(token, newPassword);
    }

    // Đăng ký
    @Override
    public void register(RegisterRequest registerRequest) {
        // Kiểm tra username/email đã tồn tại
        if (credentialRepository.existsByUsername(registerRequest.getUser())) {
            throw new IllegalArgumentException("Username already exists.");

        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        // Kiểm tra độ dài của mật khẩu nhập vào
        if (registerRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }
        // Kiểm tra độ dài của userName nhập vào
        if (registerRequest.getUser().length() < 6) {
            throw new IllegalArgumentException("New userName must be at least 6 characters long.");
        }
        // Tạo User
        User user = new User();
        user.setFullName(registerRequest.getUser());
        user.setEmail(registerRequest.getEmail());
        // Lưu vào cơ sở dữ liệu
        userRepository.save(user);

        // Tạo Credential
        Credential credential = new Credential();
        credential.setUsername(registerRequest.getUser());
        credential.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        credential.setRoleBasedAuthority(RoleBasedAuthority.ROLE_USER); // Mặc định role là USER
        credential.setIsEnabled(false); // Tài khoản chưa kích hoạt
        credential.setIsAccountNonLocked(false); // Tài Khoản chưa được mở khóa
        credential.setIsAccountNonExpired(true);
        credential.setIsCredentialsNonExpired(true);
        credential.setUser(user);

        // Lưu vào cơ sở dữ liệu
        credentialRepository.save(credential);

        // Tạo token kích hoạt tài khoản
        String activationToken = jwtUtil.generateToken(registerRequest.getEmail());

        // Gửi email kích hoạt tài khoản
        String emailContent = "Dear " + registerRequest.getUser() + ",\n\n"
                + "Thank you for registering. Please click the link below to activate your account:\n"
                + "http://localhost:9050/user-service/api/account/activate?token=" + activationToken + "\n\n"
                + "Best regards,\nYour App Team";
        emailService.sendSimpleMessage(registerRequest.getEmail(), "Account Xanh Shop", emailContent);
    }

    private String generateActivationToken(String email) {
        return jwtUtil.generateToken(email);
    }

    private String generateResetToken(String email) {
        return jwtUtil.generateToken(email);
    }

    private void validateResetToken(String token) {
        String username = jwtUtil.extractUsername(token);
        if (username == null || !jwtUtil.validateToken(token, username)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
    }

    private void updatePassword(String token, String newPassword) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByCredentialUsername(username)
                .orElseThrow(() -> new UserObjectNotFoundException("User not found"));
        user.getCredential().setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);
    }

// Đăng nhập
    public LoginResponse login(LoginRequest loginRequest){
        // Kiểm tra username
        Credential credential = credentialRepository.findByUsername(loginRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));

        // Kiểm tra password
        if (!passwordEncoder.matches(loginRequest.getPassword(), credential.getPassword())) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        // Kiểm tra trạng thái tài khoản đã kích hoạt chưa
        if (!credential.getIsEnabled()) {
            throw new IllegalArgumentException("Account is not activated. Please check your email for activation.");
        }

        // Kiểm tra tài khoản có bị khóa không
        if (!credential.getIsAccountNonLocked()) {
            throw new IllegalArgumentException("Sorry, Your account has been locked.");
        }

        // Kiểm tra quyền hạn (role)
        String role = credential.getRoleBasedAuthority().name(); // Giả sử là ROLE_USER hoặc ROLE_ADMIN

        // Tạo token JWT
        String token = jwtUtil.generateToken(credential.getUsername());

        // Trả về thông tin đăng nhập cùng với role
        return new LoginResponse(credential.getUsername(), token, role);
    }

    // Đổi mật khẩu
    public void changePassword(ChangePasswordRequest request){
        // Tìm user theo username
        Credential credential = credentialRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("UserName not found."));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), credential.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // Kiểm tra độ dài của mật khẩu mới
        if (request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        // Cập nhật mật khẩu mới
        credential.setPassword(passwordEncoder.encode(request.getNewPassword()));
        credentialRepository.save(credential);
    }

// Profile Hiển thị thông tin
    @Override
    public UserDTO getUserProfile(String userName) {
        Credential credential = credentialRepository.findByUsername(userName)
                .orElseThrow(()-> new CredentialNotFoundException("Credential not found"));
        User user = userRepository.findByCredentialUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserDTO.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .sex(user.getSex())
                .email(user.getEmail())
                .phone(user.getPhone())
                .adress(user.getAdress())
                .birthday(user.getBirthday())
                .imageUrl(user.getImageUrl()) // Trả về chuỗi Base64 của avatar
                .role(credential.getRoleBasedAuthority()) // Trả về quyền hạn
                .build();
    }
// update profile
    @Override
    public void updateUserProfile(String userName, UserDTO userDTO) {
        User user = userRepository.findByCredentialUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Cập nhật thông tin cá nhân
        if (userDTO.getFullName() != null) user.setFullName(userDTO.getFullName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getPhone() != null) user.setPhone(userDTO.getPhone());
        if (userDTO.getAdress() != null) user.setAdress(userDTO.getAdress());
        if (userDTO.getBirthday() != null) user.setBirthday(userDTO.getBirthday());
        if (userDTO.getSex() != null) user.setSex(userDTO.getSex());

        // Cập nhật avatar (nếu có)
        if (userDTO.getImageUrl() != null) {
            user.setImageUrl(userDTO.getImageUrl());
        }

        // Lưu thông tin đã cập nhật
        userRepository.save(user);
    }

    @Override
    public long getUserCount() {
        return userRepository.countUser();
    }

    // list user in admin
    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAllUsersWithAccountStatus();
        return users.stream()
                .map(UserMapping::map)
                .collect(Collectors.toList());
    }

    // get map userId : fullName
    @Override
    public Map<Integer, String> getFullNames(List<Integer> userIds) {
        List<User> users = userRepository.findByUserIdIn(userIds);
        return users.stream().collect(Collectors.toMap(User::getUserId, User::getFullName));
    }

}
