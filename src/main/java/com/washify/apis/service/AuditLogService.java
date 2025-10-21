package com.washify.apis.service;

import com.washify.apis.dto.response.AuditLogResponse;
import com.washify.apis.entity.AuditLog;
import com.washify.apis.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Audit Logs
 * Cung cấp các phương thức quản lý và truy vấn audit trail
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    
    /**
     * Lấy tất cả audit logs, sắp xếp theo thời gian mới nhất
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy audit log theo ID
     */
    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy audit log với ID: " + id));
        return mapToAuditLogResponse(auditLog);
    }
    
    /**
     * Lấy audit logs của một user
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId).stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy audit logs theo loại entity
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType).stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy audit logs của một entity cụ thể
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy audit logs theo action
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action).stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy audit logs trong khoảng thời gian
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy audit logs của user trong khoảng thời gian
     */
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByUserAndDateRange(
            Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate).stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Tạo audit log mới (sử dụng bởi các service khác)
     */
    @Transactional
    public AuditLog createAuditLog(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Map AuditLog entity sang AuditLogResponse DTO
     */
    private AuditLogResponse mapToAuditLogResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .username(auditLog.getUser() != null ? auditLog.getUser().getUsername() : "SYSTEM")
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .description(auditLog.getDescription())
                .status(auditLog.getStatus())
                .errorMessage(auditLog.getErrorMessage())
                .details(buildDetails(auditLog))
                .timestamp(auditLog.getCreatedAt())
                .build();
    }
    
    /**
     * Build chi tiết audit log từ old/new values
     */
    private String buildDetails(AuditLog auditLog) {
        StringBuilder details = new StringBuilder();
        
        if (auditLog.getOldValue() != null && !auditLog.getOldValue().isEmpty()) {
            details.append("Old: ").append(auditLog.getOldValue());
        }
        
        if (auditLog.getNewValue() != null && !auditLog.getNewValue().isEmpty()) {
            if (details.length() > 0) {
                details.append(" | ");
            }
            details.append("New: ").append(auditLog.getNewValue());
        }
        
        return details.length() > 0 ? details.toString() : "No details available";
    }
}
