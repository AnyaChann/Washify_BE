# 📘 Soft Delete API Usage Examples

## 🎯 Mục đích

Document này hướng dẫn cách sử dụng các endpoints soft delete trong thực tế.

## 📦 Prerequisites

- Spring Boot application đã chạy tại `localhost:8080`
- Database đã có seed data
- Tool test API: Postman, curl, hoặc Thunder Client

---

## 🧪 Test Flow: Từ Xóa đến Khôi phục

### Bước 1: Tạo User mới (Setup)

```bash
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "fullName": "Test User",
  "email": "test@washify.com",
  "password": "123456",
  "phone": "0901234567",
  "address": "123 Test Street"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 10,
    "fullName": "Test User",
    "email": "test@washify.com"
  }
}
```

---

### Bước 2: Xóa mềm User

```bash
DELETE http://localhost:8080/api/users/10
```

**Điều gì xảy ra:**
- ✅ User không bị xóa vật lý khỏi database
- ✅ Column `deleted_at` được set = NOW()
- ✅ User không còn hiện trong danh sách users thông thường
- ✅ Relationships vẫn còn nguyên (orders, reviews của user này vẫn tồn tại)

**SQL thực thi:**
```sql
-- Thay vì: DELETE FROM users WHERE id = 10
-- Hibernate chạy:
UPDATE users SET deleted_at = NOW() WHERE id = 10
```

---

### Bước 3: Verify User đã bị ẩn

```bash
GET http://localhost:8080/api/users
```

**Response:** User ID 10 không còn trong danh sách

```json
{
  "success": true,
  "data": [
    { "id": 1, "fullName": "Admin User" },
    { "id": 2, "fullName": "Customer 1" }
    // User 10 không có ở đây
  ]
}
```

---

### Bước 4: Xem danh sách Users đã xóa

```bash
GET http://localhost:8080/api/soft-delete/users/deleted
```

**Response:**
```json
{
  "success": true,
  "message": "Lấy danh sách users đã xóa thành công",
  "data": [
    {
      "id": 10,
      "fullName": "Test User",
      "email": "test@washify.com",
      "deletedAt": "2024-01-15T14:30:00"
    }
  ],
  "timestamp": "2024-01-15T15:00:00"
}
```

---

### Bước 5: Khôi phục User

```bash
PUT http://localhost:8080/api/soft-delete/users/10/restore
```

**Response:**
```json
{
  "success": true,
  "message": "User đã được khôi phục thành công",
  "data": null,
  "timestamp": "2024-01-15T15:05:00"
}
```

**SQL thực thi:**
```sql
UPDATE users SET deleted_at = NULL WHERE id = 10
```

---

### Bước 6: Verify User đã được khôi phục

```bash
GET http://localhost:8080/api/users
```

**Response:** User 10 đã trở lại danh sách

```json
{
  "success": true,
  "data": [
    { "id": 1, "fullName": "Admin User" },
    { "id": 2, "fullName": "Customer 1" },
    { "id": 10, "fullName": "Test User" }  // ✅ Đã quay lại!
  ]
}
```

---

## 🔥 Xóa Vĩnh Viễn (Permanent Delete)

### ⚠️ CẢNH BÁO

Xóa vĩnh viễn sẽ:
- ❌ Xóa hoàn toàn record khỏi database
- ❌ KHÔNG THỂ khôi phục
- ❌ Có thể gây lỗi foreign key nếu có relationships

**Chỉ dùng khi:**
- Tuân thủ GDPR (xóa dữ liệu cá nhân theo yêu cầu)
- Dọn dẹp test data
- Chắc chắn 100% không cần data này

### Cách sử dụng:

```bash
DELETE http://localhost:8080/api/soft-delete/users/10/permanent
```

**Response:**
```json
{
  "success": true,
  "message": "User đã được xóa vĩnh viễn",
  "data": null
}
```

**SQL thực thi:**
```sql
DELETE FROM users WHERE id = 10  -- Xóa thật sự!
```

---

