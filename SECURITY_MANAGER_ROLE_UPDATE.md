# Cập Nhật Hệ Thống Phân Quyền - Vai Trò MANAGER

## Tổng Quan
Đã bật lại hệ thống phân quyền Spring Security và thêm vai trò **MANAGER** vào các endpoint phù hợp.

## Thay Đổi Cấu Hình Bảo Mật

### SecurityConfig.java
- **Enabled Method Security**: `@EnableMethodSecurity(prePostEnabled = true)`
- **5 Roles được hỗ trợ**:
  1. `ADMIN` - Quản trị viên hệ thống (toàn quyền)
  2. `MANAGER` - Quản lý chi nhánh (quản lý chi nhánh và nhân viên)
  3. `STAFF` - Nhân viên (xử lý đơn hàng và dịch vụ)
  4. `SHIPPER` - Shipper (giao nhận đồ giặt)
  5. `CUSTOMER` - Khách hàng (sử dụng dịch vụ)

## Chi Tiết Cập Nhật Các Controller

### 1. BranchController
**Endpoints được thêm MANAGER**:
- ✅ `GET /api/branches/statistics` - Thống kê tất cả chi nhánh
- ✅ `GET /api/branches/{id}/statistics` - Thống kê chi tiết chi nhánh

**Quyền truy cập**: `ADMIN`, `STAFF`, `MANAGER`

---

### 2. OrderController
**Endpoints được thêm MANAGER**:
- ✅ `GET /api/orders/statistics` - Thống kê tổng quan orders
- ✅ `GET /api/orders/statistics/revenue` - Thống kê doanh thu
- ✅ `GET /api/orders/statistics/top-customers` - Top customers
- ✅ `GET /api/orders/search` - Tìm kiếm orders
- ✅ `GET /api/orders/branch/{branchId}` - Orders theo chi nhánh
- ✅ `GET /api/orders/date-range` - Orders theo khoảng thời gian
- ✅ `PATCH /api/orders/batch/status` - Cập nhật status nhiều orders

**Quyền truy cập**: `ADMIN`, `STAFF`, `MANAGER`

**Lợi ích cho MANAGER**:
- Xem và quản lý orders trong chi nhánh
- Theo dõi doanh thu và hiệu suất
- Phân tích khách hàng tiềm năng

---

### 3. UserController
**Endpoints được thêm MANAGER**:
- ✅ `GET /api/users` - Danh sách tất cả users
- ✅ `GET /api/users/email/{email}` - Tìm user theo email
- ✅ `GET /api/users/search` - Tìm kiếm users
- ✅ `GET /api/users/role/{roleId}` - Users theo role
- ✅ `GET /api/users/active` - Users đang hoạt động

**Quyền truy cập**: `ADMIN`, `STAFF`, `MANAGER`

**Lợi ích cho MANAGER**:
- Xem thông tin nhân viên trong chi nhánh
- Tìm kiếm và quản lý danh sách nhân viên
- Theo dõi trạng thái hoạt động của nhân viên

---

### 4. ShipmentController
**Endpoints được thêm MANAGER**:
- ✅ `GET /api/shipments/status/{status}` - Shipments theo status
- ✅ `PATCH /api/shipments/{id}/status` - Cập nhật status shipment
- ✅ `PATCH /api/shipments/{id}/assign-shipper` - Gán shipper
- ✅ `GET /api/shipments/statistics` - Thống kê shipments

**Quyền truy cập**: `ADMIN`, `STAFF`, `MANAGER`

**Lợi ích cho MANAGER**:
- Quản lý vận chuyển trong chi nhánh
- Gán shipper cho đơn hàng
- Theo dõi hiệu suất giao hàng

---

### 5. ServiceController
**Endpoints được thêm MANAGER**:
- ✅ `POST /api/services` - Tạo dịch vụ mới
- ✅ `PUT /api/services/{id}` - Cập nhật dịch vụ
- ✅ `DELETE /api/services/{id}` - Xóa dịch vụ

**Quyền truy cập**: `ADMIN`, `STAFF`, `MANAGER`

**Lợi ích cho MANAGER**:
- Quản lý danh sách dịch vụ trong chi nhánh
- Cập nhật giá và thông tin dịch vụ
- Tối ưu hóa bảng giá dịch vụ

---

### 6. PaymentController
**Endpoints được thêm MANAGER**:
- ✅ `GET /api/payments/statistics` - Thống kê thanh toán
- ✅ `GET /api/payments/method/{method}` - Payments theo phương thức
- ✅ `GET /api/payments/date-range` - Payments theo khoảng thời gian

**Quyền truy cập**: `ADMIN`, `STAFF`, `MANAGER`

**Lợi ích cho MANAGER**:
- Theo dõi doanh thu và thanh toán
- Phân tích phương thức thanh toán phổ biến
- Báo cáo tài chính chi nhánh

---

## Phạm Vi Vai Trò MANAGER

### Quyền được cấp ✅
1. **Xem thống kê và báo cáo**:
   - Thống kê chi nhánh, orders, doanh thu
   - Báo cáo shipments, payments
   - Top customers

