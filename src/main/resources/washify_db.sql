-- =========================
-- 1. USERS & ROLES
-- =========================
CREATE TABLE users (
                       id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                       full_name       VARCHAR(100) NOT NULL,
                       email           VARCHAR(100) UNIQUE NOT NULL,
                       password        VARCHAR(255) NOT NULL,
                       phone           VARCHAR(20),
                       address         VARCHAR(255),
                       created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       branch_id       BIGINT NULL,
                       CONSTRAINT fk_users_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE roles (
                       id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name        VARCHAR(50) UNIQUE NOT NULL,
                       description VARCHAR(255)
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_userroles_user FOREIGN KEY (user_id) REFERENCES users(id),
                            CONSTRAINT fk_userroles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- =========================
-- 2. BRANCHES (optional)
-- =========================
CREATE TABLE branches (
                          id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name         VARCHAR(100) NOT NULL,
                          address      VARCHAR(255) NOT NULL,
                          phone        VARCHAR(20),
                          manager_name VARCHAR(100),
                          created_at   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 3. SERVICES
-- =========================
CREATE TABLE services (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name            VARCHAR(100) NOT NULL,
                          description     TEXT,
                          price           DECIMAL(10,2) NOT NULL,
                          estimated_time  INT COMMENT 'in hours',
                          is_active       BOOLEAN DEFAULT TRUE
);

-- =========================
-- 4. ORDERS
-- =========================
CREATE TABLE orders (
                        id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id         BIGINT NOT NULL,
                        branch_id       BIGINT NULL,
                        order_date      DATETIME DEFAULT CURRENT_TIMESTAMP,
                        status          ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
                        total_amount    DECIMAL(10,2) DEFAULT 0.00,
                        notes           TEXT,
                        CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT fk_order_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
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
                          payment_method  ENUM('CASH', 'CARD', 'ONLINE') NOT NULL,
                          payment_status  ENUM('PENDING', 'PAID', 'FAILED') DEFAULT 'PENDING',
                          payment_date    DATETIME DEFAULT CURRENT_TIMESTAMP,
                          amount          DECIMAL(10,2) NOT NULL,
                          CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- =========================
-- 7. SHIPPERS (optional)
-- =========================
CREATE TABLE shippers (
                          id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name           VARCHAR(100) NOT NULL,
                          phone          VARCHAR(20),
                          vehicle_number VARCHAR(50),
                          is_active      BOOLEAN DEFAULT TRUE
);

-- =========================
-- 8. SHIPMENTS (0..1 per order)
-- =========================
CREATE TABLE shipments (
                           id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                           order_id         BIGINT UNIQUE,
                           user_id          BIGINT NOT NULL,
                           shipper_id       BIGINT NULL,
                           address          VARCHAR(255) NOT NULL,
                           delivery_status  ENUM('PENDING', 'SHIPPING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
                           delivery_date    DATETIME,
                           shipper_name     VARCHAR(100),
                           shipper_phone    VARCHAR(20),
                           CONSTRAINT fk_shipment_order FOREIGN KEY (order_id) REFERENCES orders(id),
                           CONSTRAINT fk_shipment_user FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fk_shipment_shipper FOREIGN KEY (shipper_id) REFERENCES shippers(id)
);

-- =========================
-- 9. REVIEWS
-- =========================
CREATE TABLE reviews (
                         id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id    BIGINT NOT NULL,
                         user_id     BIGINT NOT NULL,
                         rating      INT CHECK (rating BETWEEN 1 AND 5),
                         comment     TEXT,
                         created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id),
                         CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =========================
-- 10. PROMOTIONS
-- =========================
CREATE TABLE promotions (
                            id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                            code            VARCHAR(50) UNIQUE NOT NULL,
                            description     VARCHAR(255),
                            discount_type   ENUM('PERCENT', 'FIXED') NOT NULL,
                            discount_value  DECIMAL(10,2) NOT NULL,
                            start_date      DATETIME,
                            end_date        DATETIME,
                            is_active       BOOLEAN DEFAULT TRUE
);

CREATE TABLE order_promotions (
                                  order_id       BIGINT NOT NULL,
                                  promotion_id   BIGINT NOT NULL,
                                  PRIMARY KEY (order_id, promotion_id),
                                  CONSTRAINT fk_orderpromo_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_orderpromo_promo FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);

-- =========================
-- 11. NOTIFICATIONS
-- =========================
CREATE TABLE notifications (
                               id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id         BIGINT NOT NULL,
                               title           VARCHAR(100),
                               message         TEXT,
                               is_read         BOOLEAN DEFAULT FALSE,
                               created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =========================
-- 12. AUDIT LOG
-- =========================
CREATE TABLE audit_log (
                           id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id         BIGINT NULL,
                           entity_type     VARCHAR(50),
                           entity_id       BIGINT,
                           action          VARCHAR(50),
                           old_value       TEXT,
                           new_value       TEXT,
                           created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =========================
-- 13. ATTACHMENTS (optional)
-- =========================
CREATE TABLE attachments (
                             id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id      BIGINT NULL,
                             shipment_id   BIGINT NULL,
                             file_url      VARCHAR(255) NOT NULL,
                             file_type     VARCHAR(50),
                             uploaded_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_attach_order FOREIGN KEY (order_id) REFERENCES orders(id),
                             CONSTRAINT fk_attach_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id)
);
