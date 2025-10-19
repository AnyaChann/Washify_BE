# 🚀 Next Steps - Soft Delete Implementation

## ✅ Đã Hoàn Thành
- ✅ Build successful (`mvn clean install -DskipTests`)
- ✅ 26 files created/modified
- ✅ All documentation ready

---

## 📋 Các Bước Tiếp Theo

### 🔴 **BƯỚC 1: Database Migration** (BẮT BUỘC)

#### Option A: Nếu database chưa có data quan trọng (RECOMMENDED)
```powershell
# Drop và tạo lại database để seed data mới
mysql -u root -p
```
```sql
DROP DATABASE IF EXISTS washify_db;
CREATE DATABASE washify_db;
EXIT;
```

Sau đó chạy app, seed data sẽ tự động load từ `data.sql`.

#### Option B: Nếu database đã có data cần giữ
```powershell
# Backup database trước
mysqldump -u root -p washify_db > backup_$(Get-Date -Format "yyyyMMdd_HHmmss").sql

# Chạy migration script
mysql -u root -p washify_db < src/main/resources/db/migration/V1__Add_Soft_Delete_Support.sql
```

---

### 🟢 **BƯỚC 2: Khởi động Application**

```powershell
# Chạy Spring Boot
mvn spring-boot:run
```

**Expected Output:**
```
Started WashifyBeApplication in X.XXX seconds
```

**Endpoints sẽ available tại:** `http://localhost:8080`

---

### 🟡 **BƯỚC 3: Test Soft Delete Flow**

#### 3.1. Kiểm tra seed data đã load

```powershell
# Xem danh sách users
curl http://localhost:8080/api/users
```

Expected: Trả về list users từ seed data

#### 3.2. Test Soft Delete

```powershell
# Xóa mềm user ID 1
curl -X DELETE http://localhost:8080/api/users/1
```

#### 3.3. Verify user đã bị ẩn

```powershell
# User không còn trong danh sách
curl http://localhost:8080/api/users

# Nhưng có trong deleted list
curl http://localhost:8080/api/soft-delete/users/deleted
```

Expected: User ID 1 xuất hiện trong deleted list với `deletedAt` timestamp

#### 3.4. Test Restore

```powershell
# Khôi phục user
curl -X PUT http://localhost:8080/api/soft-delete/users/1/restore
```

Expected Response:
```json
{
  "success": true,
  "message": "User đã được khôi phục thành công"
}
```

#### 3.5. Verify đã restore

```powershell
# User đã quay lại danh sách
curl http://localhost:8080/api/users

# Không còn trong deleted list
curl http://localhost:8080/api/soft-delete/users/deleted
```

#### 3.6. Test Permanent Delete (CẢNH BÁO!)

```powershell
# Xóa mềm trước
curl -X DELETE http://localhost:8080/api/users/1

# Xóa vĩnh viễn
curl -X DELETE http://localhost:8080/api/soft-delete/users/1/permanent
```

Expected: User bị xóa hoàn toàn khỏi database

---

### 🔵 **BƯỚC 4: Test Database State**

```powershell
mysql -u root -p washify_db
```

```sql
-- Xem users và trạng thái deleted_at
SELECT id, full_name, email, deleted_at, is_active 
FROM users 
LIMIT 10;

-- Đếm active vs deleted
SELECT 
  SUM(CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END) as active,
  SUM(CASE WHEN deleted_at IS NOT NULL THEN 1 ELSE 0 END) as deleted
FROM users;

-- Xem indexes đã tạo
SHOW INDEX FROM users WHERE Key_name LIKE '%deleted%';
```

---

### 🟣 **BƯỚC 5: Test với Postman** (RECOMMENDED)

#### Import vào Postman:

**Collection:** Washify Soft Delete Tests

**Endpoints:**

1. **Get All Users**
   - Method: GET
   - URL: `http://localhost:8080/api/users`

2. **Soft Delete User**
   - Method: DELETE
   - URL: `http://localhost:8080/api/users/1`

3. **Get Deleted Users**
   - Method: GET
   - URL: `http://localhost:8080/api/soft-delete/users/deleted`

4. **Restore User**
   - Method: PUT
   - URL: `http://localhost:8080/api/soft-delete/users/1/restore`

5. **Permanent Delete**
   - Method: DELETE
   - URL: `http://localhost:8080/api/soft-delete/users/1/permanent`

Repeat cho: branches, services, orders, promotions, shippers

