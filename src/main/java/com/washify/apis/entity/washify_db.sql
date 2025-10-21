CREATE DATABASE IF NOT EXISTS washify_db;
-- =========================
-- 1. BRANCHES (Tạo trước vì users có FK tới branches)
-- =========================
CREATE TABLE branches (
                          id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name         VARCHAR(100) NOT NULL,
                          address      VARCHAR(255) NOT NULL,
                          phone        VARCHAR(20),
                          manager_name VARCHAR(100),
                          is_active    BOOLEAN DEFAULT TRUE,
                          created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          deleted_at   DATETIME COMMENT 'Soft delete timestamp',
                          INDEX idx_branches_deleted (deleted_at),
                          INDEX idx_branches_active (is_active)
);

-- =========================
-- 2. USERS & ROLES
-- =========================
CREATE TABLE users (
                       id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                       full_name       VARCHAR(100) NOT NULL,
                       username        VARCHAR(50) UNIQUE NOT NULL COMMENT 'Username để đăng nhập',
                       email           VARCHAR(100) UNIQUE NOT NULL,
                       password        VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã mã hóa',
                       phone           VARCHAR(20),
                       address         VARCHAR(255),
                       is_active       BOOLEAN DEFAULT TRUE,
                       require_password_change BOOLEAN DEFAULT FALSE COMMENT 'Bắt buộc đổi password (Guest User)',
                       require_email_verification_for_password_change BOOLEAN DEFAULT FALSE COMMENT 'Bảo mật 2 lớp cho đổi password',
                       created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at      DATETIME COMMENT 'Soft delete timestamp',
                       branch_id       BIGINT NULL,
                       CONSTRAINT fk_users_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
                       INDEX idx_users_username (username),
                       INDEX idx_users_email (email),
                       INDEX idx_users_deleted (deleted_at),
                       INDEX idx_users_active (is_active)
);

