-- ========================================
-- SOFT DELETE MIGRATION SCRIPT
-- ========================================
-- Purpose: Add soft delete support to Washify database
-- Date: 2025
-- Version: 1.0
-- ========================================

-- IMPORTANT: Backup your database before running this script!
-- mysqldump -u root -p washify_db > washify_backup_$(date +%Y%m%d).sql

USE washify_db;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

-- ========================================
-- PHASE 1: ADD COLUMNS
-- ========================================

-- Users table
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Soft delete timestamp',
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE COMMENT 'Active status flag',
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation time',
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time';

-- Branches table
ALTER TABLE branches 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Soft delete timestamp',
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE COMMENT 'Active status flag',
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation time',
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time';

-- Services table
ALTER TABLE services 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Soft delete timestamp',
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation time',
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time';

-- Orders table
ALTER TABLE orders 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Soft delete timestamp';

-- Promotions table
ALTER TABLE promotions 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Soft delete timestamp',
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation time',
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time';

-- Shippers table
ALTER TABLE shippers 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Soft delete timestamp',
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation time',
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time';

-- ========================================
-- PHASE 2: UPDATE EXISTING RECORDS
-- ========================================

-- Set is_active = TRUE cho tất cả existing users
UPDATE users SET is_active = TRUE WHERE is_active IS NULL;

-- Set is_active = TRUE cho tất cả existing branches
UPDATE branches SET is_active = TRUE WHERE is_active IS NULL;

-- Set created_at cho existing records (dùng earliest possible timestamp)
UPDATE users SET created_at = '2025-01-01 00:00:00' WHERE created_at IS NULL;
UPDATE branches SET created_at = '2025-01-01 00:00:00' WHERE created_at IS NULL;
UPDATE services SET created_at = '2025-01-01 00:00:00' WHERE created_at IS NULL;
UPDATE promotions SET created_at = '2025-01-01 00:00:00' WHERE created_at IS NULL;
UPDATE shippers SET created_at = '2025-01-01 00:00:00' WHERE created_at IS NULL;

-- ========================================
-- PHASE 3: ADD INDEXES FOR PERFORMANCE
-- ========================================

-- Indexes on deleted_at for faster queries
CREATE INDEX IF NOT EXISTS idx_users_deleted_at ON users(deleted_at);
CREATE INDEX IF NOT EXISTS idx_branches_deleted_at ON branches(deleted_at);
CREATE INDEX IF NOT EXISTS idx_services_deleted_at ON services(deleted_at);
CREATE INDEX IF NOT EXISTS idx_orders_deleted_at ON orders(deleted_at);
CREATE INDEX IF NOT EXISTS idx_promotions_deleted_at ON promotions(deleted_at);
CREATE INDEX IF NOT EXISTS idx_shippers_deleted_at ON shippers(deleted_at);

-- Composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_users_active_deleted ON users(is_active, deleted_at);
CREATE INDEX IF NOT EXISTS idx_branches_active_deleted ON branches(is_active, deleted_at);

-- ========================================
-- PHASE 4: ADD CONSTRAINTS (Optional)
-- ========================================

-- Ensure is_active is NOT NULL
ALTER TABLE users MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE branches MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- ========================================
-- PHASE 5: VERIFICATION
-- ========================================

-- Check column existence
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'washify_db'
  AND COLUMN_NAME IN ('deleted_at', 'is_active', 'created_at', 'updated_at')
  AND TABLE_NAME IN ('users', 'branches', 'services', 'orders', 'promotions', 'shippers')
ORDER BY TABLE_NAME, COLUMN_NAME;

-- Count records by status
SELECT 'users' as table_name,
    COUNT(*) as total,
    SUM(CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END) as active,
    SUM(CASE WHEN deleted_at IS NOT NULL THEN 1 ELSE 0 END) as deleted
FROM users
UNION ALL
SELECT 'branches',
    COUNT(*),
    SUM(CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END),
    SUM(CASE WHEN deleted_at IS NOT NULL THEN 1 ELSE 0 END)
FROM branches
UNION ALL
SELECT 'services',
    COUNT(*),
    SUM(CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END),
    SUM(CASE WHEN deleted_at IS NOT NULL THEN 1 ELSE 0 END)
FROM services
UNION ALL
SELECT 'orders',
    COUNT(*),
    SUM(CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END),
    SUM(CASE WHEN deleted_at IS NOT NULL THEN 1 ELSE 0 END)
FROM orders
UNION ALL
SELECT 'promotions',
    COUNT(*),
    SUM(CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END),
    SUM(CASE WHEN deleted_at IS NOT NULL THEN 1 ELSE 0 END)
FROM promotions
UNION ALL
SELECT 'shippers',
    COUNT(*),
    SUM(CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END),
    SUM(CASE WHEN deleted_at IS NOT NULL THEN 1 ELSE 0 END)
FROM shippers;

-- Check indexes
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = 'washify_db'
  AND INDEX_NAME LIKE '%deleted_at%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

-- ========================================
-- ROLLBACK SCRIPT (Use if needed)
-- ========================================

/*
-- CAUTION: This will remove all soft delete functionality!
-- Only run if you need to rollback the migration.

USE washify_db;

SET FOREIGN_KEY_CHECKS=0;

-- Drop indexes
DROP INDEX IF EXISTS idx_users_deleted_at ON users;
DROP INDEX IF EXISTS idx_branches_deleted_at ON branches;
DROP INDEX IF EXISTS idx_services_deleted_at ON services;
DROP INDEX IF EXISTS idx_orders_deleted_at ON orders;
DROP INDEX IF EXISTS idx_promotions_deleted_at ON promotions;
DROP INDEX IF EXISTS idx_shippers_deleted_at ON shippers;
DROP INDEX IF EXISTS idx_users_active_deleted ON users;
DROP INDEX IF EXISTS idx_branches_active_deleted ON branches;

-- Drop columns
ALTER TABLE users DROP COLUMN IF EXISTS deleted_at;
ALTER TABLE users DROP COLUMN IF EXISTS is_active;
ALTER TABLE users DROP COLUMN IF EXISTS created_at;
ALTER TABLE users DROP COLUMN IF EXISTS updated_at;

ALTER TABLE branches DROP COLUMN IF EXISTS deleted_at;
ALTER TABLE branches DROP COLUMN IF EXISTS is_active;
ALTER TABLE branches DROP COLUMN IF EXISTS created_at;
ALTER TABLE branches DROP COLUMN IF EXISTS updated_at;

ALTER TABLE services DROP COLUMN IF EXISTS deleted_at;
ALTER TABLE services DROP COLUMN IF EXISTS created_at;
ALTER TABLE services DROP COLUMN IF EXISTS updated_at;

ALTER TABLE orders DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE promotions DROP COLUMN IF EXISTS deleted_at;
ALTER TABLE promotions DROP COLUMN IF EXISTS created_at;
ALTER TABLE promotions DROP COLUMN IF EXISTS updated_at;

ALTER TABLE shippers DROP COLUMN IF EXISTS deleted_at;
ALTER TABLE shippers DROP COLUMN IF EXISTS created_at;
ALTER TABLE shippers DROP COLUMN IF EXISTS updated_at;

SET FOREIGN_KEY_CHECKS=1;
*/

-- ========================================
-- MIGRATION COMPLETE
-- ========================================

SELECT '✅ Soft Delete Migration Completed Successfully!' as status;
SELECT 'Next Step: Restart Spring Boot application to use new soft delete features' as next_action;
