# ✅ Railway Environment Variables Checklist

## Cách 1: Dùng Internal MySQL (Khuyến nghị - Nhanh hơn)

```properties
DATABASE_URL=jdbc:mysql://mysql.railway.internal:3306/railway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
DATABASE_USERNAME=root
DATABASE_PASSWORD=wFoZlBNznVAjYdxKoMWiGEFdvphmtGmi
```

## Cách 2: Dùng Public URL (Nếu cách 1 không được)

```properties
DATABASE_URL=jdbc:mysql://shortline.proxy.rlwy.net:10338/railway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
DATABASE_USERNAME=root
DATABASE_PASSWORD=wFoZlBNznVAjYdxKoMWiGEFdvphmtGmi
```

---

## Các biến còn lại (Copy từng dòng vào Railway Variables):

### Hibernate & SQL
```properties
HIBERNATE_DDL_AUTO=update
SQL_INIT_MODE=always
SHOW_SQL=false
SQL_BINDER_LOG_LEVEL=INFO
```

### Logging
```properties
LOG_LEVEL_ROOT=INFO
LOG_LEVEL_APP=INFO
LOG_LEVEL_SPRING=INFO
LOG_LEVEL_HIBERNATE=WARN
```

### JWT (⚠️ GENERATE MỚI - Chạy lệnh dưới trên PowerShell)

**Lệnh generate JWT Secret:**
```powershell
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

**Rồi thêm vào Railway:**
```properties
JWT_SECRET=<KẾT_QUẢ_TỪ_LỆNH_TRÊN>
JWT_EXPIRATION=43200000
```

### Email
```properties
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-gmail-app-password
FRONTEND_URL=https://your-frontend.com
```

### Passwords
```properties
DEFAULT_PASSWORD=Washify@2025
GUEST_DEFAULT_PASSWORD=Guest@2025
```

### MoMo Payment (Test)
```properties
MOMO_PARTNER_CODE=MOMOBKUN20180529
MOMO_ACCESS_KEY=klm05TvNBzhg7h7j
MOMO_SECRET_KEY=at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa
MOMO_ENDPOINT=https://test-payment.momo.vn/v2/gateway/api/create
MOMO_REDIRECT_URL=https://washifybe-production.up.railway.app/api/payments/momo/callback
MOMO_IPN_URL=https://washifybe-production.up.railway.app/api/payments/momo/ipn
```

---

## 🔍 Debug Steps:

1. **Check logs sau khi deploy:**
   - Railway → Logs → Tìm "HikariPool-1 - Start completed"
   - Nếu thấy lỗi database → Thử Cách 2 (Public URL)

2. **Test health endpoint:**
   ```
   https://washifybe-production.up.railway.app/api/actuator/health
   ```
   - Nên trả về: `{"status":"UP"}`

3. **Test Swagger:**
   ```
   https://washifybe-production.up.railway.app/swagger-ui/index.html
   ```

4. **Test API docs:**
   ```
   https://washifybe-production.up.railway.app/api/v3/api-docs
   ```
   - Nên trả về JSON config, không phải 502

---

## ⚠️ Common Issues:

### Issue 1: 502 Bad Gateway
- **Nguyên nhân:** App crash do database connection failed
- **Fix:** Kiểm tra logs, thử Public URL thay vì Internal

### Issue 2: Application failed to start
- **Nguyên nhân:** Thiếu biến environment
- **Fix:** Đảm bảo có đủ 23 biến (check list trên)

### Issue 3: Access denied for user 'root'
- **Nguyên nhân:** Sai password
- **Fix:** Copy lại đúng `MYSQLPASSWORD` từ MySQL service

### Issue 4: Unknown database 'railway'
- **Nguyên nhân:** Database chưa được tạo
- **Fix:** Thêm `createDatabaseIfNotExist=true` vào DATABASE_URL

---

## 🎯 Tổng số biến cần có: **23 biến**

1. DATABASE_URL
2. DATABASE_USERNAME
3. DATABASE_PASSWORD
4. HIBERNATE_DDL_AUTO
5. SQL_INIT_MODE
6. SHOW_SQL
7. SQL_BINDER_LOG_LEVEL
8. LOG_LEVEL_ROOT
9. LOG_LEVEL_APP
10. LOG_LEVEL_SPRING
11. LOG_LEVEL_HIBERNATE
12. JWT_SECRET
13. JWT_EXPIRATION
14. EMAIL_USERNAME
15. EMAIL_PASSWORD
16. FRONTEND_URL
17. DEFAULT_PASSWORD
18. GUEST_DEFAULT_PASSWORD
19. MOMO_PARTNER_CODE
20. MOMO_ACCESS_KEY
21. MOMO_SECRET_KEY
22. MOMO_ENDPOINT
23. MOMO_REDIRECT_URL
24. MOMO_IPN_URL

(Actually 24 biến 😅)
