# 🧺 Washify - Laundry Management System

## 📋 Tổng quan Database

Hệ thống quản lý giặt là với **17 bảng**, hỗ trợ **Soft Delete** cho 6 entity chính, **AOP Audit Logging** tự động với 30+ operations, và tích hợp thanh toán **MoMo**.

### 🗄️ Danh sách bảng:
1. **branches** - Chi nhánh (Soft Delete ✅)
2. **users** - Người dùng (Soft Delete ✅)
3. **roles** - Vai trò người dùng
4. **user_roles** - Bảng nối Users ↔ Roles
5. **services** - Dịch vụ giặt là (Soft Delete ✅)
6. **orders** - Đơn hàng (Soft Delete ✅)
7. **order_items** - Chi tiết đơn hàng
8. **payments** - Thanh toán (CASH, MOMO)
9. **shippers** - Shipper (Soft Delete ✅)
10. **shipments** - Vận chuyển
11. **reviews** - Đánh giá
12. **promotions** - Khuyến mãi (Soft Delete ✅)
13. **order_promotions** - Bảng nối Orders ↔ Promotions
14. **notifications** - Thông báo
15. **audit_log** - Nhật ký hoạt động (AOP)
16. **attachments** - File đính kèm
17. **password_*_tokens** - 3 bảng token (reset, change, 2FA)

---

## 1. 👤 Quản lý người dùng & phân quyền
**Bảng liên quan:** `users`, `roles`, `user_roles`, `password_reset_tokens`, `password_change_tokens`, `password_change_2fa_tokens`

### Chức năng:
- **Đăng ký & Đăng nhập đa phương thức**:
  - Đăng nhập bằng username / email / phone
  - Mật khẩu mã hóa với BCrypt
  - JWT token authentication
- **Guest User Flow**:
  - Admin tạo tài khoản Guest với password tạm
  - Guest bắt buộc đổi password lần đầu đăng nhập
  - Có thể bật xác thực 2 lớp (email verification) khi đổi password
- **Reset Password**:
  - Gửi token qua email
  - Token có thời hạn và chỉ dùng 1 lần
- **Phân quyền người dùng**:
  - **ADMIN**: Quản trị toàn hệ thống
  - **STAFF**: Nhân viên xử lý đơn hàng
  - **CUSTOMER**: Khách hàng
  - **GUEST**: Tài khoản tạm thời
- **Soft Delete**:
  - Xóa mềm user, có thể khôi phục
  - Xóa vĩnh viễn sau khi xác nhận
- **Quản trị viên (Admin)**:
  - Thêm / sửa / xóa tài khoản
  - Cấp hoặc thu hồi quyền
  - Tạm khóa / kích hoạt người dùng
  - Gán nhân viên vào chi nhánh

---

## 2. 🧼 Quản lý dịch vụ giặt ủi & Chi nhánh
**Bảng liên quan:** `services`, `branches`, `promotions`

### A. Quản lý Dịch vụ (Services)
- Danh sách dịch vụ:
  - Giặt khô, giặt ướt, là ủi, giặt hấp, ...
  - Tính giá theo kg hoặc theo món
- Thêm / sửa / xóa dịch vụ **(Soft Delete ✅)**
- Đặt giá, thời gian xử lý dự kiến
- Kích hoạt / vô hiệu hóa dịch vụ
- **Audit**: CREATE_SERVICE, UPDATE_SERVICE, DELETE_SERVICE, RESTORE_SERVICE, PERMANENT_DELETE_SERVICE

### B. Quản lý Chi nhánh (Branches)
- Tạo / sửa / xóa chi nhánh **(Soft Delete ✅)**
- Thông tin: tên, địa chỉ, số điện thoại, tên quản lý
- Gán nhân viên vào chi nhánh
- Theo dõi đơn hàng theo chi nhánh
- **Audit**: RESTORE_BRANCH, PERMANENT_DELETE_BRANCH

### C. Quản lý Khuyến mãi (Promotions)
- Tạo mã giảm giá **(Soft Delete ✅)**:
  - **PERCENT**: Giảm theo % (VD: 20%)
  - **FIXED**: Giảm giá cố định (VD: 50.000đ)
