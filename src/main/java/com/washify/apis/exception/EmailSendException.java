package com.washify.apis.exception;

/**
 * Exception khi không thể gửi email
 */
public class EmailSendException extends RuntimeException {
    
    public EmailSendException(String message) {
        super(message);
    }
    
    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
