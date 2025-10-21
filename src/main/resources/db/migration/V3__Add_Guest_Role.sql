-- Migration script to add GUEST role for walk-in customers
-- Version: V3__Add_Guest_Role.sql

-- Insert GUEST role if not exists
INSERT INTO roles (name, description)
SELECT 'GUEST', 'Khách vãng lai - Tự động tạo khi Staff nhập SĐT chưa có trong hệ thống'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'GUEST'
);
