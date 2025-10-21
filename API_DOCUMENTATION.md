# 📚 Washify API Documentation for Frontend Development

**Base URL**: `http://localhost:8080/api`

**Authentication**: JWT Bearer Token (trừ các public endpoints)

**Header**: `Authorization: Bearer <token>`

---

## 📋 Table of Contents

1. [Customer Web Application](#1-customer-web-application) ⭐ **Priority 1**
2. [Admin & Manager Dashboard](#2-admin--manager-dashboard) ⭐ **Priority 2**
3. [Staff Portal](#3-staff-portal) ⭐ **Priority 3**
4. [Shipper Mobile App](#4-shipper-mobile-app) ⭐ **Priority 4**

---

# 1. 🛒 Customer Web Application

## Quyền: `CUSTOMER` hoặc Public

### 1.1 Authentication & User Management

#### POST `/api/auth/register`
**Đăng ký tài khoản mới**
- **Public**: ✅ Không cần authentication
- **Request Body**:
```json
{
  "email": "customer@example.com",
  "password": "Password123",
  "fullName": "Nguyễn Văn A",
  "phone": "0901234567"
}
```
- **Response** (201 Created):
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "data": {
    "id": 1,
    "email": "customer@example.com",
    "fullName": "Nguyễn Văn A",
    "phone": "0901234567",
    "roles": ["CUSTOMER"],
    "isActive": true,
    "createdAt": "2025-10-21T10:30:00"
  },
  "timestamp": "2025-10-21T10:30:00"
}
```

#### POST `/api/auth/login`
**Đăng nhập (hỗ trợ Username/Email/Phone)**
- **Public**: ✅ Không cần authentication
- **Request Body**:
```json
{
  "username": "customer@example.com",
  "password": "Password123"
}
```
- **Note**: 
  - Field `username` có thể là:
    - **Username**: `admin`, `manager1`
    - **Email**: `customer@example.com`
    - **Phone**: `0901234567`, `+84901234567`
  - Hệ thống tự động tìm kiếm theo thứ tự: Username → Email → Phone

- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "email": "customer@example.com",
      "fullName": "Nguyễn Văn A",
      "roles": ["CUSTOMER"]
    },
    "requirePasswordChange": false
  },
  "timestamp": "2025-10-21T10:30:00"
}
```

- **Response khi Guest User đăng nhập lần đầu**:
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "user": {
      "id": 10,
      "username": "guest_0912345678",
      "phone": "0912345678",
      "fullName": "Guest User",
      "roles": ["GUEST"]
    },
    "requirePasswordChange": true
  },
  "timestamp": "2025-10-21T10:30:00"
}
```

#### POST `/api/auth/first-time-password-change`
**Đổi mật khẩu lần đầu (cho Guest User)**
- **Auth**: ✅ GUEST (User có `requirePasswordChange = true`)
- **Request Body**:
```json
{
  "newPassword": "MyNewPassword123",
  "confirmPassword": "MyNewPassword123"
}
```
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công",
  "data": "Mật khẩu đã được cập nhật. Vui lòng đăng nhập lại với mật khẩu mới.",
  "timestamp": "2025-10-21T10:30:00"
}
```
- **Note**: 
  - Endpoint này KHÔNG yêu cầu mật khẩu cũ
  - Chỉ áp dụng cho user có `requirePasswordChange = true`
  - Sau khi đổi mật khẩu, `requirePasswordChange` sẽ tự động set về `false`
  - User cần đăng nhập lại với mật khẩu mới

#### GET `/api/users/{id}`
**Lấy thông tin cá nhân**
- **Auth**: ✅ CUSTOMER (chỉ xem thông tin của mình)
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Lấy thông tin user thành công",
  "data": {
    "id": 1,
    "email": "customer@example.com",
    "fullName": "Nguyễn Văn A",
    "phone": "0901234567",
    "address": "123 Nguyễn Huệ, Q1, TP.HCM",
    "roles": ["CUSTOMER"],
    "isActive": true,
    "createdAt": "2025-10-21T10:30:00"
  }
}
```

#### PUT `/api/users/{id}`
**Cập nhật thông tin cá nhân**
- **Auth**: ✅ CUSTOMER (chỉ cập nhật thông tin của mình)
- **Request Body**:
```json
{
  "fullName": "Nguyễn Văn A Updated",
  "phone": "0901234568",
  "address": "456 Lê Lợi, Q1, TP.HCM"
}
```

#### POST `/api/users/{id}/change-password`
**Đổi mật khẩu**
- **Auth**: ✅ CUSTOMER (chỉ đổi mật khẩu của mình)
- **Request Body**:
```json
{
  "oldPassword": "Password123",
  "newPassword": "NewPassword456"
}
```

---

### 1.2 Services (Dịch vụ giặt ủi)

#### GET `/api/services`
**Lấy danh sách tất cả dịch vụ**
- **Public**: ✅ Không cần authentication
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Lấy danh sách dịch vụ thành công",
  "data": [
    {
      "id": 1,
      "name": "Giặt khô",
      "description": "Giặt khô quần áo cao cấp",
      "price": 50000.00,
      "unit": "kg",
      "category": "DRY_CLEAN",
      "isActive": true,
      "estimatedTime": 24
    },
    {
      "id": 2,
      "name": "Giặt ủi thường",
      "description": "Giặt và ủi quần áo thường ngày",
      "price": 30000.00,
      "unit": "kg",
      "category": "WASH_IRON",
      "isActive": true,
      "estimatedTime": 12
    }
  ]
}
```

