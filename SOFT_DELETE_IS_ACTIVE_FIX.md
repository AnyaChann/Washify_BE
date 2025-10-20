# 🔧 Soft Delete + isActive Integration Fix

## 📋 Vấn đề ban đầu

**Hiện tượng:** Khi soft delete một entity có field `isActive`, chỉ có `deletedAt` được set, còn `isActive` vẫn là `1` (true).

**Ví dụ:**
```sql
-- Trước khi fix
DELETE FROM services WHERE id = 1;
-- Kết quả: deleted_at = NOW(), is_active = 1 ❌ (Inconsistent!)

-- Sau khi fix
DELETE FROM services WHERE id = 1;
-- Kết quả: deleted_at = NOW(), is_active = 0 ✅ (Consistent!)
```

## 🎯 Nguyên nhân

Hibernate `@SQLDelete` annotation chỉ set `deletedAt`:
```java
@SQLDelete(sql = "UPDATE services SET deleted_at = NOW() WHERE id = ?")
```

Nhưng không update `isActive`, dẫn đến inconsistency trong business logic.

## ✅ Giải pháp

### 1. Update `@SQLDelete` trong Entities

**Entities được fix (5 entities):**
- ✅ `Service.java`
- ✅ `User.java`
- ✅ `Branch.java`
- ✅ `Promotion.java`
- ✅ `Shipper.java`

**Before:**
```java
@SQLDelete(sql = "UPDATE services SET deleted_at = NOW() WHERE id = ?")
```

**After:**
```java
@SQLDelete(sql = "UPDATE services SET deleted_at = NOW(), is_active = 0 WHERE id = ?")
```

### 2. Update `restoreById()` trong Repositories

**Repositories được fix (5 repositories):**
- ✅ `ServiceRepository.java`
- ✅ `UserRepository.java`
- ✅ `BranchRepository.java`
- ✅ `PromotionRepository.java`
- ✅ `ShipperRepository.java`

**Before:**
```java
@Query(value = "UPDATE services SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
int restoreById(@Param("id") Long id);
```

**After:**
```java
@Query(value = "UPDATE services SET deleted_at = NULL, is_active = 1 WHERE id = :id", nativeQuery = true)
int restoreById(@Param("id") Long id);
```

## 📊 Logic Flow

### Delete Flow
```
DELETE /api/services/1
    ↓
ServiceRepository.deleteById(1)
    ↓
Hibernate triggers @SQLDelete
    ↓
UPDATE services 
SET deleted_at = NOW(), is_active = 0 
WHERE id = 1
    ↓
✅ Service marked as deleted AND inactive
```

### Restore Flow
```
PUT /api/soft-delete/services/1/restore
    ↓
ServiceRepository.restoreById(1)
    ↓
UPDATE services 
SET deleted_at = NULL, is_active = 1 
WHERE id = 1
    ↓
✅ Service restored AND active again
```

## 🔍 Verification

### Test Soft Delete
```sql
-- 1. Soft delete
DELETE FROM services WHERE id = 1;

-- 2. Verify
SELECT id, name, is_active, deleted_at FROM services WHERE id = 1;
-- Expected: is_active = 0, deleted_at = NOW()

-- 3. List deleted services
SELECT * FROM services WHERE deleted_at IS NOT NULL;
```

### Test Restore
```sql
-- 1. Restore
UPDATE services SET deleted_at = NULL, is_active = 1 WHERE id = 1;

-- 2. Verify
SELECT id, name, is_active, deleted_at FROM services WHERE id = 1;
-- Expected: is_active = 1, deleted_at = NULL

-- 3. List active services
SELECT * FROM services WHERE deleted_at IS NULL;
```

## 💡 Business Logic

### `isActive` vs `deletedAt`

| Field | Purpose | Usage |
|-------|---------|-------|
| `isActive` | **Business status** | Admin có thể tạm disable service không muốn khách hàng thấy |
| `deletedAt` | **Soft delete** | Service bị xóa mềm, có thể restore |

**Combination Logic:**
```
is_active = 1, deleted_at = NULL   → ✅ Active & Available
is_active = 0, deleted_at = NULL   → ⚠️  Disabled (can re-enable)
is_active = 1, deleted_at = NOW()  → ❌ INVALID (after fix)
is_active = 0, deleted_at = NOW()  → 🗑️  Soft Deleted
```

### When to use what?

**Scenario 1: Temporarily disable service**
```java
service.setIsActive(false);
serviceRepository.save(service);
// isActive = 0, deletedAt = NULL
```

**Scenario 2: Soft delete**
```java
serviceRepository.deleteById(id);
// isActive = 0, deletedAt = NOW()
```

**Scenario 3: Restore**
```java
serviceRepository.restoreById(id);
// isActive = 1, deletedAt = NULL
```

## 🧪 Test Endpoints

### Soft Delete Flow
```bash
# 1. Get active services
GET /api/services
# Response: [{id: 1, name: "Giặt khô", isActive: true}]

# 2. Soft delete
DELETE /api/services/1

# 3. Verify deleted
GET /api/soft-delete/services/deleted
# Response: [{id: 1, name: "Giặt khô", isActive: false, deletedAt: "2025-10-20T..."}]

# 4. Restore
PUT /api/soft-delete/services/1/restore

# 5. Verify restored
GET /api/services
# Response: [{id: 1, name: "Giặt khô", isActive: true}]
```

## 📝 Notes

1. **Entities WITHOUT `isActive`** (only `deletedAt`):
   - `Order.java`
   - `Payment.java`
   - `Shipment.java`
   - `Review.java`
   - `Notification.java`
   - `Attachment.java`
   - `AuditLog.java`

2. **Migration**: Không cần migration vì chỉ thay đổi logic DELETE/RESTORE, không đổi schema

3. **Backward Compatibility**: 
   - Existing deleted records với `is_active = 1` vẫn hoạt động
   - `@Where(clause = "deleted_at IS NULL")` vẫn filter đúng
   - Recommend: Run manual UPDATE để sync data cũ

## 🔄 Data Migration (Optional)

Nếu muốn sync data cũ:
```sql
-- Sync old soft-deleted records
UPDATE users SET is_active = 0 WHERE deleted_at IS NOT NULL AND is_active = 1;
UPDATE branches SET is_active = 0 WHERE deleted_at IS NOT NULL AND is_active = 1;
UPDATE services SET is_active = 0 WHERE deleted_at IS NOT NULL AND is_active = 1;
UPDATE promotions SET is_active = 0 WHERE deleted_at IS NOT NULL AND is_active = 1;
UPDATE shippers SET is_active = 0 WHERE deleted_at IS NOT NULL AND is_active = 1;
```

---

**Fixed on:** October 20, 2025  
**Status:** ✅ Complete  
**Affected Entities:** 5 (User, Branch, Service, Promotion, Shipper)  
**Affected Repositories:** 5 (matching entities)
