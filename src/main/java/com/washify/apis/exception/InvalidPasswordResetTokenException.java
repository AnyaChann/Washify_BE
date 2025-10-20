package com.washify.apis.exception;

/**
 * Exception khi token reset password không hợp lệ
 */
public class InvalidPasswordResetTokenException extends RuntimeException {
    
    public InvalidPasswordResetTokenException(String message) {
        super(message);
    }
    
    public InvalidPasswordResetTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
