# 🔍 Detailed Review: Controllers & Missing Endpoints

## 📊 Summary của Controllers Hiện Có

### ✅ Controllers Analysis (14 controllers reviewed)

---

## 1. ✅ OrderController - **GẦN ĐỦ** 

### Có (7 endpoints):
```
POST   /orders                      ✅ Tạo order
GET    /orders/{id}                 ✅ Chi tiết order
GET    /orders/user/{userId}        ✅ Orders của user
GET    /orders/status/{status}      ✅ Filter theo status
PATCH  /orders/{id}/status          ✅ Cập nhật status
PATCH  /orders/{id}/cancel          ✅ Hủy order
```

### ❌ Thiếu (Quan trọng):
```
❌ POST   /orders/{id}/items              - Thêm OrderItem vào order
❌ PUT    /orders/{id}/items/{itemId}     - Cập nhật OrderItem
❌ DELETE /orders/{id}/items/{itemId}     - Xóa OrderItem
❌ GET    /orders/{id}/items              - Lấy danh sách items
❌ POST   /orders/{id}/promotions         - Áp dụng mã giảm giá
❌ DELETE /orders/{id}/promotions/{promoId} - Xóa promotion
❌ GET    /orders/{id}/total              - Tính tổng tiền
❌ GET    /orders                         - Lấy TẤT CẢ orders (Admin/Staff)
```

**Priority:** 🔥 HIGH (OrderItem management thiếu hoàn toàn)

---

## 2. ✅ UserController - **ĐẦY ĐỦ**

### Có (11 endpoints):
```
GET    /users/{id}                         ✅ Chi tiết user
GET    /users/email/{email}                ✅ Tìm theo email
GET    /users                              ✅ Danh sách users
PUT    /users/{id}                         ✅ Cập nhật user
DELETE /users/{id}                         ✅ Xóa user
POST   /users/{id}/roles/{roleName}        ✅ Gán role
POST   /users/{id}/change-password         ✅ Đổi password
PUT    /users/{id}/security/password-change-2fa ✅ Toggle 2FA
```

### ❌ Có thể thêm:
```
❌ DELETE /users/{id}/roles/{roleName}     - Xóa role khỏi user
❌ GET    /users/{id}/roles                - Lấy danh sách roles của user
❌ GET    /users/{id}/orders               - Orders của user
❌ GET    /users/{id}/reviews              - Reviews của user
❌ GET    /users/{id}/notifications        - Notifications của user
❌ GET    /users/search?q={query}          - Tìm kiếm user
❌ PATCH  /users/{id}/activate             - Kích hoạt user
❌ PATCH  /users/{id}/deactivate           - Vô hiệu hóa user
```

**Priority:** 🟡 MEDIUM (Nice to have)

---

## 3. ✅ PaymentController - **TỐT**

### Có (8 endpoints):
```
POST   /payments                      ✅ Tạo payment
GET    /payments/{id}                 ✅ Chi tiết payment
GET    /payments/order/{orderId}      ✅ Payment của order
GET    /payments/status/{status}      ✅ Filter theo status
PATCH  /payments/{id}/status          ✅ Cập nhật status
PATCH  /payments/{id}/confirm         ✅ Xác nhận thanh toán
PATCH  /payments/{id}/fail            ✅ Đánh dấu thất bại
```

### ❌ Có thể thêm:
```
❌ POST   /payments/{id}/refund            - Hoàn tiền
❌ POST   /payments/webhook                - Webhook cho payment gateway
❌ GET    /payments/user/{userId}          - Payments của user
❌ GET    /payments                        - Tất cả payments (Admin)
❌ GET    /payments/statistics             - Thống kê doanh thu
```

**Priority:** 🟡 MEDIUM (Refund là quan trọng)

---

## 4. ✅ ServiceController - **ĐẦY ĐỦ**

