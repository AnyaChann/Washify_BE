-- =============================================
-- Migration V5: Add Require Password Change Field
-- Thêm field require_password_change để bắt buộc Guest User đổi mật khẩu lần đầu login
-- =============================================

-- Thêm column require_password_change vào bảng users
ALTER TABLE users 
ADD COLUMN require_password_change BOOLEAN DEFAULT FALSE COMMENT 'Bắt buộc đổi mật khẩu (dùng cho Guest User lần đầu login)';

-- Set require_password_change = TRUE cho tất cả Guest Users hiện có
-- Guest users có username bắt đầu bằng 'guest_'
UPDATE users 
SET require_password_change = TRUE 
WHERE username LIKE 'guest_%';

-- Tạo index để tăng hiệu suất query
CREATE INDEX idx_users_require_password_change ON users(require_password_change);
