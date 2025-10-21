# 📝 Audit Log System - Automatic Tracking

## 🎯 Tổng quan

Hệ thống Audit Log tự động ghi lại mọi thay đổi quan trọng trong ứng dụng sử dụng **AOP (Aspect-Oriented Programming)**. Không cần code thủ công, chỉ cần thêm annotation `@Audited` vào method.

## 🏗️ Kiến trúc

```
┌─────────────────┐
│   Controller    │
└────────┬────────┘
         │
         ↓
┌─────────────────┐       ┌──────────────────┐
│   Service       │ ←───→ │  @Audited        │
│  @Audited       │       │  Annotation      │
└────────┬────────┘       └──────────────────┘
         │                         ↓
         │                ┌──────────────────┐
         ↓                │  AuditLogAspect  │
┌─────────────────┐       │  (Intercept)     │
│   Repository    │       └────────┬─────────┘
└─────────────────┘                │
                                   ↓
                          ┌──────────────────┐
                          │  AuditLog Table  │
                          └──────────────────┘
```

## 📦 Components

### 1. **@Audited Annotation**
```java
@Audited(
    action = "UPDATE_ORDER_STATUS",
    entityType = "Order",
    description = "Cập nhật trạng thái đơn hàng"
)
public OrderResponse updateOrderStatus(Long orderId, String status) {
    // Your business logic
}
```

**Parameters:**
- `action`: Hành động thực hiện (VD: CREATE, UPDATE, DELETE)
- `entityType`: Loại entity (VD: Order, User, Payment)
- `description`: Mô tả chi tiết (optional)

### 2. **AuditLogAspect**
- Intercept tất cả methods có `@Audited`
- Tự động capture:
  - User hiện tại từ SecurityContext
  - Entity ID từ return value
  - Serialized data (JSON)
  - Timestamp
- Ghi vào database

### 3. **AuditLog Entity**
```java
AuditLog {
    id: Long
    user: User               // Người thực hiện
    entityType: String       // Loại entity
    entityId: Long           // ID của entity
    action: String           // Hành động
    oldValue: String         // Giá trị cũ (JSON)
    newValue: String         // Giá trị mới (JSON)
    createdAt: LocalDateTime // Thời gian
}
```

## 🚀 Cách sử dụng

### Step 1: Thêm @Audited vào method

```java
@Service
public class OrderService {
    
    @Audited(action = "CREATE_ORDER", entityType = "Order")
    public OrderResponse createOrder(OrderRequest request) {
        // Business logic
        Order order = orderRepository.save(newOrder);
        return mapToOrderResponse(order);
    }
    
    @Audited(action = "UPDATE_ORDER_STATUS", entityType = "Order")
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        return mapToOrderResponse(orderRepository.save(order));
    }
}
```

### Step 2: Hệ thống tự động ghi log

Khi method được gọi:
1. AOP Aspect intercept method call
2. Lấy thông tin user từ JWT
3. Extract entity ID từ return value
4. Serialize data thành JSON
5. Ghi vào `audit_log` table

**Không cần code gì thêm!** ✨

## 📊 Methods đã được Audit

### 1. **OrderService**
| Method | Action | Description |
|--------|--------|-------------|
| `createOrder()` | CREATE_ORDER | Tạo đơn hàng mới |
| `updateOrderStatus()` | UPDATE_ORDER_STATUS | Cập nhật trạng thái đơn |
| `cancelOrder()` | CANCEL_ORDER | Hủy đơn hàng |

### 2. **UserService**
| Method | Action | Description |
|--------|--------|-------------|
| `updateUser()` | UPDATE_USER | Cập nhật thông tin user |
| `deleteUser()` | DELETE_USER | Xóa user (soft delete) |
| `assignRole()` | ASSIGN_ROLE | Gán role cho user |

