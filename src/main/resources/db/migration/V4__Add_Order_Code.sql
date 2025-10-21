-- Migration script to add order_code to orders table
-- Version: V4__Add_Order_Code.sql

-- Add order_code column
ALTER TABLE orders
ADD COLUMN order_code VARCHAR(50) UNIQUE COMMENT 'Mã đơn hàng (VD: WF202510210001)';

-- Update existing orders with generated order_code
UPDATE orders
SET order_code = CONCAT('WF', DATE_FORMAT(order_date, '%Y%m%d'), LPAD(id, 4, '0'))
WHERE order_code IS NULL;

-- Make order_code NOT NULL after populating existing data
ALTER TABLE orders
MODIFY COLUMN order_code VARCHAR(50) UNIQUE NOT NULL COMMENT 'Mã đơn hàng (VD: WF202510210001)';

-- Create index for faster lookup
CREATE INDEX idx_orders_order_code ON orders(order_code);
