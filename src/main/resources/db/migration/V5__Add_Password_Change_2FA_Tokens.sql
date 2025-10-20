-- Migration V5: Add password_change_2fa_tokens table for email confirmation when toggling 2FA setting
-- This table stores tokens for confirming enable/disable of email verification for password changes

CREATE TABLE password_change_2fa_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    enable_2fa BOOLEAN NOT NULL COMMENT 'True = enabling 2FA, False = disabling 2FA',
    expiry_date DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_2fa_token_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expiry (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tokens for email confirmation of 2FA toggle';
