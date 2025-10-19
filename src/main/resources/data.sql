-- =============================================
-- WASHIFY SEED DATA
-- Dữ liệu mẫu ban đầu cho hệ thống Washify
-- =============================================

-- =============================================
-- 1. ROLES (Vai trò người dùng)
-- =============================================
INSERT INTO roles (name, description, created_at, updated_at) VALUES
('ROLE_ADMIN', 'Quản trị viên hệ thống - Toàn quyền quản lý', NOW(), NOW()),
('ROLE_MANAGER', 'Quản lý chi nhánh - Quản lý chi nhánh và nhân viên', NOW(), NOW()),
('ROLE_STAFF', 'Nhân viên - Xử lý đơn hàng và dịch vụ', NOW(), NOW()),
('ROLE_SHIPPER', 'Shipper - Giao nhận đồ giặt', NOW(), NOW()),
('ROLE_CUSTOMER', 'Khách hàng - Sử dụng dịch vụ giặt ủi', NOW(), NOW());

-- =============================================
-- 2. BRANCHES (Chi nhánh)
-- =============================================
INSERT INTO branches (name, address, phone_number, email, latitude, longitude, is_active, created_at, updated_at) VALUES
('Washify - Chi nhánh Quận 1', '123 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP.HCM', '0281234567', 'quan1@washify.vn', 10.7756590, 106.7004740, true, NOW(), NOW()),
('Washify - Chi nhánh Quận 3', '456 Võ Văn Tần, Phường 5, Quận 3, TP.HCM', '0281234568', 'quan3@washify.vn', 10.7829190, 106.6925880, true, NOW(), NOW()),
('Washify - Chi nhánh Bình Thạnh', '789 Điện Biên Phủ, Phường 25, Bình Thạnh, TP.HCM', '0281234569', 'binhthanh@washify.vn', 10.8017280, 106.7142450, true, NOW(), NOW()),
('Washify - Chi nhánh Phú Nhuận', '321 Phan Đăng Lưu, Phường 1, Phú Nhuận, TP.HCM', '0281234570', 'phunhuan@washify.vn', 10.7998160, 106.6805620, true, NOW(), NOW()),
('Washify - Chi nhánh Tân Bình', '654 Hoàng Văn Thụ, Phường 4, Tân Bình, TP.HCM', '0281234571', 'tanbinh@washify.vn', 10.7999510, 106.6536660, false, NOW(), NOW());

-- =============================================
-- 3. USERS (Người dùng)
-- Password mặc định cho tất cả: "washify123"
-- BCrypt hash: $2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8
-- =============================================

-- Admin
INSERT INTO users (email, password, full_name, phone_number, address, is_active, email_verified, created_at, updated_at) VALUES
('admin@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Admin Washify', '0901234567', '123 Admin Street, TP.HCM', true, true, NOW(), NOW());

-- Managers (Quản lý chi nhánh)
INSERT INTO users (email, password, full_name, phone_number, address, is_active, email_verified, created_at, updated_at) VALUES
('manager.quan1@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Nguyễn Văn Manager', '0901234568', '123 Nguyễn Huệ, Q1, TP.HCM', true, true, NOW(), NOW()),
('manager.quan3@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Trần Thị Lan', '0901234569', '456 Võ Văn Tần, Q3, TP.HCM', true, true, NOW(), NOW());

-- Staff (Nhân viên)
INSERT INTO users (email, password, full_name, phone_number, address, is_active, email_verified, created_at, updated_at) VALUES
('staff1@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Lê Văn Staff', '0901234570', '100 Staff Road, TP.HCM', true, true, NOW(), NOW()),
('staff2@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Phạm Thị Hoa', '0901234571', '200 Staff Avenue, TP.HCM', true, true, NOW(), NOW());