#### GET `/api/services/{id}`
**Xem chi tiết dịch vụ**
- **Public**: ✅ Không cần authentication

#### GET `/api/services/active`
**Lấy danh sách dịch vụ đang hoạt động**
- **Public**: ✅ Không cần authentication
- **Response**: Tương tự GET `/api/services` nhưng chỉ có `isActive: true`

#### GET `/api/services/category/{category}`
**Lọc dịch vụ theo danh mục**
- **Public**: ✅ Không cần authentication
- **Params**: `category` - Ví dụ: `DRY_CLEAN`, `WASH_IRON`, `IRON_ONLY`, `WASH_ONLY`

---

### 1.3 Branches (Chi nhánh)

#### GET `/api/branches`
**Lấy danh sách chi nhánh**
- **Public**: ✅ Không cần authentication
- **Response** (200 OK):
. . y```json
{
  "success": true,
  "message": "Lấy danh sách chi nhánh thành công",
  "data": [
    {
      "id": 1,
      "name": "Chi nhánh Quận 1",
      "address": "123 Nguyễn Huệ, Q1, TP.HCM",
      "phone": "0281234567",
      "email": "q1@washify.vn",
      "isActive": true,
      "openingHours": "8:00 - 22:00",
      "latitude": 10.7769,
      "longitude": 106.7009
    }
  ]
}
```

#### GET `/api/branches/active`
**Lấy chi nhánh đang hoạt động**
- **Public**: ✅ Không cần authentication

#### GET `/api/branches/{id}`
**Chi tiết chi nhánh**
- **Public**: ✅ Không cần authentication

---

### 1.4 Orders (Đặt đơn hàng)

#### POST `/api/orders`
**Tạo đơn hàng mới**
- **Auth**: ✅ CUSTOMER
- **Request Body**:
```json
{
  "branchId": 1,
  "items": [
    {
      "serviceId": 1,
      "quantity": 2.5,
      "note": "Quần áo trẻ em"
    },
    {
      "serviceId": 2,
      "quantity": 5.0
    }
  ],
  "pickupAddress": "789 Trần Hưng Đạo, Q5, TP.HCM",
  "pickupTime": "2025-10-22T14:00:00",
  "deliveryAddress": "789 Trần Hưng Đạo, Q5, TP.HCM",
  "note": "Gọi trước 15 phút",
  "promotionCode": "SUMMER2025"
}
```
- **Response** (201 Created):
```json
{
  "success": true,
  "message": "Tạo đơn hàng thành công",
  "data": {
    "id": 1,
    "orderCode": "WF202510210001",
    "userId": 1,
    "userName": "Nguyễn Văn A",
    "branchId": 1,
    "branchName": "Chi nhánh Quận 1",
    "status": "PENDING",
    "totalAmount": 200000.00,
    "discountAmount": 20000.00,
    "finalAmount": 180000.00,
    "pickupAddress": "789 Trần Hưng Đạo, Q5, TP.HCM",
    "pickupTime": "2025-10-22T14:00:00",
    "deliveryAddress": "789 Trần Hưng Đạo, Q5, TP.HCM",
    "items": [
      {
        "serviceId": 1,
        "serviceName": "Giặt khô",
        "quantity": 2.5,
        "price": 50000.00,
        "subtotal": 125000.00
      }
    ],
    "createdAt": "2025-10-21T10:30:00"
  }
}
```

#### GET `/api/orders/{id}`
**Xem chi tiết đơn hàng**
- **Auth**: ✅ CUSTOMER (chỉ xem đơn của mình)
- **Response**: Tương tự response POST `/api/orders`

#### GET `/api/orders/user/{userId}`
**Lấy tất cả đơn hàng của customer**
- **Auth**: ✅ CUSTOMER (chỉ xem đơn của mình)
- **Response**: Array of orders

#### GET `/api/orders/user/{userId}/status/{status}`
**Lọc đơn hàng theo trạng thái**
- **Auth**: ✅ CUSTOMER (chỉ xem đơn của mình)
- **Params**: 
  - `status`: `PENDING`, `CONFIRMED`, `PICKED_UP`, `IN_PROGRESS`, `READY`, `DELIVERING`, `COMPLETED`, `CANCELLED`

#### PATCH `/api/orders/{id}/cancel`
**Hủy đơn hàng**
- **Auth**: ✅ CUSTOMER (chỉ hủy đơn của mình, status = PENDING)
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Hủy đơn hàng thành công",
  "data": {
    "id": 1,
    "status": "CANCELLED",
    "cancelledAt": "2025-10-21T11:00:00"
  }
}
```

#### PATCH `/api/orders/{id}/status`
**Cập nhật trạng thái đơn hàng** (Customer chỉ có thể confirm nhận hàng)
- **Auth**: ✅ CUSTOMER
- **Request**: `?status=COMPLETED`

---

### 1.5 Promotions (Mã khuyến mãi)

#### GET `/api/promotions/active`
**Xem mã giảm giá đang hoạt động**
- **Public**: ✅ Không cần authentication
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Lấy danh sách khuyến mãi active thành công",
  "data": [
    {
      "id": 1,
      "code": "SUMMER2025",
      "description": "Giảm 10% cho đơn hàng mùa hè",
      "discountType": "PERCENTAGE",
      "discountValue": 10.0,
      "minOrderAmount": 100000.00,
      "maxDiscountAmount": 50000.00,
      "startDate": "2025-06-01T00:00:00",
      "endDate": "2025-08-31T23:59:59",
      "usageLimit": 1000,
      "usedCount": 234,
      "isActive": true
    }
  ]
}
```

