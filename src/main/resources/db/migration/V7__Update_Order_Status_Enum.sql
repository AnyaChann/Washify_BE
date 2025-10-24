-- Migration script to update old OrderStatus values to new enum values
-- This script handles the transition from the old inner enum to the new unified enum

-- Update old IN_PROGRESS status to PROCESSING
UPDATE orders 
SET status = 'PROCESSING' 
WHERE status = 'IN_PROGRESS';

-- Log the number of updated records
SELECT 'Orders updated from IN_PROGRESS to PROCESSING' as message, ROW_COUNT() as affected_rows;