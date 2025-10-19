# 🧼 Washify Backend - Soft Delete Feature

## 🎯 Quick Start

### 1. Prerequisites
- ✅ Java 21
- ✅ Maven 3.6+
- ✅ MySQL 8.0+
- ✅ Postman/curl (for testing)

### 2. Database Setup

```bash
# Tạo database
mysql -u root -p
CREATE DATABASE washify_db;
exit;

# Run migration (nếu DB đã tồn tại)
mysql -u root -p washify_db < src/main/resources/db/migration/V1__Add_Soft_Delete_Support.sql
```

### 3. Run Application

```bash
# Build project
mvn clean install

# Run Spring Boot
mvn spring-boot:run
```

Application sẽ chạy tại: http://localhost:8080

### 4. Test Soft Delete

```bash
# Xem users
curl http://localhost:8080/api/users

# Xóa mềm user ID 1
curl -X DELETE http://localhost:8080/api/users/1

# Xem users đã xóa
curl http://localhost:8080/api/soft-delete/users/deleted

# Khôi phục user
curl -X PUT http://localhost:8080/api/soft-delete/users/1/restore

# Xóa vĩnh viễn
curl -X DELETE http://localhost:8080/api/soft-delete/users/1/permanent
```

---

## 📚 Documentation

| File | Description |
|------|-------------|
| `SOFT_DELETE_GUIDE.md` | Technical implementation guide |
| `API_SOFT_DELETE_EXAMPLES.md` | API usage examples & best practices |
| `SOFT_DELETE_SUMMARY.md` | Complete implementation summary |

---

## 🌐 API Endpoints

### Soft Delete Operations

**Base URL:** `/api/soft-delete`

#### Available Endpoints (18 total)

| Entity | GET /deleted | PUT /{id}/restore | DELETE /{id}/permanent |
|--------|--------------|-------------------|------------------------|
| **User** | `/users/deleted` | `/users/{id}/restore` | `/users/{id}/permanent` |
| **Branch** | `/branches/deleted` | `/branches/{id}/restore` | `/branches/{id}/permanent` |
| **Service** | `/services/deleted` | `/services/{id}/restore` | `/services/{id}/permanent` |
| **Order** | `/orders/deleted` | `/orders/{id}/restore` | `/orders/{id}/permanent` |
| **Promotion** | `/promotions/deleted` | `/promotions/{id}/restore` | `/promotions/{id}/permanent` |
| **Shipper** | `/shippers/deleted` | `/shippers/{id}/restore` | `/shippers/{id}/permanent` |

---

## 🧪 Testing

### Run Tests

```bash
# All tests
mvn test

# Soft delete tests only
mvn test -Dtest=SoftDeleteIntegrationTest

# Specific test method
mvn test -Dtest=SoftDeleteIntegrationTest#testSoftDelete_ShouldSetDeletedAt
```

### Manual Testing Flow

1. **Create a user** (via `/api/users` POST)
2. **Soft delete** (via `/api/users/{id}` DELETE)
3. **Verify hidden** (via `/api/users` GET - should not appear)
4. **View in deleted list** (via `/api/soft-delete/users/deleted` GET)
5. **Restore** (via `/api/soft-delete/users/{id}/restore` PUT)
6. **Verify restored** (via `/api/users` GET - should appear again)

---

## 📊 Database Schema

### Soft Delete Columns

All tables with soft delete support have these columns:

```sql
deleted_at TIMESTAMP NULL       -- NULL = active, NOT NULL = deleted
is_active BOOLEAN DEFAULT TRUE  -- Active status flag (users, branches)
created_at TIMESTAMP            -- Record creation time
updated_at TIMESTAMP            -- Last update time
```

### Affected Tables

- `users`
- `branches`
- `services`
- `orders`
- `promotions`
- `shippers`

### Indexes

Performance indexes on `deleted_at`:
- `idx_users_deleted_at`
- `idx_branches_deleted_at`
- `idx_services_deleted_at`
- `idx_orders_deleted_at`
- `idx_promotions_deleted_at`
- `idx_shippers_deleted_at`

---

## 🔧 Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/washify_db
spring.datasource.username=root
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# SQL Init
spring.sql.init.mode=always
```

### Seed Data

Seed data automatically loaded from `src/main/resources/data.sql` on startup.

---

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/washify/apis/
│   │   ├── controller/
│   │   │   └── SoftDeleteController.java     ⭐ REST endpoints
│   │   ├── service/
│   │   │   └── SoftDeleteService.java        ⭐ Business logic
│   │   ├── repository/
│   │   │   ├── UserRepository.java           ⭐ Custom soft delete queries
│   │   │   ├── BranchRepository.java
│   │   │   ├── ServiceRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   ├── PromotionRepository.java
│   │   │   └── ShipperRepository.java
│   │   ├── entity/
│   │   │   ├── User.java                     ⭐ @SQLDelete, @Where
│   │   │   ├── Branch.java
│   │   │   ├── Service.java
│   │   │   ├── Order.java
│   │   │   ├── Promotion.java
│   │   │   └── Shipper.java
│   │   └── dto/
│   │       └── response/
│   │           ├── ApiResponse.java
│   │           └── UserResponse.java
│   └── resources/
│       ├── application.properties
│       ├── data.sql                          ⭐ Seed data
│       └── db/migration/
│           └── V1__Add_Soft_Delete_Support.sql
└── test/
    └── java/com/washify/apis/
        └── controller/
            └── SoftDeleteIntegrationTest.java ⭐ Test cases
```

---

## ✅ Features

### Implemented
- ✅ Soft delete for 6 critical entities
- ✅ Automatic filtering of deleted records
- ✅ Custom repository methods for deleted record access
- ✅ Centralized soft delete service
- ✅ RESTful API endpoints
- ✅ Restore functionality
- ✅ Permanent delete option (admin only)
- ✅ Integration tests
- ✅ Database migration script
- ✅ Comprehensive documentation

### Benefits
- ✅ No data loss
- ✅ Recoverable deletes
- ✅ Audit trail
- ✅ Foreign key integrity preserved
- ✅ GDPR compliant (with permanent delete)

---

## 🚨 Important Notes

### ⚠️ Permanent Delete
- Only admins should have access
- Requires confirmation dialog in frontend
- Cannot be undone
- May cause foreign key errors if relationships exist

### 📝 Best Practices
1. Always soft delete entities with relationships
2. Log all restore/permanent delete operations
3. Set up automated cleanup for old deleted records
4. Add authorization to soft delete endpoints
5. Use confirm dialogs before permanent delete

---

## 🐛 Troubleshooting

### Issue: "Unknown column 'deleted_at'"
**Solution:** Run database migration:
```bash
mysql -u root -p washify_db < src/main/resources/db/migration/V1__Add_Soft_Delete_Support.sql
```

### Issue: "Cannot restore user"
**Cause:** User not in deleted state (deleted_at = NULL)

**Check:**
```bash
curl http://localhost:8080/api/soft-delete/users/deleted
```

### Issue: Foreign key error on permanent delete
**Cause:** Entity has relationships

**Solution:** Soft delete instead, or delete related records first

---

## 📞 Support

For detailed usage examples, see:
- `API_SOFT_DELETE_EXAMPLES.md` - Complete API usage guide
- `SOFT_DELETE_GUIDE.md` - Technical implementation details

---

## 📄 License

Washify Backend © 2024

---

**Version:** 1.0  
**Last Updated:** 2024  
**Status:** ✅ Production Ready
