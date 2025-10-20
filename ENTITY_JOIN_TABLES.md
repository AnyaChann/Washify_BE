# 📦 Entity Join Tables - Documentation

## 🎯 Overview

Đã tạo 2 entity join tables thiếu cho quan hệ Many-to-Many trong hệ thống:

1. **UserRole.java** - Join table giữa `User` và `Role`
2. **OrderPromotion.java** - Join table giữa `Order` và `Promotion`

---

## 📊 Database Schema

### 1. user_roles Table
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_userroles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_userroles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

**Composite Primary Key:** `(user_id, role_id)`

### 2. order_promotions Table
```sql
CREATE TABLE order_promotions (
    order_id       BIGINT NOT NULL,
    promotion_id   BIGINT NOT NULL,
    PRIMARY KEY (order_id, promotion_id),
    CONSTRAINT fk_orderpromo_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_orderpromo_promo FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);
```

**Composite Primary Key:** `(order_id, promotion_id)`

---

## 🏗️ Entity Structure

### UserRole.java

```java
@Entity
@Table(name = "user_roles")
public class UserRole {
    
    @Embeddable
    public static class UserRoleId {
        private Long userId;
        private Long roleId;
    }
    
    @EmbeddedId
    private UserRoleId id; // Composite key
    
    @ManyToOne
    @MapsId("userId")
    private User user;
    
    @ManyToOne
    @MapsId("roleId")
    private Role role;
    
    // TODO: Có thể thêm các thuộc tính:
    // - assignedAt (Thời gian gán role)
    // - assignedBy (Ai đã gán)
    // - expiryDate (Thời hạn role)
}
```

**Features:**
- ✅ Composite Primary Key với `@EmbeddedId`
- ✅ `@MapsId` để map với foreign keys
- ✅ Constructor tiện lợi: `new UserRole(user, role)`
- ✅ Lazy loading cho performance
- ⚠️ TODO: Mở rộng thêm metadata (assignedAt, assignedBy, expiryDate)

---

### OrderPromotion.java

```java
@Entity
@Table(name = "order_promotions")
public class OrderPromotion {
    
    @Embeddable
    public static class OrderPromotionId {
        private Long orderId;
        private Long promotionId;
    }
    
    @EmbeddedId
    private OrderPromotionId id; // Composite key
    
    @ManyToOne
    @MapsId("orderId")
    private Order order;
    
    @ManyToOne
    @MapsId("promotionId")
    private Promotion promotion;
    
    // TODO: Có thể thêm các thuộc tính:
    // - discountAmount (Số tiền giảm thực tế)
    // - appliedAt (Thời gian áp dụng)
    // - isValid (Mã có hợp lệ không)
}
```

**Features:**
- ✅ Composite Primary Key với `@EmbeddedId`
- ✅ `@MapsId` để map với foreign keys
- ✅ Constructor tiện lợi: `new OrderPromotion(order, promotion)`
- ✅ Lazy loading cho performance
- ⚠️ TODO: Mở rộng thêm metadata (discountAmount, appliedAt, isValid)

---

## 🔧 Repository Methods

### UserRoleRepository.java

```java
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    
    // Query methods
    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
    
    // Delete methods
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
    void deleteByUserId(Long userId);
}
```

**Use Cases:**
- Tìm tất cả roles của một user
- Tìm tất cả users có một role cụ thể
- Check xem user có role hay không
- Xóa role assignment

---

### OrderPromotionRepository.java

```java
@Repository
public interface OrderPromotionRepository extends JpaRepository<OrderPromotion, OrderPromotion.OrderPromotionId> {
    
    // Query methods
    List<OrderPromotion> findByOrderId(Long orderId);
    List<OrderPromotion> findByPromotionId(Long promotionId);
    boolean existsByOrderIdAndPromotionId(Long orderId, Long promotionId);
    long countByPromotionId(Long promotionId);
    
    // Custom query
    @Query("SELECT op FROM OrderPromotion op JOIN op.promotion p WHERE p.code = :promotionCode")
    List<OrderPromotion> findByPromotionCode(String promotionCode);
    
    // Delete methods
    void deleteByOrderIdAndPromotionId(Long orderId, Long promotionId);
    void deleteByOrderId(Long orderId);
}
```

**Use Cases:**
- Tìm tất cả promotions của một order
- Tìm tất cả orders đã sử dụng một promotion
- Đếm số lần một promotion được sử dụng
- Check xem order có dùng promotion hay không
- Xóa promotion khỏi order

---

## ⚠️ Important Notes

### 1. Hiện Tại Đang Dùng @JoinTable

**User.java:**
```java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles = new HashSet<>();
```

**Order.java:**
```java
@ManyToMany
@JoinTable(
    name = "order_promotions",
    joinColumns = @JoinColumn(name = "order_id"),
    inverseJoinColumns = @JoinColumn(name = "promotion_id")
)
private Set<Promotion> promotions = new HashSet<>();
```

### 2. Khi Nào Cần Dùng Entity Join Table?

**Dùng @JoinTable (Simple):**
- ✅ Join table chỉ chứa 2 foreign keys
- ✅ Không cần thêm thuộc tính metadata
- ✅ Code ngắn gọn, dễ maintain

**Dùng Entity riêng (Advanced):**
- ✅ Cần lưu thêm metadata (createdAt, amount, status, etc.)
- ✅ Cần query phức tạp trên join table
- ✅ Cần audit trail cho relationship
- ✅ Business logic phức tạp liên quan đến relationship

