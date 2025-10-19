# 🔐 Authorization Summary - Washify BE

## Roles trong hệ thống
- **ADMIN**: Quản trị viên - Full quyền
- **STAFF**: Nhân viên - Quản lý operations
- **CUSTOMER**: Khách hàng - Sử dụng dịch vụ
- **SHIPPER**: Người giao hàng - Quản lý shipments

---

## 📋 Phân quyền chi tiết theo Controller

### 1. **UserController** - Quản lý Users
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/users/register` | POST | Public | Đăng ký tài khoản mới |
| `/users/{id}` | GET | ADMIN, STAFF hoặc chính user | Xem thông tin user |
| `/users/email/{email}` | GET | ADMIN, STAFF | Tìm user theo email |
| `/users` | GET | ADMIN, STAFF | Xem danh sách tất cả users |
| `/users/{id}` | PUT | ADMIN hoặc chính user | Cập nhật thông tin |
| `/users/{id}` | DELETE | ADMIN | Xóa user |
| `/users/{id}/roles/{roleName}` | POST | ADMIN | Gán role cho user |

### 2. **BranchController** - Quản lý Chi nhánh
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/branches` | POST | ADMIN | Tạo chi nhánh mới |
| `/branches/{id}` | GET | Public (permitAll) | Xem chi nhánh |
| `/branches` | GET | Public (permitAll) | Xem danh sách chi nhánh |
| `/branches/{id}` | PUT | ADMIN | Cập nhật chi nhánh |
| `/branches/{id}` | DELETE | ADMIN | Xóa chi nhánh |

### 3. **OrderController** - Quản lý Đơn hàng
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/orders` | POST | CUSTOMER, STAFF, ADMIN | Tạo đơn hàng |
| `/orders/{id}` | GET | ADMIN, STAFF, CUSTOMER | Xem đơn hàng |
| `/orders/user/{userId}` | GET | ADMIN, STAFF hoặc chính user | Xem đơn của user |
| `/orders/status/{status}` | GET | ADMIN, STAFF | Lọc đơn theo trạng thái |
| `/orders/{id}/status` | PATCH | STAFF, ADMIN | Cập nhật trạng thái |
| `/orders/{id}/cancel` | PATCH | CUSTOMER, STAFF, ADMIN | Hủy đơn hàng |

### 4. **ServiceController** - Quản lý Dịch vụ
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/services` | POST | ADMIN, STAFF | Tạo dịch vụ mới |
| `/services/{id}` | GET | Public (permitAll) | Xem dịch vụ |
| `/services` | GET | Public (permitAll) | Xem danh sách dịch vụ |
| `/services/active` | GET | Public (permitAll) | Xem dịch vụ đang hoạt động |
| `/services/search?name=` | GET | Public (permitAll) | Tìm kiếm dịch vụ |
| `/services/{id}` | PUT | ADMIN, STAFF | Cập nhật dịch vụ |
| `/services/{id}` | DELETE | ADMIN, STAFF | Xóa dịch vụ |

### 5. **PaymentController** - Quản lý Thanh toán
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/payments` | POST | CUSTOMER, STAFF, ADMIN | Tạo thanh toán |
| `/payments/{id}` | GET | ADMIN, STAFF, CUSTOMER | Xem thanh toán |
| `/payments/order/{orderId}` | GET | ADMIN, STAFF, CUSTOMER | Xem thanh toán của đơn |
| `/payments/status/{status}` | GET | ADMIN, STAFF | Lọc theo trạng thái |
| `/payments/{id}/status` | PATCH | STAFF, ADMIN | Cập nhật trạng thái |
| `/payments/{id}/confirm` | PATCH | STAFF, ADMIN | Xác nhận thanh toán |
| `/payments/{id}/fail` | PATCH | STAFF, ADMIN | Đánh dấu thất bại |

### 6. **ShipmentController** - Quản lý Vận chuyển
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/shipments` | POST | STAFF, ADMIN | Tạo shipment mới |
| `/shipments/{id}` | GET | ADMIN, STAFF, SHIPPER | Xem shipment |
| `/shipments/order/{orderId}` | GET | ADMIN, STAFF, SHIPPER | Xem shipment của đơn |
| `/shipments/shipper/{shipperId}` | GET | ADMIN, STAFF hoặc chính shipper | Xem shipments của shipper |
| `/shipments/status/{status}` | GET | ADMIN, STAFF | Lọc theo trạng thái |
| `/shipments/{id}/status` | PATCH | SHIPPER, STAFF, ADMIN | Cập nhật trạng thái |
| `/shipments/{id}/assign-shipper` | PATCH | STAFF, ADMIN | Gán shipper |