CREATE TABLE roles (
                       id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name        VARCHAR(50) UNIQUE NOT NULL COMMENT 'ADMIN, STAFF, CUSTOMER, GUEST',
                       description VARCHAR(255)
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_userroles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_userroles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- =========================
-- 3. SERVICES (Dịch vụ giặt là)
-- =========================
CREATE TABLE services (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name            VARCHAR(100) NOT NULL,
                          description     TEXT,
                          price           DECIMAL(10,2) NOT NULL COMMENT 'Giá theo kg hoặc item',
                          estimated_time  INT COMMENT 'Thời gian ước tính (giờ)',
                          is_active       BOOLEAN DEFAULT TRUE,
                          created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          deleted_at      DATETIME COMMENT 'Soft delete timestamp',
                          INDEX idx_services_deleted (deleted_at),
                          INDEX idx_services_active (is_active)
);

-- =========================
-- 4. ORDERS
-- =========================
CREATE TABLE orders (
                        id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_code      VARCHAR(50) UNIQUE NOT NULL COMMENT 'Mã đơn hàng (VD: WF202510210001)',
                        user_id         BIGINT NOT NULL,
                        branch_id       BIGINT NULL,
                        order_date      DATETIME DEFAULT CURRENT_TIMESTAMP,
                        status          ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
                        total_amount    DECIMAL(10,2) DEFAULT 0.00,
                        notes           TEXT,
                        deleted_at      DATETIME COMMENT 'Soft delete timestamp',
                        CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT fk_order_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
                        INDEX idx_orders_code (order_code),
                        INDEX idx_orders_user (user_id),
                        INDEX idx_orders_status (status),
                        INDEX idx_orders_deleted (deleted_at)
);

-- =========================
-- 5. ORDER ITEMS
-- =========================
CREATE TABLE order_items (
                             id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id    BIGINT NOT NULL,
                             service_id  BIGINT NOT NULL,
                             quantity    INT NOT NULL DEFAULT 1,
                             price       DECIMAL(10,2) NOT NULL,
                             CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_orderitem_service FOREIGN KEY (service_id) REFERENCES services(id)
);

-- =========================
-- 6. PAYMENTS (1:1 with Orders)
-- =========================
CREATE TABLE payments (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_id        BIGINT UNIQUE NOT NULL,
                          payment_method  ENUM('CASH', 'MOMO') NOT NULL COMMENT 'CASH: Tiền mặt/COD, MOMO: MoMo Wallet',
                          payment_status  ENUM('PENDING', 'PAID', 'FAILED') DEFAULT 'PENDING',
                          payment_date    DATETIME DEFAULT CURRENT_TIMESTAMP,
                          amount          DECIMAL(10,2) NOT NULL,
                          transaction_id  VARCHAR(100) COMMENT 'Mã giao dịch từ payment gateway',
                          payment_url     VARCHAR(500) COMMENT 'URL thanh toán (MOMO, VNPAY, etc.)',
                          qr_code         TEXT COMMENT 'QR code cho thanh toán (base64 hoặc URL)',
                          gateway_response TEXT COMMENT 'Response từ payment gateway (JSON)',
                          CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                          INDEX idx_payments_status (payment_status),
                          INDEX idx_payments_transaction (transaction_id)
);

-- =========================
-- 7. SHIPPERS (optional)
-- =========================
CREATE TABLE shippers (
                          id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name           VARCHAR(100) NOT NULL,
                          phone          VARCHAR(20),
                          vehicle_number VARCHAR(50),
                          is_active      BOOLEAN DEFAULT TRUE,
                          created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          deleted_at     DATETIME COMMENT 'Soft delete timestamp',
                          INDEX idx_shippers_deleted (deleted_at),
                          INDEX idx_shippers_active (is_active)
);

-- =========================
-- 8. SHIPMENTS (0..1 per order - Giao hàng tận nơi)
-- =========================
CREATE TABLE shipments (
                           id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                           order_id         BIGINT UNIQUE,
                           user_id          BIGINT NOT NULL,
                           shipper_id       BIGINT NULL,
                           address          VARCHAR(255) NOT NULL COMMENT 'Địa chỉ giao hàng',
                           delivery_status  ENUM('PENDING', 'SHIPPING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
                           delivery_date    DATETIME,
                           shipper_name     VARCHAR(100) COMMENT 'Tên shipper (cache từ shippers table)',
                           shipper_phone    VARCHAR(20) COMMENT 'SĐT shipper (cache từ shippers table)',
                           created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
                           updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           CONSTRAINT fk_shipment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                           CONSTRAINT fk_shipment_user FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fk_shipment_shipper FOREIGN KEY (shipper_id) REFERENCES shippers(id),
                           INDEX idx_shipments_status (delivery_status),
                           INDEX idx_shipments_shipper (shipper_id)
);

-- =========================
-- 9. REVIEWS (Đánh giá đơn hàng)
-- =========================
CREATE TABLE reviews (
                         id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id    BIGINT NOT NULL,
                         user_id     BIGINT NOT NULL,
                         rating      INT CHECK (rating BETWEEN 1 AND 5) COMMENT 'Điểm đánh giá từ 1-5 sao',
                         comment     TEXT,
                         created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                         CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id),
                         INDEX idx_reviews_order (order_id),
                         INDEX idx_reviews_rating (rating)
);

-- =========================
-- 10. PROMOTIONS (Mã giảm giá/Khuyến mãi)
-- =========================
CREATE TABLE promotions (
                            id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                            code            VARCHAR(50) UNIQUE NOT NULL COMMENT 'Mã khuyến mãi (VD: SUMMER2025, NEWUSER)',
                            description     VARCHAR(255),
                            discount_type   ENUM('PERCENT', 'FIXED') NOT NULL COMMENT 'PERCENT: Giảm %, FIXED: Giảm giá cố định',
                            discount_value  DECIMAL(10,2) NOT NULL COMMENT 'Giá trị giảm (VD: 20 cho 20%, hoặc 50000 cho 50k)',
                            start_date      DATETIME,
                            end_date        DATETIME,
                            is_active       BOOLEAN DEFAULT TRUE,
                            created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                            updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            deleted_at      DATETIME COMMENT 'Soft delete timestamp',
                            INDEX idx_promotions_code (code),
                            INDEX idx_promotions_deleted (deleted_at),
                            INDEX idx_promotions_active (is_active)
);

CREATE TABLE order_promotions (
                                  order_id       BIGINT NOT NULL,
                                  promotion_id   BIGINT NOT NULL,
                                  PRIMARY KEY (order_id, promotion_id),
                                  CONSTRAINT fk_orderpromo_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_orderpromo_promo FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE
);

-- =========================
-- 11. NOTIFICATIONS (Thông báo gửi đến người dùng)
-- =========================
CREATE TABLE notifications (
                               id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id         BIGINT NOT NULL,
                               title           VARCHAR(100),
                               message         TEXT,
                               type            VARCHAR(50) COMMENT 'Loại thông báo (order, payment, shipment, etc.)',
                               related_id      BIGINT COMMENT 'ID liên quan (order_id, payment_id, etc.)',
                               is_read         BOOLEAN DEFAULT FALSE,
                               read_at         DATETIME COMMENT 'Thời gian đọc thông báo',
                               created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               INDEX idx_notifications_user (user_id),
                               INDEX idx_notifications_read (is_read)
);

-- =========================
-- 12. AUDIT LOG (Enhanced with AOP)
-- =========================
-- Ghi nhận tự động mọi thao tác CRUD quan trọng
-- Hỗ trợ 30+ operations từ Order, User, Payment, Promotion, Shipper, SoftDelete services
-- Ghi nhận IP address, User Agent để tăng cường bảo mật
CREATE TABLE audit_log (
                           id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id         BIGINT NULL,
                           entity_type     VARCHAR(50),
                           entity_id       BIGINT,
                           action          VARCHAR(50),
                           old_value       TEXT,
                           new_value       TEXT,
                           ip_address      VARCHAR(45) COMMENT 'IPv4 or IPv6 address',
                           user_agent      TEXT COMMENT 'Browser/device information',
                           description     TEXT COMMENT 'Detailed operation description',
                           status          VARCHAR(20) DEFAULT 'SUCCESS' COMMENT 'SUCCESS or FAILED',
                           error_message   TEXT COMMENT 'Error details if status is FAILED',
                           created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id),
                           INDEX idx_audit_log_ip_address (ip_address),
                           INDEX idx_audit_log_status (status)
);