#### GET `/api/promotions/valid`
**Lấy mã giảm giá hợp lệ (active + trong thời hạn)**
- **Public**: ✅ Không cần authentication

#### GET `/api/promotions/code/{code}`
**Tìm mã giảm giá theo code**
- **Public**: ✅ Không cần authentication
- **Example**: GET `/api/promotions/code/SUMMER2025`

#### POST `/api/promotions/validate`
**Kiểm tra mã giảm giá có hợp lệ không**
- **Public**: ✅ Không cần authentication
- **Params**: 
  - `code`: SUMMER2025
  - `orderAmount`: 200000
- **Request**: `POST /api/promotions/validate?code=SUMMER2025&orderAmount=200000`
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Mã khuyến mãi hợp lệ",
  "data": {
    "valid": true,
    "code": "SUMMER2025",
    "discountAmount": 20000.00,
    "finalAmount": 180000.00,
    "message": "Giảm 10% (tối đa 50,000đ)"
  }
}
```
- **Response nếu không hợp lệ**:
```json
{
  "success": false,
  "message": "Mã khuyến mãi đã hết hạn",
  "data": {
    "valid": false,
    "message": "Mã khuyến mãi đã hết hạn"
  }
}
```

---

### 1.6 Payments (Thanh toán)

#### POST `/api/payments`
**Tạo thanh toán cho đơn hàng**
- **Auth**: ✅ CUSTOMER
- **Request Body**:
```json
{
  "orderId": 1,
  "paymentMethod": "MOMO",
  "amount": 180000.00
}
```
- **Payment Methods**: 
  - `CASH`: Thanh toán tiền mặt (Tại quầy / COD)
  - `MOMO`: Thanh toán qua ví MoMo
- **Response** (201 Created):
```json
{
  "success": true,
  "message": "Tạo thanh toán thành công",
  "data": {
    "id": 1,
    "orderId": 1,
    "orderCode": "WF202510210001",
    "paymentMethod": "MOMO",
    "amount": 180000.00,
    "status": "PENDING",
    "paymentUrl": "https://momo.vn/payment/xxx",
    "qrCode": "data:image/png;base64,xxx",
    "createdAt": "2025-10-21T10:30:00"
  }
}
```

#### GET `/api/payments/{id}`
**Xem chi tiết thanh toán**
- **Auth**: ✅ CUSTOMER (chỉ xem thanh toán của mình)

#### GET `/api/payments/order/{orderId}`
**Lấy thanh toán của đơn hàng**
- **Auth**: ✅ CUSTOMER (chỉ xem thanh toán đơn của mình)

#### POST `/api/payments/webhook`
**Webhook từ payment gateway** (Tự động xử lý)
- **Public**: ✅ Không cần authentication (sử dụng signature verification)
- **Note**: Endpoint này được payment gateway gọi tự động, không cần FE xử lý

---

### 1.7 Reviews (Đánh giá dịch vụ)

#### POST `/api/reviews`
**Tạo đánh giá**
- **Auth**: ✅ CUSTOMER
- **Request Body**:
```json
{
  "orderId": 1,
  "serviceId": 1,
  "rating": 5,
  "comment": "Dịch vụ rất tốt, quần áo sạch sẽ thơm tho",
  "images": ["url1.jpg", "url2.jpg"]
}
```
- **Response** (201 Created):
```json
{
  "success": true,
  "message": "Tạo đánh giá thành công",
  "data": {
    "id": 1,
    "orderId": 1,
    "serviceId": 1,
    "serviceName": "Giặt khô",
    "userId": 1,
    "userName": "Nguyễn Văn A",
    "rating": 5,
    "comment": "Dịch vụ rất tốt, quần áo sạch sẽ thơm tho",
    "images": ["url1.jpg", "url2.jpg"],
    "createdAt": "2025-10-21T10:30:00"
  }
}
```

#### GET `/api/reviews/service/{serviceId}`
**Xem đánh giá của dịch vụ**
- **Public**: ✅ Không cần authentication
- **Response**: Array of reviews

#### GET `/api/reviews/user/{userId}`
**Xem đánh giá của user**
- **Public**: ✅ Không cần authentication

#### PUT `/api/reviews/{id}`
**Cập nhật đánh giá**
- **Auth**: ✅ CUSTOMER (chỉ cập nhật đánh giá của mình)

#### DELETE `/api/reviews/{id}`
**Xóa đánh giá**
- **Auth**: ✅ CUSTOMER (chỉ xóa đánh giá của mình)

---

### 1.8 Notifications (Thông báo)

#### GET `/api/notifications/user/{userId}`
**Lấy thông báo của user**
- **Auth**: ✅ CUSTOMER (chỉ xem thông báo của mình)
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Lấy danh sách thông báo thành công",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "type": "ORDER_UPDATE",
      "title": "Đơn hàng đã được xác nhận",
      "message": "Đơn hàng WF202510210001 đã được xác nhận và đang chờ lấy hàng",
      "isRead": false,
      "orderId": 1,
      "createdAt": "2025-10-21T10:30:00"
    }
  ]
}
```

#### PATCH `/api/notifications/{id}/read`
**Đánh dấu đã đọc**
- **Auth**: ✅ CUSTOMER (chỉ đánh dấu thông báo của mình)

