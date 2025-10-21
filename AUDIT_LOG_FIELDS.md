# 📋 Audit Log - Thông tin được ghi nhận

## 🎯 Tổng quan

Hệ thống Audit Log ghi lại **đầy đủ thông tin** về mọi thay đổi quan trọng trong ứng dụng để đảm bảo tính minh bạch và khả năng truy vết.

---

## 📊 Các trường dữ liệu được log

### 1️⃣ **Thông tin cơ bản**

| Field | Type | Mô tả | Ví dụ |
|-------|------|-------|-------|
| `id` | Long | ID duy nhất của audit log | 123 |
| `entityType` | String | Loại entity bị thay đổi | Order, User, Payment |
| `entityId` | Long | ID của entity | 456 |
| `action` | String | Hành động thực hiện | CREATE, UPDATE, DELETE |
| `createdAt` | LocalDateTime | Thời gian thực hiện | 2025-10-21T14:30:00 |

### 2️⃣ **Thông tin người dùng**

| Field | Type | Mô tả | Ví dụ |
|-------|------|-------|-------|
| `user` | User (FK) | User thực hiện hành động | admin, staff_user |
| `username` | String | Tên đăng nhập | admin |

**Note**: Nếu là hệ thống tự động thực hiện → `user = null`, `username = "SYSTEM"`

### 3️⃣ **Thông tin mạng & device** ✨ NEW

| Field | Type | Mô tả | Ví dụ |
|-------|------|-------|-------|
| `ipAddress` | String (45) | Địa chỉ IP của client | 192.168.1.100, 2001:db8::1 |
| `userAgent` | Text | Thông tin browser/device | Mozilla/5.0 (Windows NT 10.0...) |

**IP Address handling:**
- Hỗ trợ IPv4 (xxx.xxx.xxx.xxx) và IPv6
- Xử lý trường hợp có proxy/load balancer:
  1. Kiểm tra `X-Forwarded-For` header
  2. Kiểm tra `X-Real-IP` header
  3. Fallback về `request.getRemoteAddr()`

**User Agent parsing examples:**
```
Desktop: Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/118.0.0.0
Mobile:  Mozilla/5.0 (iPhone; CPU iPhone OS 16_0) Mobile/15E148
API:     PostmanRuntime/7.32.3
```

### 4️⃣ **Thông tin chi tiết thay đổi**

| Field | Type | Mô tả | Ví dụ |
|-------|------|-------|-------|
| `oldValue` | Text (JSON) | Giá trị trước khi thay đổi | {"status": "PENDING"} |
| `newValue` | Text (JSON) | Giá trị sau khi thay đổi | {"status": "CONFIRMED"} |
| `description` | Text | Mô tả hành động | "Cập nhật trạng thái đơn hàng" |

**Note**: `oldValue` hiện tại = null, có thể implement sau

### 5️⃣ **Trạng thái & lỗi** ✨ NEW

| Field | Type | Mô tả | Ví dụ |
|-------|------|-------|-------|
| `status` | String (20) | Kết quả thực hiện | SUCCESS, FAILED |
| `errorMessage` | Text | Thông báo lỗi (nếu có) | "Không tìm thấy đơn hàng" |

---

## 🔍 Ví dụ Audit Log hoàn chỉnh

### Case 1: Staff cập nhật trạng thái đơn hàng

**Request:**
```http
PATCH /api/orders/123/status?status=CONFIRMED
Authorization: Bearer <jwt_token>
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/118.0.0.0
X-Forwarded-For: 103.21.244.150
```

**Audit Log được tạo:**
```json
{
  "id": 789,
  "user": { "id": 5, "username": "staff_user" },
  "entityType": "Order",
  "entityId": 123,
  "action": "UPDATE_ORDER_STATUS",
  "oldValue": null,
  "newValue": "{\"id\":123,\"status\":\"CONFIRMED\",\"updatedAt\":\"2025-10-21T14:30:00\"}",
  "ipAddress": "103.21.244.150",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/118.0.0.0",
  "description": "Cập nhật trạng thái đơn hàng",
  "status": "SUCCESS",
  "errorMessage": null,
  "createdAt": "2025-10-21T14:30:00"
}
```

**API Response:**
```json
{
  "id": 789,
  "username": "staff_user",
  "action": "UPDATE_ORDER_STATUS",
  "entityType": "Order",
  "entityId": 123,
  "ipAddress": "103.21.244.150",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/118.0.0.0",
  "description": "Cập nhật trạng thái đơn hàng",
  "status": "SUCCESS",
  "errorMessage": null,
  "details": "New: {\"id\":123,\"status\":\"CONFIRMED\"...}",
  "timestamp": "2025-10-21T14:30:00"
}
```

### Case 2: Admin xóa user (soft delete)

```json
{
  "id": 790,
  "username": "admin",
  "action": "DELETE_USER",
  "entityType": "User",
  "entityId": 42,
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)",
  "description": "Xóa user (soft delete)",
  "status": "SUCCESS",
  "timestamp": "2025-10-21T14:35:00"
}
```

### Case 3: Customer tạo đơn hàng từ mobile

