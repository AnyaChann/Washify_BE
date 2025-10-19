# 🗑️ Soft Delete Implementation Guide

## 📋 Tổng quan

Hệ thống Washify đã được implement **Soft Delete** (xóa mềm) cho các entities quan trọng để đảm bảo:
- ✅ Không mất dữ liệu vĩnh viễn
- ✅ Có thể khôi phục dữ liệu khi cần
- ✅ Giữ tính toàn vẹn referential integrity
- ✅ Audit trail và tracking lịch sử

## 🌐 REST API Endpoints

### Centralized Soft Delete Controller

**Base URL:** `/api/soft-delete`

#### User Endpoints
```http
# Lấy danh sách users đã xóa
GET /api/soft-delete/users/deleted

# Khôi phục user
PUT /api/soft-delete/users/{id}/restore

# Xóa vĩnh viễn user
DELETE /api/soft-delete/users/{id}/permanent
```

#### Branch Endpoints
```http
GET /api/soft-delete/branches/deleted
PUT /api/soft-delete/branches/{id}/restore
DELETE /api/soft-delete/branches/{id}/permanent
```

#### Service Endpoints
```http
GET /api/soft-delete/services/deleted
PUT /api/soft-delete/services/{id}/restore
DELETE /api/soft-delete/services/{id}/permanent
```

#### Order Endpoints
```http
GET /api/soft-delete/orders/deleted
PUT /api/soft-delete/orders/{id}/restore
DELETE /api/soft-delete/orders/{id}/permanent
```

#### Promotion Endpoints
```http
GET /api/soft-delete/promotions/deleted
PUT /api/soft-delete/promotions/{id}/restore
DELETE /api/soft-delete/promotions/{id}/permanent
```

#### Shipper Endpoints
```http
GET /api/soft-delete/shippers/deleted
PUT /api/soft-delete/shippers/{id}/restore
DELETE /api/soft-delete/shippers/{id}/permanent
```

### Response Format

**Success Response:**
```json
{
  "success": true,
  "message": "User đã được khôi phục thành công",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Không thể khôi phục user",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

**List Response:**
```json
{
  "success": true,
  "message": "Lấy danh sách users đã xóa thành công",
  "data": [
    {
      "id": 1,
      "fullName": "Nguyen Van A",
      "email": "a@example.com",
      "deletedAt": "2024-01-10T15:20:00"
    }
  ],
  "timestamp": "2024-01-15T10:30:00"
}
```

## 🎯 Entities đã implement Soft Delete

| Entity | Table | Deleted At Column | Lý do |
|--------|-------|-------------------|-------|
| **User** | users | deleted_at | Giữ lịch sử orders, reviews của user |
| **Branch** | branches | deleted_at | Giữ lịch sử orders được xử lý tại branch |
| **Service** | services | deleted_at | Giữ lịch sử services trong order_items |
| **Order** | orders | deleted_at | Giữ lịch sử giao dịch, không xóa vĩnh viễn |
| **Promotion** | promotions | deleted_at | Giữ lịch sử khuyến mãi đã áp dụng |
| **Shipper** | shippers | deleted_at | Giữ lịch sử shipments của shipper |

## 🔧 Cách hoạt động

### 1. **Annotations sử dụng**

```java
@Entity
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User {
    // ...
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
```

- `@SQLDelete`: Override câu lệnh DELETE mặc định thành UPDATE
- `@Where`: Tự động thêm điều kiện `deleted_at IS NULL` vào mọi query
- `deletedAt`: Column lưu timestamp khi record bị "xóa"

### 2. **Khi gọi delete()**

```java
// Thay vì xóa vật lý:
// DELETE FROM users WHERE id = 1

// Hibernate sẽ chạy:
// UPDATE users SET deleted_at = NOW() WHERE id = 1
userRepository.delete(user);
```

### 3. **Khi query dữ liệu**

```java
// Hibernate tự động thêm WHERE clause:
// SELECT * FROM users WHERE deleted_at IS NULL
userRepository.findAll();
userRepository.findById(1L);
```

## 💻 Cách sử dụng trong Code

### ✅ **Xóa mềm (Soft Delete)**

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Xóa mềm - chỉ set deleted_at
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        userRepository.delete(user); // Soft delete
        // SQL: UPDATE users SET deleted_at = NOW() WHERE id = ?
    }
}
```

### 🔄 **Khôi phục dữ liệu đã xóa**

```java
@Service
public class UserService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // Khôi phục user đã bị xóa mềm
    public void restoreUser(Long userId) {
        Query query = entityManager.createNativeQuery(
            "UPDATE users SET deleted_at = NULL WHERE id = :userId"
        );
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
}
```

### 🔍 **Tìm các record đã bị xóa**

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Query bỏ qua @Where filter để lấy cả records đã xóa
    @Query(value = "SELECT * FROM users WHERE deleted_at IS NOT NULL", 
           nativeQuery = true)
    List<User> findDeletedUsers();
    
    // Tìm user cụ thể đã bị xóa
    @Query(value = "SELECT * FROM users WHERE id = :id AND deleted_at IS NOT NULL", 
           nativeQuery = true)
    Optional<User> findDeletedUserById(@Param("id") Long id);
}
```

### 🗑️ **Xóa vĩnh viễn (Hard Delete) - Thận trọng!**

```java
@Service
public class UserService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // XÓA VĨNH VIỄN - Không thể khôi phục!
    @Transactional
    public void permanentlyDeleteUser(Long userId) {
        Query query = entityManager.createNativeQuery(
            "DELETE FROM users WHERE id = :userId"
        );
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
}
```

## 🎨 Controller Examples

### Delete Endpoint

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Soft delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User đã được xóa"));
    }
    
    // Restore deleted user
    @PutMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User đã được khôi phục"));
    }
    
    // Lấy danh sách users đã xóa
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getDeletedUsers() {
        List<User> users = userService.getDeletedUsers();
        List<UserResponse> response = users.stream()
            .map(this::mapToResponse)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
```