### 7. **ReviewController** - Quản lý Đánh giá
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/reviews` | POST | CUSTOMER | Tạo đánh giá |
| `/reviews/{id}` | GET | Public (permitAll) | Xem đánh giá |
| `/reviews/order/{orderId}` | GET | Public (permitAll) | Xem đánh giá của đơn |
| `/reviews/user/{userId}` | GET | Public (permitAll) | Xem đánh giá của user |
| `/reviews` | GET | Public (permitAll) | Xem tất cả đánh giá |
| `/reviews/rating/{rating}` | GET | Public (permitAll) | Lọc theo rating |
| `/reviews/average-rating` | GET | Public (permitAll) | Xem rating trung bình |
| `/reviews/{id}` | DELETE | ADMIN | Xóa đánh giá |

### 8. **NotificationController** - Quản lý Thông báo
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/notifications` | POST | ADMIN, STAFF | Tạo thông báo |
| `/notifications/bulk` | POST | ADMIN | Gửi thông báo hàng loạt |
| `/notifications/my` | GET | Authenticated | Xem thông báo của mình |
| `/notifications/unread` | GET | Authenticated | Xem thông báo chưa đọc |
| `/notifications/{id}/read` | PATCH | Authenticated | Đánh dấu đã đọc |
| `/notifications/read-all` | PATCH | Authenticated | Đánh dấu tất cả đã đọc |
| `/notifications/{id}` | DELETE | Authenticated | Xóa thông báo |
| `/notifications/unread/count` | GET | Authenticated | Đếm thông báo chưa đọc |

### 9. **AuthController** - Authentication
| Endpoint | Method | Roles | Logic |
|----------|--------|-------|-------|
| `/auth/login` | POST | Public | Đăng nhập |
| `/auth/register` | POST | Public | Đăng ký |
| `/auth/validate` | GET | Authenticated | Validate token |

---

## 🔧 SecurityConfig Settings

```java
// Public endpoints (không cần authentication)
- /api/auth/**
- /api/public/**
- /swagger-ui/**
- /v3/api-docs/**
- /actuator/**

// Admin only
- /api/admin/**

// Staff and Admin
- /api/staff/**

// Tất cả endpoints khác: permitAll() (cho testing)
// Nếu muốn bật security: đổi thành authenticated()
```

---

## 📝 Cách sử dụng Authorization

### 1. **Không cần token** (Public endpoints):
```bash
GET /api/branches
GET /api/services
GET /api/reviews
```

### 2. **Cần JWT Token** (Protected endpoints):
```bash
# 1. Login để lấy token
POST /api/auth/login
{
  "username": "admin",
  "password": "washify123"
}

# 2. Thêm token vào header
Authorization: Bearer <your-jwt-token>

# 3. Gọi API
GET /api/users
```

### 3. **Test với các roles**:
```bash
# ADMIN (full quyền)
username: admin
password: washify123

# STAFF 
username: staff1
password: washify123

# CUSTOMER
username: customer1
password: washify123
```

---

## ⚠️ Lưu ý quan trọng

1. **@PreAuthorize** annotations đã được thêm vào **TẤT CẢ** controllers
2. **Hiện tại** SecurityConfig set `permitAll()` cho testing nhanh
3. **Production**: Cần đổi về `.anyRequest().authenticated()` trong SecurityConfig
4. **SpEL expressions** như `#userId == authentication.principal.id` để check ownership
5. **Method security** đã enable qua `@EnableMethodSecurity(prePostEnabled = true)`

---

## 🚀 Next Steps

- [ ] Implement business logic kiểm tra ownership trong Services
- [ ] Add audit logging cho các operations quan trọng
- [ ] Implement rate limiting cho public endpoints
- [ ] Add IP whitelist cho admin operations
- [ ] Implement 2FA cho ADMIN role