### 3. **PaymentService**
| Method | Action | Description |
|--------|--------|-------------|
| `createPayment()` | CREATE_PAYMENT | Tạo thanh toán mới |
| `updatePaymentStatus()` | UPDATE_PAYMENT_STATUS | Cập nhật trạng thái thanh toán |

## 🔍 API Endpoints để xem Audit Logs

### 1. **GET `/api/audit-logs`**
Lấy tất cả audit logs (ADMIN only)

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "admin",
      "action": "UPDATE_ORDER_STATUS",
      "entityType": "Order",
      "entityId": 123,
      "details": "Changed status to CONFIRMED",
      "timestamp": "2025-10-21T10:30:00"
    }
  ]
}
```

### 2. **GET `/api/audit-logs/{id}`**
Xem chi tiết audit log

### 3. **GET `/api/audit-logs/user/{userId}`**
Xem audit logs của một user

### 4. **GET `/api/audit-logs/entity/{entityType}/{entityId}`**
Xem audit logs của một entity cụ thể

**Example:** `GET /api/audit-logs/entity/Order/123`

### 5. **GET `/api/audit-logs/action/{action}`**
Lọc theo action (CREATE, UPDATE, DELETE)

### 6. **GET `/api/audit-logs/date-range`**
Lọc theo khoảng thời gian

**Params:**
- `startDate`: 2025-10-01T00:00:00
- `endDate`: 2025-10-31T23:59:59

## 📝 Ví dụ Flow hoàn chỉnh

### Scenario: Staff cập nhật trạng thái đơn hàng

1. **Request:**
```bash
PATCH /api/orders/123/status?status=CONFIRMED
Authorization: Bearer <staff_jwt_token>
```

2. **Service Method:**
```java
@Audited(action = "UPDATE_ORDER_STATUS", entityType = "Order")
public OrderResponse updateOrderStatus(Long orderId, String status) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    order.setStatus(OrderStatus.valueOf(status));
    return mapToOrderResponse(orderRepository.save(order));
}
```

3. **AOP Aspect tự động:**
```java
// Intercept method call
// → Extract user: "staff_user"
// → Extract entity ID: 123
// → Serialize return value: {"id": 123, "status": "CONFIRMED", ...}
// → Save audit log
```

4. **Database Record:**
```sql
INSERT INTO audit_log (user_id, entity_type, entity_id, action, new_value, created_at)
VALUES (5, 'Order', 123, 'UPDATE_ORDER_STATUS', '{"id":123,"status":"CONFIRMED",...}', NOW());
```

5. **Admin có thể xem:**
```bash
GET /api/audit-logs/entity/Order/123
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 456,
      "username": "staff_user",
      "action": "UPDATE_ORDER_STATUS",
      "entityType": "Order",
      "entityId": 123,
      "details": "New: {\"id\":123,\"status\":\"CONFIRMED\"}",
      "timestamp": "2025-10-21T14:30:00"
    }
  ]
}
```

## 🎨 Frontend Integration

### Dashboard - Recent Activities
```javascript
// Lấy 10 audit logs gần nhất
fetch('/api/audit-logs?limit=10&sort=createdAt,desc')
  .then(res => res.json())
  .then(data => {
    data.data.forEach(log => {
      console.log(`${log.username} ${log.action} ${log.entityType} #${log.entityId}`);
      // Output: "admin UPDATE_ORDER_STATUS Order #123"
    });
  });
```

### Order Detail - Activity History
```javascript
// Xem lịch sử thay đổi của đơn hàng
const orderId = 123;
fetch(`/api/audit-logs/entity/Order/${orderId}`)
  .then(res => res.json())
  .then(data => {
    // Hiển thị timeline các thay đổi
    renderTimeline(data.data);
  });
```

### User Profile - Action History
```javascript
// Xem các hành động của user
const userId = 5;
fetch(`/api/audit-logs/user/${userId}`)
  .then(res => res.json())
  .then(data => {
    // Hiển thị lịch sử hoạt động
    renderUserActivity(data.data);
  });
