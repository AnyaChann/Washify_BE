# 🚀 Quick Reference Card

## 📍 Essential Endpoints

### Authentication
```
POST /api/auth/register          # Register
POST /api/auth/login             # Login (email/phone/username)
POST /api/auth/first-time-password-change  # Guest password change
```

### Services & Branches (Public)
```
GET /api/services                # All services
GET /api/services/active         # Active services only
GET /api/branches                # All branches
GET /api/branches/active         # Active branches only
```

### Orders (CUSTOMER)
```
POST /api/orders                 # Create order
GET /api/orders/{id}             # Order details
GET /api/orders/user/{userId}    # My orders
PATCH /api/orders/{id}/cancel    # Cancel order
PATCH /api/orders/{id}/status    # Update status
```

### Payments (CUSTOMER)
```
POST /api/payments               # Create payment
GET /api/payments/{id}           # Payment details
GET /api/payments/order/{orderId} # Order payment
```

### Promotions (Public)
```
GET /api/promotions/active       # Active promotions
GET /api/promotions/code/{code}  # Find by code
POST /api/promotions/validate    # Validate promo code
```

### Shipments (SHIPPER)
```
GET /api/shipments/shipper/{id}  # My shipments
PATCH /api/shipments/{id}/status # Update status
POST /api/shipments/{id}/pickup-image   # Upload pickup photo
POST /api/shipments/{id}/delivery-image # Upload delivery photo
```

---

## 🔐 Authentication Header

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

---

## 📊 Status Values

### Order Status
```
PENDING → CONFIRMED → PICKED_UP → IN_PROGRESS 
→ READY → DELIVERING → COMPLETED
↓
CANCELLED
```

### Payment Status
```
PENDING → COMPLETED
         ↓
       FAILED
         ↓
      REFUNDED
```

### Shipment Status
```
PENDING → ASSIGNED → PICKED_UP → DELIVERING → DELIVERED
                                              ↓
                                            FAILED
```

---

## 🎭 Roles & Permissions

| Role | Description | Key Endpoints |
|------|-------------|---------------|
| `CUSTOMER` | Khách hàng | Orders, Payments, Reviews |
| `GUEST` | Khách vãng lai | Same as CUSTOMER after password change |
| `ADMIN` | Quản trị viên | All endpoints |
| `MANAGER` | Quản lý chi nhánh | Branch-specific management |
| `STAFF` | Nhân viên | Order processing, Shipments |
| `SHIPPER` | Giao hàng | Shipment updates, Photo upload |

---

## 📋 Response Format

### Success
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error
```json
{
  "success": false,
  "message": "Error message",
  "error": "Details"
}
```

---

## 🚨 HTTP Status Codes

| Code | Meaning | Action |
|------|---------|--------|
| 200 | OK | Success |
| 201 | Created | Resource created |
| 400 | Bad Request | Check input validation |
| 401 | Unauthorized | Login required |
| 403 | Forbidden | No permission |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate resource |
| 500 | Server Error | Try again later |

---

## 📱 Payment Methods

```
CASH  - Tiền mặt (COD)
MOMO  - Ví MoMo
```

---

## 🌐 Base URLs

**Local Development:**
```
http://localhost:8080/api
```

**Railway Production:**
```
https://washifybe-production.up.railway.app/api
```

---

## 📅 Date Format

```
ISO 8601: YYYY-MM-DDTHH:mm:ss
Example: 2025-10-22T14:00:00
Timezone: Asia/Ho_Chi_Minh (UTC+7)
```

---

## 🔍 Multi-Login Support

Login với bất kỳ:
- **Username**: `admin`, `staff1`
- **Email**: `customer@example.com`
- **Phone**: `0901234567`, `+84901234567`

System tự động detect!

---

## 📖 Full Documentation

- **Main Index**: [docs/api/README.md](./README.md)
- **Customer**: [01-customer-web-app.md](./01-customer-web-app.md)
- **Admin/Manager**: [02-admin-manager-dashboard.md](./02-admin-manager-dashboard.md)
- **Staff**: [03-staff-portal.md](./03-staff-portal.md)
- **Shipper**: [04-shipper-mobile-app.md](./04-shipper-mobile-app.md)
- **References**: [references/](./references/)

---

**Version**: 2.0  
**Last Updated**: 2025-10-22