## 📦 Các Entities khác

### Branch Example

```bash
# Xem branches đã xóa
GET http://localhost:8080/api/soft-delete/branches/deleted

# Khôi phục branch
PUT http://localhost:8080/api/soft-delete/branches/5/restore

# Xóa vĩnh viễn
DELETE http://localhost:8080/api/soft-delete/branches/5/permanent
```

### Service Example

```bash
# Xem services đã xóa
GET http://localhost:8080/api/soft-delete/services/deleted

# Khôi phục service
PUT http://localhost:8080/api/soft-delete/services/3/restore
```

### Order Example

```bash
# Xem orders đã xóa
GET http://localhost:8080/api/soft-delete/orders/deleted

# Khôi phục order
PUT http://localhost:8080/api/soft-delete/orders/100/restore
```

### Promotion Example

```bash
# Xem promotions đã xóa
GET http://localhost:8080/api/soft-delete/promotions/deleted

# Khôi phục promotion
PUT http://localhost:8080/api/soft-delete/promotions/2/restore
```

### Shipper Example

```bash
# Xem shippers đã xóa
GET http://localhost:8080/api/soft-delete/shippers/deleted

# Khôi phục shipper
PUT http://localhost:8080/api/soft-delete/shippers/7/restore
```

---

## 🧩 Integration với Frontend

### React Example

```javascript
// Xóa mềm user
const softDeleteUser = async (userId) => {
  await axios.delete(`/api/users/${userId}`);
  toast.success('User đã được xóa');
};

// Xem users đã xóa
const getDeletedUsers = async () => {
  const response = await axios.get('/api/soft-delete/users/deleted');
  return response.data.data;
};

// Khôi phục user
const restoreUser = async (userId) => {
  await axios.put(`/api/soft-delete/users/${userId}/restore`);
  toast.success('User đã được khôi phục');
};

// Xóa vĩnh viễn (với confirm dialog)
const permanentDeleteUser = async (userId) => {
  if (confirm('CẢNH BÁO: Bạn chắc chắn muốn xóa vĩnh viễn? Hành động này không thể hoàn tác!')) {
    await axios.delete(`/api/soft-delete/users/${userId}/permanent`);
    toast.warning('User đã bị xóa vĩnh viễn');
  }
};
```

### Admin UI Component

```jsx
function DeletedUsersManager() {
  const [deletedUsers, setDeletedUsers] = useState([]);
  
  useEffect(() => {
    fetchDeletedUsers();
  }, []);
  
  const fetchDeletedUsers = async () => {
    const response = await axios.get('/api/soft-delete/users/deleted');
    setDeletedUsers(response.data.data);
  };
  
  const handleRestore = async (userId) => {
    await axios.put(`/api/soft-delete/users/${userId}/restore`);
    fetchDeletedUsers(); // Refresh list
    toast.success('Đã khôi phục user');
  };
  
  return (
    <div className="deleted-users">
      <h2>🗑️ Users đã xóa</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Email</th>
            <th>Xóa lúc</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {deletedUsers.map(user => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.fullName}</td>
              <td>{user.email}</td>
              <td>{new Date(user.deletedAt).toLocaleString()}</td>
              <td>
                <button onClick={() => handleRestore(user.id)}>
                  ♻️ Khôi phục
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

---

## 🔍 Database Queries để Debug

### Xem tất cả users (cả đã xóa)

```sql
SELECT id, full_name, email, deleted_at, is_active
FROM users
ORDER BY deleted_at DESC;
```

### Đếm users active vs deleted

```sql
SELECT 
  COUNT(*) as total,
  SUM(CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END) as active,
  SUM(CASE WHEN deleted_at IS NOT NULL THEN 1 ELSE 0 END) as deleted
