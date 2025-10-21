# 👤 Guest User System - Walk-in Customer Support

## 📋 Tổng Quan

Hệ thống **Guest User** cho phép nhân viên (STAFF) tạo đơn hàng cho khách vãng lai (walk-in customers) chỉ với số điện thoại, không cần khách hàng phải đăng ký tài khoản trước.

### Quy Trình:

```
Khách hàng đến quầy
    ↓
STAFF nhập SĐT khách hàng
    ↓
Backend kiểm tra SĐT:
    - Đã tồn tại → Dùng user đó
    - Chưa tồn tại → Tạo GUEST user tự động
    ↓
Tạo đơn hàng thành công
    ↓
(Sau này) Khách hàng cập nhật đầy đủ thông tin
    ↓
Tự động upgrade GUEST → CUSTOMER
```

---

## 🔑 Roles

### GUEST Role
- **Mục đích**: Tài khoản tạm thời cho khách vãng lai
- **Quyền hạn**: Giới hạn, chỉ có thể xem đơn hàng của mình
- **Tự động tạo**: Khi STAFF nhập SĐT chưa có trong hệ thống
- **Password**: Mặc định `Guest@123456` (từ `application.properties`)
- **Bắt buộc đổi password**: Lần đầu login phải đổi mật khẩu mới
- **Auto-upgrade**: Lên CUSTOMER khi cập nhật đầy đủ thông tin

### CUSTOMER Role
- **Mục đích**: Khách hàng chính thức với tài khoản đầy đủ
- **Upgrade từ GUEST**: Tự động khi profile đầy đủ
- **Quyền hạn**: Đầy đủ tính năng khách hàng (đặt hàng online, tracking, review, etc.)

---

## 🛠️ Cấu Hình

### 1. Database Migration

File: `V3__Add_Guest_Role.sql`

```sql
-- Insert GUEST role
INSERT INTO roles (name, description)
SELECT 'GUEST', 'Khách vãng lai - Tự động tạo khi Staff nhập SĐT chưa có trong hệ thống'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'GUEST'
);
```

### 2. Application Properties

File: `application.properties`

```properties
# Guest User Default Password
guest.default-password=Guest@123456
```

**Lưu ý**:
- Password mặc định cho tất cả GUEST users
- Khách hàng có thể đổi password sau khi upgrade lên CUSTOMER
- Production nên dùng password phức tạp hơn

---

## � First-Time Password Change

### Flow

```
STAFF tạo order với SĐT mới
    ↓
Backend tạo GUEST user
    - Username: guest_0912345678
    - Password: Guest@123456
    - requirePasswordChange: true
    ↓
Guest User đăng nhập lần đầu
    ↓
Backend response: requirePasswordChange = true
    ↓
Frontend redirect → trang đổi mật khẩu
    ↓
Guest User nhập password mới
    ↓
Backend set requirePasswordChange = false
    ↓
Guest User có thể dùng app bình thường
```

### API: Login

**Endpoint**: `POST /api/auth/login`

**Request**:
```json
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
    "requirePasswordChange": true  // ← Frontend check field này!
  }
}
```

### API: First-Time Password Change

**Endpoint**: `POST /api/auth/first-time-password-change`

**Auth**: Bearer token (GUEST role)

**Request**:
```json
{
  "newPassword": "MyNewPassword123!",
  "confirmPassword": "MyNewPassword123!"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công",
  "data": "Password updated successfully. You can now use the new password."
}
```

**Note**:
- Không cần nhập `currentPassword` (vì đã login)
- Frontend phải validate `newPassword === confirmPassword`
- Sau khi đổi thành công, `requirePasswordChange = false`
- Lần login tiếp theo không bị bắt đổi password nữa

---

## �📝 API Usage

### Case 1: Customer Tự Đặt Hàng (Online)

**Endpoint**: `POST /api/orders`

**Auth**: `CUSTOMER` role (JWT token)

**Request Body**:
```json
{
  "userId": 123,
  "branchId": 1,
  "items": [
    {
      "serviceId": 5,
      "quantity": 2
    }
  ],
  "notes": "Giao trước 5pm",
  "paymentMethod": "MOMO"
}
```