---

---

# 2. 👔 Admin & Manager Dashboard

## Quyền: `ADMIN` hoặc `MANAGER`

### 2.1 Dashboard & Statistics

#### GET `/api/orders/statistics`
**Thống kê tổng quan đơn hàng**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Response** (200 OK):
```json
{
  "success": true,
  "message": "Lấy thống kê đơn hàng thành công",
  "data": {
    "totalOrders": 1234,
    "pendingOrders": 56,
    "completedOrders": 1000,
    "cancelledOrders": 178,
    "totalRevenue": 123456789.00,
    "averageOrderValue": 100048.19
  }
}
```

#### GET `/api/orders/statistics/revenue`
**Thống kê doanh thu theo thời gian**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: 
  - `startDate`: 2025-01-01
  - `endDate`: 2025-12-31
  - `groupBy`: `DAY`, `WEEK`, `MONTH`, `YEAR`
- **Request**: `GET /api/orders/statistics/revenue?startDate=2025-10-01&endDate=2025-10-31&groupBy=DAY`
- **Response**:
```json
{
  "success": true,
  "data": {
    "period": "2025-10-01 to 2025-10-31",
    "groupBy": "DAY",
    "data": [
      {
        "date": "2025-10-01",
        "revenue": 1234567.00,
        "orderCount": 45
      },
      {
        "date": "2025-10-02",
        "revenue": 2345678.00,
        "orderCount": 67
      }
    ],
    "totalRevenue": 34567890.00,
    "totalOrders": 1234
  }
}
```

#### GET `/api/orders/statistics/top-customers`
**Top khách hàng**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: `limit=10`
- **Response**:
```json
{
  "success": true,
  "data": [
    {
      "userId": 1,
      "userName": "Nguyễn Văn A",
      "email": "customer@example.com",
      "totalOrders": 234,
      "totalSpent": 12345678.00,
      "lastOrderDate": "2025-10-20T14:30:00"
    }
  ]
}
```

#### GET `/api/payments/statistics/total-revenue`
**Tổng doanh thu từ thanh toán**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: `startDate`, `endDate`

#### GET `/api/payments/statistics/by-method`
**Thống kê theo phương thức thanh toán**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Response**:
```json
{
  "success": true,
  "data": [
    {
      "paymentMethod": "MOMO",
      "count": 456,
      "totalAmount": 45678900.00
    },
    {
      "paymentMethod": "CASH",
      "count": 234,
      "totalAmount": 23456700.00
    }
  ]
}
```

#### GET `/api/payments/statistics/by-status`
**Thống kê theo trạng thái thanh toán**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

---

### 2.2 Branch Management (MANAGER - Chỉ quản lý chi nhánh của mình)

#### GET `/api/branches`
**Lấy danh sách chi nhánh**
- **Auth**: ✅ ADMIN (xem tất cả), MANAGER (xem chi nhánh của mình)

#### POST `/api/branches`
**Tạo chi nhánh mới**
- **Auth**: ✅ ADMIN only
- **Request Body**:
```json
{
  "name": "Chi nhánh Quận 3",
  "address": "456 Võ Văn Tần, Q3, TP.HCM",
  "phone": "0281234568",
  "email": "q3@washify.vn",
  "openingHours": "7:00 - 23:00",
  "latitude": 10.7823,
  "longitude": 106.6935
}
```

#### PUT `/api/branches/{id}`
**Cập nhật chi nhánh**
- **Auth**: ✅ ADMIN (cập nhật bất kỳ), MANAGER (chỉ cập nhật chi nhánh của mình)
- **Note**: MANAGER chỉ cập nhật được khi `user.branchId == branchId`

#### DELETE `/api/branches/{id}`
**Xóa chi nhánh (soft delete)**
- **Auth**: ✅ ADMIN only

#### GET `/api/branches/{id}/statistics`
**Thống kê chi nhánh**
- **Auth**: ✅ ADMIN, MANAGER (chỉ xem chi nhánh của mình)
- **Response**:
```json
{
  "success": true,
  "data": {
    "branchId": 1,
    "branchName": "Chi nhánh Quận 1",
    "totalOrders": 456,
    "completedOrders": 400,
    "revenue": 45678900.00,
    "staffCount": 12,
    "shipperCount": 5
  }
}
```

---

### 2.3 User Management

#### GET `/api/users`
**Lấy danh sách users**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Response**: Array of users

#### GET `/api/users/search`
**Tìm kiếm user**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: `keyword=nguyen`
- **Request**: `GET /api/users/search?keyword=nguyen`

#### GET `/api/users/role/{roleId}`
**Lọc user theo role**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Example**: GET `/api/users/role/2` (lấy tất cả CUSTOMER)

#### GET `/api/users/active`
**Lấy users đang hoạt động**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### POST `/api/users/{id}/roles/{roleName}`
**Gán role cho user**
- **Auth**: ✅ ADMIN only
- **Example**: POST `/api/users/5/roles/STAFF`

#### PATCH `/api/users/batch/activate`
**Kích hoạt nhiều users**
- **Auth**: ✅ ADMIN only
- **Request Body**:
```json
{
  "userIds": [1, 2, 3, 4, 5]
}
```

#### PATCH `/api/users/batch/deactivate`
**Vô hiệu hóa nhiều users**
- **Auth**: ✅ ADMIN only

