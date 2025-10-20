# Phase 3 - Optional Enhancements Implementation Plan

**Priority:** OPTIONAL  
**Estimated Time:** 1 week  
**Total Endpoints:** ~31+ endpoints  
**Status:** IN PLANNING

---

## 🎯 Goals

Phase 3 focuses on:
1. **Complete Attachment Management** - Full file upload implementation
2. **Advanced Search & Filtering** - Multi-criteria queries
3. **Enhanced Statistics** - Comprehensive analytics
4. **Notification Management** - Full CRUD + preferences
5. **Operational Improvements** - Batch operations, bulk updates

---

## 📋 Planned Features

### Category A: Attachment Management (HIGH Priority) ✅ COMPLETED
**Goal:** Complete the stub implementation from Phase 2

#### AttachmentController (NEW - 7 endpoints) ✅ COMPLETED
1. ✅ **POST** `/api/attachments/upload` - Upload file (MultipartFile)
   - Supports both orderId and shipmentId (at least one required)
   - Saves file to local disk (uploads/ directory)
   - Returns attachment info with file URL
   - Access: ADMIN/STAFF/CUSTOMER
2. ✅ **POST** `/api/attachments` - Create attachment from existing URL
   - For files already uploaded to external storage (S3, CDN)
   - Validates orderId or shipmentId
   - Access: ADMIN/STAFF/CUSTOMER
3. ✅ **GET** `/api/attachments/{id}` - Get attachment info
   - Returns attachment metadata
   - Access: ADMIN/STAFF/CUSTOMER
4. ✅ **GET** `/api/attachments/order/{orderId}` - Get order attachments
   - Returns list of all attachments for an order
   - Access: ADMIN/STAFF/CUSTOMER
5. ✅ **GET** `/api/attachments/shipment/{shipmentId}` - Get shipment attachments
   - Returns list of all attachments for a shipment
   - Access: ADMIN/STAFF/CUSTOMER
6. ✅ **GET** `/api/attachments/{id}/download` - Download file
   - Returns file as Resource for download
   - Sets Content-Disposition header
   - Access: ADMIN/STAFF/CUSTOMER
7. ✅ **DELETE** `/api/attachments/{id}` - Delete attachment
   - Deletes both file and database record
   - Access: ADMIN/STAFF only

**Implementation Details:**
- ✅ Created AttachmentRequest DTO (fileUrl, fileType, orderId, shipmentId)
- ✅ Created AttachmentResponse DTO (id, orderId, shipmentId, fileUrl, fileType, uploadedAt)
- ✅ Created AttachmentService with full CRUD operations
- ✅ Local storage implementation (uploads/ directory)
- ✅ MultipartFile handling with UUID filename generation
- ⚠️ Production TODO: Migrate to cloud storage (AWS S3/Azure Blob)
- ⚠️ Production TODO: Add file validation (size, type, virus scan)
- ⚠️ Production TODO: Image processing (compression, thumbnails)

**Decision:** Separate AttachmentController (not merged)

---

### Category B: Advanced Search & Filtering (MEDIUM Priority)

#### OrderController Enhancements (+5 endpoints) ✅ COMPLETED
1. ✅ **GET** `/api/orders/search` - Multi-criteria search
   - Query params: userId, branchId, status, dateFrom, dateTo, minAmount, maxAmount
   - Dynamic JPQL query with optional parameters
   - Repository: `searchOrders()` method added
   - Service: `searchOrders()` with enum conversion
2. ✅ **GET** `/api/orders/user/{userId}/status/{status}` - User orders by status
   - Uses existing `findByUserIdAndStatus()` repository method
   - Service: `getOrdersByUserAndStatus()` with validation
3. ✅ **GET** `/api/orders/branch/{branchId}` - Orders by branch
   - Uses existing `findByBranchId()` repository method
   - Service: `getOrdersByBranch()` with branch validation
4. ✅ **GET** `/api/orders/date-range` - Orders by date range
   - Uses existing `findByOrderDateBetween()` repository method
   - Service: `getOrdersByDateRange()` with date validation
5. ❌ Removed duplicate - amount-range covered by search endpoint

#### ServiceController Enhancements (+2 endpoints) ✅ COMPLETED
1. ✅ **GET** `/api/services/advanced-search` - Advanced search services
   - Query params: name, minPrice, maxPrice, isActive
   - Repository: `advancedSearch()` method added with dynamic JPQL
   - Service: `advancedSearch()` method implemented
2. ✅ **GET** `/api/services/price-range` - Services by price range
   - Repository: `findByPriceRange()` method added
   - Service: `getServicesByPriceRange()` method implemented
3. ❌ Category search skipped - need to verify if category field exists

