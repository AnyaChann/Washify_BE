# 🔍 Phân Tích Controllers & Endpoints Thiếu

## 📊 Mapping: Database Tables → Controllers

### ✅ Có Controller (14 controllers)

| Database Table | Controller | Endpoint | Status |
|---------------|------------|----------|--------|
| users | ✅ UserController | /users | ✅ |
| roles | ❌ **MISSING** | - | ❌ |
| branches | ✅ BranchController | /branches | ✅ |
| services | ✅ ServiceController | /services | ✅ |
| orders | ✅ OrderController | /orders | ✅ |
| order_items | ❌ **MISSING** | - | ❌ |
| payments | ✅ PaymentController | /payments | ✅ |
| promotions | ❌ **MISSING** | - | ❌ |
| shippers | ❌ **MISSING** | - | ❌ |
| shipments | ✅ ShipmentController | /shipments | ✅ |
| reviews | ✅ ReviewController | /reviews | ✅ |
| notifications | ✅ NotificationController | /notifications | ✅ |
| audit_log | ❌ **MISSING** | - | ❌ |
| attachments | ❌ **MISSING** | - | ❌ |

### 🔐 Auth Controllers (6 controllers)
| Purpose | Controller | Endpoint |
|---------|------------|----------|
| Authentication | ✅ AuthController | /auth |
| Password Reset | ✅ PasswordResetController | /api/auth |
| Password Change | ✅ PasswordChangeController | /auth/password-change |
| Password 2FA | ✅ PasswordChange2FAController | /api/auth/security/2fa-toggle |
| Email Verification | ✅ EmailVerificationController | /api/auth/email |
| Soft Delete | ✅ SoftDeleteController | /api/soft-delete |

---

## ❌ Controllers THIẾU (6 controllers)

### 1. ❌ RoleController
**Bảng:** `roles`  
**Repository:** ✅ RoleRepository.java  
**Entity:** ✅ Role.java

**Chức năng cần có:**
```
GET    /roles              - Lấy danh sách roles
GET    /roles/{id}         - Chi tiết role
POST   /roles              - Tạo role mới (Admin)
PUT    /roles/{id}         - Cập nhật role (Admin)
DELETE /roles/{id}         - Xóa role (Admin)
GET    /roles/{id}/users   - Lấy users có role này
```

**Priority:** 🔥 HIGH (Cần cho phân quyền)

---

### 2. ❌ PromotionController
**Bảng:** `promotions`  
**Repository:** ✅ PromotionRepository.java  
**Entity:** ✅ Promotion.java

**Chức năng cần có:**
```
GET    /promotions              - Lấy danh sách khuyến mãi
GET    /promotions/{id}         - Chi tiết promotion
GET    /promotions/code/{code}  - Tìm theo mã code
POST   /promotions              - Tạo promotion (Staff/Admin)
PUT    /promotions/{id}         - Cập nhật promotion (Staff/Admin)
DELETE /promotions/{id}         - Xóa promotion (Admin)
POST   /promotions/{id}/validate - Validate mã có hợp lệ không
GET    /promotions/{id}/usage   - Thống kê lượt dùng
```

**Priority:** 🔥 HIGH (Core business logic - giảm giá)

---

### 3. ❌ ShipperController
**Bảng:** `shippers`  
**Repository:** ✅ ShipperRepository.java  
**Entity:** ✅ Shipper.java

**Chức năng cần có:**
```
GET    /shippers           - Lấy danh sách shippers
GET    /shippers/{id}      - Chi tiết shipper
GET    /shippers/active    - Shippers đang active
POST   /shippers           - Tạo shipper (Admin)
PUT    /shippers/{id}      - Cập nhật shipper (Admin)
DELETE /shippers/{id}      - Xóa shipper (Admin)
GET    /shippers/{id}/shipments - Lịch sử giao hàng
```

**Priority:** 🔥 HIGH (Quản lý giao hàng)

---

### 4. ❌ OrderItemController
**Bảng:** `order_items`  
**Repository:** ✅ OrderItemRepository.java  
**Entity:** ✅ OrderItem.java

**Note:** Thường OrderItem được quản lý qua OrderController  
**Có thể không cần controller riêng**

**Nếu cần:**
```
GET    /orders/{orderId}/items       - Items của order
POST   /orders/{orderId}/items       - Thêm item vào order
PUT    /orders/{orderId}/items/{id}  - Cập nhật item
DELETE /orders/{orderId}/items/{id}  - Xóa item
```

**Priority:** 🟡 MEDIUM (Có thể merge vào OrderController)

---

### 5. ❌ AuditLogController
**Bảng:** `audit_log`  
**Repository:** ✅ AuditLogRepository.java  
**Entity:** ✅ AuditLog.java