### Case 2: Staff Tạo Đơn Cho Khách Walk-in

**Endpoint**: `POST /api/orders`

**Auth**: `STAFF` role (JWT token)

**Request Body** (chỉ cần SĐT):
```json
{
  "phoneNumber": "0912345678",
  "branchId": 1,
  "items": [
    {
      "serviceId": 5,
      "quantity": 2
    }
  ],
  "notes": "Khách đợi tại quầy",
  "paymentMethod": "CASH"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Tạo đơn hàng thành công",
  "data": {
    "id": 456,
    "orderCode": "WF202510210001",
    "userId": 789,
    "userName": "Guest-0912345678",
    "userRole": "GUEST",
    "status": "PENDING",
    "totalAmount": 180000.00
  }
}
```

**Backend tự động**:
1. Kiểm tra SĐT `0912345678` đã tồn tại chưa
2. Nếu chưa → Tạo GUEST user:
   - Username: `guest_0912345678`
   - Password: `Guest@123456` (từ config)
   - Full Name: `Guest-0912345678`
   - Email: `0912345678@guest.washify.com`
   - Role: `GUEST`
3. Tạo đơn hàng với user vừa tạo/tìm

---

## 🔄 Auto-Upgrade: GUEST → CUSTOMER

### Điều Kiện Upgrade

Khi GUEST user cập nhật **ĐẦY ĐỦ** thông tin:
- ✅ Full Name (không còn `Guest-xxx`)
- ✅ Email (không còn `@guest.washify.com`)
- ✅ Address (không rỗng)

### Trigger

**Endpoint**: `PUT /api/users/{id}` hoặc `PATCH /api/users/{id}`

**Auth**: GUEST user tự cập nhật hoặc STAFF cập nhật cho họ

**Request Body**:
```json
{
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@gmail.com",
  "address": "123 Đường ABC, Quận 1, TP.HCM"
}
```

**Backend tự động**:
1. Update thông tin user
2. Kiểm tra profile đã đầy đủ chưa
3. Nếu đầy đủ:
   - Remove role `GUEST`
   - Add role `CUSTOMER`
   - Log upgrade event

**Response**:
```json
{
  "success": true,
  "message": "Cập nhật thông tin thành công",
  "data": {
    "id": 789,
    "username": "guest_0912345678",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@gmail.com",
    "phone": "0912345678",
    "address": "123 Đường ABC, Quận 1, TP.HCM",
    "roles": ["CUSTOMER"],  // ← Đã upgrade!
    "isActive": true
  }
}
```

---

## 🔐 Security & Validation

### Phone Number Validation

**Regex Pattern**: `^(\+84|0)[0-9]{9}$`

**Valid Examples**:
- `0912345678`
- `+84912345678`

**Invalid Examples**:
- `84912345678` (thiếu 0 hoặc +)
- `091234567` (thiếu 1 số)
- `09123456789` (thừa 1 số)

### Normalization

Backend tự động chuẩn hóa:
- `+84912345678` → `0912345678`
- `0912345678` → `0912345678`

---

## 📊 Database Schema

### Guest User Example

| Field | Value |
|-------|-------|
| id | 789 |
| username | `guest_0912345678` |
| password | `$2a$10$...` (hashed `Guest@123456`) |
| full_name | `Guest-0912345678` |
| email | `0912345678@guest.washify.com` |
| phone | `0912345678` |
| address | NULL |
| is_active | 1 |

### user_roles Table

| user_id | role_id |
|---------|---------|
| 789 | 5 (GUEST) |

**After Upgrade**:

| user_id | role_id |
|---------|---------|
| 789 | 1 (CUSTOMER) |

---

## 🎯 Use Cases

### Use Case 1: Khách Mới, Chưa Có App

**Scenario**:
- Khách hàng lần đầu đến tiệm
- Chưa cài app, chưa đăng ký
- Muốn giặt ngay

