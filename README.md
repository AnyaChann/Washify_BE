# 🧺 Washify Backend

**Laundry Management System API**

Built with Spring Boot 3.3.5 + Java 17

---

## 🚀 Quick Start

### Prerequisites
- Java 17
- MySQL 8.0
- Maven 3.8+

### Run Locally

```bash
# Clone repository
git clone https://github.com/AnyaChann/Washify_BE.git
cd Washify_BE

# Copy .env.example to .env
cp .env.example .env

# Update .env with your database credentials
# DATABASE_URL=jdbc:mysql://localhost:3306/washify_db
# DATABASE_USERNAME=root
# DATABASE_PASSWORD=yourpassword

# Build and run
./mvnw spring-boot:run
```

Application will start at: `http://localhost:8080/api`

---

## 📚 API Documentation

### 🎯 By Priority (Development Order)

1. **[Customer Web Application](./docs/api/01-customer-web-app.md)** ⭐ Priority 1
2. **[Admin & Manager Dashboard](./docs/api/02-admin-manager-dashboard.md)** ⭐ Priority 2
3. **[Staff Portal](./docs/api/03-staff-portal.md)** ⭐ Priority 3
4. **[Shipper Mobile App](./docs/api/04-shipper-mobile-app.md)** ⭐ Priority 4

### 📖 References

- **[Quick Reference Card](./docs/api/QUICK_REFERENCE.md)** - Essential endpoints & status codes
- **[Response Formats](./docs/api/references/response-formats.md)** - Success/Error responses
- **[Status Flows](./docs/api/references/status-flows.md)** - Order/Payment/Shipment flows
- **[Error Handling](./docs/api/references/error-handling.md)** - Frontend best practices
- **[Testing Guide](./docs/api/references/testing-guide.md)** - Postman collections

### 🗺️ Full Documentation

👉 **[docs/api/README.md](./docs/api/README.md)**

---

## 🏗️ Tech Stack

- **Framework**: Spring Boot 3.3.5
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Security**: JWT + Spring Security
- **ORM**: Spring Data JPA + Hibernate
- **Build Tool**: Maven
- **Documentation**: Swagger/OpenAPI
- **Deployment**: Railway.com (Free Tier)

---

## 📂 Project Structure

```
src/
├── main/
│   ├── java/com/washify/apis/
│   │   ├── config/           # Security, Dotenv, CORS
│   │   ├── controller/       # REST endpoints
│   │   ├── dto/              # Request/Response DTOs
│   │   ├── entity/           # JPA entities
│   │   ├── exception/        # Global exception handler
│   │   ├── repository/       # JPA repositories
│   │   ├── security/         # JWT authentication
│   │   └── service/          # Business logic
│   └── resources/
│       ├── application.properties
│       ├── data.sql          # Initial data
│       └── db/migration/     # Flyway migrations
└── test/                     # Unit & integration tests
```

---

## 🔐 Security

### Roles
- `ADMIN` - Full system access
- `MANAGER` - Branch management
- `STAFF` - Order processing
- `SHIPPER` - Shipment delivery
- `CUSTOMER` - Place orders
- `GUEST` - First-time customers

### Authentication
- JWT Bearer Token
- Token expiry: 24 hours
- Refresh token: Not yet implemented

---

## 🌍 Environment Variables

See `.env.example` for all configuration options.

**Essential variables:**
```properties
# Database
DATABASE_URL=jdbc:mysql://localhost:3306/washify_db
DATABASE_USERNAME=root
DATABASE_PASSWORD=yourpassword

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Email (Gmail)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
```

---

## 🚀 Deployment

### Railway.com (Production)

**Live URL**: https://washifybe-production.up.railway.app/api

**Configuration:**
- Java 17 runtime
- Memory: 512MB (optimized)
- Database: Aiven MySQL (1GB free tier)
- SSL/TLS: Enabled

**Deployment files:**
- `railway.toml` - Build & deploy config
- `system.properties` - Force Java 17
- `.env.production.example` - Production env template

---

## 🧪 Testing

### Run Tests

```bash
./mvnw test
```

### Test Coverage
- Unit tests: Service layer
- Integration tests: Soft delete functionality
- Postman collection: [docs/api/references/testing-guide.md](./docs/api/references/testing-guide.md)

---

## 📊 Database

### Schema
- 17 tables: users, roles, branches, services, orders, payments, shipments, etc.
- UTF-8 support: utf8mb4_unicode_ci
- Soft delete: Implemented across all entities
- Audit logs: Track all critical operations

### Migrations
- Flyway migrations in `src/main/resources/db/migration/`
- V1: Add soft delete support
- V2-V5: Authentication enhancements

---

## 🔧 Development

### Hot Reload

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build for Production

```bash
./mvnw clean package -DskipTests
java -jar target/Washify_BE-0.0.1-SNAPSHOT.jar
```

### Swagger UI

http://localhost:8080/swagger-ui/index.html

---

## 🐛 Known Issues

1. **Pagination**: Not yet implemented (returns all data)
2. **WebSocket**: Real-time notifications use polling
3. **File Upload**: Local storage only (no S3/cloud)
4. **Refresh Token**: Not implemented

---

## 🛠️ Roadmap

- [ ] Implement pagination for all list endpoints
- [ ] Add refresh token mechanism
- [ ] WebSocket for real-time notifications
- [ ] Cloud storage for images (S3/Azure Blob)
- [ ] Rate limiting
- [ ] API versioning
- [ ] Caching (Redis)
- [ ] Monitoring (Prometheus + Grafana)

---

## 📝 Changelog

### Version 1.1 (2025-10-22)
- ✅ Multi-method login (username/email/phone)
- ✅ First-time password change for guest users
- ✅ Configurable default passwords
- ✅ MANAGER role support
- ✅ API documentation restructured

### Version 1.0 (2025-10-21)
- ✅ Core features implemented
- ✅ JWT authentication
- ✅ Soft delete support
- ✅ Railway deployment
- ✅ Aiven MySQL integration

---

## 🤝 Contributing

This is a private academic project. Contributions are limited to team members.

---

## 👥 Team

- **Backend Lead**: [Your Name]
- **Database**: [Team Member]
- **Frontend**: [Team Member]
- **Mobile**: [Team Member]

---

## 📄 License

Private academic project - All rights reserved

---

## 📞 Support

For questions or issues:
1. Check [API Documentation](./docs/api/README.md)
2. Review [Testing Guide](./docs/api/references/testing-guide.md)
3. Contact backend team

---

**Last Updated**: 2025-10-22  
**Version**: 1.1  
**Status**: ✅ Production Ready
