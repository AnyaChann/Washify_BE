package com.washify.apis.controller;

import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller để test và verify email
 * Public endpoint để check email trước khi register
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "API kiểm tra email hợp lệ")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
     * Quick check - Email format và disposable check
     * GET /api/auth/email/check?email=test@example.com
     * Public endpoint
     */
    @GetMapping("/check")
    @Operation(summary = "Kiểm tra email nhanh", description = "Check format và disposable email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> quickCheck(@RequestParam String email) {
        log.info("Quick email check for: {}", email);
        
        Map<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("validFormat", emailVerificationService.isValidFormat(email));
        result.put("isDisposable", emailVerificationService.isDisposableEmail(email));
        
        boolean isValid = emailVerificationService.isValidFormat(email) 
            && !emailVerificationService.isDisposableEmail(email);
        
        result.put("isValid", isValid);
        
        String message = isValid 
            ? "Email hợp lệ" 
            : "Email không hợp lệ";
        
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
            .success(isValid)
            .message(message)
            .data(result)
            .timestamp(LocalDateTime.now())
            .build());
    }

    /**
     * Full check - Format + Disposable + MX records
     * GET /api/auth/email/verify?email=test@gmail.com
     * Public endpoint
     */
    @GetMapping("/verify")
    @Operation(summary = "Xác thực email đầy đủ", description = "Check format, disposable, và MX records")
    public ResponseEntity<ApiResponse<Map<String, Object>>> fullVerify(@RequestParam String email) {
        log.info("Full email verification for: {}", email);
        
        Map<String, Object> result = new HashMap<>();
        result.put("email", email);
        
        // Step 1: Format check
        boolean validFormat = emailVerificationService.isValidFormat(email);
        result.put("validFormat", validFormat);
        
        if (!validFormat) {
            result.put("isValid", false);
            result.put("reason", "Email format không hợp lệ");
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Email không hợp lệ")
                .data(result)
                .timestamp(LocalDateTime.now())
                .build());
        }
        
        // Step 2: Disposable check
        boolean isDisposable = emailVerificationService.isDisposableEmail(email);
        result.put("isDisposable", isDisposable);
        
        if (isDisposable) {
            result.put("isValid", false);
            result.put("reason", "Không chấp nhận email tạm thời/ảo");
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Email không hợp lệ")
                .data(result)
                .timestamp(LocalDateTime.now())
                .build());
        }
        
        // Step 3: MX records check
        boolean hasMX = emailVerificationService.hasMXRecord(email);
        result.put("hasMXRecords", hasMX);
        
        if (!hasMX) {
            result.put("isValid", false);
            result.put("reason", "Domain không tồn tại hoặc không thể nhận email");
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Email không hợp lệ")
                .data(result)
                .timestamp(LocalDateTime.now())
                .build());
        }
        
        // Get MX records for additional info
        List<String> mxRecords = emailVerificationService.getMXRecords(email);
        result.put("mxRecords", mxRecords);
        
        // All checks passed
        result.put("isValid", true);
        result.put("reason", "Email hợp lệ và có thể nhận email");
        
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
            .success(true)
            .message("Email hợp lệ")
            .data(result)
            .timestamp(LocalDateTime.now())
            .build());
    }

    /**
     * Deep verification with SMTP check
     * WARNING: Use sparingly, can be slow
     * GET /api/auth/email/verify-deep?email=test@gmail.com
     * Public endpoint
     */
    @GetMapping("/verify-deep")
    @Operation(summary = "Xác thực email sâu (SMTP)", 
               description = "Check đầy đủ bao gồm SMTP verification. Chậm, dùng cẩn thận!")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deepVerify(@RequestParam String email) {
        log.info("Deep email verification (SMTP) for: {}", email);
        
        Map<String, Object> result = new HashMap<>();
        result.put("email", email);
        
        // First run full verification
        boolean basicValid = emailVerificationService.validateEmail(email, false);
        result.put("basicValidation", basicValid);
        
        if (!basicValid) {
            result.put("isValid", false);
            result.put("reason", "Email failed basic validation");
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Email không hợp lệ")
                .data(result)
                .timestamp(LocalDateTime.now())
                .build());
        }
        
        // Then run SMTP check
        boolean smtpValid = emailVerificationService.verifyMailboxViaSMTP(email);
        result.put("smtpVerification", smtpValid);
        result.put("isValid", smtpValid);
        
        if (smtpValid) {
            result.put("reason", "Email tồn tại và có thể nhận email (SMTP verified)");
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Email hợp lệ (SMTP verified)")
                .data(result)
                .timestamp(LocalDateTime.now())
                .build());
        } else {
            result.put("reason", "Mailbox không tồn tại hoặc SMTP server từ chối xác thực");
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Email không hợp lệ")
                .data(result)
                .timestamp(LocalDateTime.now())
                .build());
        }
    }
}
