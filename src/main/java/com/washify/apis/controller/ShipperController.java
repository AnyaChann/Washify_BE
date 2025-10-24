package com.washify.apis.controller;

import com.washify.apis.dto.request.ShipperRequest;
import com.washify.apis.dto.response.ShipperResponse;
import com.washify.apis.service.ShipperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller quản lý Shippers (người giao hàng)
 * Cung cấp các API để quản lý thông tin và trạng thái của shipper
 */
@RestController
@RequestMapping("/shippers")
@Tag(name = "Shipper Management", description = "APIs quản lý người giao hàng")
public class ShipperController {

    @Autowired
    private ShipperService shipperService;

    /**
     * Tạo mới shipper
     * @param request Thông tin shipper cần tạo
     * @return ShipperResponse với status 201 Created
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Tạo mới shipper",
        description = "Tạo tài khoản shipper mới. Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<ShipperResponse> createShipper(@Valid @RequestBody ShipperRequest request) {
        ShipperResponse response = shipperService.createShipper(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Lấy danh sách tất cả shipper
     * @return Danh sách ShipperResponse
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Lấy danh sách shipper",
        description = "Lấy danh sách tất cả shipper. Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<List<ShipperResponse>> getAllShippers() {
        List<ShipperResponse> shippers = shipperService.getAllShippers();
        return ResponseEntity.ok(shippers);
    }

    /**
     * Lấy danh sách shippers đang hoạt động
     * @return Danh sách ShipperResponse có isActive = true
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Lấy danh sách shippers đang hoạt động",
        description = "Lấy danh sách các shippers đang trong trạng thái hoạt động (isActive = true). Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<List<ShipperResponse>> getActiveShippers() {
        List<ShipperResponse> shippers = shipperService.getActiveShippers();
        return ResponseEntity.ok(shippers);
    }

    /**
     * Lấy thông tin chi tiết shipper theo ID
     * @param id ID của shipper
     * @return ShipperResponse
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Lấy thông tin shipper theo ID",
        description = "Lấy thông tin chi tiết của một shipper. Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<ShipperResponse> getShipperById(@PathVariable Long id) {
        ShipperResponse response = shipperService.getShipperById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Tìm kiếm shippers theo số điện thoại
     * @param phone Số điện thoại cần tìm (tìm kiếm gần đúng)
     * @return Danh sách ShipperResponse phù hợp
     */
    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Tìm shippers theo số điện thoại",
        description = "Tìm kiếm shippers có số điện thoại chứa từ khóa. Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<List<ShipperResponse>> getShippersByPhone(@PathVariable String phone) {
        List<ShipperResponse> shippers = shipperService.findByPhone(phone);
        return ResponseEntity.ok(shippers);
    }

    /**
     * Tìm kiếm shippers theo tên
     * @param name Tên cần tìm kiếm (case-insensitive, tìm kiếm gần đúng)
     * @return Danh sách ShipperResponse phù hợp
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Tìm shippers theo tên",
        description = "Tìm kiếm shippers có tên chứa từ khóa (không phân biệt chữ hoa/thường). Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<List<ShipperResponse>> getShippersByName(@PathVariable String name) {
        List<ShipperResponse> shippers = shipperService.findByName(name);
        return ResponseEntity.ok(shippers);
    }

    /**
     * Lấy thống kê số lượng shipments của shipper
     * @param id ID của shipper
     * @return ShipperStatistics chứa tổng số shipments, hoàn thành, đang giao
     */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Lấy thống kê shipments của shipper",
        description = "Lấy thống kê số lượng shipments: tổng số, đã hoàn thành, đang giao hàng. Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<ShipperService.ShipperStatistics> getShipperStatistics(@PathVariable Long id) {
        ShipperService.ShipperStatistics statistics = shipperService.getShipperStatistics(id);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Cập nhật thông tin shipper
     * @param id ID của shipper cần cập nhật
     * @param request Thông tin cập nhật
     * @return ShipperResponse đã được cập nhật
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Cập nhật thông tin shipper",
        description = "Cập nhật thông tin của shipper. Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<ShipperResponse> updateShipper(
            @PathVariable Long id,
            @Valid @RequestBody ShipperRequest request) {
        ShipperResponse response = shipperService.updateShipper(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Kích hoạt shipper (set isActive = true)
     * @param id ID của shipper cần kích hoạt
     * @return ShipperResponse đã được kích hoạt
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Kích hoạt shipper",
        description = "Đặt trạng thái shipper thành active (isActive = true). Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<ShipperResponse> activateShipper(@PathVariable Long id) {
        ShipperResponse response = shipperService.activateShipper(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Vô hiệu hóa shipper (set isActive = false)
     * @param id ID của shipper cần vô hiệu hóa
     * @return ShipperResponse đã được vô hiệu hóa
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Operation(
        summary = "Vô hiệu hóa shipper",
        description = "Đặt trạng thái shipper thành inactive (isActive = false). Yêu cầu quyền ADMIN, STAFF hoặc MANAGER."
    )
    public ResponseEntity<ShipperResponse> deactivateShipper(@PathVariable Long id) {
        ShipperResponse response = shipperService.deactivateShipper(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa shipper (chỉ xóa được nếu không có shipments đang hoạt động)
     * @param id ID của shipper cần xóa
     * @return ResponseEntity với status 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Xóa shipper",
        description = "Xóa shipper khỏi hệ thống. Chỉ xóa được nếu shipper không có shipments đang giao. Yêu cầu quyền ADMIN."
    )
    public ResponseEntity<Void> deleteShipper(@PathVariable Long id) {
        shipperService.deleteShipper(id);
        return ResponseEntity.noContent().build();
    }
}