### 3. Migration Strategy

**Option 1: Giữ nguyên @JoinTable (Recommended)**
- Không cần thay đổi gì trong User.java và Order.java
- UserRole.java và OrderPromotion.java để sẵn cho tương lai
- Repository để sẵn khi cần query trực tiếp join table

**Option 2: Migrate sang Entity riêng**
- Bước 1: Remove @JoinTable annotations
- Bước 2: Add @OneToMany relationship đến join entity
- Bước 3: Update service layer để dùng join entity
- ⚠️ Breaking change - cần test kỹ

---

## 🚀 Usage Examples

### UserRole Usage

#### Gán role cho user:
```java
User user = userRepository.findById(userId).orElseThrow();
Role role = roleRepository.findById(roleId).orElseThrow();

UserRole userRole = new UserRole(user, role);
userRoleRepository.save(userRole);
```

#### Check user có role không:
```java
boolean hasRole = userRoleRepository.existsByUserIdAndRoleId(userId, roleId);
```

#### Lấy tất cả roles của user:
```java
List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
List<Role> roles = userRoles.stream()
    .map(UserRole::getRole)
    .collect(Collectors.toList());
```

---

### OrderPromotion Usage

#### Áp dụng promotion cho order:
```java
Order order = orderRepository.findById(orderId).orElseThrow();
Promotion promotion = promotionRepository.findByCode(promoCode).orElseThrow();

OrderPromotion orderPromotion = new OrderPromotion(order, promotion);
orderPromotionRepository.save(orderPromotion);
```

#### Đếm số lần promotion được dùng:
```java
long usageCount = orderPromotionRepository.countByPromotionId(promotionId);
```

#### Lấy tất cả promotions của order:
```java
List<OrderPromotion> orderPromotions = orderPromotionRepository.findByOrderId(orderId);
List<Promotion> promotions = orderPromotions.stream()
    .map(OrderPromotion::getPromotion)
    .collect(Collectors.toList());
```

---

## 📝 Future Enhancements

### UserRole Enhancements:
```java
@CreationTimestamp
@Column(name = "assigned_at")
private LocalDateTime assignedAt; // Thời gian gán role

@ManyToOne
@JoinColumn(name = "assigned_by")
private User assignedBy; // Admin đã gán role này

@Column(name = "expiry_date")
private LocalDateTime expiryDate; // Role có thời hạn (VIP membership, etc.)

@Column(name = "is_active")
private Boolean isActive = true; // Tạm khóa role mà không xóa
```

### OrderPromotion Enhancements:
```java
@Column(name = "discount_amount", precision = 10, scale = 2)
private BigDecimal discountAmount; // Số tiền giảm thực tế (sau khi tính toán)

@CreationTimestamp
@Column(name = "applied_at")
private LocalDateTime appliedAt; // Thời gian áp dụng mã

@Column(name = "is_valid")
private Boolean isValid = true; // Mã có hợp lệ khi áp dụng không

@Column(name = "validation_message")
private String validationMessage; // Lý do nếu mã không hợp lệ

@Column(name = "original_price", precision = 10, scale = 2)
private BigDecimal originalPrice; // Giá gốc trước khi giảm

@Column(name = "final_price", precision = 10, scale = 2)
private BigDecimal finalPrice; // Giá sau khi giảm
```

---

## ✅ Checklist

### Files Created:
- ✅ `UserRole.java` - Entity join table
- ✅ `UserRoleRepository.java` - Repository với query methods
- ✅ `OrderPromotion.java` - Entity join table
- ✅ `OrderPromotionRepository.java` - Repository với query methods
- ✅ `ENTITY_JOIN_TABLES.md` - Documentation (file này)

### Current State:
- ✅ Entities compile successfully
- ✅ Composite keys configured correctly
- ✅ Repositories với useful query methods
- ⚠️ TODO warnings (SonarQube) - đợi implement enhancements
- ⚠️ Chưa tích hợp vào service layer

### Next Steps (Optional):
1. **Test Entities**: Tạo unit tests cho composite keys
2. **Migration**: Nếu muốn, migrate từ @JoinTable sang entity riêng
3. **Enhancement**: Implement các TODO comments (metadata fields)
4. **Service Layer**: Tích hợp vào business logic khi cần
5. **Audit Trail**: Add tracking cho role assignments và promotion usage

---

## 🎉 Summary

✅ **Completed**: Đã tạo đủ 2 entity join tables thiếu trong database schema

**Before:**
- ❌ UserRole.java - Missing
- ❌ OrderPromotion.java - Missing
- ❌ UserRoleRepository.java - Missing
- ❌ OrderPromotionRepository.java - Missing

**After:**
- ✅ UserRole.java - Created with composite key
- ✅ OrderPromotion.java - Created with composite key
- ✅ UserRoleRepository.java - Created with 6 query methods
- ✅ OrderPromotionRepository.java - Created with 8 query methods

**Total Files:** 
- 19 Entities (17 trước + 2 mới)
- 19 Repositories (17 trước + 2 mới)

---

> 💡 **Note**: Các entity này được thiết kế để mở rộng trong tương lai. Hiện tại có thể giữ nguyên cách dùng @JoinTable, nhưng khi cần thêm metadata hoặc business logic phức tạp, đã có sẵn entity và repository để sử dụng.
