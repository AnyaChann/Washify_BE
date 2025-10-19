-- Migration: Add username column to users table
-- Date: 2025-10-19
-- Description: Add username field for authentication

-- Step 1: Add username column (nullable first)
ALTER TABLE users 
ADD COLUMN username VARCHAR(50) UNIQUE AFTER full_name;

-- Step 2: Set default username based on email (temporary)
-- You should update these manually with proper usernames
UPDATE users 
SET username = SUBSTRING_INDEX(email, '@', 1) 
WHERE username IS NULL;

-- Step 3: Make username NOT NULL after data is populated
ALTER TABLE users 
MODIFY COLUMN username VARCHAR(50) NOT NULL UNIQUE;

-- Note: Nếu bạn muốn username tự động từ email:
-- Example: 'john.doe@example.com' -> 'john.doe'
-- Hoặc bạn có thể update thủ công cho từng user