#### DELETE `/api/users/{id}`
**Xóa user (soft delete)**
- **Auth**: ✅ ADMIN only

---

### 2.4 Service Management

#### POST `/api/services`
**Tạo dịch vụ mới**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Request Body**:
```json
{
  "name": "Giặt hấp",
  "description": "Giặt hấp chăn ga gối đệm",
  "price": 80000.00,
  "unit": "kg",
  "category": "STEAM_CLEAN",
  "estimatedTime": 24,
  "isActive": true
}
```

#### PUT `/api/services/{id}`
**Cập nhật dịch vụ**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### DELETE `/api/services/{id}`
**Xóa dịch vụ (soft delete)**
- **Auth**: ✅ ADMIN only

---

### 2.5 Promotion Management

#### POST `/api/promotions`
**Tạo mã khuyến mãi**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Request Body**:
```json
{
  "code": "AUTUMN2025",
  "description": "Giảm 15% cho đơn hàng mùa thu",
  "discountType": "PERCENTAGE",
  "discountValue": 15.0,
  "minOrderAmount": 150000.00,
  "maxDiscountAmount": 100000.00,
  "startDate": "2025-09-01T00:00:00",
  "endDate": "2025-11-30T23:59:59",
  "usageLimit": 500,
  "isActive": true
}
```
- **Discount Types**: `PERCENTAGE`, `FIXED_AMOUNT`

#### GET `/api/promotions`
**Lấy tất cả mã khuyến mãi**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Response**: Bao gồm cả inactive và expired

#### PUT `/api/promotions/{id}`
**Cập nhật mã khuyến mãi**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### PATCH `/api/promotions/{id}/activate`
**Kích hoạt mã khuyến mãi**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### PATCH `/api/promotions/{id}/deactivate`
**Vô hiệu hóa mã khuyến mãi**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### GET `/api/promotions/{id}/usage`
**Xem thống kê sử dụng mã khuyến mãi**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Response**:
```json
{
  "success": true,
  "data": {
    "promotionId": 1,
    "code": "SUMMER2025",
    "usedCount": 234,
    "usageLimit": 1000,
    "remainingUses": 766,
    "totalDiscountGiven": 4680000.00
  }
}
```

---

### 2.6 Shipper Management

#### POST `/api/shippers`
**Tạo shipper mới**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Request Body**:
```json
{
  "userId": 10,
  "vehicleType": "MOTORBIKE",
  "vehicleNumber": "59A-12345",
  "phone": "0901234567",
  "isActive": true
}
```
- **Vehicle Types**: `MOTORBIKE`, `CAR`, `BICYCLE`

#### GET `/api/shippers`
**Lấy danh sách shippers**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### GET `/api/shippers/active`
**Lấy shippers đang hoạt động**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### GET `/api/shippers/{id}`
**Chi tiết shipper**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### GET `/api/shippers/phone/{phone}`
**Tìm shipper theo số điện thoại**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Example**: GET `/api/shippers/phone/0901234567`

#### GET `/api/shippers/name/{name}`
**Tìm shipper theo tên**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Example**: GET `/api/shippers/name/nguyen`

#### GET `/api/shippers/{id}/statistics`
**Thống kê shipper**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Response**:
```json
{
  "success": true,
  "data": {
    "shipperId": 1,
    "shipperName": "Nguyễn Văn B",
    "totalShipments": 456,
    "completedShipments": 420,
    "inProgressShipments": 5,
    "successRate": 92.1
  }
}
```

#### PUT `/api/shippers/{id}`
**Cập nhật thông tin shipper**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### PATCH `/api/shippers/{id}/activate`
**Kích hoạt shipper**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### PATCH `/api/shippers/{id}/deactivate`
**Vô hiệu hóa shipper**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

---

### 2.7 Order Management

#### GET `/api/orders`
**Lấy tất cả đơn hàng**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### GET `/api/orders/status/{status}`
**Lọc đơn hàng theo trạng thái**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Example**: GET `/api/orders/status/PENDING`

#### GET `/api/orders/search`
**Tìm kiếm đơn hàng**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: `keyword=WF202510210001`

#### GET `/api/orders/branch/{branchId}`
**Lấy đơn hàng theo chi nhánh**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Example**: GET `/api/orders/branch/1`

#### GET `/api/orders/date-range`
**Lọc đơn hàng theo khoảng thời gian**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: 
  - `startDate`: 2025-10-01
  - `endDate`: 2025-10-31
- **Request**: `GET /api/orders/date-range?startDate=2025-10-01&endDate=2025-10-31`

#### PATCH `/api/orders/batch/status`
**Cập nhật trạng thái nhiều đơn hàng**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Request Body**:
```json
{
  "orderIds": [1, 2, 3, 4, 5],
  "status": "CONFIRMED"
}
```

#### DELETE `/api/orders/{id}`
**Xóa đơn hàng (soft delete)**
- **Auth**: ✅ ADMIN only

---

### 2.8 Payment Management

#### GET `/api/payments`
**Lấy tất cả thanh toán**
- **Auth**: ✅ ADMIN, STAFF

#### GET `/api/payments/status/{status}`
**Lọc thanh toán theo trạng thái**
- **Auth**: ✅ ADMIN, STAFF
- **Status**: `PENDING`, `COMPLETED`, `FAILED`, `REFUNDED`

#### GET `/api/payments/order/{orderId}`
**Xem thanh toán của đơn hàng**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### PATCH `/api/payments/{id}/status`
**Cập nhật trạng thái thanh toán**
- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: `status=COMPLETED`

