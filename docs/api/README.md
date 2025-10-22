# 📚 Washify API Documentation

**Base URL**: `http://localhost:8080/api`

**Authentication**: JWT Bearer Token (trừ các public endpoints)

**Header**: `Authorization: Bearer <token>`

---

## 📋 Table of Contents

### 🎯 By Priority

1. [Customer Web Application](./01-customer-web-app.md) ⭐ **Priority 1**
2. [Admin & Manager Dashboard](./02-admin-manager-dashboard.md) ⭐ **Priority 2**
3. [Staff Portal](./03-staff-portal.md) ⭐ **Priority 3**
4. [Shipper Mobile App](./04-shipper-mobile-app.md) ⭐ **Priority 4**

### 🔧 By Module

- [Authentication](./modules/authentication.md)
- [User Management](./modules/users.md)
- [Services](./modules/services.md)
- [Branches](./modules/branches.md)
- [Orders](./modules/orders.md)
- [Payments](./modules/payments.md)
- [Promotions](./modules/promotions.md)
- [Reviews](./modules/reviews.md)
- [Shipments](./modules/shipments.md)
- [Shippers](./modules/shippers.md)
- [Notifications](./modules/notifications.md)
- [Statistics](./modules/statistics.md)
- [Audit Logs](./modules/audit-logs.md)

### 📖 References

- [Common Response Formats](./references/response-formats.md)
- [Status Flows](./references/status-flows.md)
- [Error Handling](./references/error-handling.md)
- [Testing Guide](./references/testing-guide.md)

---

## 🔐 Quick Start

### 1. Register & Login (Customer)

```bash
# Register
POST /api/auth/register
{
  "email": "customer@example.com",
  "password": "Password123",
  "fullName": "Nguyễn Văn A",
  "phone": "0901234567"
}

# Login (username, email, or phone)
POST /api/auth/login
{
  "username": "customer@example.com",  # hoặc phone: 0901234567
  "password": "Password123"
}

# Response → Save token
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "type": "Bearer"
  }
}
```

### 2. Use Token

```bash
# Add to all authenticated requests
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

---

## 🎯 Order Status Flow

```
PENDING → CONFIRMED → PICKED_UP → IN_PROGRESS → READY → DELIVERING → COMPLETED
         ↓
      CANCELLED
```

---

## 📞 Support

For questions or issues during frontend development:
- Check this documentation first
- Test endpoints with Postman
- Verify request/response format
- Check JWT token validity
- Verify user permissions

---

**Version**: 1.1  
**Last Updated**: 2025-10-21  
**Backend**: Spring Boot 3.3.5  
**Database**: MySQL 8.0  
**JWT Expiry**: 24 hours