- Giới hạn thời gian áp dụng (start_date → end_date)
- Kích hoạt / vô hiệu hóa mã
- Theo dõi số lần sử dụng mã
- Validate mã khi khách đặt hàng
- **Audit**: CREATE_PROMOTION, UPDATE_PROMOTION, DELETE_PROMOTION, ACTIVATE_PROMOTION, DEACTIVATE_PROMOTION, RESTORE_PROMOTION, PERMANENT_DELETE_PROMOTION

---

## 3. 📦 Quản lý đơn hàng (Orders)
**Bảng liên quan:** `orders`, `order_items`, `shipments`, `order_promotions`

### Chức năng:
- **Tạo đơn hàng mới** **(Soft Delete ✅)**:
  - Tạo online hoặc tại quầy
  - Tự động sinh mã đơn hàng duy nhất (VD: **WF202510210001**)
  - Thêm nhiều dịch vụ vào đơn (`order_items`)
  - Áp dụng mã khuyến mãi (có thể nhiều mã)
  - Tính tổng tiền tự động
- **Theo dõi trạng thái**:
  - **PENDING**: Chờ xử lý
  - **IN_PROGRESS**: Đang xử lý
  - **COMPLETED**: Hoàn thành
  - **CANCELLED**: Đã hủy
- **Quản lý vận chuyển** (Shipments):
  - Giao tận nhà hoặc khách tự nhận
  - Gán shipper cho đơn hàng
  - Theo dõi trạng thái giao hàng:
    - PENDING → SHIPPING → DELIVERED / CANCELLED
  - Cache thông tin shipper (name, phone) để tra cứu nhanh
- **Ghi chú & Attachments**:
  - Lưu ghi chú đặc biệt cho đơn
  - Đính kèm ảnh hoá đơn, biên nhận
- **Audit**: CREATE_ORDER, UPDATE_ORDER_STATUS, CANCEL_ORDER, RESTORE_ORDER, PERMANENT_DELETE_ORDER

---

## 4. 💳 Quản lý thanh toán (Payments)
**Bảng liên quan:** `payments`

### Chức năng:
- **Ghi nhận thanh toán** (1 order = 1 payment):
  - Tự động tạo payment khi tạo order
  - Liên kết chặt chẽ với order (1-1 relationship)
- **Phương thức thanh toán**:
  - **CASH**: Tiền mặt (tại quầy / COD)
  - **MOMO**: Ví điện tử MoMo
    - Tạo payment URL
    - Generate QR code
    - Lưu transaction_id
    - Lưu gateway_response (JSON)
- **Trạng thái thanh toán**:
  - **PENDING**: Chờ thanh toán
  - **PAID**: Đã thanh toán
  - **FAILED**: Thanh toán thất bại
- **Webhook & Callback**:
  - Nhận notification từ MoMo
  - Tự động cập nhật trạng thái
  - Gửi thông báo cho khách hàng
- **Audit**: CREATE_PAYMENT, UPDATE_PAYMENT_STATUS

---

## 5. 🚚 Quản lý Shipper & Vận chuyển
**Bảng liên quan:** `shippers`, `shipments`

### A. Quản lý Shipper
- Tạo / sửa / xóa shipper **(Soft Delete ✅)**
- Thông tin: tên, SĐT, biển số xe
- Kích hoạt / vô hiệu hóa shipper
- Thống kê hiệu suất:
  - Tổng số đơn giao
  - Đơn hoàn thành
  - Đơn đang giao
- **Audit**: CREATE_SHIPPER, UPDATE_SHIPPER, DELETE_SHIPPER, ACTIVATE_SHIPPER, DEACTIVATE_SHIPPER, RESTORE_SHIPPER, PERMANENT_DELETE_SHIPPER

### B. Quản lý Vận chuyển (Shipments)
- Gán shipper cho đơn hàng
- Theo dõi trạng thái giao hàng
- Cập nhật địa chỉ giao hàng
- Ghi nhận thời gian giao thành công
- Cache thông tin shipper để tra cứu nhanh

---

## 6. 🌟 Đánh giá & phản hồi (Reviews)
**Bảng liên quan:** `reviews`

### Chức năng:
- Khách hàng đánh giá sau khi hoàn tất đơn
- Mỗi đánh giá bao gồm:
  - Điểm sao (1–5)
  - Nội dung comment
  - Ngày tạo