**Chức năng cần có:**
```
GET    /audit-logs              - Lấy audit logs (Admin only)
GET    /audit-logs/{id}         - Chi tiết audit log
GET    /audit-logs/user/{userId} - Logs của user
GET    /audit-logs/entity/{type}/{id} - Logs của entity
GET    /audit-logs/search       - Tìm kiếm logs (by action, date, etc.)
```

**Priority:** 🟡 MEDIUM (Giám sát hệ thống)

---

### 6. ❌ AttachmentController
**Bảng:** `attachments`  
**Repository:** ✅ AttachmentRepository.java  
**Entity:** ✅ Attachment.java

**Chức năng cần có:**
```
GET    /attachments/{id}         - Xem/download attachment
POST   /orders/{orderId}/attachments - Upload file cho order
POST   /shipments/{id}/attachments   - Upload file cho shipment
DELETE /attachments/{id}         - Xóa attachment (Admin)
GET    /orders/{orderId}/attachments - Attachments của order
```

**Priority:** 🟢 LOW (File upload/download - có thể merge vào Order/Shipment)

---

## 📋 Summary

### Controllers Có (14):
```
✅ AuthController
✅ PasswordResetController
✅ PasswordChangeController
✅ PasswordChange2FAController
✅ EmailVerificationController
✅ SoftDeleteController
✅ UserController
✅ BranchController
✅ ServiceController
✅ OrderController
✅ PaymentController
✅ ShipmentController
✅ ReviewController
✅ NotificationController
```

### Controllers Thiếu (6):
```
❌ RoleController          - 🔥 HIGH Priority
❌ PromotionController     - 🔥 HIGH Priority
❌ ShipperController       - 🔥 HIGH Priority
❌ OrderItemController     - 🟡 MEDIUM Priority (có thể merge)
❌ AuditLogController      - 🟡 MEDIUM Priority
❌ AttachmentController    - 🟢 LOW Priority (có thể merge)
```

---

## 🎯 Recommendations

### Must Have (HIGH Priority):
1. **RoleController** - Cần cho quản lý phân quyền
2. **PromotionController** - Core business: mã giảm giá
3. **ShipperController** - Quản lý đội ngũ giao hàng

### Should Have (MEDIUM Priority):
4. **AuditLogController** - Giám sát và audit trail
5. **OrderItemController** - Hoặc merge vào OrderController

### Nice to Have (LOW Priority):
6. **AttachmentController** - Hoặc merge vào Order/Shipment controllers

---

## 🚀 Implementation Order

### Phase 1: Core Business Logic
```
1. PromotionController   - Mã giảm giá (khách hàng cần)
2. RoleController        - Phân quyền (admin cần)
3. ShipperController     - Giao hàng (operations cần)
```

### Phase 2: Management & Monitoring
```
4. AuditLogController    - Giám sát hệ thống
5. OrderItemController   - Quản lý chi tiết đơn hàng
```

### Phase 3: Optional Enhancements
```
6. AttachmentController  - File management
```

---

## 💡 Alternative Approaches

### Option 1: Separate Controllers (Recommended)
- Mỗi entity có controller riêng
- Dễ maintain và scale
- RESTful design chuẩn

### Option 2: Merge Related Controllers
- OrderController bao gồm OrderItem endpoints
- OrderController/ShipmentController bao gồm Attachment endpoints
- Giảm số lượng files

### Option 3: Hybrid
- Core entities: separate controllers
- Support entities (OrderItem, Attachment): merge vào parent

---

## 🔍 Missing Endpoints in Existing Controllers

### OrderController - Có thể thiếu:
```
POST   /orders/{id}/items           - Thêm items vào order
PUT    /orders/{id}/items/{itemId}  - Cập nhật item
DELETE /orders/{id}/items/{itemId}  - Xóa item
POST   /orders/{id}/promotions      - Áp dụng promotion code
GET    /orders/{id}/invoice         - Tạo invoice/bill
```

### UserController - Có thể thiếu:
```
POST   /users/{id}/roles            - Gán role cho user
DELETE /users/{id}/roles/{roleId}   - Xóa role khỏi user
GET    /users/{id}/orders           - Lịch sử orders
GET    /users/{id}/reviews          - Reviews của user
```

### PaymentController - Có thể thiếu:
```
POST   /payments/{id}/refund        - Hoàn tiền
GET    /payments/order/{orderId}    - Payment của order
POST   /payments/{id}/verify        - Verify thanh toán (callback)
```

---

## ✅ Action Items

1. **Review** các controllers hiện có - check endpoints đã đủ chưa
2. **Prioritize** 3 controllers: Promotion, Role, Shipper
3. **Design** API specs cho 3 controllers priority cao
4. **Implement** controllers theo thứ tự priority
5. **Test** endpoints với Swagger UI
6. **Document** trong Swagger với access control

---

> 🎯 **Kết luận:** Thiếu 6 controllers, trong đó 3 HIGH priority (Role, Promotion, Shipper) cần implement ngay để hệ thống hoàn chỉnh.
