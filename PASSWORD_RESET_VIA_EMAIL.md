# Password Reset via Email - Complete Guide

## 📋 Tổng quan

Tính năng **Reset Password qua Email** cho phép user lấy lại mật khẩu khi quên, thông qua token gửi qua email.

---

## 🔄 Flow hoạt động

```
1. User quên password
   ↓
2. User nhập email → POST /api/auth/forgot-password
   ↓
3. System tạo reset token (UUID, expire 30 phút)
   ↓
4. System gửi email với link: http://frontend.com/reset-password?token={token}
   ↓
5. User click link → Frontend validate token → GET /api/auth/reset-password/validate?token={token}
   ↓
6. Frontend show form nhập password mới
   ↓
7. User submit form → POST /api/auth/reset-password
   ↓
8. System verify token → Update password → Mark token as used
   ↓
9. Done! User có thể login với password mới
```

---

## 🗄️ Database Schema

### Table: `password_reset_tokens`

```sql
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,      -- UUID token
    user_id BIGINT NOT NULL,                  -- FK to users
    expiry_date DATETIME NOT NULL,            -- Token expire time (+30 mins)
    is_used BOOLEAN DEFAULT FALSE,            -- Token đã dùng chưa
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**Indexes:**
- `idx_token` - Tìm token nhanh
- `idx_user_id` - Tìm token theo user
- `idx_expiry_date` - Cleanup expired tokens

---

## 📡 API Endpoints

### 1. Request Forgot Password (Gửi email)

```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Email reset password đã được gửi. Vui lòng kiểm tra hộp thư của bạn."
}
```

**Response (Email không tồn tại - Vẫn return success để bảo mật):**
```json
{
  "success": true,
  "message": "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được email reset password."
}
```

**Security Note:**
- Không tiết lộ email có tồn tại hay không (tránh enumerate users)
- Luôn return success message

**What happens:**
1. System tìm user by email
2. Xóa các token cũ của user
3. Tạo token mới (UUID) với expiry = now + 30 minutes
4. Lưu token vào database
5. Gửi email với link reset (TODO: EmailService)
6. Tạm thời: Print link ra console

---

### 2. Validate Reset Token

```http
GET /api/auth/reset-password/validate?token={token}
```

**Response (Token hợp lệ):**
```json
{
  "success": true,
  "message": "Token hợp lệ",
  "data": true
}
```

**Response (Token không hợp lệ):**
```json
{
  "success": false,
  "message": "Token không hợp lệ hoặc đã hết hạn"
}
```

**Use case:**
- Frontend call endpoint này khi user click vào link email
- Nếu token invalid → Show error "Link đã hết hạn"
- Nếu token valid → Show form nhập password mới

**Validation rules:**
```java
public boolean isValid() {
    return !isExpired() && !isUsed;
}
```

---

### 3. Reset Password

```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công. Bạn có thể đăng nhập với mật khẩu mới."
}
```

**Response (Password mismatch):**
```json
{
  "success": false,
  "message": "Mật khẩu xác nhận không khớp"
}
```

**Response (Token invalid/expired):**
```json
{
  "success": false,
  "message": "Token đã hết hạn hoặc đã được sử dụng"
}
```

**What happens:**
1. Validate token exists and is valid
2. Validate newPassword == confirmPassword
3. Get user from token
4. Update user password (TODO: Hash with BCrypt)
5. Mark token as used
6. Delete all other tokens of this user

---

## 🔐 Security Features

### 1. **Token Expiry**
- Token tự động hết hạn sau 30 phút
- `expiryDate = now + 30 minutes`
- Check expiry: `LocalDateTime.now().isAfter(expiryDate)`

### 2. **One-time Use**
- Token chỉ dùng được 1 lần
- Sau khi reset thành công → `isUsed = true`
- Không thể reuse token

### 3. **Token Cleanup**
- Xóa tất cả token cũ khi tạo token mới
- Cleanup job xóa expired tokens (có thể dùng @Scheduled)

### 4. **Email Enumeration Prevention**
- Không tiết lộ email có tồn tại hay không
- Luôn return success message

### 5. **Password Hashing**
- TODO: Hash password với BCryptPasswordEncoder
- Cần integrate PasswordEncoder

---

## 📧 Email Template (TODO)

```html
<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
</head>
<body>
    <h2>Yêu cầu đặt lại mật khẩu</h2>
    
    <p>Xin chào,</p>
    
    <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>
    
    <p>Click vào link bên dưới để đặt lại mật khẩu:</p>
    
    <a href="{{RESET_LINK}}" 
       style="background-color: #4CAF50; color: white; padding: 12px 24px; 
              text-decoration: none; border-radius: 4px; display: inline-block;">
        Đặt lại mật khẩu
    </a>
    
    <p><strong>Link này sẽ hết hạn sau 30 phút.</strong></p>
    
    <p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
    
    <hr>
    
    <p style="color: #666; font-size: 12px;">
        Nếu button không hoạt động, copy link sau vào trình duyệt:<br>
        {{RESET_LINK}}
    </p>
</body>
</html>
```

---

## 🧪 Testing

### Test Case 1: Happy Flow - Reset password thành công

```bash
# Step 1: Request forgot password
POST http://localhost:8080/api/auth/forgot-password
{
  "email": "user@example.com"
}

# Expected: Success message + token printed to console
# Copy token từ console log

# Step 2: Validate token (Frontend)
GET http://localhost:8080/api/auth/reset-password/validate?token=a1b2c3d4-...