- Hiển thị đánh giá trên chi tiết dịch vụ
- Thống kê rating trung bình
- Nhân viên / Admin:
  - Xem tất cả đánh giá
  - Phản hồi đánh giá
  - Ẩn đánh giá vi phạm (nếu cần)

---

## 7. 🔔 Hệ thống thông báo (Notifications)
**Bảng liên quan:** `notifications`

### Chức năng:
- **Gửi thông báo real-time** cho người dùng:
  - "Đơn hàng #WF202510210001 đã hoàn tất"
  - "Shipper đang trên đường giao hàng"
  - "Thanh toán thành công 150.000đ"
  - "Khuyến mãi 20% dịp cuối tuần"
- **Phân loại thông báo**:
  - `type`: order, payment, shipment, promotion, system
  - `related_id`: ID của entity liên quan
- **Quản lý trạng thái**:
  - Đánh dấu đã đọc/chưa đọc
  - Ghi nhận thời gian đọc
  - Lọc thông báo theo loại
  - Xóa thông báo cũ

---

## 8. 📊 Hệ thống Audit Log (Nhật ký hoạt động)
**Bảng liên quan:** `audit_log`

### Chức năng:
- **Ghi log tự động với AOP** - Không cần code thủ công!
  - Sử dụng @Audited annotation
  - Tự động intercept mọi thao tác quan trọng
  - Serialize entity thành JSON (old_value, new_value)
  
### 📋 **30+ Operations được audit tự động**:

#### OrderService (3 operations):
- CREATE_ORDER
- UPDATE_ORDER_STATUS
- CANCEL_ORDER

#### UserService (3 operations):
- UPDATE_USER
- DELETE_USER
- ASSIGN_ROLE

#### PaymentService (2 operations):
- CREATE_PAYMENT
- UPDATE_PAYMENT_STATUS

#### PromotionService (5 operations):
- CREATE_PROMOTION
- UPDATE_PROMOTION
- DELETE_PROMOTION
- ACTIVATE_PROMOTION
- DEACTIVATE_PROMOTION

#### ShipperService (5 operations):
- CREATE_SHIPPER
- UPDATE_SHIPPER
- DELETE_SHIPPER
- ACTIVATE_SHIPPER
- DEACTIVATE_SHIPPER

#### SoftDeleteService (12 operations):
- RESTORE_USER, PERMANENT_DELETE_USER
- RESTORE_BRANCH, PERMANENT_DELETE_BRANCH
- RESTORE_SERVICE, PERMANENT_DELETE_SERVICE
- RESTORE_ORDER, PERMANENT_DELETE_ORDER
- RESTORE_PROMOTION, PERMANENT_DELETE_PROMOTION
- RESTORE_SHIPPER, PERMANENT_DELETE_SHIPPER

### 🔐 **Thông tin được ghi nhận**:
- **user_id**: Ai thực hiện
- **entity_type**: Entity nào (Order, User, Payment, ...)
- **entity_id**: ID của entity
- **action**: Hành động gì (CREATE, UPDATE, DELETE, ...)
- **old_value**: Giá trị cũ (JSON)
- **new_value**: Giá trị mới (JSON)
- **ip_address**: IP của client (IPv4/IPv6, xử lý proxy)
- **user_agent**: Browser/Device info
- **description**: Mô tả chi tiết
- **status**: SUCCESS / FAILED
- **error_message**: Lỗi nếu có
- **created_at**: Thời gian thực hiện

### 🎯 **Use Cases**:
- **Bảo mật**: Ai đã xóa đơn hàng? Ai thay đổi giá dịch vụ?
- **Tuân thủ**: GDPR compliance, audit trail cho cơ quan quản lý
- **Giám sát**: Phát hiện hành vi bất thường (nhiều DELETE từ cùng IP)
- **Debug**: Trace lại lịch sử thay đổi để tìm nguyên nhân lỗi
- **Phân tích**: Thống kê operations theo user, theo thời gian

---

## 9. 📊 Báo cáo & Thống kê
**Tổng hợp từ nhiều bảng:** `orders`, `payments`, `services`, `branches`, `audit_log`

