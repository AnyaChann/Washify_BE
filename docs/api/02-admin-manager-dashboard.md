# 👔 Admin & Manager Dashboard API

## Quyền: `ADMIN` hoặc `MANAGER`

---

## Table of Contents

1. [Dashboard & Statistics](#dashboard--statistics)
2. [Branch Management](#branch-management)
3. [User Management](#user-management)
4. [Service Management](#service-management)
5. [Promotion Management](#promotion-management)
6. [Shipper Management](#shipper-management)
7. [Order Management](#order-management)
8. [Payment Management](#payment-management)
9. [Notification Management](#notification-management)
10. [Review Management](#review-management)
11. [Audit Logs](#audit-logs)
12. [Soft Delete Management](#soft-delete-management)

---

## Dashboard & Statistics

> 📖 Chi tiết xem tại [Statistics Module](./modules/statistics.md)

### GET `/api/orders/statistics`
**Thống kê tổng quan đơn hàng**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/orders/statistics/revenue`
**Thống kê doanh thu theo thời gian**

- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: `startDate`, `endDate`, `groupBy` (DAY, WEEK, MONTH, YEAR)

### GET `/api/orders/statistics/top-customers`
**Top khách hàng**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/payments/statistics/total-revenue`
**Tổng doanh thu từ thanh toán**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/payments/statistics/by-method`
**Thống kê theo phương thức thanh toán**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/payments/statistics/by-status`
**Thống kê theo trạng thái thanh toán**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

---

## Branch Management

> 📖 Chi tiết xem tại [Branches Module](./modules/branches.md)

> **Note**: MANAGER chỉ quản lý chi nhánh của mình (`user.branchId == branchId`)

### GET `/api/branches`
**Lấy danh sách chi nhánh**

- **Auth**: ✅ ADMIN (xem tất cả), MANAGER (xem chi nhánh của mình)

### POST `/api/branches`
**Tạo chi nhánh mới**

- **Auth**: ✅ ADMIN only

### PUT `/api/branches/{id}`
**Cập nhật chi nhánh**

- **Auth**: ✅ ADMIN (cập nhật bất kỳ), MANAGER (chỉ cập nhật chi nhánh của mình)

### DELETE `/api/branches/{id}`
**Xóa chi nhánh (soft delete)**

- **Auth**: ✅ ADMIN only

### GET `/api/branches/{id}/statistics`
**Thống kê chi nhánh**

- **Auth**: ✅ ADMIN, MANAGER (chỉ xem chi nhánh của mình)

---

## User Management

> 📖 Chi tiết xem tại [Users Module](./modules/users.md)

### GET `/api/users`
**Lấy danh sách users**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/users/search`
**Tìm kiếm user**

- **Auth**: ✅ ADMIN, STAFF, MANAGER
- **Params**: `keyword=nguyen`

### GET `/api/users/role/{roleId}`
**Lọc user theo role**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### POST `/api/users/{id}/roles/{roleName}`
**Gán role cho user**

- **Auth**: ✅ ADMIN only

### PATCH `/api/users/batch/activate`
**Kích hoạt nhiều users**

- **Auth**: ✅ ADMIN only

### PATCH `/api/users/batch/deactivate`
**Vô hiệu hóa nhiều users**

- **Auth**: ✅ ADMIN only

### DELETE `/api/users/{id}`
**Xóa user (soft delete)**

- **Auth**: ✅ ADMIN only

---

## Service Management

> 📖 Chi tiết xem tại [Services Module](./modules/services.md)

### POST `/api/services`
**Tạo dịch vụ mới**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### PUT `/api/services/{id}`
**Cập nhật dịch vụ**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### DELETE `/api/services/{id}`
**Xóa dịch vụ (soft delete)**

- **Auth**: ✅ ADMIN only

---

## Promotion Management

> 📖 Chi tiết xem tại [Promotions Module](./modules/promotions.md)

### POST `/api/promotions`
**Tạo mã khuyến mãi**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/promotions`
**Lấy tất cả mã khuyến mãi**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### PUT `/api/promotions/{id}`
**Cập nhật mã khuyến mãi**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### PATCH `/api/promotions/{id}/activate`
**Kích hoạt mã khuyến mãi**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/promotions/{id}/usage`
**Xem thống kê sử dụng mã khuyến mãi**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

---

## Shipper Management

> 📖 Chi tiết xem tại [Shippers Module](./modules/shippers.md)

### POST `/api/shippers`
**Tạo shipper mới**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/shippers`
**Lấy danh sách shippers**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/shippers/{id}/statistics`
**Thống kê shipper**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### PATCH `/api/shippers/{id}/activate`
**Kích hoạt shipper**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

---

## Order Management

> 📖 Chi tiết xem tại [Orders Module](./modules/orders.md)

### GET `/api/orders`
**Lấy tất cả đơn hàng**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/orders/status/{status}`
**Lọc đơn hàng theo trạng thái**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/orders/search`
**Tìm kiếm đơn hàng**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### GET `/api/orders/branch/{branchId}`
**Lấy đơn hàng theo chi nhánh**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### PATCH `/api/orders/batch/status`
**Cập nhật trạng thái nhiều đơn hàng**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### DELETE `/api/orders/{id}`
**Xóa đơn hàng (soft delete)**

- **Auth**: ✅ ADMIN only

---

## Payment Management

> 📖 Chi tiết xem tại [Payments Module](./modules/payments.md)

### GET `/api/payments`
**Lấy tất cả thanh toán**

- **Auth**: ✅ ADMIN, STAFF

### GET `/api/payments/status/{status}`
**Lọc thanh toán theo trạng thái**

- **Auth**: ✅ ADMIN, STAFF

### PATCH `/api/payments/{id}/status`
**Cập nhật trạng thái thanh toán**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### POST `/api/payments/{id}/refund`
**Hoàn tiền**

- **Auth**: ✅ ADMIN, MANAGER

---

## Notification Management

> 📖 Chi tiết xem tại [Notifications Module](./modules/notifications.md)

### GET `/api/notifications`
**Lấy tất cả notifications**

- **Auth**: ✅ ADMIN, STAFF, MANAGER

### POST `/api/notifications`
**Tạo notification mới**

- **Auth**: ✅ ADMIN, STAFF

---

## Review Management

> 📖 Chi tiết xem tại [Reviews Module](./modules/reviews.md)

### GET `/api/reviews`
**Lấy tất cả đánh giá**

- **Auth**: ✅ ADMIN, STAFF

### DELETE `/api/reviews/{id}`
**Xóa đánh giá (soft delete)**

- **Auth**: ✅ ADMIN only

---

## Audit Logs

> 📖 Chi tiết xem tại [Audit Logs Module](./modules/audit-logs.md)

### GET `/api/audit-logs`
**Lấy nhật ký hoạt động**

- **Auth**: ✅ ADMIN only

### GET `/api/audit-logs/user/{userId}`
**Xem nhật ký của user**

- **Auth**: ✅ ADMIN only

### GET `/api/audit-logs/entity/{entityType}/{entityId}`
**Xem nhật ký của entity**

- **Auth**: ✅ ADMIN only

---

## Soft Delete Management

### GET `/api/soft-delete/{entity}`
**Xem các bản ghi đã xóa**

- **Auth**: ✅ ADMIN only
- **Entity**: `users`, `orders`, `services`, `branches`, etc.

### PATCH `/api/soft-delete/{entity}/{id}/restore`
**Khôi phục bản ghi đã xóa**

- **Auth**: ✅ ADMIN only

### DELETE `/api/soft-delete/{entity}/{id}/permanent`
**Xóa vĩnh viễn**

- **Auth**: ✅ ADMIN only

---

[← Back to Main Documentation](./README.md)
