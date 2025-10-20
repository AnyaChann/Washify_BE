-- Add 2FA setting for password change to users table

ALTER TABLE users 
ADD COLUMN require_email_verification_for_password_change BOOLEAN DEFAULT FALSE COMMENT 'Bảo mật 2 lớp: yêu cầu verify email khi đổi password';

-- Update existing users to have default value (OFF)
UPDATE users 
SET require_email_verification_for_password_change = FALSE 
WHERE require_email_verification_for_password_change IS NULL;