```

## 🔧 Thêm Audit cho Service mới

### Example: ReviewService

```java
@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    
    @Audited(action = "CREATE_REVIEW", entityType = "Review", 
             description = "Khách hàng tạo đánh giá")
    public ReviewResponse createReview(ReviewRequest request) {
        Review review = new Review();
        // ... business logic
        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }
    
    @Audited(action = "DELETE_REVIEW", entityType = "Review",
             description = "Admin xóa đánh giá không phù hợp")
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
```

**Chỉ cần thêm `@Audited` annotation!** Hệ thống tự động ghi log.

## ⚙️ Configuration

### Enable/Disable Audit Logging

**application.properties:**
```properties
# Enable AOP for Audit Logging (default: true)
spring.aop.auto=true

# Audit Log settings
audit.enabled=true
audit.log-to-database=true
audit.log-to-file=false
```

## 🚨 Lưu ý

### 1. **Return Value bắt buộc**
Method có `@Audited` phải return object có method `getId()` để extract entity ID.

```java
// ✅ Good
@Audited(action = "UPDATE", entityType = "Order")
public OrderResponse updateOrder(Long id) {
    return orderResponse; // Has getId()
}

// ❌ Bad
@Audited(action = "UPDATE", entityType = "Order")
public void updateOrder(Long id) {
    // No return value → Cannot extract entity ID
}
```

### 2. **Performance**
- AOP có overhead nhẹ (~1-2ms per call)
- JSON serialization có thể chậm với object lớn
- Xem xét disable audit cho read-only operations

### 3. **Database Growth**
Audit logs có thể tăng nhanh. Nên có chiến lược:
- Archive logs cũ (>6 tháng)
- Cleanup logs không quan trọng
- Add indexes cho query performance

```sql
-- Cleanup audit logs cũ hơn 1 năm
DELETE FROM audit_log WHERE created_at < NOW() - INTERVAL 1 YEAR;

-- Archive to separate table
INSERT INTO audit_log_archive SELECT * FROM audit_log WHERE created_at < '2024-01-01';
DELETE FROM audit_log WHERE created_at < '2024-01-01';
```

## 📈 Monitoring & Analytics

### Dashboard Queries

**1. Most Active Users (Last 30 days)**
```sql
SELECT u.username, COUNT(*) as action_count
FROM audit_log al
JOIN users u ON al.user_id = u.id
WHERE al.created_at > NOW() - INTERVAL 30 DAY
GROUP BY u.username
ORDER BY action_count DESC
LIMIT 10;
```

**2. Most Common Actions**
```sql
SELECT action, COUNT(*) as count
FROM audit_log
WHERE created_at > NOW() - INTERVAL 7 DAY
GROUP BY action
ORDER BY count DESC;
```

**3. Activity by Hour**
```sql
SELECT HOUR(created_at) as hour, COUNT(*) as count
FROM audit_log
WHERE DATE(created_at) = CURDATE()
GROUP BY HOUR(created_at)
ORDER BY hour;
```

## 🎯 Roadmap

### Phase 2 Enhancements
- [ ] Track old values (before changes)
- [ ] IP address tracking
- [ ] User agent tracking
- [ ] Async logging (non-blocking)
- [ ] Elasticsearch integration for search
- [ ] Real-time notifications for critical actions

---

## 📚 Related Files

- `@Audited`: `src/main/java/com/washify/apis/annotation/Audited.java`
- `AuditLogAspect`: `src/main/java/com/washify/apis/aspect/AuditLogAspect.java`
- `AuditLog`: `src/main/java/com/washify/apis/entity/AuditLog.java`
- `AuditLogService`: `src/main/java/com/washify/apis/service/AuditLogService.java`
- `AuditLogController`: `src/main/java/com/washify/apis/controller/AuditLogController.java`

---

**Version**: 1.0  
**Last Updated**: 2025-10-21  
**Tech Stack**: Spring Boot 3.3.5 + Spring AOP + AspectJ
