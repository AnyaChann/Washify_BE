# 🔑 Login với Username/Email/Phone

## 📋 Tổng Quan

Hệ thống hỗ trợ đăng nhập linh hoạt với **3 phương thức**:
1. **Username** - Tên đăng nhập (VD: `admin`, `staff1`, `customer1`)
2. **Email** - Địa chỉ email (VD: `admin@washify.vn`, `customer1@gmail.com`)
3. **Phone** - Số điện thoại (VD: `0912345678`, `0901234567`)

---

## 🔄 Cách Hoạt Động

### Backend Logic

**CustomUserDetailsService.loadUserByUsername()**:
```java
public UserDetails loadUserByUsername(String usernameOrEmailOrPhone) {
    // 1. Tìm theo username
    User user = userRepository.findByUsername(usernameOrEmailOrPhone)
        // 2. Nếu không thấy → Tìm theo email
        .orElseGet(() -> userRepository.findByEmail(usernameOrEmailOrPhone)
            // 3. Nếu vẫn không thấy → Tìm theo phone
            .orElseGet(() -> userRepository.findByPhone(usernameOrEmailOrPhone)
                // 4. Nếu vẫn không thấy → Throw exception
                .orElseThrow(() -> new UsernameNotFoundException(...))));
    
    // ... validate và return UserDetails
}
```

**Flow:**
```
User nhập: "0912345678"
    ↓
Backend tìm theo username "0912345678" → Không tìm thấy
    ↓
Backend tìm theo email "0912345678" → Không tìm thấy
    ↓
Backend tìm theo phone "0912345678" → TÌM THẤY! ✅
    ↓
Authenticate với user tìm được
    ↓
Trả về JWT token
```

---

## 🧪 Test Cases

### Test Case 1: Login bằng Username

**Request**:
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "washify123"
}
```

**Expected Response**:
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "email": "admin@washify.vn",
    "fullName": "Admin Washify",
    "roles": ["ADMIN"],
    "requirePasswordChange": false
  }
}
```

---

### Test Case 2: Login bằng Email

**Request**:
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "staff1@washify.vn",
  "password": "washify123"
}
```

**Expected Response**:
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "userId": 4,
    "username": "staff1",
    "email": "staff1@washify.vn",
    "fullName": "Lê Văn Staff",
    "roles": ["STAFF"],
    "requirePasswordChange": false
  }
}
```

---

### Test Case 3: Login bằng Phone Number

**Request**:
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "0912345678",
  "password": "Guest@123456"
}
```

**Expected Response**:
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "userId": 9,
    "username": "guest_0912345678",
    "email": "0912345678@guest.washify.com",
    "fullName": "Guest-0912345678",
    "roles": ["GUEST"],
    "requirePasswordChange": true  // ← Guest phải đổi password!
  }
}
```

---

### Test Case 4: Login Thất Bại - Không Tìm Thấy User

**Request**:
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "notexist@example.com",
  "password": "wrongpassword"
}
```

**Expected Response**:
```json
{
  "success": false,
  "message": "User not found with username/email/phone: notexist@example.com",
  "timestamp": "2025-10-21T23:00:00"
}
```

---

### Test Case 5: Login Thất Bại - Sai Password

**Request**:
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "wrongpassword123"
}
```

**Expected Response**:
```json
{
  "success": false,
  "message": "Bad credentials",
  "timestamp": "2025-10-21T23:00:00"
}
```

---

## 📊 Bảng So Sánh Login Methods

| Method | Format | Example | Use Case |
|--------|--------|---------|----------|
| **Username** | Chữ cái, số, gạch | `admin`, `staff1`, `customer123` | Nhân viên, Admin đăng nhập hàng ngày |
| **Email** | email@domain.com | `admin@washify.vn`, `user@gmail.com` | Khách hàng đăng ký qua app |
| **Phone** | 0XXXXXXXXX hoặc +84XXXXXXXXX | `0912345678`, `+84912345678` | Guest User, khách vãng lai |

---

## 🔐 Ưu Điểm

### 1. **Linh Hoạt cho User**
- Không cần nhớ username → Dùng email/phone
- Khách hàng quen dùng phone → Login bằng số điện thoại
- Admin/Staff quen dùng username → Login như cũ

