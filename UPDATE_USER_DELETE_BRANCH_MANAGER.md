# Cập Nhật Quan Trọng: User Delete & Branch Manager Permissions

## Ngày cập nhật: 2025-01-21

---

## 1. User Deletion - Soft Delete vs Hard Delete

### Endpoint DELETE hiện tại (Soft Delete)

**DELETE** `/api/users/{id}`

- **Quyền truy cập**: Chỉ ADMIN
- **Hành vi**: **SOFT DELETE** - User chỉ bị đánh dấu xóa
- **Chi tiết**:
  - Set `deleted_at = NOW()`
  - Set `is_active = 0`
  - User vẫn tồn tại trong database
  - **CÓ THỂ KHÔI PHỤC** sau này

**Ví dụ sử dụng**:
```bash
DELETE /api/users/123
Authorization: Bearer <admin_token>

# Response: 200 OK
{
  "success": true,
  "message": "Xóa user thành công (soft delete)",
  "data": null
}
```

### Endpoint XÓA VĨNH VIỄN (Hard Delete) - MỚI

**DELETE** `/api/users/{id}/permanent`

- **Quyền truy cập**: Chỉ ADMIN
- **Hành vi**: **HARD DELETE** - Xóa hoàn toàn khỏi database
- **Chi tiết**:
  - Xóa record khỏi bảng `users`
  - **KHÔNG THỂ KHÔI PHỤC**
  - Sử dụng cẩn thận!

**⚠️ CẢNH BÁO**: Endpoint này XÓA VĨNH VIỄN user khỏi hệ thống!

**Ví dụ sử dụng**:
```bash
DELETE /api/users/123/permanent
Authorization: Bearer <admin_token>

# Response: 200 OK
{
  "success": true,
  "message": "Đã xóa vĩnh viễn user khỏi hệ thống",
  "data": null
}
```

### Khi nào sử dụng?

| Tình huống | Endpoint | Lý do |
|------------|----------|-------|
| Vô hiệu hóa tài khoản tạm thời | `DELETE /api/users/{id}` | Có thể khôi phục nếu cần |
| Người dùng yêu cầu xóa tài khoản | `DELETE /api/users/{id}` | Tuân thủ GDPR - có thể khôi phục trong 30 ngày |
| Xóa dữ liệu test/spam | `DELETE /api/users/{id}/permanent` | Dọn dẹp database |
| Tuân thủ quy định xóa dữ liệu | `DELETE /api/users/{id}/permanent` | Sau 30 ngày soft delete |

---

## 2. Branch Manager Permissions - CẬP NHẬT QUAN TRỌNG

### Thay đổi quyền cập nhật chi nhánh

**Trước đây**:
- Chỉ ADMIN mới có quyền cập nhật chi nhánh

**Bây giờ**:
- ✅ **ADMIN**: Cập nhật MỌI chi nhánh
- ✅ **MANAGER**: Cập nhật CHI NHÁNH CỦA HỌ

### Endpoint PUT /api/branches/{id}

**Authorization Logic**:
```java
@PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @branchSecurity.isBranchManager(#id, authentication))")
```

**Cách hoạt động**:
1. **ADMIN**: Toàn quyền cập nhật mọi chi nhánh
2. **MANAGER**: Chỉ cập nhật chi nhánh mà họ quản lý (user.branch_id == branch.id)

### BranchSecurityService - Service mới

Tạo file: `com.washify.apis.config.BranchSecurityService`

**Chức năng**:
- Kiểm tra xem user có phải là manager của branch không
- Verify `user.branch_id == branch.id`
- Verify user có role `MANAGER`

**Method chính**:
```java
public boolean isBranchManager(Long branchId, Authentication authentication)
```

### Ví dụ sử dụng

#### Scenario 1: MANAGER cập nhật chi nhánh của họ ✅

```bash
# Manager user_id=5 thuộc branch_id=2
PUT /api/branches/2
Authorization: Bearer <manager_token>
Content-Type: application/json

{
  "name": "Chi nhánh Quận 1 - Updated",
  "address": "123 Nguyễn Huệ, Q1, HCM",
  "phone": "0901234567",
  "managerName": "Nguyễn Văn A",
  "isActive": true
}

# Response: 200 OK - Thành công!
```

#### Scenario 2: MANAGER cập nhật chi nhánh KHÁC ❌

```bash
# Manager user_id=5 thuộc branch_id=2
# Cố gắng cập nhật branch_id=3
PUT /api/branches/3
Authorization: Bearer <manager_token>

# Response: 403 Forbidden
{
  "success": false,
  "message": "Access Denied",
  "data": null
}
```

#### Scenario 3: ADMIN cập nhật BẤT KỲ chi nhánh ✅

```bash
# Admin có thể cập nhật mọi chi nhánh
PUT /api/branches/1
PUT /api/branches/2
PUT /api/branches/3
Authorization: Bearer <admin_token>

# Response: 200 OK - Tất cả đều thành công!
```

---

## 3. Database Schema

### User-Branch Relationship

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    is_active TINYINT(1) DEFAULT 1,
    branch_id BIGINT,  -- ← Chi nhánh mà user thuộc về
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,  -- ← Soft delete timestamp
    
    FOREIGN KEY (branch_id) REFERENCES branches(id)
);
```

### Role "MANAGER"

```sql
INSERT INTO roles (name, description) VALUES
('MANAGER', 'Quản lý chi nhánh - Quản lý chi nhánh và nhân viên');
```

---

## 4. Testing Guide

### Test 1: Soft Delete User

```bash
# 1. Tạo test user
POST /api/auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User"
}

# 2. Soft delete user (ADMIN)
DELETE /api/users/123

