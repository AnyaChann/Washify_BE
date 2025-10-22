# 👨‍💼 Staff Portal API

## Quyền: `STAFF`

Staff có tất cả quyền của ADMIN/MANAGER về orders, nhưng tập trung vào xử lý đơn hàng và quản lý shipments.

---

## Table of Contents

1. [Order Processing](#order-processing)
2. [Shipment Management](#shipment-management)
3. [Customer Support](#customer-support)

---

## Order Processing

> 📖 Chi tiết xem tại [Orders Module](./modules/orders.md)

### PATCH `/api/orders/{id}/status`
**Cập nhật trạng thái đơn hàng**

- **Auth**: ✅ STAFF

**Order Status Flow:**
```
PENDING → CONFIRMED (Staff xác nhận)
    ↓
CONFIRMED → PICKED_UP (Shipper lấy hàng)
    ↓
PICKED_UP → IN_PROGRESS (Đang giặt)
    ↓
IN_PROGRESS → READY (Giặt xong, sẵn sàng giao)
    ↓
READY → DELIVERING (Đang giao)
    ↓
DELIVERING → COMPLETED (Đã giao)
```

---

### GET `/api/orders/status/PENDING`
**Xem đơn hàng chờ xác nhận**

- **Auth**: ✅ STAFF
- **Use Case**: Dashboard "Pending Orders" - Cần xác nhận ngay

---

### GET `/api/orders/status/IN_PROGRESS`
**Xem đơn hàng đang xử lý**

- **Auth**: ✅ STAFF
- **Use Case**: Tracking "In Progress Orders" - Đang giặt ủi

---

### GET `/api/orders/status/READY`
**Xem đơn hàng sẵn sàng giao**

- **Auth**: ✅ STAFF
- **Use Case**: "Ready for Delivery" - Cần gán shipper

---

## Shipment Management

> 📖 Chi tiết xem tại [Shipments Module](./modules/shipments.md)

### GET `/api/shipments`
**Xem tất cả shipments**

- **Auth**: ✅ STAFF

---

### POST `/api/shipments`
**Tạo shipment (gán shipper)**

- **Auth**: ✅ STAFF

**Request Body:**
```json
{
  "orderId": 1,
  "shipperId": 5,
  "type": "PICKUP",
  "scheduledTime": "2025-10-22T14:00:00"
}
```

**Types:**
- `PICKUP`: Lấy hàng từ khách
- `DELIVERY`: Giao hàng cho khách

---

### GET `/api/shipments/status/{status}`
**Lọc shipments theo trạng thái**

- **Auth**: ✅ STAFF

**Status:**
- `PENDING`: Chưa gán shipper
- `ASSIGNED`: Đã gán shipper
- `PICKED_UP`: Shipper đã lấy hàng
- `DELIVERING`: Đang giao hàng
- `DELIVERED`: Đã giao xong
- `FAILED`: Giao thất bại

---

### GET `/api/shipments/shipper/{shipperId}`
**Xem shipments của shipper**

- **Auth**: ✅ STAFF
- **Use Case**: Tracking công việc của từng shipper

---

### PATCH `/api/shipments/{id}/assign`
**Gán shipper**

- **Auth**: ✅ STAFF, MANAGER

**Request Body:**
```json
{
  "shipperId": 5
}
```

---

### PATCH `/api/shipments/{id}/status`
**Cập nhật trạng thái shipment**

- **Auth**: ✅ STAFF, MANAGER

---

### GET `/api/shipments/statistics`
**Thống kê shipments**

- **Auth**: ✅ STAFF, MANAGER

**Response:**
```json
{
  "success": true,
  "data": {
    "todayPickups": 25,
    "todayDeliveries": 20,
    "pendingPickups": 8,
    "pendingDeliveries": 5,
    "completedToday": 15
  }
}
```

---

## Customer Support

### GET `/api/users/{id}`
**Xem thông tin khách hàng**

- **Auth**: ✅ STAFF
- **Use Case**: Tra cứu thông tin khi khách hỏi

---

### GET `/api/orders/user/{userId}`
**Xem lịch sử đơn hàng của khách**

- **Auth**: ✅ STAFF
- **Use Case**: Kiểm tra lịch sử mua hàng, xử lý khiếu nại

---

### GET `/api/reviews/user/{userId}`
**Xem đánh giá của khách**

- **Auth**: ✅ STAFF
- **Use Case**: Kiểm tra feedback của khách hàng

---

## Staff Daily Workflow

### 1. Morning Checklist
```
1. GET /api/orders/status/PENDING → Xác nhận đơn mới
2. GET /api/shipments/status/PENDING → Gán shipper cho pickups
3. GET /api/orders/status/IN_PROGRESS → Theo dõi tiến độ giặt
```

### 2. During Day
```
1. PATCH /api/orders/{id}/status → Cập nhật trạng thái đơn
2. POST /api/shipments → Tạo shipment cho deliveries
3. GET /api/shipments/statistics → Theo dõi tổng quan
```

### 3. End of Day
```
1. GET /api/orders/status/READY → Kiểm tra đơn chưa giao
2. GET /api/shipments/status/FAILED → Xử lý giao hàng thất bại
3. GET /api/orders/statistics → Báo cáo cuối ngày
```

---

[← Back to Main Documentation](./README.md)
