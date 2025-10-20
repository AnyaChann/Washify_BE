# Fix: Thêm deletedAt vào Response DTOs

## Vấn đề
Controllers không trả về thông tin `deletedAt` trong response, khiến client không biết được entity đã bị xóa mềm hay chưa.

## Giải pháp

### 1. Thêm field `deletedAt` vào Response DTOs

#### ServiceResponse.java
```java
private Boolean isActive;
private LocalDateTime deletedAt;  // ✅ ADDED
```

#### BranchResponse.java
```java
private Boolean isActive;          // ✅ ADDED
private LocalDateTime createdAt;
private LocalDateTime deletedAt;   // ✅ ADDED
```

#### PromotionResponse.java
```java
private Boolean isActive;
private LocalDateTime deletedAt;   // ✅ ADDED
```

#### ShipperResponse.java
```java
private java.time.LocalDateTime updatedAt;
private java.time.LocalDateTime deletedAt;  // ✅ ADDED
```

#### UserResponse.java
```java
// ✅ ĐÃ CÓ từ trước
private LocalDateTime deletedAt;
```

---

### 2. Update Service mappers

#### ServiceService.java
```java
private ServiceResponse mapToServiceResponse(Service service) {
    return ServiceResponse.builder()
            .id(service.getId())
            .name(service.getName())
            .description(service.getDescription())
            .price(service.getPrice())
            .estimatedTime(service.getEstimatedTime())
            .isActive(service.getIsActive())
            .deletedAt(service.getDeletedAt())  // ✅ ADDED
            .build();
}
```

#### BranchService.java
```java
private BranchResponse mapToBranchResponse(Branch branch) {
    return BranchResponse.builder()
            .id(branch.getId())
            .name(branch.getName())
            .address(branch.getAddress())
            .phone(branch.getPhone())
            .managerName(branch.getManagerName())
            .isActive(branch.getIsActive())      // ✅ ADDED
            .createdAt(branch.getCreatedAt())
            .deletedAt(branch.getDeletedAt())    // ✅ ADDED
            .build();
}
```

---

### 3. Update SoftDeleteController

#### Import thêm các Response DTOs
```java
import com.washify.apis.dto.response.*;  // ✅ CHANGED (thay vì import riêng lẻ)
```

#### Thêm mapper methods
```java
// ✅ ADDED
private ServiceResponse mapToServiceResponse(Service service) {
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

// ✅ ADDED
private BranchResponse mapToBranchResponse(Branch branch) { ... }

// ✅ ADDED
private PromotionResponse mapToPromotionResponse(Promotion promotion) { ... }

// ✅ ADDED
private ShipperResponse mapToShipperResponse(Shipper shipper) { ... }
```

#### Update endpoints để dùng Response DTOs

**TRƯỚC:**
```java
@GetMapping("/services/deleted")
public ResponseEntity<ApiResponse<List<Service>>> getDeletedServices() {
    List<Service> services = softDeleteService.getDeletedServices();
    return ResponseEntity.ok(ApiResponse.success(services, "..."));
}
```

**SAU:**
```java
@GetMapping("/services/deleted")
public ResponseEntity<ApiResponse<List<ServiceResponse>>> getDeletedServices() {  // ✅ Changed return type
    List<Service> services = softDeleteService.getDeletedServices();
    List<ServiceResponse> response = services.stream()
            .map(this::mapToServiceResponse)  // ✅ Added mapping
            .collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(response, "..."));
}
```

Tương tự cho:
- ✅ `/branches/deleted`
- ✅ `/promotions/deleted`
- ✅ `/shippers/deleted`
- ✅ `/users/deleted` (đã có mapper từ trước, chỉ cần thêm deletedAt)

---

## Lợi ích

### 1. **Best Practice**
- ✅ Không expose Entity trực tiếp ra ngoài
- ✅ Tách biệt persistence layer và presentation layer
- ✅ Dễ maintain khi thay đổi Entity

### 2. **Tránh LazyInitializationException**
- ✅ Response DTO chỉ chứa data cần thiết
- ✅ Không có references đến collections chưa load

### 3. **Thông tin đầy đủ**
- ✅ Client nhận được `deletedAt` để hiển thị thời điểm xóa
- ✅ Client nhận được `isActive` để biết trạng thái
- ✅ Có thể hiển thị "Đã xóa lúc: 2025-10-20 14:30:00"

---

## API Response Example

### GET /api/soft-delete/services/deleted

**Response:**
```json
{
  "success": true,
  "message": "Lấy danh sách services đã xóa thành công",
  "data": [
    {
      "id": 1,
      "name": "Giặt hấp",
      "description": "Giặt hấp quần áo",
      "price": 50000,
      "estimatedTime": 120,
      "isActive": false,
      "deletedAt": "2025-10-20T14:30:00"  // ✅ Có thông tin thời gian xóa
    }
  ]
}
```

### GET /api/services (active services)

**Response:**
```json
{
  "success": true,
  "message": "Lấy danh sách dịch vụ thành công",
  "data": [
    {
      "id": 2,
      "name": "Giặt sấy",
      "description": "Giặt và sấy khô",
      "price": 80000,
      "estimatedTime": 180,
      "isActive": true,
      "deletedAt": null  // ✅ Chưa bị xóa
    }
  ]
}
```

---

## Testing

```bash
# 1. Xóa mềm một service
DELETE /api/services/1

# 2. Lấy danh sách services đã xóa
GET /api/soft-delete/services/deleted
# Expect: Có deletedAt và isActive = false

# 3. Lấy danh sách services active
GET /api/services
# Expect: deletedAt = null và isActive = true
```

---

## Files Changed

1. ✅ `ServiceResponse.java` - Added `deletedAt`
2. ✅ `BranchResponse.java` - Added `isActive`, `deletedAt`
3. ✅ `PromotionResponse.java` - Added `deletedAt`
4. ✅ `ShipperResponse.java` - Added `deletedAt`
5. ✅ `ServiceService.java` - Map `deletedAt` in mapper
6. ✅ `BranchService.java` - Map `isActive`, `deletedAt` in mapper
7. ✅ `SoftDeleteController.java` - Use Response DTOs + add mappers
8. ✅ `UserResponse.java` - Already had `deletedAt`

---

## Summary

✅ **Tất cả Response DTOs đã có field `deletedAt`**  
✅ **Tất cả Service mappers đã map field `deletedAt`**  
✅ **SoftDeleteController đã dùng Response DTOs thay vì Entity**  
✅ **Client có thể nhận thông tin soft delete đầy đủ**  

🎯 **Kết quả:** API responses giờ đây cung cấp thông tin đầy đủ về trạng thái soft delete!
