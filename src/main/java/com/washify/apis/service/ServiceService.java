package com.washify.apis.service;

import com.washify.apis.annotation.Audited;
import com.washify.apis.dto.request.ServiceRequest;
import com.washify.apis.dto.response.ServiceResponse;
import com.washify.apis.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Service (dịch vụ giặt là)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceService {
    
    private final ServiceRepository serviceRepository;
    
    /**
     * Tạo dịch vụ mới
     */
    @Audited(action = "CREATE_SERVICE", entityType = "Service", description = "Tạo dịch vụ giặt ủi mới")
    public ServiceResponse createService(ServiceRequest request) {
        com.washify.apis.entity.Service service = new com.washify.apis.entity.Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setEstimatedTime(request.getEstimatedTime());
        service.setIsActive(request.getIsActive());
        
        com.washify.apis.entity.Service savedService = serviceRepository.save(service);
        return mapToServiceResponse(savedService);
    }
    
    /**
     * Lấy thông tin dịch vụ theo ID
     */
    @Transactional(readOnly = true)
    public ServiceResponse getServiceById(Long serviceId) {
        com.washify.apis.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + serviceId));
        return mapToServiceResponse(service);
    }
    
    /**
     * Lấy tất cả dịch vụ
     */
    @Transactional(readOnly = true)
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::mapToServiceResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy các dịch vụ đang hoạt động
     */
    @Transactional(readOnly = true)
    public List<ServiceResponse> getActiveServices() {
        return serviceRepository.findByIsActive(true).stream()
                .map(this::mapToServiceResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật dịch vụ
     */
    @Audited(action = "UPDATE_SERVICE", entityType = "Service", description = "Cập nhật thông tin dịch vụ")
    public ServiceResponse updateService(Long serviceId, ServiceRequest request) {
        com.washify.apis.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + serviceId));
        
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setEstimatedTime(request.getEstimatedTime());
        service.setIsActive(request.getIsActive());
        
        com.washify.apis.entity.Service updatedService = serviceRepository.save(service);
        return mapToServiceResponse(updatedService);
    }
    
    /**
     * Xóa dịch vụ
     */
    @Audited(action = "DELETE_SERVICE", entityType = "Service", description = "Xóa dịch vụ")
    public void deleteService(Long serviceId) {
        if (!serviceRepository.existsById(serviceId)) {
            throw new RuntimeException("Không tìm thấy dịch vụ với ID: " + serviceId);
        }
        serviceRepository.deleteById(serviceId);
    }
    
    /**
     * Tìm kiếm dịch vụ theo tên
     */
    @Transactional(readOnly = true)
    public List<ServiceResponse> searchServicesByName(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToServiceResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private ServiceResponse mapToServiceResponse(com.washify.apis.entity.Service service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .estimatedTime(service.getEstimatedTime())
                .isActive(service.getIsActive())
                .deletedAt(service.getDeletedAt())
                .build();
    }
    
    // ========================================
    // PHASE 3: ADVANCED SEARCH METHODS
    // ========================================
    
    /**
     * Tìm kiếm dịch vụ theo nhiều tiêu chí
     */
    @Transactional(readOnly = true)
    public List<ServiceResponse> advancedSearch(String name, Double minPrice, Double maxPrice, Boolean isActive) {
        // Validate price range
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new RuntimeException("Giá tối thiểu không thể lớn hơn giá tối đa");
        }
        
        List<com.washify.apis.entity.Service> services = serviceRepository.advancedSearch(
            name, minPrice, maxPrice, isActive
        );
        
        return services.stream()
                .map(this::mapToServiceResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy dịch vụ theo khoảng giá
     */
    @Transactional(readOnly = true)
    public List<ServiceResponse> getServicesByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice < 0 || maxPrice < 0) {
            throw new RuntimeException("Giá không thể âm");
        }
        if (minPrice > maxPrice) {
            throw new RuntimeException("Giá tối thiểu không thể lớn hơn giá tối đa");
        }
        
        List<com.washify.apis.entity.Service> services = serviceRepository.findByPriceRange(minPrice, maxPrice);
        return services.stream()
                .map(this::mapToServiceResponse)
                .collect(Collectors.toList());
    }
}
