# Review Phân Quyền Toàn Bộ Controllers

## 🎯 5 Roles trong hệ thống
1. **ADMIN** - Quản trị viên hệ thống (toàn quyền)
2. **MANAGER** - Quản lý chi nhánh (quản lý chi nhánh và nhân viên)
3. **STAFF** - Nhân viên (xử lý đơn hàng và dịch vụ)
4. **SHIPPER** - Shipper (giao nhận đồ giặt)
5. **CUSTOMER** - Khách hàng (sử dụng dịch vụ)

---

## 📋 Chi Tiết Phân Quyền Từng Controller

### 1. ✅ BranchController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| `/api/branches` | POST | ADMIN | Tạo chi nhánh |
| `/api/branches` | GET | Public | Xem danh sách |
| `/api/branches/{id}` | GET | Public | Xem chi tiết |
| `/api/branches/{id}` | PUT | ADMIN, MANAGER (own) | MANAGER chỉ cập nhật chi nhánh của họ |
| `/api/branches/{id}` | DELETE | ADMIN | Xóa chi nhánh |
| `/api/branches/statistics` | GET | ADMIN, STAFF, MANAGER | Thống kê tất cả chi nhánh |
| `/api/branches/{id}/statistics` | GET | ADMIN, STAFF, MANAGER | Thống kê chi tiết |
| `/api/branches/search` | GET | Public | Tìm kiếm |
| `/api/branches/nearby` | GET | Public | Chi nhánh gần |

**Đánh giá**: ✅ Hợp lý. MANAGER có quyền cập nhật chi nhánh của họ.

---

### 2. ⚠️ OrderController - CẦN XEM XÉT
| Endpoint | Method | Quyền | Vấn đề? |
|----------|--------|-------|---------|
| `/api/orders` | POST | CUSTOMER, STAFF, ADMIN | ✅ OK |
| `/api/orders` | GET | ADMIN, STAFF, CUSTOMER | ✅ OK |
| `/api/orders/{id}` | GET | ADMIN, STAFF, CUSTOMER | ✅ OK |
| `/api/orders/user/{userId}` | GET | ADMIN, STAFF hoặc owner | ✅ OK |
| `/api/orders/status/{status}` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/orders/{id}` | PUT | STAFF, ADMIN | ⚠️ **MANAGER nên có** |
| `/api/orders/{id}` | DELETE | CUSTOMER, STAFF, ADMIN | ✅ OK |
| `/api/orders/{id}/cancel` | PATCH | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/orders/{id}/status` | PATCH | CUSTOMER, STAFF, ADMIN | ✅ OK |
| `/api/orders/statistics` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/orders/statistics/revenue` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/orders/statistics/top-customers` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/orders/search` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/orders/user/{userId}/status/{status}` | GET | ADMIN, STAFF hoặc owner | ⚠️ **MANAGER nên có** |
| `/api/orders/branch/{branchId}` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/orders/date-range` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/orders/batch/status` | PATCH | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/orders/batch` | DELETE | ADMIN, STAFF | ⚠️ **MANAGER nên có?** |

**Vấn đề**:
- `/api/orders/status/{status}` - MANAGER nên xem được orders theo status
- `/api/orders/{id}` PUT - MANAGER nên cập nhật được orders
- `/api/orders/{id}/cancel` - MANAGER nên hủy được orders
- `/api/orders/user/{userId}/status/{status}` - MANAGER nên xem được

---

### 3. ✅ UserController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| `/api/users/{id}` | GET | ADMIN, STAFF, MANAGER hoặc owner | ✅ OK |
| `/api/users/email/{email}` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/users` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/users/{id}` | PUT | ADMIN hoặc owner | ✅ OK |
| `/api/users/{id}` | DELETE | ADMIN | ✅ OK - Soft delete |
| `/api/users/{id}/roles/{roleName}` | POST | ADMIN | ✅ OK |
| `/api/users/{id}/change-password` | POST | ADMIN hoặc owner | ✅ OK |
| `/api/users/search` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/users/role/{roleId}` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/users/active` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/users/batch/activate` | PATCH | ADMIN | ✅ OK |
| `/api/users/batch/deactivate` | PATCH | ADMIN | ✅ OK |

**Đánh giá**: ✅ Hợp lý. MANAGER có quyền xem danh sách nhân viên.

---