#### UserController Enhancements (+3 endpoints) ✅ COMPLETED
1. ✅ **GET** `/api/users/search` - Search users
   - Query params: username, email, fullName, roleId
   - Repository: `searchUsers()` method added with LEFT JOIN on roles
   - Service: `searchUsers()` method implemented
   - **Fixed:** Changed from u.userRoles to u.roles relationship
2. ✅ **GET** `/api/users/role/{roleId}` - Users by role
   - Repository: `findByRoleId()` method added
   - Service: `getUsersByRole()` method implemented
3. ✅ **GET** `/api/users/active` - Only active users
   - Uses existing repository method
   - Service: `getActiveUsers()` method implemented

---

### Category C: Statistics & Analytics (MEDIUM Priority)

#### OrderController Statistics (+3 endpoints) ✅ COMPLETED

1. ✅ **GET** `/api/orders/statistics` - Order statistics
   - Returns OrderStatistics inner class with 7 fields
   - Total/pending/inProgress/completed/cancelled counts
   - Total revenue and average order value
2. ✅ **GET** `/api/orders/statistics/revenue` - Revenue by period
   - Takes startDate and endDate as ISO date-time parameters
   - Returns RevenueStatistics with 5 fields (date range, revenue, orderCount, avgValue)
3. ✅ **GET** `/api/orders/statistics/top-customers` - Top customers by order count/value
   - Takes limit parameter (default 10)
   - Returns List<TopCustomer> with userId, username, fullName, orderCount, totalValue
   - Combines data from Order and User repositories

#### ShipmentController Statistics (+2 endpoints) ✅ COMPLETED
1. ✅ **GET** `/api/shipments/statistics` - Overall shipment statistics
   - Returns ShipmentStatistics inner class with 7 fields
   - Total/pending/inTransit/delivered/cancelled counts
   - Success rate and average delivery time in hours
   - Repository: `countAllShipments()` and `getAverageDeliveryTimeInHours()` added
   - Service: `getShipmentStatistics()` method implemented
2. **GET** `/api/shipments/statistics/shipper/{shipperId}` - Already exists from Phase 2! ✅

#### BranchController Statistics (+2 endpoints) ✅ COMPLETED
1. ✅ **GET** `/api/branches/statistics` - All branch performance comparison
   - Returns List of BranchStatistics inner class (6 fields)
   - Per-branch: branchId, name, totalOrders, completedOrders, totalRevenue, completionRate
   - Repository: 4 new queries (countOrdersByBranchId, sumRevenueByBranchId, countCompletedOrdersByBranchId, getAverageOrderValueByBranchId)
   - Service: `getAllBranchStatistics()` method implemented
2. ✅ **GET** `/api/branches/{id}/statistics` - Single branch detailed statistics
   - Returns BranchDetailStatistics inner class (10 fields)
   - Includes branch info + orders/revenue + average order value
   - Service: `getBranchDetailStatistics()` method implemented

---

### Category D: Notification Management (LOW Priority)

#### NotificationController (NEW - 7 endpoints)
1. **POST** `/api/notifications` - Create notification (Admin/Staff)
2. **GET** `/api/notifications` - Get all notifications (Admin/Staff)
3. **GET** `/api/notifications/user/{userId}` - Get user notifications
4. **GET** `/api/notifications/unread` - Get unread notifications (current user)
5. **PATCH** `/api/notifications/{id}/read` - Mark as read
6. **PATCH** `/api/notifications/read-all` - Mark all as read
7. **DELETE** `/api/notifications/{id}` - Delete notification

**Features:**
- User-specific notifications
- Read/unread status
- Notification types (ORDER_UPDATE, PAYMENT, SHIPMENT, PROMOTION)
- Push notification support (future)

---

### Category E: Batch Operations (LOW Priority)

#### OrderController Batch Operations (+2 endpoints)
1. **PATCH** `/api/orders/batch/status` - Update multiple order statuses
   - Body: `{ orderIds: [1,2,3], status: "COMPLETED" }`
2. **DELETE** `/api/orders/batch` - Cancel multiple orders

#### UserController Batch Operations (+2 endpoints)
1. **PATCH** `/api/users/batch/activate` - Activate multiple users
2. **PATCH** `/api/users/batch/deactivate` - Deactivate multiple users

---

### Category F: Operational Enhancements (LOW Priority)

#### ReviewController Enhancements (+3 endpoints)
1. **GET** `/api/reviews/service/{serviceId}` - Reviews by service
2. **GET** `/api/reviews/user/{userId}` - Reviews by user
3. **GET** `/api/reviews/rating/{rating}` - Reviews by rating (1-5 stars)

