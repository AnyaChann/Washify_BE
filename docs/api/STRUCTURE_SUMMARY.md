# 📄 API Documentation Structure Summary

## ✅ Đã tách API documentation thành các module riêng biệt

### 📂 Cấu trúc mới:

```
docs/api/
├── README.md                          # Main index với quick start
├── 01-customer-web-app.md             # Priority 1: Customer endpoints
├── 02-admin-manager-dashboard.md      # Priority 2: Admin/Manager endpoints
├── 03-staff-portal.md                 # Priority 3: Staff endpoints
├── 04-shipper-mobile-app.md           # Priority 4: Shipper endpoints
└── references/
    ├── response-formats.md            # Success/Error response formats
    ├── status-flows.md                # Order/Payment/Shipment status flows
    ├── error-handling.md              # Frontend error handling guide
    └── testing-guide.md               # Postman test flows
```

---

## 🎯 Lợi ích của cấu trúc mới

### 1. **Phát triển tuần tự theo Priority**
- Priority 1: Customer Web App (Core features)
- Priority 2: Admin/Manager Dashboard (Management)
- Priority 3: Staff Portal (Operations)
- Priority 4: Shipper Mobile App (Logistics)

### 2. **Dễ navigate và maintain**
- Mỗi role có file riêng
- References tách biệt để tái sử dụng
- Table of contents rõ ràng

### 3. **Giảm token usage**
- Không cần load toàn bộ doc mỗi lần
- Chỉ đọc phần cần thiết
- Faster context loading

### 4. **Better collaboration**
- Frontend teams có thể làm song song
- Customer team → 01-customer-web-app.md
- Admin team → 02-admin-manager-dashboard.md
- Mobile team → 04-shipper-mobile-app.md

### 5. **Easier updates**
- Chỉ update file liên quan
- Không sợ conflict khi nhiều người edit
- Git diff rõ ràng hơn

---

## 📝 Cách sử dụng

### Cho Frontend Developer:

1. **Bắt đầu:** Đọc [docs/api/README.md](../README.md)
2. **Chọn role:** Navigate đến file tương ứng
3. **Chi tiết module:** Click vào link "📖 Chi tiết xem tại [Module]"
4. **References:** Tham khảo `references/` khi cần

### Ví dụ workflow:

**Scenario: Develop Customer Order Page**

```
1. Đọc docs/api/01-customer-web-app.md
   → Section: Orders
   
2. Implement:
   - GET /api/services (view services)
   - POST /api/orders (create order)
   - GET /api/orders/{id} (check status)
   
3. Tham khảo:
   - references/response-formats.md (error handling)
   - references/status-flows.md (order status flow)
   - references/testing-guide.md (Postman test)
```

---

## 🔄 Migration từ file cũ

### File cũ: `API_DOCUMENTATION.md`
- ✅ Giữ lại với redirect notice
- ✅ Nội dung cũ vẫn ở dưới (backup)
- ⚠️ Sẽ xóa sau khi frontend confirm migrate xong

### Action items:

**Cho Backend Team:**
1. ✅ Tách documentation thành modules
2. ✅ Tạo structure mới trong `docs/api/`
3. ✅ Update README với redirect
4. ⏳ Thông báo frontend team về thay đổi
5. ⏳ Sau khi frontend confirm → Xóa nội dung cũ trong `API_DOCUMENTATION.md`

**Cho Frontend Team:**
1. ⏳ Review structure mới
2. ⏳ Update bookmarks/links
3. ⏳ Confirm đã migrate → Báo backend team
4. ⏳ Update Postman collection structure

---

## 📚 Nội dung mỗi file

### [01-customer-web-app.md](./01-customer-web-app.md)
- Authentication & User Management
  * Register, Login, First-time password change
  * Profile management, Change password
- Services (Public)
- Branches (Public)
- Orders (CUSTOMER)
- Payments (CUSTOMER)
- Promotions (Public)
- Reviews (CUSTOMER)
- Notifications (CUSTOMER)

### [02-admin-manager-dashboard.md](./02-admin-manager-dashboard.md)
- Dashboard & Statistics
- Branch Management (MANAGER restricted to own branch)
- User Management
- Service Management
- Promotion Management
- Shipper Management
- Order Management
- Payment Management
- Notification Management
- Review Management
- Audit Logs (ADMIN only)
- Soft Delete Management (ADMIN only)

### [03-staff-portal.md](./03-staff-portal.md)
- Order Processing (Status updates)
- Shipment Management (Create, assign shipper)
- Customer Support (View history)
- Daily Workflow examples

### [04-shipper-mobile-app.md](./04-shipper-mobile-app.md)
- Shipment Operations (View, update status)
- Image Upload (Pickup/Delivery photos)
- Statistics (Personal performance)
- Mobile app screens suggestion
- Daily Workflow examples

### [references/response-formats.md](./references/response-formats.md)
- Success Response format
- Error Response format
- Validation Error format
- HTTP Status Codes
- Date/Time format
- Pagination (future)

### [references/status-flows.md](./references/status-flows.md)
- Order Status Flow (8 statuses)
- Payment Status Flow (4 statuses)
- Shipment Status Flow (6 statuses)
- User/Service/Branch/Promotion Status
- Notification Status

### [references/error-handling.md](./references/error-handling.md)
- Frontend error handling best practices
- Common error scenarios (401, 403, 400, 404, 409, 500)
- Loading states
- Request timeout
- Retry logic
- Client-side validation
- Error tracking (Sentry)

### [references/testing-guide.md](./references/testing-guide.md)
- Postman collection structure
- Sample test flows (Customer, Guest, Staff, Shipper)
- Environment variables
- Pre-request scripts
- Common test cases
- Performance testing
- Error scenarios

---

## 🔍 Search Tips

### Tìm endpoint theo method + path:
```
Example: "POST /api/orders"
→ Tìm trong 01-customer-web-app.md
```

### Tìm theo chức năng:
```
Example: "Create shipment"
→ Tìm trong 03-staff-portal.md
```

### Tìm theo role:
```
CUSTOMER → 01-customer-web-app.md
ADMIN → 02-admin-manager-dashboard.md
STAFF → 03-staff-portal.md
SHIPPER → 04-shipper-mobile-app.md
```

### Tìm status flow:
```
Example: "Order status"
→ references/status-flows.md
```

### Tìm error handling:
```
Example: "401 error"
→ references/error-handling.md
```

---

## 📞 Support

Có thắc mắc về documentation structure:
- Check [docs/api/README.md](./README.md) first
- Review references/ folder
- Contact backend team

---

**Created**: 2025-10-22  
**Structure Version**: 2.0  
**Backend**: Spring Boot 3.3.5  
**Database**: MySQL 8.0