#### POST `/api/payments/{id}/refund`
**Hoàn tiền**
- **Auth**: ✅ ADMIN, MANAGER
- **Params**: `reason=Customer request`
- **Request**: `POST /api/payments/1/refund?reason=Customer request`

---

### 2.9 Notification Management

#### GET `/api/notifications`
**Lấy tất cả notifications**
- **Auth**: ✅ ADMIN, STAFF, MANAGER

#### POST `/api/notifications`
**Tạo notification mới**
- **Auth**: ✅ ADMIN, STAFF
- **Request Body**:
```json
{
  "userId": 1,
  "type": "PROMOTION",
  "title": "Khuyến mãi mới",
  "message": "Giảm 20% cho đơn hàng đầu tiên"
}
```
- **Types**: `ORDER_UPDATE`, `PROMOTION`, `SYSTEM`, `REMINDER`

---

### 2.10 Review Management

#### GET `/api/reviews`
**Lấy tất cả đánh giá**
- **Auth**: ✅ ADMIN, STAFF

#### GET `/api/reviews/order/{orderId}`
**Xem đánh giá của đơn hàng**
- **Auth**: ✅ ADMIN, STAFF

#### DELETE `/api/reviews/{id}`
**Xóa đánh giá (soft delete)**
- **Auth**: ✅ ADMIN only

---

### 2.11 Audit Logs (Nhật ký hệ thống)

#### GET `/api/audit-logs`
**Lấy nhật ký hoạt động**
- **Auth**: ✅ ADMIN only
- **Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "userName": "Admin User",
      "action": "UPDATE_ORDER",
      "entityType": "Order",
      "entityId": 123,
      "details": "Changed status from PENDING to CONFIRMED",
      "ipAddress": "192.168.1.1",
      "timestamp": "2025-10-21T10:30:00"
    }
  ]
}
```

#### GET `/api/audit-logs/user/{userId}`
**Xem nhật ký của user**
- **Auth**: ✅ ADMIN only

#### GET `/api/audit-logs/entity/{entityType}/{entityId}`
**Xem nhật ký của entity**
- **Auth**: ✅ ADMIN only
- **Example**: GET `/api/audit-logs/entity/Order/123`

---

### 2.12 Soft Delete Management

#### GET `/api/soft-delete/{entity}`
**Xem các bản ghi đã xóa**
- **Auth**: ✅ ADMIN only
- **Entity**: `users`, `orders`, `services`, `branches`, etc.
- **Example**: GET `/api/soft-delete/orders`

#### PATCH `/api/soft-delete/{entity}/{id}/restore`
**Khôi phục bản ghi đã xóa**
- **Auth**: ✅ ADMIN only
- **Example**: PATCH `/api/soft-delete/orders/123/restore`

#### DELETE `/api/soft-delete/{entity}/{id}/permanent`
**Xóa vĩnh viễn**
- **Auth**: ✅ ADMIN only
- **Example**: DELETE `/api/soft-delete/orders/123/permanent`

---

---

# 3. 👨‍💼 Staff Portal

## Quyền: `STAFF`

### 3.1 Order Processing

Staff có tất cả quyền của ADMIN/MANAGER về orders, nhưng tập trung vào:

#### PATCH `/api/orders/{id}/status`
**Cập nhật trạng thái đơn hàng**
- **Auth**: ✅ STAFF
- **Flow**:
  1. PENDING → CONFIRMED (Staff xác nhận)
  2. CONFIRMED → PICKED_UP (Shipper lấy hàng)
  3. PICKED_UP → IN_PROGRESS (Đang giặt)
  4. IN_PROGRESS → READY (Giặt xong, sẵn sàng giao)
  5. READY → DELIVERING (Đang giao)
  6. DELIVERING → COMPLETED (Đã giao)

#### GET `/api/orders/status/PENDING`
**Xem đơn hàng chờ xác nhận**
- **Auth**: ✅ STAFF

#### GET `/api/orders/status/IN_PROGRESS`
**Xem đơn hàng đang xử lý**
- **Auth**: ✅ STAFF

---

### 3.2 Shipment Management

#### GET `/api/shipments`
**Xem tất cả shipments**
- **Auth**: ✅ STAFF

#### POST `/api/shipments`
**Tạo shipment (gán shipper)**
- **Auth**: ✅ STAFF
- **Request Body**:
```json
{
  "orderId": 1,
  "shipperId": 5,
  "type": "PICKUP",
  "scheduledTime": "2025-10-22T14:00:00"
}
```
- **Types**: `PICKUP` (lấy hàng), `DELIVERY` (giao hàng)

#### GET `/api/shipments/status/{status}`
**Lọc shipments theo trạng thái**
- **Auth**: ✅ STAFF
- **Status**: `PENDING`, `ASSIGNED`, `PICKED_UP`, `DELIVERING`, `DELIVERED`, `FAILED`

#### GET `/api/shipments/shipper/{shipperId}`
**Xem shipments của shipper**
- **Auth**: ✅ STAFF

#### PATCH `/api/shipments/{id}/assign`
**Gán shipper**
- **Auth**: ✅ STAFF, MANAGER
- **Request Body**:
```json
{
  "shipperId": 5
}
```

#### PATCH `/api/shipments/{id}/status`
**Cập nhật trạng thái shipment**
- **Auth**: ✅ STAFF, MANAGER

#### GET `/api/shipments/statistics`
**Thống kê shipments**
- **Auth**: ✅ STAFF, MANAGER

---

### 3.3 Customer Support

#### GET `/api/users/{id}`
**Xem thông tin khách hàng**
- **Auth**: ✅ STAFF

#### GET `/api/orders/user/{userId}`
**Xem lịch sử đơn hàng của khách**
- **Auth**: ✅ STAFF

#### GET `/api/reviews/user/{userId}`
**Xem đánh giá của khách**
- **Auth**: ✅ STAFF

---

---

# 4. 🚚 Shipper Mobile App

## Quyền: `SHIPPER`

### 4.1 Shipment Operations

#### GET `/api/shipments/shipper/{shipperId}`
**Xem shipments được gán**
- **Auth**: ✅ SHIPPER (chỉ xem shipments của mình)
- **Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "orderId": 123,
      "orderCode": "WF202510210001",
      "type": "PICKUP",
      "status": "ASSIGNED",
      "pickupAddress": "123 Nguyễn Huệ, Q1",
      "deliveryAddress": "456 Lê Lợi, Q3",
      "scheduledTime": "2025-10-22T14:00:00",
      "customerName": "Nguyễn Văn A",
      "customerPhone": "0901234567",
      "note": "Gọi trước 15 phút"
    }
  ]
}
```