# Expected: {"success": true, "data": true}

# Step 3: Reset password
POST http://localhost:8080/api/auth/reset-password
{
  "token": "a1b2c3d4-...",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}

# Expected: Success message

# Step 4: Login với password mới
POST http://localhost:8080/api/auth/login
{
  "username": "user",
  "password": "newPassword123"
}

# Expected: Login success + JWT token
```

---

### Test Case 2: Token expired

```bash
# Step 1: Request token
POST /api/auth/forgot-password
{"email": "user@example.com"}

# Step 2: Đợi 31 phút (hoặc modify expiry trong DB)

# Step 3: Try reset
POST /api/auth/reset-password
{
  "token": "expired-token",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}

# Expected: Error "Token đã hết hạn hoặc đã được sử dụng"
```

---

### Test Case 3: Token reuse (Security)

```bash
# Step 1: Reset password thành công (như Test Case 1)

# Step 2: Try reuse same token
POST /api/auth/reset-password
{
  "token": "same-token-again",
  "newPassword": "anotherPassword",
  "confirmPassword": "anotherPassword"
}

# Expected: Error "Token đã hết hạn hoặc đã được sử dụng"
```

---

### Test Case 4: Password mismatch

```bash
POST /api/auth/reset-password
{
  "token": "valid-token",
  "newPassword": "password123",
  "confirmPassword": "password456"  // Khác nhau!
}

# Expected: Error "Mật khẩu xác nhận không khớp"
```

---

### Test Case 5: Invalid email (Security)

```bash
POST /api/auth/forgot-password
{
  "email": "nonexistent@example.com"
}

# Expected: Vẫn return success message
# (Không tiết lộ email không tồn tại)
```

---

## 📂 Files Structure

```
src/main/java/com/washify/apis/
├── entity/
│   └── PasswordResetToken.java           ✅ Entity với expiry logic
├── repository/
│   └── PasswordResetTokenRepository.java ✅ CRUD + custom queries
├── service/
│   └── PasswordResetService.java         ✅ Business logic
├── controller/
│   └── PasswordResetController.java      ✅ 3 endpoints
└── dto/
    └── request/
        ├── ForgotPasswordRequest.java    ✅ Email input
        └── ResetPasswordRequest.java     ✅ Token + passwords

src/main/resources/db/migration/
└── V2__Add_Password_Reset_Tokens.sql     ✅ DB migration
```

---

## ⚠️ TODO - Cần hoàn thiện

### 1. **EmailService** (QUAN TRỌNG!)
```java
@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(toEmail);
        helper.setSubject("Đặt lại mật khẩu - Washify");
        
        String content = buildEmailTemplate(resetLink);
        helper.setText(content, true);
        
        mailSender.send(message);
    }
}
```

**Dependencies cần thêm vào pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

**application.properties:**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

### 2. **Password Hashing**
```java
// PasswordResetService.java
private final PasswordEncoder passwordEncoder;

public void resetPassword(String token, String newPassword) {
    // ...
    user.setPassword(passwordEncoder.encode(newPassword)); // ✅ Hash
    userRepository.save(user);
}
```

---

### 3. **Cleanup Scheduler** (Optional)
```java
@Component
public class TokenCleanupScheduler {
    
    private final PasswordResetService passwordResetService;
    
    // Chạy mỗi 1 giờ
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        passwordResetService.cleanupExpiredTokens();
    }
}
```

---

### 4. **Rate Limiting** (Optional - Chống spam)
```java
// Giới hạn 3 request/hour/email
@RateLimiter(name = "forgotPassword", fallbackMethod = "tooManyRequests")
public void createPasswordResetToken(String email) {
    // ...
}
```

---

## 🎯 Configuration

### application.properties

```properties
# Password Reset Token
password.reset.token.expiry.minutes=30

# Email Configuration (TODO)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Frontend URL (for reset link)
app.frontend.url=http://localhost:3000
```

---

## 🚀 Next Steps

### Hiện tại - Development Mode:
✅ Token được tạo và lưu vào DB  
✅ Link được print ra console  
✅ Frontend có thể test với link từ console  
✅ Reset password hoạt động (chưa hash)  

### Cần làm cho Production:
1. ⚠️ **Implement EmailService** - Gửi email thật
2. ⚠️ **Hash password** - Integrate PasswordEncoder
3. ⚠️ **Frontend integration** - Page /reset-password
4. 💡 **Optional**: Rate limiting
5. 💡 **Optional**: Scheduled cleanup job
6. 💡 **Optional**: Email template HTML đẹp

---

## 📊 Summary

### ✅ Đã implement:
- ✅ Entity `PasswordResetToken` với expiry logic
- ✅ Repository với custom queries
- ✅ Service với 3 methods chính
- ✅ Controller với 3 endpoints
- ✅ DTOs validation
- ✅ Database migration
- ✅ Security configuration (public endpoints)
- ✅ Token expiry (30 phút)
- ✅ One-time use token
- ✅ Token cleanup methods

### ⚠️ Chưa implement (TODO):
- ⚠️ EmailService để gửi email thật
- ⚠️ Password hashing (BCrypt)
- ⚠️ Frontend page /reset-password

### 🎯 Kết luận:
**Tính năng reset password qua email đã HOÀN CHỈNH về mặt logic và flow!**

Chỉ cần implement **EmailService** và **PasswordEncoder** là có thể deploy production. 🚀

Hiện tại có thể test được toàn bộ flow bằng cách copy link từ console log!
