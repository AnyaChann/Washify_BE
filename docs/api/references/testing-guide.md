# 🧪 Testing Guide

## Postman Collection Structure

Recommend creating collections for each role:

### 1. Customer Collection
- Public endpoints
- CUSTOMER authenticated endpoints

### 2. Admin Collection
- ADMIN authenticated endpoints

### 3. Staff Collection
- STAFF authenticated endpoints

### 4. Shipper Collection
- SHIPPER authenticated endpoints

---

## Sample Test Flows

### Customer Flow

```
1. POST /api/auth/register
   → Register new customer account

2. POST /api/auth/login
   → Login with email/phone/username
   → Save JWT token

3. GET /api/services
   → View available services

4. GET /api/promotions/active
   → Check active promotions

5. POST /api/orders
   → Create new order
   → Use promotion code

6. GET /api/orders/{id}
   → Check order status

7. POST /api/payments
   → Create payment (MOMO or CASH)

8. GET /api/payments/order/{orderId}
   → Check payment status

9. POST /api/reviews
   → Leave review after order completed

10. GET /api/notifications/user/{userId}
    → Check notifications
```

---

### Guest User Flow

```
1. Walk-in tại cửa hàng
   → Staff tạo Guest User với phone

2. POST /api/auth/login
   {
     "username": "0912345678",
     "password": "Guest@123456"
   }
   → Response: requirePasswordChange = true
   → Save token

3. POST /api/auth/first-time-password-change
   {
     "newPassword": "MyNewPassword123",
     "confirmPassword": "MyNewPassword123"
   }
   → Change password

4. POST /api/auth/login
   {
     "username": "0912345678",
     "password": "MyNewPassword123"
   }
   → Login with new password

5. Use app like normal CUSTOMER
```

---

### Staff Flow

```
1. POST /api/auth/login
   {
     "username": "staff1",
     "password": "staff123"
   }
   → Login as STAFF
   → Save token

2. GET /api/orders/status/PENDING
   → View pending orders

3. PATCH /api/orders/{id}/status?status=CONFIRMED
   → Confirm order

4. POST /api/shipments
   {
     "orderId": 1,
     "shipperId": 5,
     "type": "PICKUP",
     "scheduledTime": "2025-10-22T14:00:00"
   }
   → Assign shipper

5. GET /api/shipments/status/ASSIGNED
   → Track assigned shipments

6. PATCH /api/orders/{id}/status?status=IN_PROGRESS
   → Update order to in progress

7. PATCH /api/orders/{id}/status?status=READY
   → Mark order as ready for delivery

8. POST /api/shipments
   {
     "orderId": 1,
     "shipperId": 5,
     "type": "DELIVERY",
     "scheduledTime": "2025-10-22T16:00:00"
   }
   → Create delivery shipment
```

---

### Shipper Flow

```
1. POST /api/auth/login
   {
     "username": "shipper1",
     "password": "shipper123"
   }
   → Login as SHIPPER
   → Save token

2. GET /api/shipments/shipper/{shipperId}
   → View assigned shipments

3. GET /api/shipments/{id}
   → Get shipment details

4. PATCH /api/shipments/{id}/status?status=PICKED_UP
   → Update status after pickup

5. POST /api/shipments/{id}/pickup-image
   → Upload pickup image (FormData)

6. PATCH /api/shipments/{id}/status?status=DELIVERING
   → Update status to delivering

7. PATCH /api/shipments/{id}/status?status=DELIVERED
   → Mark as delivered

8. POST /api/shipments/{id}/delivery-image
   → Upload delivery image

9. GET /api/shipments/statistics
   → Check personal statistics
```

---

## Environment Variables

### Local Development
```
BASE_URL=http://localhost:8080/api
```

### Railway Production
```
BASE_URL=https://washifybe-production.up.railway.app/api
```

---

## Postman Pre-request Script

Save token automatically after login:

```javascript
// After login response
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.token) {
        pm.environment.set("jwt_token", jsonData.data.token);
    }
}
```

---

## Postman Authorization

Set for entire collection:

```
Type: Bearer Token
Token: {{jwt_token}}
```

---

## Common Test Cases

### 1. Authentication Tests

**Test Register:**
```
POST /api/auth/register
Body: {
  "email": "test@example.com",
  "password": "Test123456",
  "fullName": "Test User",
  "phone": "0901234567"
}
Expected: 201 Created
```

**Test Login with Email:**
```
POST /api/auth/login
Body: {
  "username": "test@example.com",
  "password": "Test123456"
}
Expected: 200 OK + token
```

**Test Login with Phone:**
```
POST /api/auth/login
Body: {
  "username": "0901234567",
  "password": "Test123456"
}
Expected: 200 OK + token
```

**Test Invalid Login:**
```
POST /api/auth/login
Body: {
  "username": "test@example.com",
  "password": "WrongPassword"
}
Expected: 401 Unauthorized
```

---

### 2. Authorization Tests

**Test Access Without Token:**
```
GET /api/orders
Header: (No Authorization)
Expected: 401 Unauthorized
```

**Test Access Wrong Role:**
```
GET /api/admin/users
Header: Authorization: Bearer <customer_token>
Expected: 403 Forbidden
```

---

### 3. Validation Tests

**Test Invalid Email:**
```
POST /api/auth/register
Body: {
  "email": "invalid-email",
  "password": "Test123456",
  ...
}
Expected: 400 Bad Request + validation errors
```

**Test Missing Required Fields:**
```
POST /api/orders
Body: {
  "branchId": 1
  // Missing items
}
Expected: 400 Bad Request
```

---

### 4. Business Logic Tests

**Test Promotion Validation:**
```
POST /api/promotions/validate?code=EXPIRED&orderAmount=100000
Expected: 400 Bad Request + "Mã khuyến mãi đã hết hạn"
```

**Test Order Cancel After Confirmed:**
```
PATCH /api/orders/{id}/cancel
(Order status = CONFIRMED)
Expected: 400 Bad Request + "Không thể hủy đơn hàng đã xác nhận"
```

---

## Performance Testing

### Load Test Orders Endpoint
```bash
# Using Apache Bench
ab -n 1000 -c 10 -H "Authorization: Bearer <token>" http://localhost:8080/api/orders
```

### Expected Response Times
- GET requests: < 200ms
- POST requests: < 500ms
- Image upload: < 2s

---

## Error Scenarios to Test

1. **Network Timeout**: Cancel request mid-flight
2. **Duplicate Requests**: Submit same order twice
3. **Concurrent Updates**: Two users update same order
4. **Invalid Token**: Expired or malformed JWT
5. **SQL Injection**: Test with SQL keywords in input
6. **XSS**: Test with HTML/JavaScript in text fields

---

[← Back to Main Documentation](../README.md)
