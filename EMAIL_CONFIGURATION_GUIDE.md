# 📧 Hướng dẫn cấu hình Email cho Password Reset

## 🎯 Tổng quan

Để gửi email reset password, bạn cần cấu hình SMTP server trong `application.properties`.

---

## ✅ Đã hoàn thành

### 1. Dependencies
```xml
<!-- Đã có trong pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

### 2. EmailService
✅ `src/main/java/com/washify/apis/service/EmailService.java`
- Gửi email HTML với template đẹp
- Method `sendPasswordResetEmail(email, token)`
- Error handling và logging

### 3. PasswordResetService đã integrate
✅ Sử dụng `EmailService` để gửi email
✅ Sử dụng `PasswordEncoder` để hash password

---

## 📝 Cấu hình Email

### Option 1: Gmail (Khuyến nghị cho Development)

#### Bước 1: Tạo App Password của Gmail

1. Truy cập: https://myaccount.google.com/security
2. Bật **2-Step Verification** (nếu chưa bật)
3. Vào **App passwords**: https://myaccount.google.com/apppasswords
4. Chọn **Mail** và **Windows Computer**
5. Click **Generate** → Copy mật khẩu 16 ký tự

#### Bước 2: Cập nhật application.properties

```properties
# Email Configuration - Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# App Configuration
app.frontend.url=http://localhost:3000
app.name=Washify
```

#### Bước 3: Sử dụng Environment Variables (Khuyến nghị)

Để không commit thông tin nhạy cảm vào Git:

**Windows PowerShell:**
```powershell
$env:EMAIL_USERNAME="your-email@gmail.com"
$env:EMAIL_PASSWORD="your-app-password-16-chars"
$env:FRONTEND_URL="http://localhost:3000"
```

**application.properties:**
```properties
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
app.frontend.url=${FRONTEND_URL:http://localhost:3000}
```

---

### Option 2: Outlook/Hotmail

```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

### Option 3: Custom SMTP Server

```properties
spring.mail.host=smtp.your-domain.com
spring.mail.port=587
spring.mail.username=noreply@your-domain.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🧪 Testing Email

### Test 1: Gửi email thật

```bash
# Step 1: Cấu hình email credentials (như trên)

# Step 2: Start server
mvn spring-boot:run

# Step 3: Request forgot password
POST http://localhost:8080/api/auth/forgot-password
Content-Type: application/json

{
  "email": "real-email@gmail.com"
}

# Step 4: Check email inbox
# Bạn sẽ nhận được email với link reset password
```

---

### Test 2: Verify email template

Email sẽ có format như sau:

```
Subject: Đặt lại mật khẩu - Washify

Body:
┌────────────────────────────────────────┐
│         🧺 Washify                      │
├────────────────────────────────────────┤
│  Yêu cầu đặt lại mật khẩu              │
│                                         │
│  Xin chào,                              │
│                                         │
│  Click vào nút bên dưới:               │
│                                         │
│  ┌──────────────────────┐              │
│  │  🔐 Đặt lại mật khẩu  │  ← Button   │
│  └──────────────────────┘              │
│                                         │
│  ⚠️ Link hết hạn sau 30 phút           │
│  ⚠️ Chỉ dùng được 1 lần                │
│                                         │
│  Link: http://localhost:3000/...       │
└────────────────────────────────────────┘
```

---

## 🚨 Troubleshooting

### Lỗi 1: Authentication failed

```
Caused by: javax.mail.AuthenticationFailedException: 535-5.7.8 Username and Password not accepted
```

**Giải pháp:**
- ✅ Bật 2-Step Verification trong Gmail
- ✅ Tạo App Password (KHÔNG dùng password thường)
- ✅ Copy đúng 16 ký tự (không có dấu cách)

---

### Lỗi 2: Connection timeout

```
com.sun.mail.util.MailConnectException: Couldn't connect to host, port: smtp.gmail.com, 587
```

**Giải pháp:**
- ✅ Check firewall/antivirus blocking port 587
- ✅ Check internet connection
- ✅ Try port 465 với SSL:
  ```properties
  spring.mail.port=465
  spring.mail.properties.mail.smtp.ssl.enable=true
  ```

---

### Lỗi 3: Email gửi vào Spam

**Giải pháp:**
- ✅ Dùng email domain chính thức (không phải Gmail cá nhân)
- ✅ Cấu hình SPF, DKIM, DMARC records
- ✅ Thêm "Reply-To" header
- ✅ Tránh từ ngữ spam trong subject/body

---

## 🔐 Security Best Practices

### 1. Never commit credentials
```gitignore
# .gitignore
application-local.properties
.env
```

### 2. Use Environment Variables
```bash
# .env file (add to .gitignore)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
FRONTEND_URL=http://localhost:3000
```

### 3. Use Spring Profiles
```properties
# application-dev.properties
spring.mail.username=dev-email@gmail.com

# application-prod.properties
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
```

Run with profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 📊 Email Sending Flow

```
User request forgot password
         ↓
PasswordResetService.createPasswordResetToken(email)
         ↓
Generate UUID token + Save to DB
         ↓
EmailService.sendPasswordResetEmail(email, token)
         ↓
Create HTML email with template
         ↓
JavaMailSender.send(message)
         ↓
SMTP Server (Gmail)
         ↓
User's Email Inbox ✉️
```

---

## 📝 Configuration Checklist

**Trước khi test, check các điều sau:**

- [ ] Đã tạo App Password trong Gmail
- [ ] Đã cập nhật `spring.mail.username` với email thật
- [ ] Đã cập nhật `spring.mail.password` với App Password
- [ ] Đã cập nhật `app.frontend.url` (nếu có frontend)
- [ ] Không commit credentials vào Git
- [ ] Email test thật tồn tại trong database
- [ ] MySQL đang chạy
- [ ] Server Spring Boot đang chạy

---

## 🎯 Production Recommendations

### 1. Use Email Service Providers

Thay vì Gmail, dùng dịch vụ chuyên nghiệp:

- **SendGrid** (free 100 emails/day)
- **AWS SES** (cheap, reliable)
- **Mailgun** (developer-friendly)
- **Postmark** (transactional emails)

### 2. Email Template Management

Tách HTML template ra file riêng:

```java
@Value("classpath:templates/email/password-reset.html")
private Resource emailTemplate;
```

### 3. Email Queue

Dùng RabbitMQ/Redis queue để gửi email async:

```java
@Async
public void sendPasswordResetEmail(String email, String token) {
    // Send email asynchronously
}
```

### 4. Monitoring & Logging

```java
log.info("Email sent to: {}", email);
log.error("Failed to send email: {}", e.getMessage());
```

### 5. Rate Limiting

Giới hạn số email gửi trong 1 giờ:

```java
@RateLimiter(name = "emailSender", fallbackMethod = "rateLimitFallback")
public void sendPasswordResetEmail(...) {
    // ...
}
```

---

## ✅ Summary

### Hiện trạng:
✅ EmailService đã implement xong  
✅ HTML template đẹp  
✅ Error handling  
✅ Logging  
✅ Integration với PasswordResetService  

### Cần làm:
⚠️ Cấu hình Gmail App Password  
⚠️ Update application.properties với credentials  
⚠️ Test gửi email thật  

### Next Steps:
1. Tạo App Password trong Gmail
2. Update application.properties
3. Run server và test
4. Check email inbox
5. Click link reset password
6. Verify flow hoàn chỉnh

---

## 🚀 Ready to Go!

Sau khi cấu hình email xong, tính năng **Password Reset via Email** sẽ hoàn toàn production-ready! 🎉

```bash
# Test ngay!
POST /api/auth/forgot-password
{
  "email": "your-real-email@gmail.com"
}

# → Check email inbox → Click link → Reset password → Done! ✅
```
