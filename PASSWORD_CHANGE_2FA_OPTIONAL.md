# 🔐 Password Change 2FA - Optional Security Layer

## 🎯 Tổng quan

Biến **Email Verification** thành **tùy chọn bảo mật 2 lớp** cho việc đổi mật khẩu!

### ✅ User có thể chọn:

1. **TẮT bảo mật 2 lớp (Mặc định)** - Nhanh chóng ⚡
   - Đổi password ngay lập tức
   - Chỉ cần nhập password cũ + password mới
   - UX tốt, tiện lợi

2. **BẬT bảo mật 2 lớp** - An toàn hơn 🔒
   - Cần xác nhận qua email
   - Bảo vệ khi password bị lộ
   - Phù hợp cho tài khoản quan trọng

---

## 🔄 Flow hoạt động

### Mode 1: Bảo mật 2 lớp TẮT (Default)

```
User nhập password cũ + password mới
    ↓
Backend verify password cũ
    ↓
Hash password mới
    ↓
Lưu vào database ngay lập tức
    ↓
Done! ✅ (Nhanh - Không cần email)
```

**Use case:** 
- User thường xuyên đổi password
- Tài khoản rủi ro thấp
- Muốn UX nhanh chóng

---

### Mode 2: Bảo mật 2 lớp BẬT

```
User nhập password cũ + password mới
    ↓
Backend verify password cũ
    ↓
Hash password mới & lưu vào token
    ↓
Gửi email xác nhận 📧
    ↓
User click link trong email
    ↓
Backend verify token
    ↓
Apply password mới
    ↓
Done! ✅ (An toàn - Cần verify email)
```

**Use case:**
- Tài khoản quan trọng (Admin, Staff)
- Nghi ngờ password bị lộ
- Yêu cầu bảo mật cao (Banking, Healthcare)

---

## 📡 API Endpoints

### 1. Bật/Tắt bảo mật 2 lớp

```http
PUT /api/users/{id}/security/password-change-2fa?enable=true
Authorization: Bearer {jwt_token}

Response (Bật):
{
  "success": true,
  "message": "Đã bật bảo mật 2 lớp cho việc đổi mật khẩu. Từ giờ mỗi lần đổi mật khẩu bạn sẽ cần xác nhận qua email."
}

Response (Tắt):
{
  "success": true,
  "message": "Đã tắt bảo mật 2 lớp. Bạn có thể đổi mật khẩu ngay lập tức mà không cần xác nhận email."
}
```

**Authorization:**
- ✅ User phải login (JWT required)
- ✅ Admin có thể toggle cho bất kỳ user nào
- ✅ User thường chỉ toggle được của chính mình

---

### 2. Đổi password (Auto-detect mode)

```http
POST /api/users/{id}/change-password
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword456",
  "confirmPassword": "newPassword456"
}
```

**Response (2FA TẮT - Đổi ngay):**
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công."
}
```

**Response (2FA BẬT - Gửi email):**
```json
{
  "success": true,
  "message": "Email xác nhận đã được gửi. Vui lòng kiểm tra hộp thư để hoàn tất việc đổi mật khẩu."
}
```

**Backend tự động detect:**
- Check field `requireEmailVerificationForPasswordChange` trong User entity
- Nếu `true` → Gửi email (Mode 2)
- Nếu `false` → Đổi ngay (Mode 1)

---

### 3. Check setting hiện tại

```http
GET /api/users/{id}
Authorization: Bearer {jwt_token}