**Flow**:
1. Staff hỏi SĐT: `0912345678`
2. Staff tạo order với `phoneNumber: "0912345678"`
3. Backend tự tạo GUEST user
4. In hóa đơn, khách thanh toán CASH
5. Sau này khách tải app → Đăng nhập bằng SĐT → Thấy lịch sử đơn hàng

### Use Case 2: Khách Cũ, Đã Có SĐT Trong Hệ Thống

**Scenario**:
- Khách đã từng đến 1 lần (có GUEST account)
- Lần này quay lại

**Flow**:
1. Staff nhập SĐT: `0912345678`
2. Backend tìm thấy user (GUEST)
3. Dùng user đó để tạo đơn
4. Khách thấy lịch sử đơn cũ

### Use Case 3: Khách Upgrade Lên CUSTOMER

**Scenario**:
- Khách đã có GUEST account
- Tải app, muốn đăng ký chính thức

**Flow**:
1. Khách đăng nhập app với SĐT: `0912345678`, password: `Guest@123456`
2. App yêu cầu cập nhật thông tin
3. Khách nhập: Full Name, Email, Address
4. Backend tự động upgrade GUEST → CUSTOMER
5. Khách giờ có thể đặt hàng online, dùng MoMo, etc.

---

## 🧪 Testing

### Test Case 1: Tạo Order Với SĐT Mới

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer {STAFF_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "0999888777",
    "branchId": 1,
    "items": [{"serviceId": 1, "quantity": 1}],
    "notes": "Test guest user"
  }'
```

**Expected**:
- Tạo GUEST user với username `guest_0999888777`
- Order được tạo thành công

### Test Case 2: Tạo Order Với SĐT Đã Tồn Tại

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer {STAFF_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "0999888777",
    "branchId": 1,
    "items": [{"serviceId": 2, "quantity": 1}]
  }'
```

**Expected**:
- Không tạo user mới
- Dùng GUEST user đã có
- Order được tạo thành công

### Test Case 3: Auto-Upgrade GUEST → CUSTOMER

```bash
curl -X PUT http://localhost:8080/api/users/789 \
  -H "Authorization: Bearer {GUEST_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyễn Văn B",
    "email": "nguyenvanb@gmail.com",
    "address": "456 Street, District 2"
  }'
```

**Expected**:
- User info updated
- Role changed from `GUEST` to `CUSTOMER`
- Log: "Successfully upgraded user 789 from GUEST to CUSTOMER"

---

## 🔍 Troubleshooting

### Issue 1: GUEST Role Not Found

**Error**: `RuntimeException: GUEST role not found`

**Cause**: Migration chưa chạy hoặc role chưa được tạo

**Fix**:
```sql
INSERT INTO roles (name, description) VALUES 
('GUEST', 'Khách vãng lai');
```

### Issue 2: Invalid Phone Number

**Error**: `Số điện thoại không hợp lệ`

**Cause**: Format SĐT sai

**Fix**: Đảm bảo SĐT theo format `0XXXXXXXXX` hoặc `+84XXXXXXXXX`

### Issue 3: GUEST Không Upgrade

**Log**: `User 123 profile incomplete, cannot upgrade to CUSTOMER`

**Cause**: Thông tin chưa đầy đủ

**Fix**: Cập nhật cả 3 fields: `fullName`, `email`, `address`

---

## 📚 Related Files

- `V3__Add_Guest_Role.sql` - Migration script
- `GuestUserService.java` - Guest user logic
- `OrderService.java` - Order creation with phone number
- `UserService.java` - Auto-upgrade logic
- `OrderRequest.java` - DTO with phoneNumber field
- `application.properties` - Guest password config

---

## ✅ TODO / Future Enhancements

- [ ] Add SMS notification when GUEST user is created
- [ ] Add email notification for upgrade to CUSTOMER
- [ ] Add analytics for GUEST → CUSTOMER conversion rate
- [ ] Add option to merge duplicate users (same person, different phones)
- [ ] Add admin panel to manually upgrade users
- [ ] Add bulk SMS to GUEST users promoting app download

---

**Version**: 1.0  
**Last Updated**: 2025-10-21  
**Author**: Washify Development Team
