package com.washify.apis.service;

import com.washify.apis.dto.request.RoleRequest;
import com.washify.apis.dto.response.RoleResponse;
import com.washify.apis.entity.Role;
import com.washify.apis.repository.RoleRepository;
import com.washify.apis.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Role (vai trò người dùng)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    
    /**
     * Tạo role mới
     */
    public RoleResponse createRole(RoleRequest request) {
        // Kiểm tra role name đã tồn tại chưa
        if (roleRepository.existsByName(request.getName().toUpperCase())) {
            throw new RuntimeException("Role '" + request.getName() + "' đã tồn tại");
        }
        
        Role role = new Role();
        role.setName(request.getName().toUpperCase()); // Chuyển về uppercase
        role.setDescription(request.getDescription());
        
        Role savedRole = roleRepository.save(role);
        return mapToRoleResponse(savedRole);
    }
    
    /**
     * Lấy role theo ID
     */
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role với ID: " + id));
        return mapToRoleResponse(role);
    }
    
    /**
     * Lấy role theo name
     */
    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String name) {
        Role role = roleRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role với tên: " + name));
        return mapToRoleResponse(role);
    }
    
    /**
     * Lấy tất cả roles
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật role
     */
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role với ID: " + id));
        
        // Nếu đổi name, kiểm tra name mới đã tồn tại chưa
        if (!role.getName().equalsIgnoreCase(request.getName())) {
            if (roleRepository.existsByName(request.getName().toUpperCase())) {
                throw new RuntimeException("Role '" + request.getName() + "' đã tồn tại");
            }
            role.setName(request.getName().toUpperCase());
        }
        
        role.setDescription(request.getDescription());
        
        Role updatedRole = roleRepository.save(role);
        return mapToRoleResponse(updatedRole);
    }
    
    /**
     * Xóa role
     */
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role với ID: " + id));
        
        // Kiểm tra xem có user nào đang dùng role này không
        long userCount = userRoleRepository.countByRoleId(id);
        if (userCount > 0) {
            throw new RuntimeException("Không thể xóa role vì còn " + userCount + " user đang sử dụng");
        }
        
        roleRepository.delete(role);
    }
    
    /**
     * Đếm số user có role này
     */
    @Transactional(readOnly = true)
    public long countUsersByRoleId(Long roleId) {
        return userRoleRepository.countByRoleId(roleId);
    }
    
    /**
     * Map entity sang response DTO
     */
    private RoleResponse mapToRoleResponse(Role role) {
        long userCount = userRoleRepository.countByRoleId(role.getId());
        
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .userCount(userCount)
                .build();
    }
}
