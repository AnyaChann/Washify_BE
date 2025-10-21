-- =============================================
-- WASHIFY SEED DATA
-- Dữ liệu mẫu ban đầu cho hệ thống Washify
-- Khớp với cấu trúc JPA Entities
-- CHỈ CHẠY KHI DATABASE TRỐNG
-- =============================================
--
-- THÔNG TIN HỆ THỐNG:
-- - Payment Methods: CASH (Tiền mặt/Tại quầy/COD), MOMO (MoMo Wallet)
-- - Roles: ADMIN, MANAGER, STAFF, SHIPPER, CUSTOMER, GUEST
-- - Guest User System: Tự động tạo khi Staff nhập SĐT chưa có
-- - Order Code Format: WF + YYYYMMDD + 4-digit ID (VD: WF202510210001)
--
-- MẬT KHẨU MẶC ĐỊNH (từ application.properties):
-- - Admin/Manager/Staff/Shipper/Customer: app.default-password = "washify123"
-- - Guest Users: guest.default-password = "Guest@123456"
--
-- =============================================

-- Kiểm tra xem database đã có dữ liệu chưa
-- Nếu đã có dữ liệu thì không chạy script này
SET @role_count = (SELECT COUNT(*) FROM roles);

-- Chỉ thực hiện khi database trống
-- =============================================
-- XÓA DỮ LIỆU CŨ (NẾU CÓ)
-- =============================================
-- Tắt foreign key check tạm thời
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu theo thứ tự ngược (từ child đến parent)
DELETE FROM notifications WHERE @role_count > 0;
DELETE FROM reviews WHERE @role_count > 0;
DELETE FROM shipments WHERE @role_count > 0;
DELETE FROM payments WHERE @role_count > 0;
DELETE FROM order_items WHERE @role_count > 0;
DELETE FROM order_promotions WHERE @role_count > 0;
DELETE FROM orders WHERE @role_count > 0;
DELETE FROM shippers WHERE @role_count > 0;
DELETE FROM promotions WHERE @role_count > 0;
DELETE FROM services WHERE @role_count > 0;
DELETE FROM user_roles WHERE @role_count > 0;
DELETE FROM users WHERE @role_count > 0;
DELETE FROM branches WHERE @role_count > 0;
DELETE FROM roles WHERE @role_count > 0;

-- Reset AUTO_INCREMENT
ALTER TABLE roles AUTO_INCREMENT = 1;
ALTER TABLE branches AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE services AUTO_INCREMENT = 1;
ALTER TABLE promotions AUTO_INCREMENT = 1;
ALTER TABLE shippers AUTO_INCREMENT = 1;
ALTER TABLE orders AUTO_INCREMENT = 1;
ALTER TABLE order_items AUTO_INCREMENT = 1;
ALTER TABLE payments AUTO_INCREMENT = 1;
ALTER TABLE shipments AUTO_INCREMENT = 1;
ALTER TABLE reviews AUTO_INCREMENT = 1;
ALTER TABLE notifications AUTO_INCREMENT = 1;

-- Bật lại foreign key check
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 1. ROLES (Vai trò người dùng)
-- Columns: id, name, description
-- =============================================
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Quản trị viên hệ thống - Toàn quyền quản lý'),
('MANAGER', 'Quản lý chi nhánh - Quản lý chi nhánh và nhân viên'),
('STAFF', 'Nhân viên - Xử lý đơn hàng và dịch vụ'),
('SHIPPER', 'Shipper - Giao nhận đồ giặt'),
('CUSTOMER', 'Khách hàng - Sử dụng dịch vụ giặt ủi'),
('GUEST', 'Khách vãng lai - Tự động tạo khi Staff nhập SĐT chưa có trong hệ thống');

