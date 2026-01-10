# 📚 UTE Training Points System - Backend API

> Hệ thống Quản lý Điểm rèn luyện cho Sinh viên Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🚀 Quick Start

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Run Locally
```bash
# 1. Clone repository
git clone https://github.com/YueLouis/UTE-Training-Points-System.git
cd UTE-Training-Points-System

# 2. Configure database
cp .env.example .env
# Edit .env with your database credentials

# 3. Run application
./mvnw spring-boot:run

# 4. Access Swagger UI
open http://localhost:8080/swagger-ui/index.html
```

## 📖 Documentation

- **[📘 Tài liệu chi tiết (Vietnamese)](docs/DETAILS.md)** - Đầy đủ luồng nghiệp vụ, database schema, API specs
- **[🚀 Deployment Guide](DEPLOYMENT_GUIDE.md)** - Local setup & Railway production deployment
- **[📋 Production Readiness](PRODUCTION_READINESS.md)** - Checklist & known limitations
- **[📊 Implementation Summary](IMPLEMENTATION_SUMMARY.md)** - Architecture overview & design decisions
- **[🌐 Live API Documentation](https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html)** - Swagger UI on Railway
- **[🎯 GitHub Repository](https://github.com/YueLouis/UTE-Training-Points-System)**

## ✨ Key Features

### For Students
- ✅ Đăng nhập đa phương thức (MSSV / Email / SĐT)
- ✅ Khôi phục mật khẩu qua OTP Email
- ✅ Đăng ký sự kiện online & offline
- ✅ Hoàn thành khảo sát với mã bí mật (chống gian lận)
- ✅ Xem bảng điểm & xếp loại tự động
- ✅ Nhận thông báo khi được cộng điểm

### For Admins
- ✅ Quản lý sự kiện (CRUD + đóng/mở)
- ✅ Check-in/Check-out sinh viên tại sự kiện
- ✅ Phê duyệt điểm (audit log đầy đủ)
- ✅ Tra cứu thông tin sinh viên

## 🏗️ Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.x |
| Database | MySQL 8.0 (Railway) |
| ORM | Spring Data JPA |
| Security | BCrypt, JWT (upcoming) |
| Docs | Swagger/OpenAPI 3.0 |
| Email | Gmail SMTP |
| Deployment | Railway.app |

## 📊 Database Schema

10 core tables:
- `users` - Sinh viên & Admin
- `events` - Sự kiện (online/offline)
- `event_registrations` - Đăng ký & điểm danh
- `point_transactions` - Lịch sử cộng điểm
- `student_semester_summary` - Tổng kết & xếp loại
- `notifications` - Thông báo
- `password_reset_codes` - Quản lý OTP
- _+ 3 danh mục phụ_

## 🔐 Security

- **Password**: BCrypt hashing (không lưu plaintext)
- **OTP**: SHA-256 hashed, 120s expiration, one-time use
- **Auth**: Token-based (JWT trong production)
- **RBAC**: `STUDENT` vs `ADMIN` permissions

## 🌍 Deployment

**Production:** [Railway.app](https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html)

Environment variables (set in Railway):
```bash
SPRING_PROFILES_ACTIVE=production
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_app_password
# ... see .env.example for full list
```

## 📡 API Overview

### Core Endpoints

| Module | Endpoint | Method | Description |
|--------|----------|--------|-------------|
| Auth | `/api/auth/login` | POST | Đăng nhập |
| Auth | `/api/auth/forgot-password/*` | POST | Quy trình OTP 3 bước |
| Events | `/api/events` | GET/POST | Danh sách & tạo sự kiện |
| Registration | `/api/event-registrations` | POST/PUT | Đăng ký & check-in/out |
| Points | `/api/points/summary/{studentId}` | GET | Bảng điểm & xếp loại |
| Notifications | `/api/notifications/user/{userId}` | GET | Thông báo cá nhân |

_Full API specs:_ [Swagger UI](https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html)

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw verify
```

## 📝 Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history.

## 🤝 Contributing

1. Fork the repo
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

## 👥 Team

Developed by UTE Training Points System Team  
© 2026 All Rights Reserved

---

**Links:**
- 🌐 [Live API](https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html)
- 📖 [Full Documentation](docs/DETAILS.md)
- 🚂 [Railway Deployment](https://railway.com/invite/C8qZFcVV4S6)