#### BranchController Enhancements (+2 endpoints)
1. **GET** `/api/branches/search` - Search branches
   - Query params: name, address, isActive
2. **GET** `/api/branches/nearby` - Find nearby branches
   - Query params: lat, lng, radius

---

## 📊 Phase 3 Breakdown

| Category | Priority | Endpoints | Estimated Time |
|----------|----------|-----------|----------------|
| A. Attachment Management | HIGH | 6 | 2 days |
| B. Advanced Search | MEDIUM | 11 | 2 days |
| C. Statistics & Analytics | MEDIUM | 7 | 1.5 days |
| D. Notification Management | LOW | 7 | 1 day |
| E. Batch Operations | LOW | 4 | 1 day |
| F. Operational Enhancements | LOW | 5 | 0.5 days |
| **TOTAL** | | **40** | **8 days** |

---

## 🎯 Recommended Implementation Order

### Sprint 1: High-Value Features (3 days)
1. **Attachment Management** (2 days)
   - Complete ShipmentController attachment implementation
   - Or create AttachmentController
   - Integrate cloud storage (or local storage for MVP)

2. **Advanced Search - Orders** (1 day)
   - Multi-criteria search endpoint
   - Most valuable for Staff/Admin

### Sprint 2: Analytics & Insights (2 days)
3. **Order Statistics** (1 day)
   - Revenue analytics
   - Top customers
   - Service popularity

4. **Branch Statistics** (0.5 day)
   - Performance metrics per branch

5. **Shipment Statistics** (0.5 day)
   - Overall shipment analytics

### Sprint 3: User Experience (2 days)
6. **Notification Management** (1 day)
   - Full CRUD for notifications
   - Read/unread tracking

7. **Service/User Search** (1 day)
   - Search enhancements for both entities

### Sprint 4: Efficiency Features (1 day)
8. **Batch Operations** (1 day)
   - Bulk status updates
   - Bulk activation/deactivation

9. **Review & Branch Enhancements** (optional if time permits)

---

## 🔧 Technical Considerations

### File Upload (Attachment Management)
**Option 1: Local Storage (MVP)**
- Store in `uploads/` directory
- Serve via Spring MVC
- Simple but not scalable

**Option 2: Cloud Storage (Production)**
- AWS S3 with Spring Cloud AWS
- Azure Blob Storage with Azure SDK
- Signed URLs for secure access
- CDN for performance

**Recommendation:** Start with Option 1 for Phase 3, migrate to Option 2 later.

### Search Implementation
**Option 1: Query Parameters**
```java
@GetMapping("/search")
public List<OrderResponse> search(
    @RequestParam(required=false) Long userId,
    @RequestParam(required=false) String status,
    // ... more params
)
```

**Option 2: Request Body with DTO**
```java
@PostMapping("/search")
public List<OrderResponse> search(@RequestBody OrderSearchRequest request)
```

**Recommendation:** Option 1 for simple searches, Option 2 for complex criteria.

### Statistics Caching
- Use `@Cacheable` for expensive calculations
- Cache invalidation on data changes
- Redis for distributed caching (optional)

---

## 📝 Files to Create/Modify

### New Files (if separate controllers chosen):
- `AttachmentController.java`
- `AttachmentService.java`
- `AttachmentRequest.java`
- `AttachmentResponse.java`
- `NotificationController.java`
- `NotificationService.java`
- `NotificationRequest.java`
- `NotificationResponse.java`

### Files to Enhance:
- `OrderController.java` - +10 endpoints
- `OrderService.java` - +10 methods
- `PaymentController.java` - May need statistics
- `ShipmentController.java` - Complete attachment stubs
- `ShipmentService.java` - Full attachment logic
- `ServiceController.java` - +3 search endpoints
- `UserController.java` - +5 search/batch endpoints
- `ReviewController.java` - +3 filter endpoints
- `BranchController.java` - +4 enhancement endpoints

### Repository Additions Needed:
- `OrderRepository` - Custom queries for search/statistics
- `ServiceRepository` - Price range queries
- `UserRepository` - Role-based queries
- `ReviewRepository` - Service/user/rating filters
- `BranchRepository` - Location-based queries (optional)

---

## 🎬 Let's Start!

**Ready to begin Phase 3?**

I recommend starting with:
1. **Attachment Management** - High value, completes Phase 2 stubs
2. **Order Statistics** - High value for business insights

Which would you like to start with?
A. Complete Attachment Management (file upload)
B. Order Statistics & Analytics
C. Advanced Search (Orders)
D. Your choice / custom priority

---

**Created:** October 21, 2025, 02:30 AM  
**Next Update:** After completing first sprint
