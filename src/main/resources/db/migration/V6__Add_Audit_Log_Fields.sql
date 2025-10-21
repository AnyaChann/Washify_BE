-- Migration V6: Add additional audit log fields
-- Thêm các trường bổ sung cho audit log để tracking đầy đủ hơn

-- Thêm IP address
ALTER TABLE audit_log ADD COLUMN ip_address VARCHAR(45) AFTER new_value;

-- Thêm User Agent
ALTER TABLE audit_log ADD COLUMN user_agent TEXT AFTER ip_address;

-- Thêm Description
ALTER TABLE audit_log ADD COLUMN description TEXT AFTER user_agent;

-- Thêm Status
ALTER TABLE audit_log ADD COLUMN status VARCHAR(20) DEFAULT 'SUCCESS' AFTER description;

-- Thêm Error Message
ALTER TABLE audit_log ADD COLUMN error_message TEXT AFTER status;

-- Tạo index cho IP address để query nhanh
CREATE INDEX idx_audit_log_ip_address ON audit_log(ip_address);

-- Tạo index cho status để filter
CREATE INDEX idx_audit_log_status ON audit_log(status);

-- Comment
ALTER TABLE audit_log COMMENT = 'Bảng ghi log audit trail với đầy đủ thông tin user, IP, device';