-- =============================================
-- 2. BRANCHES (Chi nhánh)
-- Columns: id, name, address, phone, manager_name, is_active, created_at, updated_at, deleted_at
-- =============================================
INSERT INTO branches (name, address, phone, manager_name, is_active, created_at, updated_at) VALUES
('Washify - Chi nhánh Quận 1', '123 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP.HCM', '0281234567', 'Nguyễn Văn Manager', true, NOW(), NOW()),
('Washify - Chi nhánh Quận 3', '456 Võ Văn Tần, Phường 5, Quận 3, TP.HCM', '0281234568', 'Trần Thị Lan', true, NOW(), NOW()),
('Washify - Chi nhánh Bình Thạnh', '789 Điện Biên Phủ, Phường 25, Bình Thạnh, TP.HCM', '0281234569', 'Lê Văn Hùng', true, NOW(), NOW()),
('Washify - Chi nhánh Phú Nhuận', '321 Phan Đăng Lưu, Phường 1, Phú Nhuận, TP.HCM', '0281234570', 'Phạm Thị Mai', true, NOW(), NOW()),
('Washify - Chi nhánh Tân Bình', '654 Hoàng Văn Thụ, Phường 4, Tân Bình, TP.HCM', '0281234571', 'Hoàng Văn Nam', false, NOW(), NOW());

-- =============================================
-- 3. USERS (Người dùng)
-- Columns: id, full_name, username, email, password, phone, address, is_active, created_at, updated_at, deleted_at, branch_id
-- Password mặc định (từ application.properties):
--   - Admin/Manager/Staff/Shipper/Customer: app.default-password = "washify123"
--   - Guest Users: guest.default-password = "Guest@123456"
-- BCrypt hash: $2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8
-- =============================================

-- Admin (không thuộc branch nào)
INSERT INTO users (full_name, username, email, password, phone, address, is_active, branch_id, created_at, updated_at) VALUES
('Admin Washify', 'admin', 'admin@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0901234567', '123 Admin Street, TP.HCM', true, NULL, NOW(), NOW());

-- Managers (thuộc các chi nhánh)
INSERT INTO users (full_name, username, email, password, phone, address, is_active, branch_id, created_at, updated_at) VALUES
('Nguyễn Văn Manager', 'manager1', 'manager.quan1@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0901234568', '123 Nguyễn Huệ, Q1, TP.HCM', true, 1, NOW(), NOW()),
('Trần Thị Lan', 'manager2', 'manager.quan3@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0901234569', '456 Võ Văn Tần, Q3, TP.HCM', true, 2, NOW(), NOW());

-- Staff (thuộc các chi nhánh)
INSERT INTO users (full_name, username, email, password, phone, address, is_active, branch_id, created_at, updated_at) VALUES
('Lê Văn Staff', 'staff1', 'staff1@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0901234570', '100 Staff Road, TP.HCM', true, 1, NOW(), NOW()),
('Phạm Thị Hoa', 'staff2', 'staff2@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0901234571', '200 Staff Avenue, TP.HCM', true, 2, NOW(), NOW());