## ⚠️ Lưu ý quan trọng

### 1. **Foreign Key Constraints**
```sql
-- Vẫn giữ được FK relationship vì record không bị xóa vật lý
SELECT * FROM orders WHERE user_id = 1; -- Vẫn hoạt động
```

### 2. **Unique Constraints**
```java
// Vấn đề: Email unique nhưng user đã xóa vẫn giữ email
// Giải pháp: Có thể null email khi xóa hoặc thêm suffix

@PreRemove
public void preRemove() {
    this.email = this.email + "_deleted_" + this.id;
}
```

### 3. **Cascade Operations**
```java
// CẨN THẬN với cascade delete
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private Set<Order> orders;

// Khi delete user, tất cả orders cũng bị soft delete
```

### 4. **Performance**
```sql
-- Thêm index cho deleted_at để tăng performance
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_services_deleted_at ON services(deleted_at);
```

## 📊 Database Schema

### Migration Script (MySQL)

```sql
-- Thêm deleted_at column cho các bảng
ALTER TABLE users ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL;
ALTER TABLE branches ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL;
ALTER TABLE services ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL;
ALTER TABLE orders ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL;
ALTER TABLE promotions ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL;
ALTER TABLE shippers ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL;

-- Thêm index để tăng performance query
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_branches_deleted_at ON branches(deleted_at);
CREATE INDEX idx_services_deleted_at ON services(deleted_at);
CREATE INDEX idx_orders_deleted_at ON orders(deleted_at);
CREATE INDEX idx_promotions_deleted_at ON promotions(deleted_at);
CREATE INDEX idx_shippers_deleted_at ON shippers(deleted_at);
```

## 🔄 Best Practices

### ✅ DO
- Sử dụng soft delete cho entities có relationships phức tạp
- Implement restore functionality cho users
- Log actions khi soft delete/restore
- Định kỳ archive hoặc hard delete dữ liệu quá cũ
- Thêm index cho `deleted_at` column

### ❌ DON'T
- Không hard delete entities có FK relationships
- Không quên handle unique constraints
- Không soft delete entities đơn giản (như logs)
- Không cascade delete nếu không cần thiết

## 🧪 Testing

```java
@Test
public void testSoftDelete() {
    // Create user
    User user = new User();
    user.setEmail("test@example.com");
    userRepository.save(user);
    
    // Soft delete
    userRepository.delete(user);
    
    // Không tìm thấy bằng findAll (do @Where filter)
    List<User> users = userRepository.findAll();
    assertFalse(users.contains(user));
    
    // Nhưng vẫn tồn tại trong DB
    User deletedUser = userRepository.findDeletedUserById(user.getId()).get();
    assertNotNull(deletedUser.getDeletedAt());
}
```

## 📝 Summary

Soft Delete giúp:
1. ✅ Bảo vệ dữ liệu khỏi bị xóa nhầm
2. ✅ Giữ referential integrity
3. ✅ Audit trail đầy đủ
4. ✅ Có thể rollback operations
5. ✅ Compliance với regulations (GDPR, etc.)

**Khi nào nên dùng Soft Delete?**
- Entities có nhiều relationships
- Dữ liệu quan trọng (users, orders, transactions)
- Cần audit trail
- Production systems

**Khi nào nên dùng Hard Delete?**
- Temporary data (OTP, sessions)
- Log files
- Cache data
- Test data
