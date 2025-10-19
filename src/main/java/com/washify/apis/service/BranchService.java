package com.washify.apis.service;

import com.washify.apis.dto.request.BranchRequest;
import com.washify.apis.dto.response.BranchResponse;
import com.washify.apis.entity.Branch;
import com.washify.apis.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Branch
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BranchService {
    
    private final BranchRepository branchRepository;
    
    /**
     * Tạo chi nhánh mới
     */
    public BranchResponse createBranch(BranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setManagerName(request.getManagerName());
        
        Branch savedBranch = branchRepository.save(branch);
        return mapToBranchResponse(savedBranch);
    }
    
    /**
     * Lấy thông tin chi nhánh theo ID
     */
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId));
        return mapToBranchResponse(branch);
    }
    
    /**
     * Lấy tất cả chi nhánh
     */
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::mapToBranchResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật chi nhánh
     */
    public BranchResponse updateBranch(Long branchId, BranchRequest request) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId));
        
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setManagerName(request.getManagerName());
        
        Branch updatedBranch = branchRepository.save(branch);
        return mapToBranchResponse(updatedBranch);
    }
    
    /**
     * Xóa chi nhánh
     */
    public void deleteBranch(Long branchId) {
        if (!branchRepository.existsById(branchId)) {
            throw new RuntimeException("Không tìm thấy chi nhánh với ID: " + branchId);
        }
        branchRepository.deleteById(branchId);
    }
    
    /**
     * Map Entity sang DTO Response
     */
    private BranchResponse mapToBranchResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .managerName(branch.getManagerName())
                .createdAt(branch.getCreatedAt())
                .build();
    }
}
