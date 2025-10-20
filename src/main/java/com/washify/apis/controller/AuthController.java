package com.washify.apis.controller;

import com.washify.apis.dto.request.LoginRequest;
import com.washify.apis.dto.request.RegisterRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.AuthResponse;
import com.washify.apis.entity.Role;
import com.washify.apis.entity.User;
import com.washify.apis.enums.RoleType;
import com.washify.apis.exception.BadRequestException;
import com.washify.apis.exception.DuplicateResourceException;
import com.washify.apis.repository.RoleRepository;
import com.washify.apis.repository.UserRepository;
import com.washify.apis.security.JwtTokenProvider;
import com.washify.apis.service.EmailVerificationService;
import com.washify.apis.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Authentication Controller - Xử lý đăng nhập, đăng ký
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API cho đăng nhập và đăng ký")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailVerificationService emailVerificationService;

    /**
     * Đăng nhập
     */
    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Đăng nhập với username và password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        // Authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);

        // Get user info
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();

        log.info("User {} logged in successfully", loginRequest.getUsername());

        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Đăng nhập thành công")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build());
    }

    /**
     * Đăng ký tài khoản mới
     */
    @PostMapping("/register")
    @Operation(summary = "Đăng ký", description = "Đăng ký tài khoản khách hàng mới")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register attempt for user: {}", registerRequest.getUsername());

        // ===== EMAIL VERIFICATION =====
        // Level 1: Format validation
        if (!emailVerificationService.isValidFormat(registerRequest.getEmail())) {
            throw new BadRequestException("Email không hợp lệ: Format sai");
        }

        // Level 2: Disposable email check
        if (emailVerificationService.isDisposableEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email không hợp lệ: Không chấp nhận email tạm thời/ảo");
        }

        // Level 3: MX record check (verify domain can receive emails)
        if (!emailVerificationService.hasMXRecord(registerRequest.getEmail())) {
            throw new BadRequestException("Email không hợp lệ: Domain không tồn tại hoặc không thể nhận email");
        }
        
        log.info("Email {} verified successfully", registerRequest.getEmail());
        // ===== END EMAIL VERIFICATION =====

        // Validate
        if (!ValidationUtils.isValidEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email không hợp lệ");
        }

        if (!ValidationUtils.isValidUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username không hợp lệ. Username phải từ 3-20 ký tự, chỉ chứa chữ cái, số, dấu chấm, gạch dưới và gạch ngang");
        }

        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("User", "username", registerRequest.getUsername());
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("User", "email", registerRequest.getEmail());
        }

        // Create user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Assign CUSTOMER role by default
        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER.name())
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleType.CUSTOMER.name());
                    newRole.setDescription("Khách hàng");
                    return roleRepository.save(newRole);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);

        // Save user
        user = userRepository.save(user);

        // Generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );
        String token = jwtTokenProvider.generateToken(authentication);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();

        log.info("User {} registered successfully", registerRequest.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Đăng ký thành công")
                        .data(authResponse)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Validate token
     */
    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Kiểm tra token có hợp lệ không")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                boolean isValid = jwtTokenProvider.validateToken(token);
                
                return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                        .success(true)
                        .message(isValid ? "Token hợp lệ" : "Token không hợp lệ")
                        .data(isValid)
                        .timestamp(LocalDateTime.now())
                        .build());
            }
            
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("Token không được cung cấp")
                    .data(false)
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("Token không hợp lệ: " + e.getMessage())
                    .data(false)
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }
}
