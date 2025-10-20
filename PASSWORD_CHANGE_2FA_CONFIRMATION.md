# Tính năng: Xác nhận qua Email khi Bật/Tắt Bảo mật 2 lớp

## 📋 Tổng quan
Tính năng này yêu cầu xác nhận qua email trước khi bật hoặc tắt bảo mật 2 lớp cho việc đổi mật khẩu. Điều này tăng cường bảo mật bằng cách đảm bảo chỉ chủ sở hữu tài khoản thực sự mới có thể thay đổi cài đặt quan trọng này.

## 🔐 Luồng hoạt động

### Khi người dùng muốn BẬT bảo mật 2 lớp:
1. **Request**: User gửi request bật 2FA
   ```
   PUT /api/users/{id}/security/password-change-2fa?enable=true
   ```

2. **Token Generation**: Hệ thống tạo token và lưu vào DB
   - Token có thời hạn 30 phút
   - Lưu trạng thái `enable_2fa = true`

3. **Email Gửi**: Email xác nhận được gửi tới user
   - Chủ đề: "🔒 Xác nhận bật bảo mật 2 lớp"
   - Màu xanh lá, biểu tượng khóa
   - Có hộp thông tin giải thích lợi ích của 2FA

4. **Xác nhận**: User click link trong email
   ```
   POST /api/auth/security/2fa-toggle/confirm?token={TOKEN}
   ```

5. **Kích hoạt**: Hệ thống cập nhật `requireEmailVerificationForPasswordChange = true`

### Khi người dùng muốn TẮT bảo mật 2 lớp:
1. **Request**: User gửi request tắt 2FA
   ```
   PUT /api/users/{id}/security/password-change-2fa?enable=false
   ```

2. **Token Generation**: Hệ thống tạo token và lưu vào DB
   - Token có thời hạn 30 phút
   - Lưu trạng thái `enable_2fa = false`

3. **Email Gửi**: Email cảnh báo được gửi tới user
   - Chủ đề: "⚡ Xác nhận tắt bảo mật 2 lớp"
   - Màu cam, biểu tượng tia chớp
   - Có hộp cảnh báo về việc giảm bảo mật

4. **Xác nhận**: User click link trong email
   ```
   POST /api/auth/security/2fa-toggle/confirm?token={TOKEN}
   ```

5. **Vô hiệu hóa**: Hệ thống cập nhật `requireEmailVerificationForPasswordChange = false`

## 🗂️ Cấu trúc Code

### 1. Entity: `PasswordChange2FAToken.java`
```java
@Entity
@Table(name = "password_change_2fa_tokens")
public class PasswordChange2FAToken {
    private Long id;
    private String token;        // UUID
    private User user;           // FK -> users
    private boolean enable2FA;   // true = bật, false = tắt
    private LocalDateTime expiryDate;
    private boolean isUsed;
    private LocalDateTime createdAt;
}
```

**Validation Methods:**
- `isExpired()`: Kiểm tra token đã hết hạn chưa
- `isValid()`: Kiểm tra token còn hợp lệ không (chưa hết hạn và chưa dùng)

### 2. Repository: `PasswordChange2FATokenRepository.java`
```java
public interface PasswordChange2FATokenRepository extends JpaRepository<...> {
    Optional<PasswordChange2FAToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
    void deleteByUser(User user);
}
```

### 3. Service: `PasswordChange2FAService.java`
**Main Methods:**

#### a. `request2FAToggle(userId, enable)`
- Tìm user theo ID
- Kiểm tra nếu đã ở trạng thái yêu cầu → throw exception
- Xóa token cũ của user (nếu có)
- Tạo token mới với thời hạn 30 phút
- Gửi email xác nhận
- Return void

#### b. `validate2FAToggleToken(token)`
- Tìm token trong DB
- Kiểm tra token có tồn tại không
- Kiểm tra token còn valid không (chưa hết hạn, chưa dùng)
- Return boolean

#### c. `confirm2FAToggle(token)`
- Validate token (throw exception nếu invalid)
- Lấy thông tin user và enable2FA từ token
- Cập nhật `user.requireEmailVerificationForPasswordChange = enable2FA`
- Đánh dấu token đã sử dụng (`isUsed = true`)
- Lưu vào DB
- Return void

#### d. `cleanupExpiredTokens()`
- Xóa tất cả token đã hết hạn
- Nên chạy định kỳ (scheduled task)

### 4. Controller: `PasswordChange2FAController.java`
**Public Endpoints** (không cần JWT authentication):