### Có (8 endpoints):
```
POST   /services                      ✅ Tạo service
GET    /services/{id}                 ✅ Chi tiết service
GET    /services                      ✅ Tất cả services
GET    /services/active               ✅ Services đang active
GET    /services/search?name={name}   ✅ Tìm kiếm theo tên
PUT    /services/{id}                 ✅ Cập nhật service
DELETE /services/{id}                 ✅ Xóa service
```

### ❌ Có thể thêm:
```
❌ GET    /services/popular              - Services phổ biến nhất
❌ GET    /services/price-range          - Filter theo giá
❌ PATCH  /services/{id}/activate        - Kích hoạt service
❌ PATCH  /services/{id}/deactivate      - Vô hiệu hóa service
```

**Priority:** 🟢 LOW (Đã đủ cho MVP)

---

## 5. ✅ BranchController - **ĐƠN GIẢN NHƯNG OK**

### Có (5 endpoints):
```
POST   /branches                      ✅ Tạo branch
GET    /branches/{id}                 ✅ Chi tiết branch
GET    /branches                      ✅ Tất cả branches
PUT    /branches/{id}                 ✅ Cập nhật branch
DELETE /branches/{id}                 ✅ Xóa branch
```

### ❌ Có thể thêm:
```
❌ GET    /branches/active               - Branches đang hoạt động
❌ GET    /branches/{id}/users           - Nhân viên của branch
❌ GET    /branches/{id}/orders          - Orders của branch
❌ GET    /branches/{id}/services        - Services của branch
❌ GET    /branches/search               - Tìm kiếm branch
```

**Priority:** 🟢 LOW (Đủ cho hiện tại)

---

## 6. ✅ ShipmentController - **TỐT**

### Có (8 endpoints):
```
POST   /shipments                        ✅ Tạo shipment
GET    /shipments/{id}                   ✅ Chi tiết shipment
GET    /shipments/order/{orderId}        ✅ Shipment của order
GET    /shipments/shipper/{shipperId}    ✅ Shipments của shipper
GET    /shipments/status/{status}        ✅ Filter theo status
PATCH  /shipments/{id}/status            ✅ Cập nhật status
PATCH  /shipments/{id}/assign-shipper    ✅ Gán shipper
```

### ❌ Có thể thêm:
```
❌ GET    /shipments                       - Tất cả shipments (Admin)
❌ POST   /shipments/{id}/attachments      - Upload ảnh giao hàng
❌ GET    /shipments/{id}/attachments      - Xem ảnh giao hàng
❌ PATCH  /shipments/{id}/complete         - Hoàn thành giao hàng
❌ PATCH  /shipments/{id}/cancel           - Hủy giao hàng
```

**Priority:** 🟡 MEDIUM (Attachments quan trọng)

---

## 7. ✅ ReviewController - **TỐT**

### Có (8 endpoints):
```
POST   /reviews                      ✅ Tạo review
GET    /reviews/{id}                 ✅ Chi tiết review
GET    /reviews/order/{orderId}      ✅ Review của order
GET    /reviews/user/{userId}        ✅ Reviews của user
GET    /reviews                      ✅ Tất cả reviews
GET    /reviews/rating/{rating}      ✅ Filter theo rating
GET    /reviews/average-rating       ✅ Rating trung bình
DELETE /reviews/{id}                 ✅ Xóa review
```

### ❌ Có thể thêm:
```
❌ PUT    /reviews/{id}                    - Cập nhật review
❌ GET    /reviews/service/{serviceId}     - Reviews của service
❌ POST   /reviews/{id}/reply              - Reply review (Staff)
❌ GET    /reviews/recent                  - Reviews gần đây
```

**Priority:** 🟢 LOW (Đã khá đầy đủ)

---

## 8. ✅ NotificationController - **ĐẦY ĐỦ**

### Có (9 endpoints):
```
POST   /notifications                ✅ Tạo notification
POST   /notifications/bulk           ✅ Gửi hàng loạt
GET    /notifications/my             ✅ Notifications của tôi
GET    /notifications/unread         ✅ Chưa đọc
PATCH  /notifications/{id}/read      ✅ Đánh dấu đã đọc
PATCH  /notifications/read-all       ✅ Đánh dấu tất cả
DELETE /notifications/{id}           ✅ Xóa notification
GET    /notifications/unread/count   ✅ Đếm chưa đọc
```