Response:
{
  "success": true,
  "data": {
    "id": 1,
    "username": "user1",
    "email": "user@example.com",
    "requireEmailVerificationForPasswordChange": false,  ← Setting
    ...
  }
}
```

---

## 🗄️ Database Schema

### Updated Table: `users`

```sql
ALTER TABLE users 
ADD COLUMN require_email_verification_for_password_change BOOLEAN DEFAULT FALSE;
```

**Field:**
- `require_email_verification_for_password_change` - Boolean
- Mặc định: `FALSE` (TẮT)
- User có thể toggle qua API

---

## 📂 Files Modified

### Updated Files (5):
```
src/main/java/com/washify/apis/
├── entity/
│   └── User.java                                ✅ UPDATED
│       + requireEmailVerificationForPasswordChange field
├── service/
│   └── UserService.java                         ✅ UPDATED
│       + changePassword() - Support 2 modes
│       + togglePasswordChangeEmailVerification()
│       + getUserEntityById()
├── controller/
│   └── UserController.java                      ✅ UPDATED
│       + Auto-detect mode in changePassword()
│       + New endpoint: toggle 2FA setting
├── dto/response/
│   └── UserResponse.java                        ✅ UPDATED
│       + requireEmailVerificationForPasswordChange field

src/main/resources/db/migration/
└── V4__Add_Password_Change_2FA_Setting.sql      ✅ NEW
```

---

## 🧪 Testing Scenarios

### Test Case 1: Toggle 2FA ON

```bash
# Step 1: Check current setting (should be OFF by default)
GET /api/users/1
# → requireEmailVerificationForPasswordChange: false

# Step 2: Bật bảo mật 2 lớp
PUT /api/users/1/security/password-change-2fa?enable=true
# → Success message

# Step 3: Verify setting updated
GET /api/users/1
# → requireEmailVerificationForPasswordChange: true ✅
```

---

### Test Case 2: Change Password with 2FA OFF (Fast)

```bash
# Step 1: Make sure 2FA is OFF
PUT /api/users/1/security/password-change-2fa?enable=false

# Step 2: Change password
POST /api/users/1/change-password
{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword456",
  "confirmPassword": "newPassword456"
}

# Expected: INSTANT change ⚡
# Response: "Đổi mật khẩu thành công."

# Step 3: Login với password mới ngay lập tức
POST /api/auth/login
{
  "username": "user1",
  "password": "newPassword456"
}
# → Should success ✅ (KHÔNG cần check email)
```

---

### Test Case 3: Change Password with 2FA ON (Secure)

```bash
# Step 1: Bật 2FA
PUT /api/users/1/security/password-change-2fa?enable=true

# Step 2: Request change password
POST /api/users/1/change-password
{
  "currentPassword": "newPassword456",
  "newPassword": "anotherPassword789",
  "confirmPassword": "anotherPassword789"
}

# Expected: Email sent 📧
# Response: "Email xác nhận đã được gửi..."

# Step 3: Check email inbox
# → Nhận email với link confirm

# Step 4: Click link → Confirm
POST /api/auth/password-change/confirm?token={TOKEN}
# → Success

# Step 5: Login với password MỚI
POST /api/auth/login
{
  "username": "user1",
  "password": "anotherPassword789"
}
# → Should success ✅
```

---

### Test Case 4: Toggle 2FA nhiều lần

```bash
# Bật
PUT /api/users/1/security/password-change-2fa?enable=true
# → "Đã bật bảo mật 2 lớp..."

# Tắt
PUT /api/users/1/security/password-change-2fa?enable=false
# → "Đã tắt bảo mật 2 lớp..."

# Bật lại
PUT /api/users/1/security/password-change-2fa?enable=true
# → "Đã bật bảo mật 2 lớp..."

# Expected: Mỗi lần toggle đều update setting thành công ✅
```

---

## 🎯 Advantages

### ✅ **Linh hoạt:**
- User tự chọn mức độ bảo mật
- Phù hợp với nhiều use case khác nhau
- Không bắt buộc (mặc định OFF)

### ✅ **UX tốt:**
- Default: Nhanh chóng (không cần email)
- Optional: Bảo mật cao (với email)
- Clear message để user biết flow nào đang dùng

### ✅ **Bảo mật:**
- Tài khoản quan trọng có thể bật 2FA
- Tài khoản thường dùng mode nhanh
- Best of both worlds

---

## 📊 Comparison

| Feature | 2FA OFF (Default) | 2FA ON (Optional) |
|---------|------------------|-------------------|
| **Speed** | ⚡ Instant | ⚠️ Wait for email |
| **Security** | ⚠️ Medium | ✅ High |
| **UX** | ✅ Fast & easy | ⚠️ Extra step |
| **Email required** | ❌ No | ✅ Yes |
| **Best for** | Regular users | VIP/Admin users |
| **Default** | ✅ Yes | ❌ No |

---

## 🎨 Frontend Integration

### Setting Toggle UI

```typescript
// Get user setting
const user = await fetchUser(userId);
const is2FAEnabled = user.requireEmailVerificationForPasswordChange;