### 2. **Tích Hợp Tốt với Guest User System**
- Guest User tạo tự động với phone → Có thể login ngay bằng phone
- Không cần nhớ username auto-generated (`guest_0912345678`)

### 3. **Giảm Friction**
- Ít lỗi "Username not found"
- User có nhiều lựa chọn → Dễ login hơn

---

## 🚨 Lưu Ý Bảo Mật

### ⚠️ **Timing Attack Prevention**

**Vấn đề**: 
- Tìm theo username → email → phone có thể leak thông tin về user tồn tại
- Attacker có thể dò xem email/phone có trong hệ thống không

**Giải pháp hiện tại**:
- Error message chung: "User not found with username/email/phone: xxx"
- Không tiết lộ field nào tìm thấy/không tìm thấy

**Giải pháp nâng cao** (Future enhancement):
```java
// Luôn hash password dù user không tồn tại
if (user == null) {
    passwordEncoder.encode("dummy-password-to-prevent-timing-attack");
    throw new UsernameNotFoundException("Invalid credentials");
}
```

### ⚠️ **Rate Limiting**

**Khuyến nghị**:
```java
@RateLimiter(name = "login", fallbackMethod = "loginRateLimitFallback")
public ResponseEntity<ApiResponse<AuthResponse>> login(...) {
    // ... login logic
}
```

**Config**:
```yaml
resilience4j.ratelimiter:
  instances:
    login:
      limit-for-period: 5    # Tối đa 5 lần
      limit-refresh-period: 1m  # Trong 1 phút
      timeout-duration: 0
```

---

## 📝 Swagger Documentation

**Endpoint**: `POST /api/auth/login`

**Description**:
```
Đăng nhập với username/email/phone và password để lấy JWT token.

Login Methods:
- Username: "admin", "staff1", "customer1", etc.
- Email: "admin@washify.vn", "customer1@gmail.com", etc.
- Phone: "0912345678", "0901234567", etc.

Flow:
1. Gửi username/email/phone + password
2. Server xác thực
3. Trả về JWT token + user info
4. Sử dụng token cho các API khác

Guest User:
- requirePasswordChange = true → Phải đổi password lần đầu
- Frontend redirect to change password page
```

**Request Body**:
```json
{
  "username": "string (username/email/phone)",
  "password": "string"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "JWT token (valid 24h)",
    "userId": 123,
    "username": "string",
    "email": "string",
    "roles": ["ADMIN", "STAFF", ...],
    "requirePasswordChange": false
  }
}
```

---

## 🎯 Use Cases

### Use Case 1: Admin Login (Desktop)
```
Admin quen thuộc với username
→ Nhập: "admin" + "washify123"
→ Login thành công ✅
```

### Use Case 2: Customer Login (Mobile App)
```
Khách hàng nhớ email đăng ký
→ Nhập: "customer1@gmail.com" + password
→ Login thành công ✅
```

### Use Case 3: Guest User Login (Walk-in Customer)
```
Staff tạo order cho khách với SĐT: 0912345678
→ Backend tạo GUEST user tự động
→ Khách về nhà tải app
→ Nhập: "0912345678" + "Guest@123456"
→ Login thành công ✅
→ Bắt đổi password lần đầu
```

### Use Case 4: Forgot Username
```
User quên username
→ Dùng email hoặc phone để login
→ Không cần contact support ✅
```

---

## 📚 Related Files

- `CustomUserDetailsService.java` - Load user by username/email/phone
- `AuthController.java` - Login endpoint với updated documentation
- `LoginRequest.java` - DTO với comment rõ ràng
- `UserRepository.java` - findByUsername(), findByEmail(), findByPhone()

---

## ✅ Checklist

- [x] CustomUserDetailsService hỗ trợ username/email/phone
- [x] AuthController updated documentation
- [x] LoginRequest DTO updated comment
- [x] Code compiled successfully
- [ ] Test login bằng username
- [ ] Test login bằng email
- [ ] Test login bằng phone
- [ ] Test error handling
- [ ] Frontend update login form (placeholder: "Username/Email/Phone")

---

**Version**: 1.0  
**Created**: 2025-10-21  
**Author**: Washify Development Team
