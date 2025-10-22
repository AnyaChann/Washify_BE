# 🔄 Status Flows

## Order Status Flow

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

    ↓ (Có thể CANCEL ở bất kỳ bước nào trước IN_PROGRESS)
CANCELLED       → Đã hủy
```

### Status Details

| Status | Description | Who Can Update | Next Status |
|--------|-------------|---------------|-------------|
| `PENDING` | Đơn hàng mới tạo | System | CONFIRMED, CANCELLED |
| `CONFIRMED` | Staff đã xác nhận | STAFF, ADMIN, MANAGER | PICKED_UP, CANCELLED |
| `PICKED_UP` | Shipper đã lấy hàng | SHIPPER, STAFF | IN_PROGRESS |
| `IN_PROGRESS` | Đang giặt ủi | STAFF, ADMIN, MANAGER | READY |
| `READY` | Sẵn sàng giao | STAFF, ADMIN, MANAGER | DELIVERING |
| `DELIVERING` | Đang giao hàng | SHIPPER, STAFF | COMPLETED |
| `COMPLETED` | Hoàn thành | CUSTOMER, STAFF | - |
| `CANCELLED` | Đã hủy | CUSTOMER (if PENDING), STAFF, ADMIN | - |

---

## Payment Status Flow

```
PENDING         → Payment được tạo, chờ thanh toán
    ↓
COMPLETED       → Thanh toán thành công

FAILED          → Thanh toán thất bại

REFUNDED        → Đã hoàn tiền
```

### Status Details

| Status | Description | Triggers |
|--------|-------------|----------|
| `PENDING` | Chờ thanh toán | Payment gateway pending |
| `COMPLETED` | Thành công | Payment gateway callback success |
| `FAILED` | Thất bại | Payment gateway callback failed |
| `REFUNDED` | Đã hoàn tiền | Admin/Manager manual refund |

---

## Shipment Status Flow

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

### Status Details

| Status | Description | Who Can Update | Next Status |
|--------|-------------|---------------|-------------|
| `PENDING` | Chưa gán shipper | System | ASSIGNED |
| `ASSIGNED` | Đã gán shipper | STAFF, MANAGER | PICKED_UP, FAILED |
| `PICKED_UP` | Đã lấy hàng | SHIPPER | DELIVERING |
| `DELIVERING` | Đang giao | SHIPPER | DELIVERED, FAILED |
| `DELIVERED` | Giao thành công | SHIPPER | - |
| `FAILED` | Giao thất bại | SHIPPER, STAFF | ASSIGNED (retry) |

---

## User Status

| Status | Description | Can Login | Who Can Update |
|--------|-------------|-----------|---------------|
| `ACTIVE` | Hoạt động | ✅ Yes | ADMIN |
| `INACTIVE` | Vô hiệu hóa | ❌ No | ADMIN |
| `DELETED` | Đã xóa (soft delete) | ❌ No | ADMIN |

---

## Service/Branch Status

| Status | Description | Visible to Customer | Who Can Update |
|--------|-------------|---------------------|---------------|
| `ACTIVE` | Hoạt động | ✅ Yes | STAFF, ADMIN, MANAGER |
| `INACTIVE` | Vô hiệu hóa | ❌ No | STAFF, ADMIN, MANAGER |
| `DELETED` | Đã xóa (soft delete) | ❌ No | ADMIN |

---

## Promotion Status

| Status | Description | Can Use | Who Can Update |
|--------|-------------|---------|---------------|
| `ACTIVE` | Hoạt động | ✅ Yes (if within date range) | STAFF, ADMIN, MANAGER |
| `INACTIVE` | Vô hiệu hóa | ❌ No | STAFF, ADMIN, MANAGER |
| `EXPIRED` | Hết hạn | ❌ No | System (auto) |

---

## Notification Status

| Status | Description | Shows Badge | Who Can Update |
|--------|-------------|-------------|---------------|
| `UNREAD` | Chưa đọc | ✅ Yes | System |
| `READ` | Đã đọc | ❌ No | User (owner) |

---

[← Back to Main Documentation](../README.md)