-- Shippers
INSERT INTO users (email, password, full_name, phone_number, address, is_active, email_verified, created_at, updated_at) VALUES
('shipper1@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Hoàng Văn Shipper', '0901234572', '300 Shipper Lane, TP.HCM', true, true, NOW(), NOW()),
('shipper2@washify.vn', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Vũ Thị Mai', '0901234573', '400 Shipper Street, TP.HCM', true, true, NOW(), NOW());

-- Customers (Khách hàng)
INSERT INTO users (email, password, full_name, phone_number, address, is_active, email_verified, created_at, updated_at) VALUES
('customer1@gmail.com', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Nguyễn Minh Khách', '0907777777', '789 Nguyễn Trãi, Q5, TP.HCM', true, true, NOW(), NOW()),
('customer2@gmail.com', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Trần Thị Hương', '0908888888', '321 Lê Lợi, Q1, TP.HCM', true, true, NOW(), NOW()),
('customer3@gmail.com', '$2a$10$xK5nN7nK5nN7nK5nN7nK5uXqZ8yY8yY8yY8yY8yY8yY8yY8yY8yY8', 'Lê Quang Minh', '0909999999', '654 Cách Mạng Tháng 8, Q10, TP.HCM', true, true, NOW(), NOW());

-- =============================================
-- 4. USER_ROLES (Gán vai trò cho người dùng)
-- =============================================
-- Admin
INSERT INTO user_roles (user_id, role_id) VALUES 
((SELECT id FROM users WHERE email = 'admin@washify.vn'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));

-- Managers
INSERT INTO user_roles (user_id, role_id) VALUES 
((SELECT id FROM users WHERE email = 'manager.quan1@washify.vn'), (SELECT id FROM roles WHERE name = 'ROLE_MANAGER')),
((SELECT id FROM users WHERE email = 'manager.quan3@washify.vn'), (SELECT id FROM roles WHERE name = 'ROLE_MANAGER'));

-- Staff
INSERT INTO user_roles (user_id, role_id) VALUES 
((SELECT id FROM users WHERE email = 'staff1@washify.vn'), (SELECT id FROM roles WHERE name = 'ROLE_STAFF')),
((SELECT id FROM users WHERE email = 'staff2@washify.vn'), (SELECT id FROM roles WHERE name = 'ROLE_STAFF'));

-- Shippers
INSERT INTO user_roles (user_id, role_id) VALUES 
((SELECT id FROM users WHERE email = 'shipper1@washify.vn'), (SELECT id FROM roles WHERE name = 'ROLE_SHIPPER')),
((SELECT id FROM users WHERE email = 'shipper2@washify.vn'), (SELECT id FROM roles WHERE name = 'ROLE_SHIPPER'));

-- Customers
INSERT INTO user_roles (user_id, role_id) VALUES 
((SELECT id FROM users WHERE email = 'customer1@gmail.com'), (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
((SELECT id FROM users WHERE email = 'customer2@gmail.com'), (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER')),
((SELECT id FROM users WHERE email = 'customer3@gmail.com'), (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER'));

-- =============================================
-- 5. SERVICES (Dịch vụ giặt ủi)
-- =============================================
INSERT INTO services (name, description, price, unit, estimated_time, is_active, created_at, updated_at) VALUES
-- Giặt thường
('Giặt thường - Quần áo', 'Giặt sạch quần áo thường ngày, giặt máy tiêu chuẩn', 20000, 'kg', 24, true, NOW(), NOW()),
('Giặt hấp - Quần áo cao cấp', 'Giặt hấp cho quần áo cao cấp, vải mỏng manh', 35000, 'kg', 48, true, NOW(), NOW()),

-- Giặt chăn màn
('Giặt chăn đơn', 'Giặt chăn đơn, mền mỏng', 50000, 'cái', 48, true, NOW(), NOW()),
('Giặt chăn đôi/nệm', 'Giặt chăn đôi, nệm, chăn ga gối cao cấp', 80000, 'cái', 72, true, NOW(), NOW()),
('Giặt rèm cửa', 'Giặt rèm cửa, màn cửa các loại', 60000, 'm2', 48, true, NOW(), NOW()),

-- Ủi là
('Ủi áo sơ mi', 'Ủi phẳng áo sơ mi, áo công sở', 15000, 'cái', 24, true, NOW(), NOW()),
('Ủi quần tây/vest', 'Ủi quần tây, quần âu, áo vest', 20000, 'cái', 24, true, NOW(), NOW()),
('Ủi váy/đầm', 'Ủi váy, đầm các loại', 25000, 'cái', 24, true, NOW(), NOW()),

-- Giặt khô
('Giặt khô áo vest/blazer', 'Giặt khô chuyên nghiệp cho áo vest, blazer', 70000, 'cái', 72, true, NOW(), NOW()),
('Giặt khô áo khoác dạ', 'Giặt khô áo khoác dạ, áo khoác cao cấp', 90000, 'cái', 72, true, NOW(), NOW()),

-- Dịch vụ đặc biệt
('Giặt giày thể thao', 'Giặt sạch giày thể thao, sneaker', 50000, 'đôi', 48, true, NOW(), NOW()),
('Giặt gấu bông', 'Giặt gấu bông, thú nhồi bông các size', 40000, 'cái', 48, true, NOW(), NOW()),
('Giặt hấp rèm, sofa', 'Giặt hấp rèm cửa lớn, sofa vải', 150000, 'bộ', 96, false, NOW(), NOW());

-- =============================================
-- 6. PROMOTIONS (Khuyến mãi)
-- =============================================
INSERT INTO promotions (code, description, discount_type, discount_value, min_order_value, max_discount, start_date, end_date, usage_limit, used_count, is_active, created_at, updated_at) VALUES
-- Giảm giá phần trăm
('WELCOME50', 'Giảm 50% cho khách hàng mới - Đơn hàng đầu tiên', 'PERCENTAGE', 50.00, 100000, 50000, '2025-01-01', '2025-12-31', 1000, 0, true, NOW(), NOW()),
('SUMMER20', 'Giảm 20% mùa hè - Áp dụng tất cả dịch vụ', 'PERCENTAGE', 20.00, 200000, 100000, '2025-06-01', '2025-08-31', 500, 0, true, NOW(), NOW()),
('VIP30', 'Giảm 30% cho khách hàng VIP', 'PERCENTAGE', 30.00, 500000, 200000, '2025-01-01', '2025-12-31', NULL, 0, true, NOW(), NOW()),

-- Giảm giá cố định
('SAVE50K', 'Giảm 50.000đ cho đơn từ 300.000đ', 'FIXED', 50000.00, 300000, 50000, '2025-01-01', '2025-12-31', NULL, 0, true, NOW(), NOW()),
('SAVE100K', 'Giảm 100.000đ cho đơn từ 1.000.000đ', 'FIXED', 100000.00, 1000000, 100000, '2025-01-01', '2025-12-31', NULL, 0, true, NOW(), NOW()),

-- Khuyến mãi ngày lễ
('TET2025', 'Khuyến mãi Tết 2025 - Giảm 25%', 'PERCENTAGE', 25.00, 150000, 150000, '2025-01-20', '2025-02-10', 1000, 0, true, NOW(), NOW()),
('WOMEN8', 'Giảm 20% Ngày Quốc tế Phụ nữ 8/3', 'PERCENTAGE', 20.00, 100000, 80000, '2025-03-07', '2025-03-09', 500, 0, false, NOW(), NOW());

-- =============================================
-- 7. SHIPPERS (Thông tin shipper)
-- =============================================
INSERT INTO shippers (user_id, vehicle_type, vehicle_number, is_available, current_latitude, current_longitude, rating, total_deliveries, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email = 'shipper1@washify.vn'), 'MOTORCYCLE', '59-A1 12345', true, 10.7756590, 106.7004740, 4.8, 150, NOW(), NOW()),
((SELECT id FROM users WHERE email = 'shipper2@washify.vn'), 'MOTORCYCLE', '59-B2 67890', true, 10.7829190, 106.6925880, 4.5, 98, NOW(), NOW());

-- =============================================
-- 8. SAMPLE ORDERS (Đơn hàng mẫu)
-- =============================================

-- Đơn hàng 1: Đã hoàn thành
INSERT INTO orders (user_id, branch_id, pickup_address, delivery_address, total_price, final_price, order_status, payment_status, notes, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email = 'customer1@gmail.com'), 
 (SELECT id FROM branches WHERE name = 'Washify - Chi nhánh Quận 1'),
 '789 Nguyễn Trãi, Q5, TP.HCM',
 '789 Nguyễn Trãi, Q5, TP.HCM',
 500000, 450000, 'COMPLETED', 'PAID', 'Giao trước 10h sáng', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- Đơn hàng 2: Đang xử lý
INSERT INTO orders (user_id, branch_id, pickup_address, delivery_address, total_price, final_price, order_status, payment_status, notes, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email = 'customer2@gmail.com'),
 (SELECT id FROM branches WHERE name = 'Washify - Chi nhánh Quận 3'),
 '321 Lê Lợi, Q1, TP.HCM',
 '321 Lê Lợi, Q1, TP.HCM',
 300000, 300000, 'PROCESSING', 'PENDING', 'Không dùng nước xả vải', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW());

-- Đơn hàng 3: Chờ lấy đồ
INSERT INTO orders (user_id, branch_id, pickup_address, delivery_address, total_price, final_price, order_status, payment_status, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email = 'customer3@gmail.com'),
 (SELECT id FROM branches WHERE name = 'Washify - Chi nhánh Bình Thạnh'),
 '654 Cách Mạng Tháng 8, Q10, TP.HCM',
 '654 Cách Mạng Tháng 8, Q10, TP.HCM',
 200000, 200000, 'PENDING', 'PENDING', NOW(), NOW());

-- =============================================
-- 9. ORDER ITEMS (Chi tiết đơn hàng)
-- =============================================
-- Đơn hàng 1
INSERT INTO order_items (order_id, service_id, quantity, price, subtotal, created_at) VALUES
(1, (SELECT id FROM services WHERE name = 'Giặt thường - Quần áo'), 10, 20000, 200000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, (SELECT id FROM services WHERE name = 'Ủi áo sơ mi'), 10, 15000, 150000, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, (SELECT id FROM services WHERE name = 'Giặt chăn đơn'), 3, 50000, 150000, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- Đơn hàng 2
INSERT INTO order_items (order_id, service_id, quantity, price, subtotal, created_at) VALUES
(2, (SELECT id FROM services WHERE name = 'Giặt thường - Quần áo'), 8, 20000, 160000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, (SELECT id FROM services WHERE name = 'Giặt khô áo vest/blazer'), 2, 70000, 140000, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Đơn hàng 3
INSERT INTO order_items (order_id, service_id, quantity, price, subtotal, created_at) VALUES
(3, (SELECT id FROM services WHERE name = 'Giặt thường - Quần áo'), 10, 20000, 200000, NOW());

-- =============================================
-- 10. PAYMENTS (Thanh toán)
-- =============================================
-- Thanh toán cho đơn hàng 1 (đã hoàn thành)
INSERT INTO payments (order_id, amount, payment_method, payment_status, transaction_id, payment_date, created_at, updated_at) VALUES
(1, 450000, 'CASH', 'COMPLETED', NULL, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- Thanh toán cho đơn hàng 2 (chưa thanh toán)
INSERT INTO payments (order_id, amount, payment_method, payment_status, created_at, updated_at) VALUES
(2, 300000, 'MOMO', 'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- =============================================
-- 11. SHIPMENTS (Giao nhận)
-- =============================================
-- Giao nhận cho đơn hàng 1
INSERT INTO shipments (order_id, shipper_id, pickup_address, delivery_address, pickup_time, delivery_time, shipment_status, created_at, updated_at) VALUES
(1, (SELECT id FROM shippers WHERE user_id = (SELECT id FROM users WHERE email = 'shipper1@washify.vn')),
 '789 Nguyễn Trãi, Q5, TP.HCM',
 '789 Nguyễn Trãi, Q5, TP.HCM',
 DATE_SUB(NOW(), INTERVAL 7 DAY),
 DATE_SUB(NOW(), INTERVAL 5 DAY),
 'DELIVERED',
 DATE_SUB(NOW(), INTERVAL 7 DAY),
 DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =============================================
-- 12. REVIEWS (Đánh giá)
-- =============================================
INSERT INTO reviews (order_id, user_id, rating, comment, created_at, updated_at) VALUES
(1, (SELECT id FROM users WHERE email = 'customer1@gmail.com'),
 5, 'Dịch vụ tuyệt vời, quần áo sạch sẽ và thơm tho. Shipper rất nhiệt tình!',
 DATE_SUB(NOW(), INTERVAL 5 DAY),
 DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =============================================
-- 13. NOTIFICATIONS (Thông báo mẫu)
-- =============================================
INSERT INTO notifications (user_id, title, message, notification_type, is_read, created_at) VALUES
-- Thông báo cho customer1
((SELECT id FROM users WHERE email = 'customer1@gmail.com'),
 'Đơn hàng đã hoàn thành',
 'Đơn hàng #1 của bạn đã được giao thành công. Cảm ơn bạn đã sử dụng dịch vụ Washify!',
 'ORDER_UPDATE',
 true,
 DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- Thông báo cho customer2
((SELECT id FROM users WHERE email = 'customer2@gmail.com'),
 'Đơn hàng đang được xử lý',
 'Đơn hàng #2 của bạn đang được xử lý tại chi nhánh Quận 3.',
 'ORDER_UPDATE',
 false,
 DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Thông báo khuyến mãi
((SELECT id FROM users WHERE email = 'customer3@gmail.com'),
 'Khuyến mãi đặc biệt!',
 'Giảm ngay 50% cho đơn hàng đầu tiên với mã WELCOME50. Áp dụng đến 31/12/2025.',
 'PROMOTION',
 false,
 NOW());

-- =============================================
-- KẾT THÚC SEED DATA
-- =============================================