-- =========================
-- 13. ATTACHMENTS (File đính kèm/Hình ảnh)
-- =========================
CREATE TABLE attachments (
                             id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id      BIGINT NULL,
                             shipment_id   BIGINT NULL,
                             file_url      VARCHAR(255) NOT NULL COMMENT 'URL file trên storage',
                             file_type     VARCHAR(50) COMMENT 'image/jpeg, application/pdf, etc.',
                             uploaded_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_attach_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_attach_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE,
                             INDEX idx_attachments_order (order_id)
);

-- =========================
-- 14. PASSWORD TOKENS (Quản lý token cho đổi mật khẩu)
-- =========================
CREATE TABLE password_reset_tokens (
                                       id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id         BIGINT NOT NULL,
                                       token           VARCHAR(255) UNIQUE NOT NULL,
                                       expiry_date     DATETIME NOT NULL,
                                       is_used         BOOLEAN DEFAULT FALSE,
                                       created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_reset_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       INDEX idx_reset_token (token),
                                       INDEX idx_reset_expiry (expiry_date)
);

CREATE TABLE password_change_tokens (
                                        id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id         BIGINT NOT NULL,
                                        token           VARCHAR(255) UNIQUE NOT NULL,
                                        expiry_date     DATETIME NOT NULL,
                                        is_used         BOOLEAN DEFAULT FALSE,
                                        created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                                        CONSTRAINT fk_change_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                        INDEX idx_change_token (token),
                                        INDEX idx_change_expiry (expiry_date)
);

CREATE TABLE password_change_2fa_tokens (
                                            id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            user_id         BIGINT NOT NULL,
                                            token           VARCHAR(255) UNIQUE NOT NULL,
                                            expiry_date     DATETIME NOT NULL,
                                            is_verified     BOOLEAN DEFAULT FALSE,
                                            created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                                            CONSTRAINT fk_2fa_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                            INDEX idx_2fa_token (token),
                                            INDEX idx_2fa_expiry (expiry_date)
);