### 4. ✅ ShipmentController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| `/api/shipments` | POST | STAFF, ADMIN | ✅ OK |
| `/api/shipments` | GET | ADMIN, STAFF, SHIPPER | ✅ OK |
| `/api/shipments/{id}` | GET | ADMIN, STAFF, SHIPPER | ✅ OK |
| `/api/shipments/shipper/{shipperId}` | GET | ADMIN, STAFF hoặc own | ✅ OK |
| `/api/shipments/status/{status}` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/shipments/{id}/status` | PATCH | SHIPPER, STAFF, ADMIN, MANAGER | ✅ OK |
| `/api/shipments/{id}/assign-shipper` | PATCH | STAFF, ADMIN, MANAGER | ✅ OK |
| `/api/shipments/{id}/attachments` | POST | SHIPPER, STAFF, ADMIN | ⚠️ **MANAGER nên có?** |
| `/api/shipments/{id}/proof-of-delivery` | GET | CUSTOMER, SHIPPER, STAFF, ADMIN | ✅ OK |
| `/api/shipments/{id}/attachments` | DELETE | SHIPPER, STAFF, ADMIN | ⚠️ **MANAGER nên có?** |
| `/api/shipments/statistics` | GET | ADMIN, STAFF, MANAGER | ✅ OK |

**Vấn đề nhỏ**: MANAGER có nên quản lý attachments không?

---

### 5. ✅ PaymentController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| `/api/payments` | POST | CUSTOMER, STAFF, ADMIN | ✅ OK |
| `/api/payments` | GET | ADMIN, STAFF, CUSTOMER | ✅ OK |
| `/api/payments/{id}` | GET | ADMIN, STAFF, CUSTOMER | ✅ OK |
| `/api/payments/order/{orderId}` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/payments/{id}` | PUT | STAFF, ADMIN | ⚠️ **MANAGER nên có?** |
| `/api/payments/{id}/status` | PATCH | STAFF, ADMIN | ⚠️ **MANAGER nên có?** |
| `/api/payments/{id}/refund` | POST | STAFF, ADMIN | ⚠️ **MANAGER nên có?** |
| `/api/payments/{id}` | DELETE | ADMIN | ✅ OK |
| `/api/payments/statistics` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/payments/method/{method}` | GET | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/payments/date-range` | GET | ADMIN, STAFF, MANAGER | ✅ OK |

**Vấn đề**: 
- `/api/payments/order/{orderId}` - MANAGER nên xem được
- Cập nhật/refund payment - MANAGER có nên có quyền không?

---

### 6. ✅ ServiceController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| `/api/services` | POST | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/services` | GET | Public | ✅ OK |
| `/api/services/{id}` | GET | Public | ✅ OK |
| `/api/services/active` | GET | Public | ✅ OK |
| `/api/services/search` | GET | Public | ✅ OK |
| `/api/services/{id}` | PUT | ADMIN, STAFF, MANAGER | ✅ OK |
| `/api/services/{id}` | DELETE | ADMIN, STAFF, MANAGER | ✅ OK |

**Đánh giá**: ✅ Hợp lý. MANAGER có quyền quản lý dịch vụ.

---

### 7. ⚠️ NotificationController - CẦN THÊM MANAGER
| Endpoint | Method | Quyền | Vấn đề |
|----------|--------|-------|--------|
| `/api/notifications` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/notifications` | POST | ADMIN | ✅ OK |
| `/api/notifications/user/{userId}` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/notifications/{id}/read` | PUT | ADMIN, STAFF | ⚠️ **MANAGER nên có** |

**Vấn đề**: MANAGER nên xem được notifications.

---

### 8. ⚠️ PromotionController - CẦN THÊM MANAGER
| Endpoint | Method | Quyền | Vấn đề |
|----------|--------|-------|--------|
| `/api/promotions` | POST | STAFF, ADMIN | ⚠️ **MANAGER nên có** |
| `/api/promotions` | GET | Public | ✅ OK |
| `/api/promotions/{id}` | GET | Public | ✅ OK |
| `/api/promotions/active` | GET | Public | ✅ OK |
| `/api/promotions/code/{code}` | GET | STAFF, ADMIN | ⚠️ **MANAGER nên có** |
| `/api/promotions/{id}` | PUT | STAFF, ADMIN | ⚠️ **MANAGER nên có** |
| `/api/promotions/{id}/activate` | PATCH | STAFF, ADMIN | ⚠️ **MANAGER nên có** |
| `/api/promotions/{id}/deactivate` | PATCH | STAFF, ADMIN | ⚠️ **MANAGER nên có** |
| `/api/promotions/{id}` | DELETE | ADMIN | ✅ OK |

**Vấn đề**: MANAGER nên quản lý promotions trong chi nhánh.

---

### 9. ✅ ReviewController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| `/api/reviews` | POST | CUSTOMER | ✅ OK |
| `/api/reviews` | GET | Public | ✅ OK |
| `/api/reviews/{id}` | GET | Public | ✅ OK |
| `/api/reviews/order/{orderId}` | GET | Public | ✅ OK |
| `/api/reviews/service/{serviceId}` | GET | Public | ✅ OK |
| `/api/reviews/user/{userId}` | GET | Public | ✅ OK |
| `/api/reviews/rating/{rating}` | GET | Public | ✅ OK |
| `/api/reviews/{id}` | DELETE | ADMIN | ✅ OK |

**Đánh giá**: ✅ Hợp lý.

---

### 10. ✅ RoleController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| `/api/roles` | POST | ADMIN | ✅ OK |
| `/api/roles` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có?** |
| `/api/roles/{id}` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có?** |
| `/api/roles/name/{name}` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có?** |
| `/api/roles/{id}` | PUT | ADMIN | ✅ OK |
| `/api/roles/{id}` | DELETE | ADMIN | ✅ OK |
| `/api/roles/{id}/users` | GET | ADMIN | ✅ OK |

