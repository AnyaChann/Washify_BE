# 🧺 Laundry Management System — Chức năng chính

## 1. 👤 Quản lý người dùng & phân quyền
**Bảng liên quan:** `Users`, `Roles`, `User_Roles`

### Chức năng:
- Đăng ký / đăng nhập / đăng xuất người dùng
- Quản lý hồ sơ cá nhân (tên, số điện thoại, địa chỉ, ...)
- Phân quyền người dùng (Admin, Staff, Customer)
- Quản trị viên (Admin) có thể:
  - Thêm / sửa / xóa tài khoản nhân viên
  - Cấp hoặc thu hồi quyền của tài khoản
  - Tạm khóa / kích hoạt lại người dùng

---

## 2. 🧼 Quản lý dịch vụ giặt ủi
**Bảng liên quan:** `Services`, `Promotions`, `Branches`

### Chức năng:
- Hiển thị danh sách dịch vụ (giặt khô, giặt ướt, ủi, theo kg, theo món, ...)
- Thêm mới, chỉnh sửa, hoặc xóa dịch vụ
- Đặt giá, thời gian xử lý dự kiến cho từng dịch vụ
- Quản lý chi nhánh (tên, địa chỉ, hotline, dịch vụ cung cấp)
- Quản lý khuyến mãi:
  - Tạo mã giảm giá (theo % hoặc giá trị cố định)
  - Giới hạn số lượt hoặc thời gian sử dụng mã

---

## 3. 📦 Quản lý đơn hàng (Orders)
**Bảng liên quan:** `Orders`, `Order_Items`, `Shipments`

### Chức năng:
- Tạo đơn hàng mới cho khách hàng (online hoặc tại quầy)
- Thêm nhiều loại dịch vụ vào cùng một đơn (`Order_Items`)
- Theo dõi trạng thái đơn hàng:
  - Đang nhận → Đang xử lý → Hoàn tất → Đã giao → Đã hủy
- Tính tổng tiền tự động (theo đơn giá + số lượng)
- Quản lý vận chuyển:
  - Giao tận nhà hoặc khách tự nhận
  - Theo dõi trạng thái giao hàng (`Shipments`)

---

## 4. 💳 Quản lý thanh toán (Payments)
**Bảng liên quan:** `Payments`

### Chức năng:
- Ghi nhận thanh toán cho từng đơn hàng (1–1)
- Hỗ trợ nhiều hình thức thanh toán:
  - Tiền mặt, chuyển khoản, ví điện tử
- Ghi log lịch sử thanh toán (thời gian, trạng thái)
- Cập nhật trạng thái: “Đã thanh toán”, “Chờ xử lý”, “Hoàn tiền”

---

## 5. 🌟 Đánh giá & phản hồi (Reviews)
**Bảng liên quan:** `Reviews`

### Chức năng:
- Khách hàng có thể đánh giá đơn hàng sau khi hoàn tất
- Mỗi đánh giá bao gồm:
  - Điểm sao (1–5), nội dung, ngày tạo
- Nhân viên / Admin có thể phản hồi hoặc ẩn đánh giá tiêu cực

---

## 6. 🔔 Thông báo & ghi log hệ thống
**Bảng liên quan:** `Notifications`, `Audit_Log`

### Chức năng:
- Gửi thông báo cho người dùng:
  - “Đơn hàng #123 đã hoàn tất”
  - “Khuyến mãi 20% dịp cuối tuần”
- Ghi log hoạt động hệ thống (`Audit_Log`):
  - Ai đã tạo / sửa / xóa dữ liệu gì và khi nào
  - Dùng để giám sát và tăng cường bảo mật

---

## 7. 📊 Báo cáo & Thống kê
**Tổng hợp từ nhiều bảng:** `Orders`, `Payments`, `Services`, `Branches`

### Chức năng:
- Báo cáo doanh thu theo:
  - Ngày, tuần, tháng, chi nhánh, loại dịch vụ
- Thống kê:
  - Số lượng đơn hàng, khách hàng, dịch vụ phổ biến
- Biểu đồ hiệu suất:
  - So sánh năng suất nhân viên / chi nhánh theo thời gian

---

> 🧠 **Ghi chú:**  
> Cấu trúc trên dựa theo mô hình DB hiện tại (với các bảng: `Users`, `Roles`, `Orders`, `Order_Items`, `Payments`, `Shipments`, `Reviews`, `Promotions`, `Branches`, `Notifications`, `Audit_Log`).  
> Khi mở rộng tính năng (ví dụ: đặt lịch pickup, gợi ý dịch vụ, thống kê AI, ...), có thể thêm bảng phụ mà không phá vỡ quan hệ hiện có.