#### GET `/api/shipments/{id}`
**Chi tiết shipment**
- **Auth**: ✅ SHIPPER (chỉ xem shipments của mình)

#### PATCH `/api/shipments/{id}/status`
**Cập nhật trạng thái shipment**
- **Auth**: ✅ SHIPPER
- **Flow**:
  1. ASSIGNED → PICKED_UP (Đã lấy hàng)
  2. PICKED_UP → DELIVERING (Đang giao)
  3. DELIVERING → DELIVERED (Đã giao xong)
- **Request**: `PATCH /api/shipments/1/status?status=PICKED_UP`

#### POST `/api/shipments/{id}/pickup-image`
**Upload ảnh lấy hàng**
- **Auth**: ✅ SHIPPER
- **Request**: FormData with image file
- **Response**:
```json
{
  "success": true,
  "message": "Upload ảnh lấy hàng thành công",
  "data": {
    "shipmentId": 1,
    "imageUrl": "https://storage.example.com/pickups/123.jpg",
    "uploadedAt": "2025-10-22T14:05:00"
  }
}
```

#### POST `/api/shipments/{id}/delivery-image`
**Upload ảnh giao hàng**
- **Auth**: ✅ SHIPPER
- **Request**: FormData with image file

#### GET `/api/shipments/{id}/images`
**Xem ảnh của shipment**
- **Auth**: ✅ SHIPPER

#### GET `/api/shipments/statistics`
**Thống kê cá nhân của shipper**
- **Auth**: ✅ SHIPPER
- **Response**:
```json
{
  "success": true,
  "data": {
    "todayPickups": 12,
    "todayDeliveries": 10,
    "completedToday": 8,
    "pendingPickups": 4,
    "pendingDeliveries": 2
  }
}
```

---

---

# 📝 Common Response Formats

## Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2025-10-21T10:30:00"
}
```

## Error Response
```json
{
  "success": false,
  "message": "Error message",
  "error": "Detailed error description",
  "timestamp": "2025-10-21T10:30:00"
}
```

## Validation Error Response
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Email không hợp lệ",
    "password": "Mật khẩu phải có ít nhất 8 ký tự"
  },
  "timestamp": "2025-10-21T10:30:00"
}
```

---

# 🔐 Authentication Flow

## 1. Register (Customer)
```
POST /api/auth/register
→ Nhận user object + message
→ Cần login để lấy token
```

## 2. Login
```
POST /api/auth/login
→ Nhận token + user info
→ Lưu token vào localStorage/sessionStorage
```

## 3. Authenticated Requests
```
Header: Authorization: Bearer <token>
→ Mọi request sau đều gửi kèm header này
```

## 4. Token Expiration
```
401 Unauthorized response
→ Redirect to login page
→ Clear stored token
```

---

# 🎯 Order Status Flow

```
PENDING         → Customer tạo đơn
    ↓
CONFIRMED       → Staff xác nhận
    ↓
PICKED_UP       → Shipper lấy hàng
    ↓
IN_PROGRESS     → Đang giặt ủi
    ↓
READY           → Giặt xong, sẵn sàng giao
    ↓
DELIVERING      → Shipper đang giao
    ↓
COMPLETED       → Hoàn thành
```

**Có thể CANCELLED ở bất kỳ bước nào (trước IN_PROGRESS)**

---

# 💳 Payment Status Flow

```
PENDING         → Payment được tạo, chờ thanh toán
    ↓
COMPLETED       → Thanh toán thành công
    
FAILED          → Thanh toán thất bại

REFUNDED        → Đã hoàn tiền
```

---

# 🚚 Shipment Status Flow

```
PENDING         → Shipment được tạo
    ↓
ASSIGNED        → Đã gán shipper
    ↓
PICKED_UP       → Shipper đã lấy hàng
    ↓
DELIVERING      → Đang giao hàng
    ↓
DELIVERED       → Đã giao thành công

FAILED          → Giao thất bại (có thể retry)
```

---

# 🔔 Notification Types

- **ORDER_UPDATE**: Cập nhật trạng thái đơn hàng
- **PROMOTION**: Thông báo khuyến mãi
- **SYSTEM**: Thông báo hệ thống
- **REMINDER**: Nhắc nhở (đánh giá, thanh toán, etc.)
- **PAYMENT_STATUS**: Cập nhật trạng thái thanh toán