### ❌ Có thể thêm:
```
❌ GET    /notifications/type/{type}       - Filter theo type
❌ DELETE /notifications/clear-all         - Xóa tất cả
❌ GET    /notifications/settings          - Cài đặt thông báo
```

**Priority:** 🟢 LOW (Đã rất đầy đủ)

---

## ❌ CONTROLLERS THIẾU HOÀN TOÀN

### 🔥 1. RoleController - **CRITICAL MISSING**

```
GET    /roles                              - Lấy tất cả roles
GET    /roles/{id}                         - Chi tiết role
POST   /roles                              - Tạo role mới (Admin)
PUT    /roles/{id}                         - Cập nhật role (Admin)
DELETE /roles/{id}                         - Xóa role (Admin)
GET    /roles/{id}/users                   - Users có role này
GET    /roles/{id}/permissions             - Permissions của role
POST   /roles/{id}/permissions/{perm}     - Gán permission
DELETE /roles/{id}/permissions/{perm}     - Xóa permission
```

**Impact:** 🔥 CRITICAL - Không thể quản lý phân quyền!

---

### 🔥 2. PromotionController - **CRITICAL MISSING**

```
GET    /promotions                         - Tất cả promotions
GET    /promotions/{id}                    - Chi tiết promotion
GET    /promotions/code/{code}             - Tìm theo code
GET    /promotions/active                  - Promotions đang active
POST   /promotions                         - Tạo promotion (Staff/Admin)
PUT    /promotions/{id}                    - Cập nhật promotion
DELETE /promotions/{id}                    - Xóa promotion (Admin)
POST   /promotions/validate                - Validate code hợp lệ
GET    /promotions/{id}/usage              - Thống kê usage
GET    /promotions/{id}/orders             - Orders đã dùng promo
PATCH  /promotions/{id}/activate           - Kích hoạt promo
PATCH  /promotions/{id}/deactivate         - Vô hiệu promo
```

**Impact:** 🔥 CRITICAL - Core business feature!

---

### 🔥 3. ShipperController - **HIGH PRIORITY MISSING**

```
GET    /shippers                           - Tất cả shippers
GET    /shippers/{id}                      - Chi tiết shipper
GET    /shippers/active                    - Shippers đang active
POST   /shippers                           - Tạo shipper (Admin)
PUT    /shippers/{id}                      - Cập nhật shipper
DELETE /shippers/{id}                      - Xóa shipper (Admin)
GET    /shippers/{id}/shipments            - Lịch sử shipments
GET    /shippers/{id}/statistics           - Thống kê shipper
PATCH  /shippers/{id}/activate             - Kích hoạt shipper
PATCH  /shippers/{id}/deactivate           - Vô hiệu shipper
GET    /shippers/available                 - Shippers rảnh
```

**Impact:** 🔥 HIGH - Không quản lý được đội giao hàng!

---

### 🟡 4. AuditLogController - **MEDIUM PRIORITY**

```
GET    /audit-logs                         - Tất cả logs (Admin)
GET    /audit-logs/{id}                    - Chi tiết log
GET    /audit-logs/user/{userId}           - Logs của user
GET    /audit-logs/entity/{type}/{id}      - Logs của entity
GET    /audit-logs/action/{action}         - Logs theo action
GET    /audit-logs/search                  - Tìm kiếm logs
GET    /audit-logs/export                  - Export logs
```

**Impact:** 🟡 MEDIUM - Giám sát và audit

---

### 🟢 5. AttachmentController - **LOW PRIORITY**

```
GET    /attachments/{id}                   - Xem/download
POST   /attachments/order/{orderId}        - Upload cho order
POST   /attachments/shipment/{shipmentId}  - Upload cho shipment
DELETE /attachments/{id}                   - Xóa attachment
GET    /attachments/order/{orderId}        - Attachments của order
GET    /attachments/shipment/{shipmentId}  - Attachments của shipment
```

