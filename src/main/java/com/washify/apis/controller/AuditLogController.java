package com.washify.apis.controller;

import com.washify.apis.dto.response.AuditLogResponse;
import com.washify.apis.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller quản lý Audit Logs
 * Cung cấp các API để truy vấn nhật ký hoạt động hệ thống
 * CHỈ dành cho Admin - quan trọng cho security và compliance
 */
@RestController
@RequestMapping("/audit-logs")
@Tag(name = "Audit Log Management", description = "APIs quản lý nhật ký hoạt động hệ thống")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Lấy tất cả audit logs (sắp xếp theo thời gian mới nhất)
     * @return Danh sách AuditLogResponse
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Lấy tất cả audit logs",
        description = "Lấy toàn bộ nhật ký hoạt động, sắp xếp theo thời gian mới nhất. Chỉ ADMIN mới có quyền truy cập."
    )
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        List<AuditLogResponse> auditLogs = auditLogService.getAllAuditLogs();
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Lấy audit log theo ID
     * @param id ID của audit log
     * @return AuditLogResponse
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Lấy audit log theo ID",
        description = "Lấy thông tin chi tiết của một audit log. Chỉ ADMIN mới có quyền truy cập."
    )
    public ResponseEntity<AuditLogResponse> getAuditLogById(@PathVariable Long id) {
        AuditLogResponse auditLog = auditLogService.getAuditLogById(id);
        return ResponseEntity.ok(auditLog);
    }

    /**
     * Lấy audit logs của một user
     * @param userId ID của user
     * @return Danh sách AuditLogResponse
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Lấy audit logs theo user",
        description = "Lấy tất cả nhật ký hoạt động của một user cụ thể. Chỉ ADMIN mới có quyền truy cập."
    )
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByUser(@PathVariable Long userId) {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogsByUserId(userId);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Lấy audit logs theo loại entity
     * @param entityType Loại entity (Order, User, Payment, etc.)
     * @return Danh sách AuditLogResponse
     */
    @GetMapping("/entity-type/{entityType}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Lấy audit logs theo loại entity",
        description = "Lấy nhật ký theo loại entity (Order, User, Payment, Service, etc.). Chỉ ADMIN mới có quyền truy cập."
    )
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntityType(@PathVariable String entityType) {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogsByEntityType(entityType);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Lấy audit logs của một entity cụ thể
     * @param entityType Loại entity
     * @param entityId ID của entity
     * @return Danh sách AuditLogResponse
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Lấy audit logs của một entity cụ thể",
        description = "Lấy tất cả nhật ký thay đổi của một entity cụ thể (VD: Order #123). Chỉ ADMIN mới có quyền truy cập."
    )
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogsByEntity(entityType, entityId);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Lấy audit logs theo action
     * @param action Hành động (CREATE, UPDATE, DELETE)
     * @return Danh sách AuditLogResponse
     */
    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Lấy audit logs theo action",
        description = "Lấy nhật ký theo hành động (CREATE, UPDATE, DELETE). Chỉ ADMIN mới có quyền truy cập."
    )
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByAction(@PathVariable String action) {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogsByAction(action);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Lấy audit logs trong khoảng thời gian
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd'T'HH:mm:ss)
     * @return Danh sách AuditLogResponse
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Lấy audit logs theo khoảng thời gian",
        description = "Lấy nhật ký trong khoảng thời gian cụ thể. Format: yyyy-MM-dd'T'HH:mm:ss. Chỉ ADMIN mới có quyền truy cập."
    )
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Lấy audit logs của user trong khoảng thời gian
     * @param userId ID của user
     * @param startDate Ngày bắt đầu (format: yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate Ngày kết thúc (format: yyyy-MM-dd'T'HH:mm:ss)
     * @return Danh sách AuditLogResponse
     */
    @GetMapping("/user/{userId}/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Lấy audit logs của user theo khoảng thời gian",
        description = "Lấy nhật ký của một user trong khoảng thời gian cụ thể. Format: yyyy-MM-dd'T'HH:mm:ss. Chỉ ADMIN mới có quyền truy cập."
    )
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogsByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(auditLogs);
    }
}