---

# ⚠️ Important Notes for Frontend Development

## 1. Error Handling
- Luôn kiểm tra `success` field trong response
- Hiển thị `message` field cho user
- Log `error` field cho debugging

## 2. Loading States
- Hiển thị loading indicator khi call API
- Disable buttons khi đang submit form
- Timeout: 30 seconds cho mọi request

## 3. Pagination (Future Enhancement)
- Hiện tại: API trả về toàn bộ data
- Tương lai: Sẽ có `page`, `size`, `totalPages` params

## 4. Real-time Updates (Future Enhancement)
- Hiện tại: Polling (call API định kỳ)
- Tương lai: WebSocket cho real-time notifications

## 5. File Upload
- Max size: 10MB per file
- Supported formats: JPG, PNG, PDF
- Use FormData for multipart/form-data

## 6. Date/Time Format
- API nhận: `YYYY-MM-DDTHH:mm:ss` (ISO 8601)
- API trả về: `YYYY-MM-DDTHH:mm:ss`
- Timezone: Asia/Ho_Chi_Minh (UTC+7)

## 7. Authorization
- Store JWT token securely
- Include token in every authenticated request
- Handle 401 (Unauthorized) → redirect to login
- Handle 403 (Forbidden) → show "No permission" message

---

# 🧪 Testing Endpoints

## Postman Collection
Recommend creating collections for each role:
1. **Customer Collection**: Public + CUSTOMER endpoints
2. **Admin Collection**: ADMIN endpoints
3. **Staff Collection**: STAFF endpoints
4. **Shipper Collection**: SHIPPER endpoints

## Sample Test Flow

### Customer Flow:
```
1. POST /api/auth/register (Register)
2. POST /api/auth/login (Login → Get token)
   - Có thể dùng username, email hoặc phone để login
3. GET /api/services (View services)
4. GET /api/promotions/active (Check promotions)
5. POST /api/orders (Create order)
6. GET /api/orders/{id} (Check order status)
7. POST /api/payments (Make payment)
8. POST /api/reviews (Leave review)
```

### Guest User Flow:
```
1. Walk-in tại cửa hàng → Staff tạo Guest User
2. POST /api/auth/login (Login với phone và password mặc định)
   - username: số điện thoại (VD: 0912345678)
   - password: Guest@123456
3. Nhận response với requirePasswordChange = true
4. POST /api/auth/first-time-password-change (Đổi mật khẩu ngay)
   - newPassword: mật khẩu mới
   - confirmPassword: xác nhận mật khẩu
5. POST /api/auth/login (Login lại với mật khẩu mới)
6. Sử dụng app như Customer bình thường
```

### Staff Flow:
```
1. POST /api/auth/login (Login as STAFF)
2. GET /api/orders/status/PENDING (View pending orders)
3. PATCH /api/orders/{id}/status?status=CONFIRMED (Confirm order)
4. POST /api/shipments (Assign shipper)
5. GET /api/shipments/status/ASSIGNED (Track shipments)
```

---

# 📞 Support

For questions or issues during frontend development:
- Check this documentation first
- Test endpoints with Postman
- Verify request/response format
- Check JWT token validity
- Verify user permissions

---

# 📝 Latest Updates

## Version 1.1 - Authentication Enhancements (2025-10-21)

### ✨ Multi-Method Login
- **Flexible Login**: Người dùng có thể đăng nhập bằng Username, Email hoặc Phone Number
- **Automatic Detection**: Hệ thống tự động nhận diện và tìm kiếm theo thứ tự: Username → Email → Phone
- **Use Cases**:
  - Admin/Staff: Login bằng username (vd: `admin`, `manager1`)
  - Customer: Login bằng email (vd: `customer@example.com`)
  - Guest User: Login bằng phone (vd: `0912345678`)

### 🔐 First-Time Password Change for Guest Users
- **Guest User Flow**: 
  1. Guest User được tạo tại cửa hàng bởi Staff
  2. Login lần đầu với phone + password mặc định (`Guest@123456`)
  3. Response có field `requirePasswordChange: true`
  4. Bắt buộc đổi mật khẩu qua endpoint `/api/auth/first-time-password-change`
  5. Login lại với mật khẩu mới
- **Security**: 
  - Không cần mật khẩu cũ cho lần đổi đầu tiên
  - Tự động set `requirePasswordChange = false` sau khi đổi thành công

### 🔑 Default Password Configuration
- **Centralized Config**: Mật khẩu mặc định được quản lý tập trung trong `application.properties`
- **Configuration**:
  ```properties
  app.default-password=${DEFAULT_PASSWORD:washify123}
  guest.default-password=${GUEST_DEFAULT_PASSWORD:Guest@123456}
  ```
- **Environment Variables**: Có thể override qua biến môi trường `DEFAULT_PASSWORD` và `GUEST_DEFAULT_PASSWORD`

### 🔄 Migration V5
- **Database Changes**: Thêm column `require_password_change` vào bảng `users`
- **Auto-Setup**: Guest Users tự động có `require_password_change = true`
- **Indexing**: Index trên column để optimize query performance

---

**Version**: 1.1  
**Last Updated**: 2025-10-21  
**Backend**: Spring Boot 3.3.5  
**Database**: MySQL 8.0  
**JWT Expiry**: 24 hours

