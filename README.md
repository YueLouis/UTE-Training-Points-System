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
- ✅ Khôi phục mật khẩu qua Email Link (token-based, industry standard)
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

20+ tables including:
- `users` - Sinh viên & Admin
- `events` - Sự kiện (online/offline)
- `event_registrations` - Đăng ký & điểm danh
- `point_transactions` - Lịch sử cộng điểm
- `student_semester_summary` - Tổng kết & xếp loại theo kỳ
- `student_points_cumulative` - CTXH/CDNN tích lũy (max 40/8)
- `password_reset_tokens` - Token-based password reset
- `org_units` - Cấu trúc tổ chức (12 khoa, viện, phòng, đoàn, CLB)
- `roles`, `permissions`, `user_roles_scoped` - RBAC system
- `notifications` - Thông báo
- _+ other supporting tables_

## 🔐 Security

- **Password**: BCrypt hashing (không lưu plaintext)
- **Password Reset**: Token/link via email (SHA-256 hashed, 15min expiration, one-time use)
- **Auth**: JWT access token (30min) + refresh token (7 days)
- **RBAC**: `STUDENT` vs `ADMIN` permissions (expanding to scoped org_units)

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
| Auth | `/api/auth/refresh` | POST | Refresh JWT token |
| Auth | `/api/auth/forgot-password` | POST | Request reset link (token via email) |
| Auth | `/api/reset-password` | POST | Reset password with token from link |
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

---

## ⚙️ Configuration

### Environment Variables

Set these in Railway or local `.env`:

```bash
# Database
DATABASE_URL=jdbc:mysql://host:port/database
DB_USER=root
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_long_random_secret_at_least_32_characters

# Mail (for password reset)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Password Reset
RESET_PEPPER=your_pepper_secret
RESET_FRONTEND_URL=https://your-frontend.com/reset-password

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend.com

# Profile
SPRING_PROFILES_ACTIVE=production
```

### P0 Production Safety Checklist

- ⚠️ **Rotate ALL secrets** if ever committed to Git (DB password, JWT secret, mail password)
- ⚠️ Set `CORS_ALLOWED_ORIGINS` to your FE domain (not `*` in production)
- ⚠️ Verify `server.error.include-stacktrace=never` in production profile
- ⚠️ Set strong `JWT_SECRET` (min 32 chars) and `RESET_PEPPER`

### How to Run

**Development:**
```bash
./mvnw spring-boot:run
```

**Production (Railway):**
- Set all environment variables above
- Push to GitHub → Railway auto-deploys
- Flyway migrations (V1-V7) run automatically

### Password Reset Flow

**Token/Link Method (Current):**
1. User requests reset → receives email with link: `https://frontend/reset-password?token=ABC123`
2. User clicks link → enters new password → frontend sends token + password to `/api/reset-password`
3. Backend validates token (not used, not expired) → updates password → marks token as used

**Security:**
- Token: 256-bit random, SHA-256 hashed with server pepper
- Expiration: 15 minutes
- One-time use only
- Email never reveals if account exists (always returns 200 OK)

---
