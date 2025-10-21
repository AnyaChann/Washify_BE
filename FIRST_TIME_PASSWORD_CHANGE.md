# 🔐 First-Time Password Change for Guest Users

## 📋 Tổng Quan

**Vấn đề**: Guest Users được tạo tự động với password mặc định (`Guest@123456`). Để bảo mật, cần bắt buộc họ đổi mật khẩu lần đầu tiên đăng nhập.

**Giải pháp**: Thêm flag `requirePasswordChange` vào User entity. Khi login, backend trả về flag này. Frontend kiểm tra và redirect đến trang đổi mật khẩu nếu `requirePasswordChange = true`.

---

## 🛠️ Thay Đổi Code

### 1. Database Migration

**File**: `V5__Add_Require_Password_Change.sql`

```sql
-- Thêm column require_password_change
ALTER TABLE users 
ADD COLUMN require_password_change BOOLEAN DEFAULT FALSE 
COMMENT 'Bắt buộc đổi mật khẩu (dùng cho Guest User lần đầu login)';

-- Set TRUE cho tất cả Guest Users
UPDATE users 
SET require_password_change = TRUE 
WHERE username LIKE 'guest_%';

-- Index
CREATE INDEX idx_users_require_password_change ON users(require_password_change);
```

### 2. User Entity

**File**: `User.java`

```java
@Column(name = "require_password_change")
private Boolean requirePasswordChange = false;
```

### 3. GuestUserService

**File**: `GuestUserService.java`

```java
private User createGuestUser(String phoneNumber) {
    // ... existing code ...
    guestUser.setRequirePasswordChange(true); // ← Added
    // ... existing code ...
}
```

### 4. AuthResponse DTO

**File**: `AuthResponse.java`

```java
private Boolean requirePasswordChange; // ← Added
```

### 5. AuthController - Login

**File**: `AuthController.java`

```java
AuthResponse authResponse = AuthResponse.builder()
    // ... existing fields ...
    .requirePasswordChange(user.getRequirePasswordChange() != null 
        && user.getRequirePasswordChange()) // ← Added
    .build();
```

### 6. FirstTimePasswordChangeRequest DTO (NEW)

**File**: `FirstTimePasswordChangeRequest.java`

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstTimePasswordChangeRequest {
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 50, message = "Mật khẩu mới phải từ 6-50 ký tự")
    private String newPassword;
    
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
```

### 7. AuthController - First-Time Password Change (NEW)

**File**: `AuthController.java`

**Endpoint**: `POST /api/auth/first-time-password-change`

```java
@PostMapping("/first-time-password-change")
public ResponseEntity<ApiResponse<String>> firstTimePasswordChange(
        @Valid @RequestBody FirstTimePasswordChangeRequest request,
        Authentication authentication) {
    
    String username = authentication.getName();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BadRequestException("User not found"));
    
    // Kiểm tra requirePasswordChange
    if (user.getRequirePasswordChange() == null || !user.getRequirePasswordChange()) {
        throw new BadRequestException("Tài khoản này không cần đổi mật khẩu lần đầu");
    }
    
    // Validate passwords match
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
        throw new BadRequestException("Mật khẩu mới và xác nhận mật khẩu không khớp");
    }
    
    // Update password
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    user.setRequirePasswordChange(false); // ← Clear flag
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
    
    return ResponseEntity.ok(/* success response */);
}
```

---

## 🔄 Flow Hoàn Chỉnh

### Step 1: STAFF Tạo Order Cho Khách Walk-in

```bash
POST /api/orders
Authorization: Bearer {STAFF_TOKEN}

{
  "phoneNumber": "0912345678",
  "branchId": 1,
  "items": [{"serviceId": 1, "quantity": 1}]
}
```

**Backend tự động**:
- Tạo GUEST user với username: `guest_0912345678`
- Set `requirePasswordChange = true`
- Password: `Guest@123456`

### Step 2: Guest User Login Lần Đầu

```bash
POST /api/auth/login

{
  "username": "guest_0912345678",
  "password": "Guest@123456"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1...",
    "userId": 789,
    "username": "guest_0912345678",
    "roles": ["GUEST"],
    "requirePasswordChange": true  // ← Frontend check này!
  }
}
```

### Step 3: Frontend Redirect

```javascript
// Frontend code (React/Vue/Angular)
if (response.data.requirePasswordChange) {
  // Redirect to change password page
  router.push('/change-password');
}
```

### Step 4: Guest User Đổi Mật Khẩu

```bash
POST /api/auth/first-time-password-change
Authorization: Bearer {GUEST_TOKEN}

{
  "newPassword": "MySecurePass123!",
  "confirmPassword": "MySecurePass123!"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công"
}
```

**Backend tự động**:
- Update password (BCrypt hash)
- Set `requirePasswordChange = false`

### Step 5: Login Lại Với Password Mới

```bash
POST /api/auth/login

{
  "username": "guest_0912345678",
  "password": "MySecurePass123!"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1...",
    "requirePasswordChange": false  // ← Không cần đổi nữa
  }
}
```

---

## 🧪 Testing

### Test Case 1: Login GUEST User Lần Đầu

**Scenario**: Guest user vừa được tạo, chưa đổi password

**Request**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "guest_0912345678",
    "password": "Guest@123456"
  }'
```