### Chức năng:
- **Báo cáo doanh thu**:
  - Theo ngày, tuần, tháng, năm
  - Theo chi nhánh
  - Theo loại dịch vụ
  - Theo phương thức thanh toán
- **Thống kê đơn hàng**:
  - Số lượng đơn theo trạng thái
  - Tỷ lệ hoàn thành / hủy
  - Thời gian xử lý trung bình
- **Thống kê khách hàng**:
  - Số khách hàng mới
  - Khách hàng trung thành (số đơn > X)
  - Dịch vụ phổ biến
- **Thống kê hiệu suất**:
  - Năng suất nhân viên
  - Năng suất shipper
  - So sánh chi nhánh
- **Audit Analytics**:
  - Số lượng operations theo user
  - Phát hiện hành vi bất thường
  - IP address phổ biến

---

## 🛡️ Tính năng nâng cao

### 1. **Soft Delete System** ✅
- **6 entity hỗ trợ Soft Delete**:
  - users, branches, services, orders, promotions, shippers
- **Cơ chế**:
  - `@SQLDelete`: UPDATE deleted_at = NOW(), is_active = 0
  - `@Where`: Chỉ query record có deleted_at IS NULL
- **SoftDeleteService**:
  - restore*(): Khôi phục từ thùng rác
  - permanentlyDelete*(): Xóa vĩnh viễn khỏi DB
  - Có thể list tất cả deleted records

### 2. **AOP Audit Logging** 🔍
- **Tự động** ghi log với @Audited annotation
- **30+ operations** từ 6 services
- Capture IP address, User Agent
- Serialize entity sang JSON
- Status tracking (SUCCESS/FAILED)

### 3. **MoMo Payment Integration** 💰
- Generate payment URL & QR code
- Webhook callback từ MoMo
- Lưu transaction_id & gateway_response
- Retry mechanism cho failed payments

### 4. **Guest User Flow** 👤
- Admin tạo Guest User với password tạm
- Bắt buộc đổi password lần đầu
- Có thể bật xác thực email 2 lớp

### 5. **Multi-method Login** 🔐
- Login bằng username / email / phone
- JWT token authentication
- Password reset qua email
- Token expiry & refresh

---

## 🚀 Roadmap mở rộng

### Short-term (1-3 tháng):
- [ ] Tích hợp VNPay, ZaloPay
- [ ] Realtime notification với WebSocket
- [ ] Export báo cáo Excel/PDF
- [ ] Đặt lịch pickup

### Medium-term (3-6 tháng):
- [ ] Mobile app (React Native / Flutter)
- [ ] Loyalty program (điểm tích lũy)
- [ ] AI recommendation (gợi ý dịch vụ)
- [ ] Multi-tenant support (franchise)

### Long-term (6-12 tháng):
- [ ] Machine Learning cho demand forecasting
- [ ] IoT integration (máy giặt thông minh)
- [ ] Blockchain cho supply chain tracking
- [ ] International expansion

---

## 📝 Ghi chú kỹ thuật

### Database Design Principles:
- **Normalization**: Đạt 3NF, tránh data redundancy
- **Indexing**: Index trên foreign keys, deleted_at, status fields
- **Soft Delete**: Dùng @SQLDelete + @Where của Hibernate
- **Audit Trail**: Mọi thay đổi quan trọng đều được log
- **Data Integrity**: Foreign key constraints với ON DELETE CASCADE

### Performance Optimization:
- **Lazy Loading**: Fetch chỉ khi cần (FetchType.LAZY)
- **JSON Serialization**: @JsonIgnore để tránh circular reference
- **Caching**: Cache-able cho services, branches, roles
- **Pagination**: Phân trang cho danh sách lớn

### Security:
- **Password**: BCrypt với salt
- **JWT**: Token-based authentication
- **IP Tracking**: Ghi nhận IP trong audit log
- **2FA**: Optional email verification
- **Role-based Access Control**: @PreAuthorize với Spring Security

---

> 💡 **Tổng kết:**  
> Database hiện tại có **17 bảng**, hỗ trợ đầy đủ nghiệp vụ giặt là từ quản lý người dùng, đơn hàng, thanh toán, vận chuyển đến audit logging và thống kê. Thiết kế linh hoạt, dễ mở rộng, và tuân thủ các nguyên tắc database design tốt nhất.
