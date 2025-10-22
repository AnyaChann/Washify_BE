# 🚚 Shipper Mobile App API

## Quyền: `SHIPPER`

Shipper chỉ có quyền xem và cập nhật shipments được gán cho mình.

---

## Table of Contents

1. [Shipment Operations](#shipment-operations)
2. [Image Upload](#image-upload)
3. [Statistics](#statistics)
4. [Shipper Daily Workflow](#shipper-daily-workflow)

---

## Shipment Operations

### GET `/api/shipments/shipper/{shipperId}`
**Xem shipments được gán**

- **Auth**: ✅ SHIPPER (chỉ xem shipments của mình)

**Response:**
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

---

### GET `/api/shipments/{id}`
**Chi tiết shipment**

- **Auth**: ✅ SHIPPER (chỉ xem shipments của mình)

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "orderId": 123,
    "orderCode": "WF202510210001",
    "type": "PICKUP",
    "status": "ASSIGNED",
    "pickupAddress": "123 Nguyễn Huệ, Q1, TP.HCM",
    "deliveryAddress": "456 Lê Lợi, Q3, TP.HCM",
    "scheduledTime": "2025-10-22T14:00:00",
    "customerName": "Nguyễn Văn A",
    "customerPhone": "0901234567",
    "note": "Gọi trước 15 phút",
    "shipperInfo": {
      "id": 5,
      "name": "Shipper Name",
      "phone": "0987654321",
      "vehicleType": "MOTORBIKE",
      "vehicleNumber": "59A-12345"
    },
    "createdAt": "2025-10-21T10:00:00",
    "updatedAt": "2025-10-21T10:30:00"
  }
}
```

---

### PATCH `/api/shipments/{id}/status`
**Cập nhật trạng thái shipment**

- **Auth**: ✅ SHIPPER

**Shipment Status Flow:**
```
ASSIGNED → PICKED_UP (Đã lấy hàng)
    ↓
PICKED_UP → DELIVERING (Đang giao)
    ↓
DELIVERING → DELIVERED (Đã giao xong)
    ↓
Or → FAILED (Giao thất bại)
```

**Request:**
```
PATCH /api/shipments/1/status?status=PICKED_UP
```

**Response:**
```json
{
  "success": true,
  "message": "Cập nhật trạng thái shipment thành công",
  "data": {
    "id": 1,
    "status": "PICKED_UP",
    "updatedAt": "2025-10-22T14:05:00"
  }
}
```

---

## Image Upload

### POST `/api/shipments/{id}/pickup-image`
**Upload ảnh lấy hàng**

- **Auth**: ✅ SHIPPER
- **Request**: FormData with image file

**Request Example (JavaScript):**
```javascript
const formData = new FormData();
formData.append('image', imageFile);

fetch('/api/shipments/1/pickup-image', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token
  },
  body: formData
})
```

**Response:**
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

---

### POST `/api/shipments/{id}/delivery-image`
**Upload ảnh giao hàng**

- **Auth**: ✅ SHIPPER
- **Request**: FormData with image file
- **Use Case**: Chụp ảnh khi giao hàng thành công (proof of delivery)

---

### GET `/api/shipments/{id}/images`
**Xem ảnh của shipment**

- **Auth**: ✅ SHIPPER

**Response:**
```json
{
  "success": true,
  "data": {
    "shipmentId": 1,
    "pickupImages": [
      {
        "url": "https://storage.example.com/pickups/123-1.jpg",
        "uploadedAt": "2025-10-22T14:05:00"
      }
    ],
    "deliveryImages": [
      {
        "url": "https://storage.example.com/deliveries/123-1.jpg",
        "uploadedAt": "2025-10-22T16:30:00"
      }
    ]
  }
}
```

---

## Statistics

### GET `/api/shipments/statistics`
**Thống kê cá nhân của shipper**

- **Auth**: ✅ SHIPPER

**Response:**
```json
{
  "success": true,
  "data": {
    "todayPickups": 12,
    "todayDeliveries": 10,
    "completedToday": 8,
    "pendingPickups": 4,
    "pendingDeliveries": 2,
    "totalEarnings": 500000.00
  }
}
```

---

## Shipper Daily Workflow

### 1. Start Shift (Morning)
```
1. GET /api/shipments/shipper/{shipperId} → Xem tất cả shipments
2. GET /api/shipments/statistics → Kiểm tra tổng quan hôm nay
3. Filter shipments by status ASSIGNED → Danh sách cần lấy hàng
```

### 2. Pickup Process
```
1. Navigate to customer location (using address & GPS)
2. Call customer (customerPhone) 15 minutes before
3. Pickup items
4. POST /api/shipments/{id}/pickup-image → Upload ảnh lấy hàng
5. PATCH /api/shipments/{id}/status?status=PICKED_UP → Cập nhật trạng thái
```

### 3. Return to Store
```
1. Return items to branch
2. Staff updates order to IN_PROGRESS
3. Wait for items to be ready
```

### 4. Delivery Process
```
1. GET /api/shipments/shipper/{shipperId}?status=ASSIGNED&type=DELIVERY
2. Navigate to delivery address
3. Call customer 15 minutes before
4. Deliver items
5. POST /api/shipments/{id}/delivery-image → Upload ảnh giao hàng
6. PATCH /api/shipments/{id}/status?status=DELIVERED → Hoàn thành
```

### 5. End Shift (Evening)
```
1. GET /api/shipments/statistics → Kiểm tra tổng kết hôm nay
2. Report failed deliveries (if any)
```

---

## Mobile App Screens Suggestion

### 1. Home Screen
- Today's statistics
- Pending pickups count
- Pending deliveries count
- Total earnings today

### 2. Shipments List
- Tab: Pickups / Deliveries
- Filter by status: ASSIGNED, IN_PROGRESS, COMPLETED
- Card showing: Order code, customer name, address, scheduled time

### 3. Shipment Detail
- Customer info (name, phone, address)
- Order items preview
- Map navigation button
- Call customer button
- Upload image button
- Update status button

### 4. Camera Screen
- Capture pickup/delivery photo
- Preview and confirm
- Auto-upload

### 5. Profile
- Personal info
- Vehicle info
- Statistics (total deliveries, success rate, etc.)

---

## Error Handling for Shippers

### Common Errors

**1. Access Denied (403)**
```json
{
  "success": false,
  "message": "Bạn không có quyền xem shipment này"
}
```
- **Cause**: Trying to access another shipper's shipment
- **Solution**: Only access your own shipments

**2. Invalid Status Transition**
```json
{
  "success": false,
  "message": "Không thể chuyển từ ASSIGNED sang DELIVERED"
}
```
- **Cause**: Skipping status steps
- **Solution**: Follow the correct flow (ASSIGNED → PICKED_UP → DELIVERING → DELIVERED)

**3. Image Upload Failed**
```json
{
  "success": false,
  "message": "Kích thước ảnh quá lớn (max 10MB)"
}
```
- **Cause**: Image file too large
- **Solution**: Compress image before upload

---

[← Back to Main Documentation](./README.md)