**Expected**:
- ✅ Login thành công
- ✅ `requirePasswordChange = true`
- ✅ Token hợp lệ

### Test Case 2: Đổi Password Lần Đầu

**Scenario**: Guest user đổi password sau khi login

**Request**:
```bash
curl -X POST http://localhost:8080/api/auth/first-time-password-change \
  -H "Authorization: Bearer {GUEST_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "newPassword": "NewPass123!",
    "confirmPassword": "NewPass123!"
  }'
```

**Expected**:
- ✅ Password updated
- ✅ `requirePasswordChange = false`
- ✅ Log: "User guest_0912345678 changed password successfully on first login"

### Test Case 3: Login Với Password Mới

**Scenario**: Guest user login lại sau khi đổi password

**Request**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "guest_0912345678",
    "password": "NewPass123!"
  }'
```

**Expected**:
- ✅ Login thành công
- ✅ `requirePasswordChange = false`
- ✅ Không bị redirect đến change password

### Test Case 4: Password Mismatch

**Scenario**: newPassword != confirmPassword

**Request**:
```bash
curl -X POST http://localhost:8080/api/auth/first-time-password-change \
  -H "Authorization: Bearer {GUEST_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "newPassword": "NewPass123!",
    "confirmPassword": "DifferentPass456!"
  }'
```

**Expected**:
- ❌ Error 400
- ❌ Message: "Mật khẩu mới và xác nhận mật khẩu không khớp"

### Test Case 5: Non-Guest User Calls Endpoint

**Scenario**: CUSTOMER user cố gọi first-time-password-change

**Request**:
```bash
curl -X POST http://localhost:8080/api/auth/first-time-password-change \
  -H "Authorization: Bearer {CUSTOMER_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "newPassword": "NewPass123!",
    "confirmPassword": "NewPass123!"
  }'
```

**Expected**:
- ❌ Error 400
- ❌ Message: "Tài khoản này không cần đổi mật khẩu lần đầu"

---

## 🔐 Security Considerations

### ✅ Đã Implement:

1. **Password Hashing**: BCrypt với salt tự động
2. **Token Authentication**: JWT với expiry 24h
3. **Input Validation**: 
   - Min 6 chars for password
   - Password match validation
4. **Authorization Check**: Chỉ authenticated users mới gọi được endpoint
5. **Flag Reset**: `requirePasswordChange = false` sau khi đổi thành công

### ⚠️ Recommendations:

1. **Password Strength**: Thêm regex validation
   ```java
   // At least 1 uppercase, 1 lowercase, 1 digit, 1 special char
   @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
   ```

2. **Rate Limiting**: Giới hạn số lần đổi password (防止暴力破解)
   ```java
   @RateLimiter(name = "password-change", fallbackMethod = "rateLimitFallback")
   ```

3. **Password History**: Không cho dùng lại password cũ
   ```java
   List<PasswordHistory> history = passwordHistoryRepository
       .findTop5ByUserIdOrderByCreatedAtDesc(userId);
   ```

4. **Email Notification**: Thông báo khi password thay đổi
   ```java
   emailService.sendPasswordChangedEmail(user.getEmail());
   ```

5. **Session Invalidation**: Logout tất cả sessions khác sau khi đổi password
   ```java
   sessionRegistry.getAllSessions(user, false)
       .forEach(SessionInformation::expireNow);
   ```

---

## 📊 Database Changes

### Before

```sql
SELECT id, username, require_password_change FROM users WHERE username = 'guest_0912345678';
```

| id | username | require_password_change |
|----|----------|------------------------|
| 789 | guest_0912345678 | NULL |

### After Migration V5

```sql
SELECT id, username, require_password_change FROM users WHERE username = 'guest_0912345678';
```

| id | username | require_password_change |
|----|----------|------------------------|
| 789 | guest_0912345678 | 1 (TRUE) |

### After Password Change

```sql
SELECT id, username, require_password_change FROM users WHERE username = 'guest_0912345678';
```

| id | username | require_password_change |
|----|----------|------------------------|
| 789 | guest_0912345678 | 0 (FALSE) |

---

## 📚 Related Files

### New Files:
- `V5__Add_Require_Password_Change.sql` - Migration script
- `FirstTimePasswordChangeRequest.java` - DTO cho request
- `FIRST_TIME_PASSWORD_CHANGE.md` - Documentation (this file)

### Modified Files:
- `User.java` - Added `requirePasswordChange` field
- `GuestUserService.java` - Set flag khi tạo guest user
- `AuthResponse.java` - Added `requirePasswordChange` field
- `AuthController.java` - Added login response & new endpoint
- `GUEST_USER_GUIDE.md` - Updated với first-time password change section
- `data.sql` - Added comment về requirePasswordChange

---

## ✅ Checklist

- [x] Database migration created (V5)
- [x] User entity updated
- [x] GuestUserService sets flag on creation
- [x] AuthResponse includes flag
- [x] AuthController returns flag in login response
- [x] FirstTimePasswordChangeRequest DTO created
- [x] AuthController endpoint implemented
- [x] GUEST_USER_GUIDE.md updated
- [x] Code compiled successfully
- [ ] Run migration V5
- [ ] Test first-time login flow
- [ ] Test password change
- [ ] Test login with new password
- [ ] Frontend implementation

---

**Version**: 1.0  
**Created**: 2025-10-21  
**Author**: Washify Development Team