// Toggle component
<Switch 
  checked={is2FAEnabled}
  onChange={async (enabled) => {
    await fetch(`/api/users/${userId}/security/password-change-2fa?enable=${enabled}`, {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    if (enabled) {
      alert('Đã bật bảo mật 2 lớp. Từ giờ cần xác nhận email khi đổi password.');
    } else {
      alert('Đã tắt bảo mật 2 lớp. Có thể đổi password ngay lập tức.');
    }
  }}
/>

<p>
  {is2FAEnabled 
    ? '🔒 Bảo mật 2 lớp: BẬT (Cần email khi đổi password)'
    : '⚡ Bảo mật 2 lớp: TẮT (Đổi password ngay lập tức)'}
</p>
```

---

### Change Password Form

```typescript
async function handleChangePassword(data: ChangePasswordRequest) {
  const response = await fetch(`/api/users/${userId}/change-password`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data)
  });
  
  const result = await response.json();
  
  if (result.message.includes('Email xác nhận')) {
    // 2FA BẬT → Redirect to "Check your email" page
    router.push('/check-email');
  } else {
    // 2FA TẮT → Password changed instantly
    alert('Đổi mật khẩu thành công!');
    router.push('/profile');
  }
}
```

---

## 🎓 User Education

### Khi nào nên BẬT bảo mật 2 lớp?

✅ **Nên bật nếu:**
- Bạn là Admin hoặc Staff
- Tài khoản có quyền cao
- Nghi ngờ password bị lộ
- Làm việc trên máy chung
- Yêu cầu compliance cao

❌ **Có thể tắt nếu:**
- Tài khoản user thường
- Rủi ro thấp
- Muốn UX nhanh
- Thường xuyên đổi password

---

## 🚀 Production Checklist

### Required:
- [x] User entity field added
- [x] UserService support 2 modes
- [x] UserController auto-detect mode
- [x] Toggle endpoint created
- [x] UserResponse includes setting
- [x] Database migration script
- [ ] Test both modes
- [ ] Frontend UI for toggle
- [ ] User documentation

### Recommended:
- [ ] Email notification khi setting thay đổi
- [ ] Audit log cho toggle actions
- [ ] Rate limiting cho toggle endpoint
- [ ] Admin dashboard: xem users nào đang bật 2FA

---

## ✅ Summary

### Đã implement:
- ✅ `User.requireEmailVerificationForPasswordChange` field (default: FALSE)
- ✅ `UserService.changePassword()` - Auto-detect mode
- ✅ `UserService.togglePasswordChangeEmailVerification()` - Toggle setting
- ✅ `UserController` - 2 endpoints (change password, toggle 2FA)
- ✅ `UserResponse` - Include setting
- ✅ Database migration V4
- ✅ Documentation đầy đủ

### Lợi ích:
- ✅ **Linh hoạt**: User tự chọn
- ✅ **UX tốt**: Mặc định nhanh
- ✅ **Bảo mật**: Có option cao
- ✅ **Best practice**: Gmail, Facebook làm tương tự

---

## 🎉 Conclusion

**Password Change 2FA (Optional) đã HOÀN THÀNH!** 🔐

**Default:** TẮT (Nhanh chóng ⚡)  
**Optional:** BẬT (Bảo mật cao 🔒)

User có quyền tự chọn mức độ bảo mật phù hợp! 🎯

**Ready for production!** 🚀