**Impact:** 🟢 LOW - Có thể merge vào Order/Shipment

---

## 📈 Statistics

### Controllers Status:
```
✅ Complete:         2 (NotificationController, ServiceController)
✅ Good:             4 (PaymentController, ShipmentController, ReviewController, UserController)
⚠️  Missing Items:   2 (OrderController, BranchController)
❌ Missing:          6 (Role, Promotion, Shipper, OrderItem, AuditLog, Attachment)

Total: 8/14 có, 6/14 thiếu
```

### Endpoints Status:
```
✅ Implemented:      ~70 endpoints
❌ Missing Critical: ~35 endpoints
❌ Nice to Have:     ~25 endpoints

Total Missing: ~60 endpoints
```

---

## 🎯 Priority Action Plan

### Phase 1: CRITICAL (Must Have) - 1-2 weeks
1. **PromotionController** (12 endpoints)
   - Core business: Mã giảm giá
   - Blocking: Không thể chạy promotion campaigns
   
2. **RoleController** (9 endpoints)
   - Core system: Quản lý phân quyền
   - Blocking: Không thể quản lý roles động
   
3. **ShipperController** (11 endpoints)
   - Core operations: Quản lý giao hàng
   - Blocking: Không thể assign/track shippers

4. **OrderController improvements** (8 endpoints)
   - OrderItem management
   - Promotion application
   - Total calculation

**Estimated:** 40 endpoints - 1-2 weeks

---

### Phase 2: Important (Should Have) - 1 week
5. **AuditLogController** (7 endpoints)
   - System monitoring
   - Compliance & security
   
6. **PaymentController improvements** (5 endpoints)
   - Refund functionality
   - Statistics
   - Webhook support

7. **ShipmentController improvements** (5 endpoints)
   - Attachment support
   - Better status management

**Estimated:** 17 endpoints - 1 week

---

### Phase 3: Nice to Have (Optional) - 1 week
8. **AttachmentController** (6 endpoints)
   - File management
   - Or merge into existing controllers

9. **Enhancement endpoints** (25 endpoints)
   - Search/Filter improvements
   - Statistics endpoints
   - Convenience methods

**Estimated:** 31 endpoints - 1 week

---

## 💡 Recommendations

### Immediate Actions:
1. ✅ **Create PromotionController** - Mã giảm giá là critical!
2. ✅ **Create RoleController** - Phân quyền cần quản lý động
3. ✅ **Create ShipperController** - Quản lý đội giao hàng
4. ⚠️  **Enhance OrderController** - Thêm OrderItem management

### Consider:
- **Merge AttachmentController** vào Order/Shipment controllers
- **Add statistics endpoints** cho reports
- **Add webhook support** cho payment gateway
- **Add search/filter** improvements

### Long-term:
- **API versioning** (/api/v1, /api/v2)
- **GraphQL endpoint** cho flexible queries
- **WebSocket** cho real-time notifications
- **Rate limiting** cho API protection

---

## ✅ Summary

### Missing Controllers: 6
- 🔥 RoleController (CRITICAL)
- 🔥 PromotionController (CRITICAL)
- 🔥 ShipperController (HIGH)
- 🟡 AuditLogController (MEDIUM)
- 🟢 AttachmentController (LOW - có thể merge)
- ⚠️  OrderItemController (có thể merge vào OrderController)

### Missing Endpoints in Existing Controllers: ~30
- OrderController: 8 endpoints
- UserController: 8 endpoints  
- PaymentController: 5 endpoints
- ShipmentController: 5 endpoints
- Others: 4 endpoints

### Total Work Remaining:
- **Critical:** 40 endpoints (1-2 weeks)
- **Important:** 17 endpoints (1 week)
- **Optional:** 31 endpoints (1 week)

**Total: ~88 endpoints to complete the API**

---

> 🎯 **Recommendation:** Focus on Phase 1 (Critical) first. Promotions, Roles, và Shippers là blockers cho business operations!