2. **Quản lý nhân viên**:
   - Xem danh sách nhân viên
   - Tìm kiếm và lọc users
   - Theo dõi hoạt động nhân viên

3. **Quản lý đơn hàng**:
   - Xem và tìm kiếm orders
   - Cập nhật status orders
   - Quản lý orders theo chi nhánh

4. **Quản lý dịch vụ**:
   - Tạo/cập nhật/xóa dịch vụ
   - Quản lý bảng giá

5. **Quản lý vận chuyển**:
   - Xem và cập nhật shipments
   - Gán shipper cho đơn hàng
   - Theo dõi tiến độ giao hàng

### Quyền KHÔNG có ❌
1. **User Management**:
   - Xóa users (chỉ ADMIN - soft delete)
   - Xóa vĩnh viễn users (chỉ ADMIN - hard delete)
   - Gán roles (chỉ ADMIN)
   - Kích hoạt/vô hiệu hóa users hàng loạt (chỉ ADMIN)

2. **Branch Management**:
   - Tạo/xóa chi nhánh (chỉ ADMIN)
   - ✅ **CẬP NHẬT**: MANAGER có quyền cập nhật chi nhánh của họ

3. **System Configuration**:
   - Cấu hình hệ thống (chỉ ADMIN)

---

## Kiểm Tra và Testing

### Manual Testing Steps
1. **Đăng nhập với MANAGER account**:
   ```bash
   POST /api/auth/login
   {
     "username": "manager_username",
     "password": "manager_password"
   }
   ```

2. **Test Branch Statistics**:
   ```bash
   GET /api/branches/statistics
   Authorization: Bearer <manager_token>
   # Expected: 200 OK với danh sách thống kê
   ```

3. **Test Order Management**:
   ```bash
   GET /api/orders/statistics
   Authorization: Bearer <manager_token>
   # Expected: 200 OK với thống kê orders
   ```

4. **Test User Search**:
   ```bash
   GET /api/users/search?roleId=3
   Authorization: Bearer <manager_token>
   # Expected: 200 OK với danh sách staff
   ```

5. **Test Service Management**:
   ```bash
   POST /api/services
   Authorization: Bearer <manager_token>
   {
     "name": "Test Service",
     "description": "Test",
     "price": 50000,
     "isActive": true
   }
   # Expected: 201 Created
   ```

### Expected Behaviors
- ✅ **MANAGER có quyền**: Response 200/201 với dữ liệu
- ❌ **MANAGER không có quyền**: Response 403 Forbidden
- ❌ **Không có token**: Response 401 Unauthorized

---

## Database Roles

Trong `data.sql`:
```sql
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Quản trị viên hệ thống - Toàn quyền quản lý'),
('MANAGER', 'Quản lý chi nhánh - Quản lý chi nhánh và nhân viên'),
('STAFF', 'Nhân viên - Xử lý đơn hàng và dịch vụ'),
('SHIPPER', 'Shipper - Giao nhận đồ giặt'),
('CUSTOMER', 'Khách hàng - Sử dụng dịch vụ giặt ủi');
```

---

## Next Steps

### 1. Branch-Scoped Authorization (Optional)
Hiện tại MANAGER có quyền xem tất cả chi nhánh. Nếu muốn giới hạn MANAGER chỉ xem chi nhánh của mình:

```java
@PreAuthorize("hasRole('MANAGER') and @branchSecurity.isBranchManager(#branchId, authentication)")
```

### 2. Create Test Users
Tạo test users cho mỗi role trong `data.sql` để test.

### 3. API Documentation
Cập nhật Swagger annotations để phản ánh role MANAGER.

### 4. Integration Tests
Viết integration tests để verify security configuration:
```java
@Test
@WithMockUser(roles = "MANAGER")
void testManagerCanAccessBranchStatistics() {
    // Test implementation
}
```

---

## Files Changed

1. ✅ `SecurityConfig.java` - Enabled method security
2. ✅ `BranchController.java` - Added MANAGER to statistics endpoints
3. ✅ `OrderController.java` - Added MANAGER to management and statistics endpoints
4. ✅ `UserController.java` - Added MANAGER to user search and list endpoints
5. ✅ `ShipmentController.java` - Added MANAGER to shipment management endpoints
6. ✅ `ServiceController.java` - Added MANAGER to service CRUD operations
7. ✅ `PaymentController.java` - Added MANAGER to payment statistics endpoints

---

## Security Best Practices

1. **Method-level Security**: Sử dụng `@PreAuthorize` cho từng endpoint
2. **Role Hierarchy**: ADMIN > MANAGER > STAFF > SHIPPER/CUSTOMER
3. **Least Privilege**: MANAGER chỉ có quyền cần thiết cho công việc
4. **Audit Logging**: Xem xét thêm audit log cho các thao tác quan trọng

---

## Contact
- **Created**: 2025-01-XX
- **Last Updated**: 2025-01-XX
- **Status**: ✅ Ready for Testing
