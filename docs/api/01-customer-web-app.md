# 🛒 Customer Web Application API

## Quyền: `CUSTOMER` hoặc Public

---

## Table of Contents

1. [Authentication & User Management](#authentication--user-management)
2. [Services (Dịch vụ)](#services)
3. [Branches (Chi nhánh)](#branches)
4. [Orders (Đặt hàng)](#orders)
5. [Payments (Thanh toán)](#payments)
6. [Promotions (Khuyến mãi)](#promotions)
7. [Reviews (Đánh giá)](#reviews)
8. [Notifications (Thông báo)](#notifications)

---

## Authentication & User Management

### POST `/api/auth/register`
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

---

### POST `/api/auth/login`
**Đăng nhập (hỗ trợ Username/Email/Phone)**

- **Public**: ✅ Không cần authentication
- **Request Body**:
```json
{
  "username": "customer@example.com",
  "password": "Password123"
}
```

- **Note**: Field `username` có thể là:
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

---

### POST `/api/auth/first-time-password-change`
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

---

### GET `/api/users/{id}`
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

---

### PUT `/api/users/{id}`
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

---

### POST `/api/users/{id}/change-password`
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

## Services

### GET `/api/services`
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

---

### GET `/api/services/{id}`
**Xem chi tiết dịch vụ**

- **Public**: ✅ Không cần authentication

---

### GET `/api/services/active`
**Lấy danh sách dịch vụ đang hoạt động**

- **Public**: ✅ Không cần authentication
- **Response**: Tương tự GET `/api/services` nhưng chỉ có `isActive: true`

---

### GET `/api/services/category/{category}`
**Lọc dịch vụ theo danh mục**

- **Public**: ✅ Không cần authentication
- **Params**: `category` - Ví dụ: `DRY_CLEAN`, `WASH_IRON`, `IRON_ONLY`, `WASH_ONLY`

---

## Branches

### GET `/api/branches`
**Lấy danh sách chi nhánh**

- **Public**: ✅ Không cần authentication
- **Response** (200 OK):
```json
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

---

### GET `/api/branches/active`
**Lấy chi nhánh đang hoạt động**

- **Public**: ✅ Không cần authentication

---

### GET `/api/branches/{id}`
**Chi tiết chi nhánh**

- **Public**: ✅ Không cần authentication

---

## Orders

### POST `/api/orders`
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

---

### GET `/api/orders/{id}`
**Xem chi tiết đơn hàng**

- **Auth**: ✅ CUSTOMER (chỉ xem đơn của mình)

---

### GET `/api/orders/user/{userId}`
**Lấy tất cả đơn hàng của customer**

- **Auth**: ✅ CUSTOMER (chỉ xem đơn của mình)

---

### GET `/api/orders/user/{userId}/status/{status}`
**Lọc đơn hàng theo trạng thái**

- **Auth**: ✅ CUSTOMER (chỉ xem đơn của mình)
- **Status**: `PENDING`, `CONFIRMED`, `PICKED_UP`, `IN_PROGRESS`, `READY`, `DELIVERING`, `COMPLETED`, `CANCELLED`

---

### PATCH `/api/orders/{id}/cancel`
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

---

### PATCH `/api/orders/{id}/status`
**Cập nhật trạng thái đơn hàng** (Customer chỉ có thể confirm nhận hàng)

- **Auth**: ✅ CUSTOMER
- **Request**: `?status=COMPLETED`

---

## Payments

### POST `/api/payments`
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

---

### GET `/api/payments/{id}`
**Xem chi tiết thanh toán**

- **Auth**: ✅ CUSTOMER (chỉ xem thanh toán của mình)

---

### GET `/api/payments/order/{orderId}`
**Lấy thanh toán của đơn hàng**

- **Auth**: ✅ CUSTOMER (chỉ xem thanh toán đơn của mình)

---

### POST `/api/payments/webhook`
**Webhook từ payment gateway** (Tự động xử lý)

- **Public**: ✅ Không cần authentication (sử dụng signature verification)
- **Note**: Endpoint này được payment gateway gọi tự động, không cần FE xử lý

---

## Promotions

### GET `/api/promotions/active`
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

---

### GET `/api/promotions/valid`
**Lấy mã giảm giá hợp lệ (active + trong thời hạn)**

- **Public**: ✅ Không cần authentication

---

### GET `/api/promotions/code/{code}`
**Tìm mã giảm giá theo code**

- **Public**: ✅ Không cần authentication
- **Example**: GET `/api/promotions/code/SUMMER2025`

---

### POST `/api/promotions/validate`
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

## Reviews

### POST `/api/reviews`
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

---

### GET `/api/reviews/service/{serviceId}`
**Xem đánh giá của dịch vụ**

- **Public**: ✅ Không cần authentication

---

### GET `/api/reviews/user/{userId}`
**Xem đánh giá của user**

- **Public**: ✅ Không cần authentication

---

### PUT `/api/reviews/{id}`
**Cập nhật đánh giá**

- **Auth**: ✅ CUSTOMER (chỉ cập nhật đánh giá của mình)

---

### DELETE `/api/reviews/{id}`
**Xóa đánh giá**

- **Auth**: ✅ CUSTOMER (chỉ xóa đánh giá của mình)

---

## Notifications

### GET `/api/notifications/user/{userId}`
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

---

### PATCH `/api/notifications/{id}/read`
**Đánh dấu đã đọc**

- **Auth**: ✅ CUSTOMER (chỉ đánh dấu thông báo của mình)

---

[← Back to Main Documentation](./README.md)