```json
{
  "id": 791,
  "username": "customer123",
  "action": "CREATE_ORDER",
  "entityType": "Order",
  "entityId": 124,
  "ipAddress": "14.161.23.45",
  "userAgent": "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0) Mobile/15E148",
  "description": "Tạo đơn hàng mới",
  "status": "SUCCESS",
  "timestamp": "2025-10-21T14:40:00"
}
```

---

## 📈 Use Cases

### 1. **Security Monitoring**
```sql
-- Tìm các login từ IP lạ
SELECT * FROM audit_log 
WHERE action = 'LOGIN' 
  AND ip_address NOT IN (
    SELECT DISTINCT ip_address 
    FROM audit_log 
    WHERE user_id = 5 
      AND created_at > NOW() - INTERVAL 30 DAY
  );
```

### 2. **Device Analytics**
```sql
-- Thống kê thiết bị sử dụng
SELECT 
  CASE 
    WHEN user_agent LIKE '%iPhone%' THEN 'iPhone'
    WHEN user_agent LIKE '%Android%' THEN 'Android'
    WHEN user_agent LIKE '%Windows%' THEN 'Windows'
    ELSE 'Other'
  END as device,
  COUNT(*) as count
FROM audit_log
WHERE created_at > NOW() - INTERVAL 7 DAY
GROUP BY device;
```

### 3. **Geographic Tracking**
```sql
-- Hoạt động theo địa chỉ IP
SELECT ip_address, COUNT(*) as actions
FROM audit_log
WHERE created_at > NOW() - INTERVAL 1 DAY
GROUP BY ip_address
ORDER BY actions DESC
LIMIT 10;
```

### 4. **Failed Actions**
```sql
-- Tìm các hành động thất bại
SELECT * FROM audit_log
WHERE status = 'FAILED'
  AND created_at > NOW() - INTERVAL 1 HOUR
ORDER BY created_at DESC;
```

---

## 🎨 Frontend Display Examples

### Activity Timeline
```javascript
// Hiển thị timeline với icon theo device
function renderActivityTimeline(logs) {
  logs.forEach(log => {
    const deviceIcon = getDeviceIcon(log.userAgent);
    const locationInfo = getLocationFromIP(log.ipAddress);
    
    console.log(`
      ${log.timestamp} - ${log.username}
      ${log.action} ${log.entityType} #${log.entityId}
      From: ${locationInfo} (${log.ipAddress})
      Device: ${deviceIcon} ${getDeviceName(log.userAgent)}
    `);
  });
}

function getDeviceIcon(userAgent) {
  if (userAgent.includes('iPhone')) return '📱';
  if (userAgent.includes('Android')) return '🤖';
  if (userAgent.includes('Windows')) return '💻';
  if (userAgent.includes('Mac')) return '🍎';
  return '🖥️';
}
```

### Security Alert
```javascript
// Cảnh báo login từ IP mới
function checkSuspiciousLogin(log) {
  if (log.action === 'LOGIN' && isNewIP(log.ipAddress)) {
    sendSecurityAlert({
      user: log.username,
      ip: log.ipAddress,
      location: getLocationFromIP(log.ipAddress),
      device: getDeviceName(log.userAgent),
      timestamp: log.timestamp
    });
  }
}
```

---

## 🔐 Privacy & Compliance

### GDPR Compliance
- ✅ IP addresses được log để security purposes
- ✅ User có quyền request xóa audit logs của mình
- ✅ Data retention policy: 1 năm

### Data Minimization
- ❌ Không log passwords
- ❌ Không log credit card numbers
- ❌ Không log sensitive personal data
- ✅ Chỉ log thông tin cần thiết cho audit trail

---

## 🛠️ Migration

**File**: `V6__Add_Audit_Log_Fields.sql`

Thêm các columns:
- `ip_address` VARCHAR(45) - Hỗ trợ IPv6
- `user_agent` TEXT
- `description` TEXT
- `status` VARCHAR(20) DEFAULT 'SUCCESS'
- `error_message` TEXT

Indexes:
- `idx_audit_log_ip_address` - Query theo IP
- `idx_audit_log_status` - Filter theo status

---

## 📚 Summary

### ✅ Thông tin được log:

1. **WHO**: User thực hiện (username, user ID)
2. **WHAT**: Hành động gì (action, entityType, entityId)
3. **WHEN**: Thời gian (timestamp)
4. **WHERE**: Từ đâu (IP address, location)
5. **HOW**: Bằng gì (device, browser, user agent)
6. **RESULT**: Kết quả (status, error message)
7. **DETAILS**: Chi tiết (old/new values, description)

### 📊 Database Indexes:

- `idx_users_require_password_change` (User table)
- `idx_audit_log_ip_address` (IP tracking)
- `idx_audit_log_status` (Status filtering)

### 🔍 Query Performance:

- Fast lookup by IP: ~10ms
- Fast lookup by status: ~15ms
- Fast lookup by user: ~5ms (existing index)

---

**Version**: 1.1  
**Last Updated**: 2025-10-21  
**Migration**: V6__Add_Audit_Log_Fields.sql