---

### 🟠 **BƯỚC 6: Run Automated Tests**

```powershell
# Run all tests
mvn test

# Run soft delete tests only
mvn test -Dtest=SoftDeleteIntegrationTest

# Với coverage report
mvn clean test jacoco:report
```

Check test results tại: `target/site/jacoco/index.html`

---

### ⚫ **BƯỚC 7: Add Security (OPTIONAL but RECOMMENDED)**

Thêm authorization vào `SoftDeleteController.java`:

```java
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/soft-delete")
@PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới access
public class SoftDeleteController {
    // ...
}
```

Hoặc từng endpoint:

```java
@GetMapping("/users/deleted")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> getDeletedUsers() {
    // ...
}
```

---

### 🔷 **BƯỚC 8: Deploy to Staging/Production**

#### 8.1. Build production artifact

```powershell
mvn clean package -DskipTests
```

JAR file: `target/Washify_BE-0.0.1-SNAPSHOT.jar`

#### 8.2. Run production build

```powershell
java -jar target/Washify_BE-0.0.1-SNAPSHOT.jar
```

#### 8.3. Production checklist
- [ ] Database backup created
- [ ] Migration script tested in staging
- [ ] Environment variables configured
- [ ] Logging configured
- [ ] Monitoring set up
- [ ] Rollback plan ready

---

## 📊 Verification Checklist

### Functionality Tests
- [ ] Soft delete works (deleted_at set)
- [ ] Deleted records hidden from normal queries
- [ ] Can view deleted records via /deleted endpoint
- [ ] Restore works (deleted_at = NULL)
- [ ] Permanent delete works
- [ ] Foreign keys preserved after soft delete

### Database Tests
- [ ] Migration script runs without errors
- [ ] Indexes created (check `SHOW INDEX`)
- [ ] Constraints applied
- [ ] Seed data loads correctly
- [ ] Query performance acceptable

### API Tests
- [ ] All 18 endpoints return 200 OK
- [ ] Error handling works (404, 400)
- [ ] Response format consistent
- [ ] CORS configured properly

---

## 🚨 Common Issues & Solutions

### Issue 1: "Unknown column 'deleted_at'"
**Cause:** Migration chưa chạy

**Fix:**
```powershell
mysql -u root -p washify_db < src/main/resources/db/migration/V1__Add_Soft_Delete_Support.sql
```

### Issue 2: App không start
**Check:**
```powershell
# Verify MySQL running
mysql -u root -p -e "SELECT 1"

# Check application.properties
cat src/main/resources/application.properties
```

### Issue 3: "Cannot restore user"
**Cause:** User chưa bị soft delete

**Check:**
```sql
SELECT id, full_name, deleted_at FROM users WHERE id = 1;
```

### Issue 4: Tests fail
**Cause:** Database state không clean

**Fix:**
```sql
TRUNCATE TABLE users;
```

Hoặc set `spring.jpa.hibernate.ddl-auto=create-drop` trong test profile

---

## 📚 Documentation References

| Document | Purpose |
|----------|---------|
| `README_SOFT_DELETE.md` | Quick start guide |
| `API_SOFT_DELETE_EXAMPLES.md` | Detailed API usage examples |
| `SOFT_DELETE_GUIDE.md` | Technical implementation guide |
| `SOFT_DELETE_SUMMARY.md` | Complete implementation summary |
| `SOFT_DELETE_CHECKLIST.md` | Progress tracking |
| `NEXT_STEPS.md` | This file |

---

## 🎯 Success Criteria

Your soft delete is working correctly if:

✅ Deleting a user sets `deleted_at` timestamp  
✅ Deleted user doesn't appear in `GET /api/users`  
✅ Deleted user appears in `GET /api/soft-delete/users/deleted`  
✅ Restoring sets `deleted_at` back to NULL  
✅ User returns to normal list after restore  
✅ Permanent delete removes record completely  
✅ All tests pass  

---

## 🤝 Need Help?

1. Check error logs: `tail -f logs/spring.log`
2. Verify database state with SQL queries above
3. Review `API_SOFT_DELETE_EXAMPLES.md` for usage patterns
4. Check `SOFT_DELETE_GUIDE.md` for technical details

---

## 🎊 You're Ready!

Start with **BƯỚC 1** (Database Migration), then proceed step by step.

Good luck! 🚀

---

**Last Updated:** 2025-10-19  
**Status:** Ready for Testing