#### a. `GET /api/auth/security/2fa-toggle/validate?token={token}`
```json
// Success response
{
  "message": "Token hợp lệ",
  "success": true
}

// Error response
{
  "message": "Token không hợp lệ hoặc đã hết hạn",
  "success": false
}
```

#### b. `POST /api/auth/security/2fa-toggle/confirm?token={token}`
```json
// Success response
{
  "message": "Cài đặt bảo mật 2 lớp đã được cập nhật thành công",
  "success": true
}
```

### 5. UserController Update
**Protected Endpoint** (cần JWT authentication):

#### `PUT /api/users/{id}/security/password-change-2fa?enable={true|false}`
```java
// OLD logic (instant toggle):
userService.togglePasswordChangeEmailVerification(id, enable);

// NEW logic (send email confirmation):
passwordChange2FAService.request2FAToggle(id, enable);

// Response message:
"Email xác nhận đã được gửi. Vui lòng kiểm tra hộp thư để hoàn tất việc bật/tắt bảo mật 2 lớp."
```

### 6. Email Template: `EmailService.java`
**Method**: `send2FAToggleConfirmationEmail(toEmail, token, enable)`

**Khi BẬT 2FA** (`enable = true`):
- 🎨 Màu xanh lá (#27ae60)
- 🔒 Icon khóa
- 📦 Info Box: Giải thích lợi ích của 2FA
- 🔗 Button: "Xác nhận bật bảo mật 2 lớp"

**Khi TẮT 2FA** (`enable = false`):
- 🎨 Màu cam (#e67e22)
- ⚡ Icon tia chớp
- ⚠️ Warning Box: Cảnh báo giảm bảo mật
- 🔗 Button: "Xác nhận tắt bảo mật 2 lớp"

## 💾 Database Migration

### V5__Add_Password_Change_2FA_Tokens.sql
```sql
CREATE TABLE password_change_2fa_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    enable_2fa BOOLEAN NOT NULL,
    expiry_date DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_2fa_token_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expiry (expiry_date)
);
```

## 🔒 Security Features

### 1. Token Security
- ✅ UUID random token (không đoán được)
- ✅ Thời hạn 30 phút
- ✅ One-time use (đánh dấu `isUsed` sau khi dùng)
- ✅ Tự động cleanup tokens hết hạn

### 2. Access Control
- ✅ Toggle endpoint: Cần JWT + (ADMIN role hoặc chính user đó)
- ✅ Confirm endpoint: Public (xác thực qua token trong email)

### 3. Business Logic Validation
- ✅ Kiểm tra nếu đã ở trạng thái yêu cầu
- ✅ Xóa token cũ trước khi tạo token mới
- ✅ Validate token trước khi confirm

### 4. User Experience
- ✅ Email templates khác nhau cho bật/tắt
- ✅ Thông báo rõ ràng về hành động
- ✅ Hướng dẫn và giải thích trong email

## 🧪 Testing Flow

### Test Case 1: Bật 2FA thành công
```bash
# Step 1: Request bật 2FA (cần JWT token)
PUT http://localhost:8080/api/users/1/security/password-change-2fa?enable=true
Authorization: Bearer {JWT_TOKEN}

# Expected: 200 OK
# Response: "Email xác nhận đã được gửi..."

# Step 2: Kiểm tra email inbox
# Expected: Email với subject "🔒 Xác nhận bật bảo mật 2 lớp"

# Step 3: Validate token (optional)
GET http://localhost:8080/api/auth/security/2fa-toggle/validate?token={TOKEN}

# Expected: 200 OK
# Response: "Token hợp lệ"

# Step 4: Confirm
POST http://localhost:8080/api/auth/security/2fa-toggle/confirm?token={TOKEN}

# Expected: 200 OK
# Response: "Cài đặt bảo mật 2 lớp đã được cập nhật thành công"

# Step 5: Verify in database
SELECT require_email_verification_for_password_change FROM users WHERE id = 1;
# Expected: TRUE
```

### Test Case 2: Tắt 2FA thành công
```bash
# Step 1: Request tắt 2FA
PUT http://localhost:8080/api/users/1/security/password-change-2fa?enable=false
Authorization: Bearer {JWT_TOKEN}

# Step 2: Check email
# Expected: Email với subject "⚡ Xác nhận tắt bảo mật 2 lớp"

# Step 3: Confirm
POST http://localhost:8080/api/auth/security/2fa-toggle/confirm?token={TOKEN}

# Step 4: Verify
SELECT require_email_verification_for_password_change FROM users WHERE id = 1;
# Expected: FALSE
```

### Test Case 3: Token hết hạn
```bash
# Step 1: Request bật 2FA
PUT http://localhost:8080/api/users/1/security/password-change-2fa?enable=true

# Step 2: Đợi > 30 phút

# Step 3: Confirm với token cũ
POST http://localhost:8080/api/auth/security/2fa-toggle/confirm?token={EXPIRED_TOKEN}

# Expected: 400 BAD REQUEST
# Response: "Token không hợp lệ hoặc đã hết hạn"
```

### Test Case 4: Token đã sử dụng
```bash
# Step 1: Request và confirm thành công
# Step 2: Thử confirm lại với cùng token
POST http://localhost:8080/api/auth/security/2fa-toggle/confirm?token={USED_TOKEN}

# Expected: 400 BAD REQUEST
```

### Test Case 5: Request trùng trạng thái
```bash
# Giả sử user đã bật 2FA

# Step 1: Request bật 2FA lại
PUT http://localhost:8080/api/users/1/security/password-change-2fa?enable=true

# Expected: 400 BAD REQUEST
# Response: "Bảo mật 2 lớp đã được bật rồi"
```

## 📊 Error Handling

### Exception Messages
```java
// Trong PasswordChange2FAService

// Case 1: User không tồn tại
throw new ResourceNotFoundException("User", "id", userId);

// Case 2: Đã ở trạng thái yêu cầu
if (user.isRequireEmailVerificationForPasswordChange() == enable) {
    throw new IllegalStateException(
        enable 
            ? "Bảo mật 2 lớp đã được bật rồi"
            : "Bảo mật 2 lớp đã được tắt rồi"
    );
}

// Case 3: Token không hợp lệ
throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn");
```

## 🔄 Tích hợp với các tính năng khác

### 1. Password Change Flow
Sau khi user bật/tắt 2FA, việc đổi mật khẩu sẽ theo logic:

**Khi 2FA BẬT** (`requireEmailVerificationForPasswordChange = true`):
```
User đổi password → Gửi email xác nhận → User click link → Password được đổi
```

**Khi 2FA TẮT** (`requireEmailVerificationForPasswordChange = false`):
```
User đổi password → Kiểm tra password cũ → Password được đổi ngay
```

### 2. User Profile Response
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "requireEmailVerificationForPasswordChange": true,  // Hiển thị trạng thái 2FA
  ...
}
```

## 📝 Notes

### Maintenance Tasks
1. **Cleanup expired tokens**: Nên tạo scheduled task chạy mỗi ngày
   ```java
   @Scheduled(cron = "0 0 0 * * *") // Chạy lúc 0h mỗi ngày
   public void cleanupExpiredTokens() {
       passwordChange2FAService.cleanupExpiredTokens();
   }
   ```

2. **Monitor token usage**: Log khi token được tạo và sử dụng
   ```java
   log.info("2FA toggle token created for user: {}, enable: {}", userId, enable);
   log.info("2FA toggle confirmed for user: {}, enable: {}", userId, enable);
   ```

### Future Enhancements
- [ ] Thêm rate limiting cho việc request token (tránh spam)
- [ ] Log audit trail cho mọi thay đổi 2FA setting
- [ ] Thông báo khi có người cố thay đổi 2FA (failed attempts)
- [ ] Option để yêu cầu xác nhận password hiện tại trước khi gửi email

## 🎯 Summary

### Created Files:
1. `PasswordChange2FAToken.java` - Entity
2. `PasswordChange2FATokenRepository.java` - Repository
3. `PasswordChange2FAService.java` - Service (4 methods)
4. `PasswordChange2FAController.java` - Controller (2 endpoints)
5. `V5__Add_Password_Change_2FA_Tokens.sql` - Migration

### Updated Files:
1. `UserController.java` - Thay đổi logic toggle endpoint
2. `EmailService.java` - Thêm method và template

### API Endpoints:
1. `PUT /api/users/{id}/security/password-change-2fa?enable={bool}` - Protected
2. `GET /api/auth/security/2fa-toggle/validate?token={token}` - Public
3. `POST /api/auth/security/2fa-toggle/confirm?token={token}` - Public

### Database Changes:
- Bảng mới: `password_change_2fa_tokens`
- 3 indexes: token, user_id, expiry_date
- Foreign key: user_id → users(id)