FROM users;
```

### Xem users xóa trong 7 ngày gần đây

```sql
SELECT id, full_name, email, deleted_at
FROM users
WHERE deleted_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY deleted_at DESC;
```

### Manual restore (nếu cần)

```sql
UPDATE users 
SET deleted_at = NULL 
WHERE id = 10;
```

### Manual soft delete (nếu cần)

```sql
UPDATE users 
SET deleted_at = NOW() 
WHERE id = 10;
```

---

## ✅ Best Practices

### 1. Quyền Truy Cập

```java
// Chỉ ADMIN được xem deleted records
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/soft-delete/users/deleted")
public ResponseEntity<?> getDeletedUsers() {
    // ...
}
```

### 2. Audit Log

```java
// Log mọi hành động restore/permanent delete
@PutMapping("/users/{id}/restore")
public ResponseEntity<?> restoreUser(@PathVariable Long id) {
    auditService.log("RESTORE_USER", id, getCurrentUser());
    boolean restored = softDeleteService.restoreUser(id);
    // ...
}
```

### 3. Confirmation Dialog

```javascript
// Frontend PHẢI confirm trước khi permanent delete
const permanentDelete = (id) => {
  Swal.fire({
    title: '⚠️ CẢNH BÁO',
    text: 'Xóa vĩnh viễn không thể hoàn tác. Bạn chắc chắn?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Xóa vĩnh viễn',
    cancelButtonText: 'Hủy'
  }).then((result) => {
    if (result.isConfirmed) {
      axios.delete(`/api/soft-delete/users/${id}/permanent`);
    }
  });
};
```

### 4. Auto-Cleanup Policy

```java
// Scheduled job: Xóa vĩnh viễn records đã xóa mềm > 30 ngày
@Scheduled(cron = "0 0 2 * * ?") // 2AM mỗi ngày
public void cleanupOldDeletedRecords() {
    LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
    userRepository.permanentlyDeleteOlderThan(cutoff);
}
```

---

## 🐛 Troubleshooting

### Issue: "Không thể khôi phục user"

**Nguyên nhân:**
- User ID không tồn tại
- User chưa bị xóa mềm (deleted_at = NULL)

**Giải pháp:**
```bash
# Check user status
GET /api/soft-delete/users/deleted

# Verify ID có trong danh sách không
```

### Issue: Foreign Key Error khi permanent delete

**Nguyên nhân:**
- User còn relationships (orders, reviews, etc.)

**Giải pháp:**
```java
// Soft delete thay vì permanent delete
// HOẶC xóa cascade relationships trước
```

---

## 📊 Metrics & Monitoring

### Track Soft Delete Operations

```java
// Prometheus metrics
@Timed(value = "soft_delete.restore", description = "Time to restore entity")
public boolean restoreUser(Long id) {
    // ...
}

@Counted(value = "soft_delete.permanent_delete", description = "Count of permanent deletes")
public boolean permanentlyDeleteUser(Long id) {
    // ...
}
```

### Dashboard Queries

```sql
-- Deleted users per day (last 30 days)
SELECT DATE(deleted_at) as date, COUNT(*) as count
FROM users
WHERE deleted_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(deleted_at)
ORDER BY date DESC;

-- Restore rate
SELECT 
  (SELECT COUNT(*) FROM users WHERE deleted_at IS NOT NULL) as currently_deleted,
  (SELECT COUNT(*) FROM audit_log WHERE action = 'RESTORE_USER') as total_restores;
```

---

## 🎓 Summary

**Soft Delete Flow:**
```
User exists → DELETE call → deleted_at set → Hidden from queries
→ Can view in /deleted → Can restore → deleted_at = NULL → Back to normal
→ OR permanent delete → Gone forever ❌
```

**Key Endpoints:**
- `GET /api/soft-delete/{entity}/deleted` - Xem đã xóa
- `PUT /api/soft-delete/{entity}/{id}/restore` - Khôi phục
- `DELETE /api/soft-delete/{entity}/{id}/permanent` - Xóa vĩnh viễn

**Remember:**
- ✅ Soft delete = Safe, reversible
- ⚠️ Permanent delete = Dangerous, irreversible
- 🔒 Only admins should access soft delete endpoints
- 📝 Always log restore/permanent delete actions
