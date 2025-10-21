-- Migration script to add MoMo payment support
-- Version: V2__Add_MoMo_Payment_Support.sql

-- Add new columns to payments table
ALTER TABLE payments
ADD COLUMN transaction_id VARCHAR(255) COMMENT 'Mã giao dịch từ MoMo',
ADD COLUMN payment_url VARCHAR(500) COMMENT 'URL thanh toán MoMo',
ADD COLUMN qr_code TEXT COMMENT 'QR code cho thanh toán MoMo',
ADD COLUMN gateway_response TEXT COMMENT 'Response từ MoMo (JSON format)';

-- Update payment_method enum to support new payment methods
-- Note: In MySQL, we need to modify the enum
ALTER TABLE payments
MODIFY COLUMN payment_method ENUM('CASH', 'MOMO') NOT NULL
COMMENT 'Phương thức thanh toán: CASH=Tiền mặt (Tại quầy/COD), MOMO=Ví MoMo';

-- Add index for faster lookup by transaction_id
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);

-- Add index for payment_method for statistics queries
CREATE INDEX idx_payments_payment_method ON payments(payment_method);

-- Comments for documentation
ALTER TABLE payments 
MODIFY COLUMN transaction_id VARCHAR(255) COMMENT 'Mã giao dịch từ MoMo',
MODIFY COLUMN payment_url VARCHAR(500) COMMENT 'URL redirect để customer thanh toán qua MoMo',
MODIFY COLUMN qr_code TEXT COMMENT 'QR code string cho thanh toán MoMo',
MODIFY COLUMN gateway_response TEXT COMMENT 'Full response JSON từ MoMo gateway để audit';
