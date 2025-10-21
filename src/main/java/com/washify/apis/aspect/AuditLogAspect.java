package com.washify.apis.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.washify.apis.annotation.Audited;
import com.washify.apis.entity.AuditLog;
import com.washify.apis.entity.User;
import com.washify.apis.repository.AuditLogRepository;
import com.washify.apis.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * AOP Aspect để tự động ghi audit log cho các method có @Audited annotation
 * 
 * Cách hoạt động:
 * 1. Intercept các method có @Audited
 * 2. Lấy thông tin user đang logged in từ SecurityContext
 * 3. Lấy thông tin IP address từ HTTP request
 * 4. Capture return value của method (entity ID)
 * 5. Ghi audit log vào database
 * 
 * @author Washify Team
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Advice chạy sau khi method có @Audited return successfully
     * 
     * @param joinPoint Thông tin về method được intercept
     * @param audited Annotation @Audited
     * @param result Giá trị trả về của method
     */
    @AfterReturning(
        pointcut = "@annotation(audited)",
        returning = "result"
    )
    public void logAudit(JoinPoint joinPoint, Audited audited, Object result) {
        try {
            // 1. Lấy thông tin user hiện tại
            User currentUser = getCurrentUser();
            
            // 2. Lấy entity ID từ result
            Long entityId = extractEntityId(result);
            
            // 3. Serialize result to JSON
            String newValue = serializeToJson(result);
            
            // 4. Lấy thông tin HTTP request
            String ipAddress = getClientIpAddress();
            String userAgent = getUserAgent();
            
            // 5. Tạo audit log
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(currentUser);
            auditLog.setEntityType(audited.entityType());
            auditLog.setEntityId(entityId);
            auditLog.setAction(audited.action());
            auditLog.setOldValue(null); // Old value tracking can be added later if needed
            auditLog.setNewValue(newValue);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setDescription(audited.description());
            auditLog.setStatus("SUCCESS");
            
            // 6. Lưu vào database
            auditLogRepository.save(auditLog);
            
            log.info("Audit log created: {} {} by user {}", 
                audited.action(), audited.entityType(), 
                currentUser != null ? currentUser.getUsername() : "SYSTEM");
                
        } catch (Exception e) {
            // Không throw exception để không ảnh hưởng business logic
            log.error("Failed to create audit log", e);
        }
    }
    
    /**
     * Lấy user hiện tại từ SecurityContext và load từ database
     */
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                // Load actual User entity from database to avoid TransientPropertyValueException
                return userRepository.findByUsername(username).orElse(null);
            }
            
            return null;
        } catch (Exception e) {
            log.error("Failed to get current user", e);
            return null;
        }
    }
    
    /**
     * Extract entity ID từ return value
     * Assumes entity có method getId()
     */
    private Long extractEntityId(Object result) {
        if (result == null) {
            return null;
        }
        
        try {
            Method getIdMethod = result.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(result);
            
            if (id instanceof Long) {
                return (Long) id;
            } else if (id instanceof Integer) {
                return ((Integer) id).longValue();
            }
        } catch (Exception e) {
            log.debug("Could not extract entity ID from result", e);
        }
        
        return null;
    }
    
    /**
     * Serialize object thành JSON string
     */
    private String serializeToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
            return obj.toString();
        }
    }
    
    /**
     * Lấy IP address từ HTTP request
     * Xử lý cả trường hợp có proxy/load balancer
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Kiểm tra X-Forwarded-For header (khi có proxy/load balancer)
                String ip = request.getHeader("X-Forwarded-For");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    // X-Forwarded-For có thể chứa nhiều IP, lấy IP đầu tiên
                    return ip.split(",")[0].trim();
                }
                
                // Kiểm tra X-Real-IP header
                ip = request.getHeader("X-Real-IP");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
                
                // Fallback về remote address
                return request.getRemoteAddr();
            }
            
            return null;
        } catch (Exception e) {
            log.error("Failed to get client IP address", e);
            return null;
        }
    }
    
    /**
     * Lấy User Agent từ HTTP request
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
            
            return null;
        } catch (Exception e) {
            log.error("Failed to get user agent", e);
            return null;
        }
    }
}