**Vấn đề nhỏ**: MANAGER có nên xem danh sách roles không (để assign cho nhân viên)?

---

### 11. ⚠️ ShipperController - CẦN THÊM MANAGER
| Endpoint | Method | Quyền | Vấn đề |
|----------|--------|-------|--------|
| `/api/shippers` | POST | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/shippers` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/shippers/{id}` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/shippers/available` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/shippers/{id}` | PUT | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/shippers/{id}/activate` | PATCH | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/shippers/{id}/deactivate` | PATCH | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/shippers/stats` | GET | ADMIN, STAFF | ⚠️ **MANAGER nên có** |
| `/api/shippers/{id}` | DELETE | ADMIN | ✅ OK |
| `/api/shippers/{id}/restore` | POST | ADMIN | ✅ OK |
| `/api/shippers/{id}/permanent` | DELETE | ADMIN | ✅ OK |

**Vấn đề**: MANAGER nên quản lý shippers trong chi nhánh của họ.

---

### 12. ✅ AuditLogController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| Tất cả endpoints | * | ADMIN | ✅ OK - Chỉ ADMIN xem audit logs |

**Đánh giá**: ✅ Hợp lý. Audit logs chỉ cho ADMIN.

---

### 13. ✅ AttachmentController - HỢP LÝ
| Endpoint | Method | Quyền | Ghi chú |
|----------|--------|-------|---------|
| Tất cả endpoints | * | ADMIN, STAFF, CUSTOMER | ✅ OK |
| `/api/attachments/{id}/permanent` | DELETE | ADMIN, STAFF | ✅ OK |

**Đánh giá**: ✅ Hợp lý.

---

## 📊 Tổng Kết & Đề Xuất

### ✅ Controllers HỢP LÝ (Không cần thay đổi)
1. ✅ **BranchController** - MANAGER có quyền cập nhật chi nhánh của họ
2. ✅ **UserController** - MANAGER xem được danh sách nhân viên
3. ✅ **ServiceController** - MANAGER quản lý dịch vụ
4. ✅ **ShipmentController** - MANAGER quản lý vận chuyển
5. ✅ **PaymentController** - MANAGER xem thống kê (một số endpoint cần xem xét)
6. ✅ **ReviewController** - Public, hợp lý
7. ✅ **AuditLogController** - Chỉ ADMIN
8. ✅ **AttachmentController** - Hợp lý

### ⚠️ Controllers CẦN BỔ SUNG MANAGER
1. **NotificationController** ⚠️ HIGH PRIORITY
   - MANAGER nên xem notifications
   - Thêm MANAGER vào: GET `/api/notifications`, GET `/api/notifications/user/{userId}`

2. **PromotionController** ⚠️ MEDIUM PRIORITY
   - MANAGER nên quản lý promotions trong chi nhánh
   - Thêm MANAGER vào các endpoint quản lý promotion

3. **ShipperController** ⚠️ HIGH PRIORITY
   - MANAGER nên quản lý shippers trong chi nhánh
   - Thêm MANAGER vào hầu hết endpoints

4. **OrderController** ⚠️ MEDIUM PRIORITY
   - Một số endpoints còn thiếu MANAGER:
     - GET `/api/orders/status/{status}`
     - PUT `/api/orders/{id}`
     - PATCH `/api/orders/{id}/cancel`
     - GET `/api/orders/user/{userId}/status/{status}`

5. **RoleController** ⚠️ LOW PRIORITY
   - MANAGER có nên xem danh sách roles không?
   - Nếu MANAGER cần assign roles cho nhân viên → thêm quyền xem

---

## 🎯 Khuyến Nghị Ưu Tiên

### 🔴 HIGH PRIORITY - Cần sửa ngay
1. **NotificationController** - MANAGER phải xem được notifications
2. **ShipperController** - MANAGER phải quản lý shippers trong chi nhánh

### 🟡 MEDIUM PRIORITY - Nên sửa
1. **PromotionController** - MANAGER nên quản lý promotions
2. **OrderController** - Bổ sung MANAGER vào một số endpoints còn thiếu

### 🟢 LOW PRIORITY - Tùy chọn
1. **RoleController** - Tùy business logic có cần MANAGER xem roles không
2. **PaymentController** - Một số endpoint quản lý payment (PUT, refund) - cân nhắc

---

## 💡 Nguyên Tắc Phân Quyền Được Áp Dụng

### ✅ Đúng
1. **ADMIN** - Toàn quyền tất cả endpoints
2. **MANAGER** - Quản lý chi nhánh: orders, users, services, shipments, statistics
3. **STAFF** - Xử lý vận hành: orders, payments, shipments
4. **SHIPPER** - Chỉ liên quan đến giao hàng
5. **CUSTOMER** - Tạo orders, xem thông tin cá nhân, reviews

### ⚠️ Cần cải thiện
- Một số endpoints liên quan đến quản lý chi nhánh chưa có MANAGER
- Cần nhất quán hơn trong việc phân quyền MANAGER

---

Bạn muốn tôi sửa luôn các vấn đề ưu tiên cao không?
