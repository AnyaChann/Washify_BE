package com.washify.apis.controller;

import com.washify.apis.dto.request.ServiceRequest;
import com.washify.apis.dto.response.ApiResponse;
import com.washify.apis.dto.response.ServiceResponse;
import com.washify.apis.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến Service (dịch vụ giặt là)
 */
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {
    
    private final ServiceService serviceService;
    
    /**
     * Tạo dịch vụ mới
     * POST /api/services
     * Chỉ Admin và Staff
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(@Valid @RequestBody ServiceRequest request) {
        ServiceResponse service = serviceService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service, "Tạo dịch vụ thành công"));
    }
    
    /**
     * Lấy thông tin dịch vụ theo ID
     * GET /api/services/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(@PathVariable Long id) {
        ServiceResponse service = serviceService.getServiceById(id);
        return ResponseEntity.ok(ApiResponse.success(service, "Lấy thông tin dịch vụ thành công"));
    }
    
    /**
     * Lấy tất cả dịch vụ
     * GET /api/services
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getAllServices() {
        List<ServiceResponse> services = serviceService.getAllServices();
        return ResponseEntity.ok(ApiResponse.success(services, "Lấy danh sách dịch vụ thành công"));
    }
    
    /**
     * Lấy các dịch vụ đang hoạt động
     * GET /api/services/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getActiveServices() {
        List<ServiceResponse> services = serviceService.getActiveServices();
        return ResponseEntity.ok(ApiResponse.success(services, "Lấy danh sách dịch vụ hoạt động thành công"));
    }
    
    /**
     * Tìm kiếm dịch vụ theo tên
     * GET /api/services/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> searchServicesByName(@RequestParam String name) {
        List<ServiceResponse> services = serviceService.searchServicesByName(name);
        return ResponseEntity.ok(ApiResponse.success(services, "Tìm kiếm dịch vụ thành công"));
    }
    
    /**
     * Cập nhật dịch vụ
     * PUT /api/services/{id}
     * Chỉ Admin và Staff
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request) {
        ServiceResponse service = serviceService.updateService(id, request);
        return ResponseEntity.ok(ApiResponse.success(service, "Cập nhật dịch vụ thành công"));
    }
    
    /**
     * Xóa dịch vụ
     * DELETE /api/services/{id}
     * Chỉ Admin và Staff
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa dịch vụ thành công"));
    }
}