# 3. Kiểm tra user đã bị xóa
GET /api/users/123
# Expected: 404 Not Found

# 4. Verify trong database
SELECT id, username, deleted_at, is_active FROM users WHERE id = 123;
# Expected: deleted_at NOT NULL, is_active = 0
```

### Test 2: Hard Delete User

```bash
# 1. Hard delete user (ADMIN)
DELETE /api/users/123/permanent

# 2. Kiểm tra trong database
SELECT * FROM users WHERE id = 123;
# Expected: 0 rows (user đã bị xóa hoàn toàn)
```

### Test 3: Manager Update Own Branch

```bash
# 1. Đăng nhập với MANAGER account
POST /api/auth/login
{
  "username": "manager1",
  "password": "password"
}

# 2. Lấy thông tin user để biết branch_id
GET /api/users/me
# Response: user có branch_id = 2

# 3. Cập nhật chi nhánh của mình
PUT /api/branches/2
{
  "name": "Branch Updated",
  "address": "New Address",
  "phone": "0909999999",
  "managerName": "Manager Name",
  "isActive": true
}
# Expected: 200 OK ✅

# 4. Thử cập nhật chi nhánh khác
PUT /api/branches/3
# Expected: 403 Forbidden ❌
```

### Test 4: Admin Update Any Branch

```bash
# 1. Đăng nhập với ADMIN account
POST /api/auth/login
{
  "username": "admin",
  "password": "admin123"
}

# 2. Cập nhật bất kỳ chi nhánh nào
PUT /api/branches/1
PUT /api/branches/2
PUT /api/branches/3
# Expected: Tất cả 200 OK ✅
```

---

## 5. Files Changed

### New Files
1. ✅ `BranchSecurityService.java` - Service kiểm tra quyền branch-level

### Modified Files
1. ✅ `BranchController.java` - Cập nhật @PreAuthorize cho PUT endpoint
2. ✅ `UserController.java` - Thêm endpoint hard delete, cập nhật comment
3. ✅ `UserService.java` - Thêm method permanentlyDeleteUser()

---

## 6. API Summary

### User Management APIs

| Method | Endpoint | Access | Action | Can Restore? |
|--------|----------|--------|--------|--------------|
| DELETE | `/api/users/{id}` | ADMIN | Soft Delete | ✅ Yes |
| DELETE | `/api/users/{id}/permanent` | ADMIN | Hard Delete | ❌ No |
| POST | `/api/users/{id}/restore` | ADMIN | Restore | N/A |

### Branch Management APIs

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/branches` | Public | Danh sách chi nhánh |
| GET | `/api/branches/{id}` | Public | Chi tiết chi nhánh |
| POST | `/api/branches` | ADMIN | Tạo chi nhánh mới |
| PUT | `/api/branches/{id}` | ADMIN, MANAGER (own) | Cập nhật chi nhánh |
| DELETE | `/api/branches/{id}` | ADMIN | Xóa chi nhánh |

---

## 7. Security Best Practices

### Soft Delete vs Hard Delete

**Nên dùng Soft Delete**:
- ✅ Tuân thủ GDPR (có thể khôi phục trong 30 ngày)
- ✅ Audit trail - giữ lịch sử
- ✅ Tránh mất dữ liệu quan trọng
- ✅ Có thể phân tích dữ liệu sau này

**Nên dùng Hard Delete**:
- Dọn dẹp dữ liệu test/spam
- Sau thời gian lưu trữ soft delete (vd: 30 ngày)
- Tuân thủ yêu cầu xóa dữ liệu hoàn toàn

### Branch Manager Permissions

**Tốt**:
- ✅ MANAGER chỉ cập nhật chi nhánh của họ
- ✅ Không thể xem/sửa chi nhánh khác
- ✅ ADMIN vẫn có toàn quyền

**Cải thiện trong tương lai**:
- Thêm audit log cho mọi thay đổi branch
- MANAGER có thể xem thống kê của chi nhánh khác (read-only)
- Branch hierarchy (manager cấp cao quản lý nhiều chi nhánh)

---

## 8. Migration Notes

Nếu bạn đã có data trong production:

```sql
-- 1. Gán branch_id cho các MANAGER users
UPDATE users 
SET branch_id = (SELECT id FROM branches WHERE manager_name = users.full_name LIMIT 1)
WHERE id IN (SELECT user_id FROM user_roles WHERE role_id = (SELECT id FROM roles WHERE name = 'MANAGER'));

-- 2. Kiểm tra
SELECT u.id, u.username, u.full_name, u.branch_id, b.name as branch_name
FROM users u
LEFT JOIN branches b ON u.branch_id = b.id
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE r.name = 'MANAGER';
```

---

## 9. Rollback Plan

Nếu cần quay lại version cũ:

### Revert BranchController
```java
// OLD version
@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(...)
```

### Remove BranchSecurityService
```bash
rm src/main/java/com/washify/apis/config/BranchSecurityService.java
```

### Revert UserController & UserService
- Xóa endpoint `/api/users/{id}/permanent`
- Xóa method `permanentlyDeleteUser()`

---

## 10. Status

- ✅ **HOÀN THÀNH**: User soft delete với comment rõ ràng
- ✅ **HOÀN THÀNH**: User hard delete endpoint mới
- ✅ **HOÀN THÀNH**: Branch manager có quyền cập nhật chi nhánh của họ
- ✅ **HOÀN THÀNH**: BranchSecurityService để kiểm tra quyền
- ⏳ **PENDING**: Testing với dữ liệu thực tế
- ⏳ **PENDING**: Cập nhật Swagger documentation

---

**Ready for Testing!** 🚀