-- Customers (không thuộc branch)
INSERT INTO users (full_name, username, email, password, phone, address, is_active, branch_id, created_at, updated_at) VALUES
('Nguyễn Minh Khách', 'customer1', 'customer1@gmail.com', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0907777777', '789 Nguyễn Trãi, Q5, TP.HCM', true, NULL, NOW(), NOW()),
('Trần Thị Hương', 'customer2', 'customer2@gmail.com', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0908888888', '321 Lê Lợi, Q1, TP.HCM', true, NULL, NOW(), NOW()),
('Lê Quang Minh', 'customer3', 'customer3@gmail.com', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0909999999', '654 Cách Mạng Tháng 8, Q10, TP.HCM', true, NULL, NOW(), NOW());

-- Guest Users (Khách vãng lai - tự động tạo bởi Staff)
-- Password: Guest@123456 (default từ application.properties)
INSERT INTO users (full_name, username, email, password, phone, address, is_active, branch_id, created_at, updated_at) VALUES
('Guest-0912345678', 'guest_0912345678', '0912345678@guest.washify.com', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0912345678', NULL, true, NULL, NOW(), NOW()),
('Guest-0923456789', 'guest_0923456789', '0923456789@guest.washify.com', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', '0923456789', NULL, true, NULL, NOW(), NOW());

-- =============================================
-- 4. USER_ROLES (Gán vai trò cho người dùng)
-- Columns: user_id, role_id
-- =============================================
-- Admin
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1); -- admin@washify.vn -> ADMIN

-- Managers
INSERT INTO user_roles (user_id, role_id) VALUES 
(2, 2), -- manager.quan1@washify.vn -> MANAGER
(3, 2); -- manager.quan3@washify.vn -> MANAGER

-- Staff
INSERT INTO user_roles (user_id, role_id) VALUES 
(4, 3), -- staff1@washify.vn -> STAFF
(5, 3); -- staff2@washify.vn -> STAFF

-- Customers
INSERT INTO user_roles (user_id, role_id) VALUES 
(6, 5), -- customer1@gmail.com -> CUSTOMER
(7, 5), -- customer2@gmail.com -> CUSTOMER
(8, 5); -- customer3@gmail.com -> CUSTOMER

-- Guest Users
INSERT INTO user_roles (user_id, role_id) VALUES 
(9, 6),  -- guest_0912345678 -> GUEST
(10, 6); -- guest_0923456789 -> GUEST

-- =============================================
-- 5. SERVICES (Dịch vụ giặt ủi)
-- Columns: id, name, description, price, estimated_time, is_active, created_at, updated_at, deleted_at
-- =============================================
INSERT INTO services (name, description, price, estimated_time, is_active, created_at, updated_at) VALUES
-- Giặt thường
('Giặt thường - Quần áo', 'Giặt sạch quần áo thường ngày, giặt máy tiêu chuẩn', 20000.00, 24, true, NOW(), NOW()),
('Giặt hấp - Quần áo cao cấp', 'Giặt hấp cho quần áo cao cấp, vải mỏng manh', 35000.00, 48, true, NOW(), NOW()),

-- Giặt chăn màn
('Giặt chăn đơn', 'Giặt chăn đơn, mền mỏng', 50000.00, 48, true, NOW(), NOW()),
('Giặt chăn đôi/nệm', 'Giặt chăn đôi, nệm, chăn ga gối cao cấp', 80000.00, 72, true, NOW(), NOW()),
('Giặt rèm cửa', 'Giặt rèm cửa, màn cửa các loại', 60000.00, 48, true, NOW(), NOW()),

-- Ủi là
('Ủi áo sơ mi', 'Ủi phẳng áo sơ mi, áo công sở', 15000.00, 24, true, NOW(), NOW()),
('Ủi quần tây/vest', 'Ủi quần tây, quần âu, áo vest', 20000.00, 24, true, NOW(), NOW()),
('Ủi váy/đầm', 'Ủi váy, đầm các loại', 25000.00, 24, true, NOW(), NOW()),

-- Giặt khô
('Giặt khô áo vest/blazer', 'Giặt khô chuyên nghiệp cho áo vest, blazer', 70000.00, 72, true, NOW(), NOW()),
('Giặt khô áo khoác dạ', 'Giặt khô áo khoác dạ, áo khoác cao cấp', 90000.00, 72, true, NOW(), NOW()),

-- Dịch vụ đặc biệt
('Giặt giày thể thao', 'Giặt sạch giày thể thao, sneaker', 50000.00, 48, true, NOW(), NOW()),
('Giặt gấu bông', 'Giặt gấu bông, thú nhồi bông các size', 40000.00, 48, true, NOW(), NOW()),
('Giặt hấp rèm, sofa', 'Giặt hấp rèm cửa lớn, sofa vải', 150000.00, 96, false, NOW(), NOW());

-- =============================================
-- 6. PROMOTIONS (Khuyến mãi)
-- Columns: id, code, description, discount_type, discount_value, start_date, end_date, is_active, created_at, updated_at, deleted_at
-- =============================================
INSERT INTO promotions (code, description, discount_type, discount_value, start_date, end_date, is_active, created_at, updated_at) VALUES
-- Giảm giá phần trăm
('WELCOME50', 'Giảm 50% cho khách hàng mới - Đơn hàng đầu tiên', 'PERCENT', 50.00, '2025-01-01 00:00:00', '2025-12-31 23:59:59', true, NOW(), NOW()),
('SUMMER20', 'Giảm 20% mùa hè - Áp dụng tất cả dịch vụ', 'PERCENT', 20.00, '2025-06-01 00:00:00', '2025-08-31 23:59:59', true, NOW(), NOW()),
('VIP30', 'Giảm 30% cho khách hàng VIP', 'PERCENT', 30.00, '2025-01-01 00:00:00', '2025-12-31 23:59:59', true, NOW(), NOW()),

-- Giảm giá cố định
('SAVE50K', 'Giảm 50.000đ cho đơn từ 300.000đ', 'FIXED', 50000.00, '2025-01-01 00:00:00', '2025-12-31 23:59:59', true, NOW(), NOW()),
('SAVE100K', 'Giảm 100.000đ cho đơn từ 1.000.000đ', 'FIXED', 100000.00, '2025-01-01 00:00:00', '2025-12-31 23:59:59', true, NOW(), NOW()),

-- Khuyến mãi ngày lễ
('TET2025', 'Khuyến mãi Tết 2025 - Giảm 25%', 'PERCENT', 25.00, '2025-01-20 00:00:00', '2025-02-10 23:59:59', true, NOW(), NOW()),
('WOMEN8', 'Giảm 20% Ngày Quốc tế Phụ nữ 8/3', 'PERCENT', 20.00, '2025-03-07 00:00:00', '2025-03-09 23:59:59', false, NOW(), NOW());

-- =============================================
-- 7. SHIPPERS (Thông tin shipper)
-- Columns: id, name, phone, vehicle_number, is_active, created_at, updated_at, deleted_at
-- =============================================
INSERT INTO shippers (name, phone, vehicle_number, is_active, created_at, updated_at) VALUES
('Hoàng Văn Shipper', '0901234572', '59-A1 12345', true, NOW(), NOW()),
('Vũ Thị Mai', '0901234573', '59-B2 67890', true, NOW(), NOW());

-- =============================================
-- 8. SAMPLE ORDERS (Đơn hàng mẫu)
-- Columns: id, order_code, user_id, branch_id, order_date, status, total_amount, notes
-- =============================================

-- Đơn hàng 1: Đã hoàn thành
INSERT INTO orders (order_code, user_id, branch_id, order_date, status, total_amount, notes) VALUES
('WF202510140001', 6, 1, DATE_SUB(NOW(), INTERVAL 7 DAY), 'COMPLETED', 450000.00, 'Giao trước 10h sáng');

-- Đơn hàng 2: Đang xử lý
INSERT INTO orders (order_code, user_id, branch_id, order_date, status, total_amount, notes) VALUES
('WF202510190001', 7, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), 'IN_PROGRESS', 300000.00, 'Không dùng nước xả vải');

-- Đơn hàng 3: Chờ xử lý
INSERT INTO orders (order_code, user_id, branch_id, order_date, status, total_amount) VALUES
('WF202510210001', 8, 3, NOW(), 'PENDING', 200000.00);

-- =============================================
-- 9. ORDER ITEMS (Chi tiết đơn hàng)
-- Columns: id, order_id, service_id, quantity, price
-- =============================================
-- Đơn hàng 1 (ID = 1)
INSERT INTO order_items (order_id, service_id, quantity, price) VALUES
(1, 1, 10, 20000.00),  -- Giặt thường 10kg
(1, 6, 10, 15000.00),  -- Ủi áo sơ mi 10 cái
(1, 3, 2, 50000.00);   -- Giặt chăn đơn 2 cái

-- Đơn hàng 2 (ID = 2)
INSERT INTO order_items (order_id, service_id, quantity, price) VALUES
(2, 1, 8, 20000.00),   -- Giặt thường 8kg
(2, 9, 2, 70000.00);   -- Giặt khô vest 2 cái

-- Đơn hàng 3 (ID = 3)
INSERT INTO order_items (order_id, service_id, quantity, price) VALUES
(3, 1, 10, 20000.00);  -- Giặt thường 10kg

-- =============================================
-- 10. PAYMENTS (Thanh toán)
-- Columns: id, order_id, payment_method, payment_status, payment_date, amount
-- Payment Methods: CASH (Tiền mặt/Tại quầy/COD), MOMO (MoMo Wallet)
-- =============================================
-- Thanh toán cho đơn hàng 1 (đã hoàn thành - Thanh toán tiền mặt)
INSERT INTO payments (order_id, payment_method, payment_status, payment_date, amount) VALUES
(1, 'CASH', 'PAID', DATE_SUB(NOW(), INTERVAL 5 DAY), 450000.00);

-- Thanh toán cho đơn hàng 2 (chưa thanh toán - Thanh toán MoMo)
INSERT INTO payments (order_id, payment_method, payment_status, payment_date, amount) VALUES
(2, 'MOMO', 'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY), 300000.00);

-- Thanh toán cho đơn hàng 3 (chưa thanh toán - Tiền mặt tại quầy)
INSERT INTO payments (order_id, payment_method, payment_status, payment_date, amount) VALUES
(3, 'CASH', 'PENDING', NOW(), 200000.00);

-- =============================================
-- 11. SHIPMENTS (Giao nhận)
-- Columns: id, order_id, user_id, shipper_id, address, delivery_status, delivery_date, shipper_name, shipper_phone
-- =============================================
-- Giao nhận cho đơn hàng 1 (đã giao)
INSERT INTO shipments (order_id, user_id, shipper_id, address, delivery_status, delivery_date, shipper_name, shipper_phone) VALUES
(1, 6, 1, '789 Nguyễn Trãi, Q5, TP.HCM', 'DELIVERED', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Hoàng Văn Shipper', '0901234572');

-- Giao nhận cho đơn hàng 2 (đang giao)
INSERT INTO shipments (order_id, user_id, shipper_id, address, delivery_status, delivery_date, shipper_name, shipper_phone) VALUES
(2, 7, 2, '321 Lê Lợi, Q1, TP.HCM', 'SHIPPING', NOW(), 'Vũ Thị Mai', '0901234573');

-- =============================================
-- 12. REVIEWS (Đánh giá)
-- Columns: id, order_id, user_id, rating, comment, created_at
-- =============================================
INSERT INTO reviews (order_id, user_id, rating, comment, created_at) VALUES
(1, 6, 5, 'Dịch vụ tuyệt vời, quần áo sạch sẽ và thơm tho. Shipper rất nhiệt tình!', DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =============================================
-- 13. NOTIFICATIONS (Thông báo mẫu)
-- Columns: id, user_id, title, message, is_read, created_at
-- =============================================
INSERT INTO notifications (user_id, title, message, is_read, created_at) VALUES
-- Thông báo cho customer1
(6, 'Đơn hàng đã hoàn thành', 'Đơn hàng #1 của bạn đã được giao thành công. Cảm ơn bạn đã sử dụng dịch vụ Washify!', true, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- Thông báo cho customer2
(7, 'Đơn hàng đang được xử lý', 'Đơn hàng #2 của bạn đang được xử lý tại chi nhánh Quận 3.', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Thông báo khuyến mãi
(8, 'Khuyến mãi đặc biệt!', 'Giảm ngay 50% cho đơn hàng đầu tiên với mã WELCOME50. Áp dụng đến 31/12/2025.', false, NOW());

-- =============================================
-- KẾT THÚC SEED DATA
-- =============================================
