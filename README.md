# UTE Training Points System - Backend API

> **Hệ thống Quản lý Điểm Rèn luyện** - Trường Đại học Sư phạm Kỹ thuật TP.HCM

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-green?logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![Railway](https://img.shields.io/badge/Railway-Production-blueviolet?logo=railway)

## 🚀 Tính Năng Chính

### 👨‍🎓 Sinh Viên
- 📝 Đăng ký & quản lý tham gia sự kiện
- 📊 Xem bảng điểm chi tiết (DRL, CTXH, CDNN)
- 🔐 Reset mật khẩu qua email xác thực (OTP/Token)
- 🔔 Nhận thông báo tức thì khi cộng điểm
- 🎯 Tham gia khảo sát online với mã bí mật

### 🔐 Quản trị Viên (Admin/CTSV)
- 🎫 Quản lý sự kiện (tạo, sửa, đóng, xóa)
- 👥 Quản lý phân quyền theo đơn vị (Khoa, Đoàn, CLB)
- ✅ Check-in/Check-out sinh viên tại sự kiện
- 💰 Cộng/duyệt điểm tự động hoặc thủ công
- 📋 Xuất báo cáo & audit log theo timeline

### 🔒 Bảo Mật
- 🛡️ JWT Token-based Authentication
- 🔑 Password hashing với BCrypt + Salt
- 🚫 Rate limiting cho login/forgot-password
- 📝 Audit log ghi lại mọi hành động quan trọng
- 🔐 Dữ liệu email xác thực OTP không lưu plaintext

---

## 📋 Yêu Cầu Hệ Thống

```bash
- Java 17+
- MySQL 8.0+
- Maven 3.8+
```

---

## 🔧 Cấu Hình & Chạy Local

### 1️⃣ Clone & Setup

```bash
git clone https://github.com/YueLouis/UTE-Training-Points-System.git
cd UTE-Training-Points-System-API
```

### 2️⃣ Config Database

Tạo file `.env` tại thư mục gốc:

```env
# Database
DEV_DATABASE_URL=jdbc:mysql://localhost:3306/railway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
DEV_DB_USER=root
DEV_DB_PASSWORD=password

# JWT
JWT_SECRET=your_super_secret_key_at_least_32_characters_long

# Email (Resend API)
RESEND_API_KEY=re_your_resend_api_key

# Password Reset
RESET_PEPPER=your_random_pepper_string
RESET_FRONTEND_URL=http://localhost:3000/reset-password
MAIL_FROM=onboarding@resend.dev
```

### 3️⃣ Build & Run

```bash
# Chạy migration Flyway + app
mvn clean install
mvn spring-boot:run

# Hoặc chỉ định profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### 4️⃣ Swagger API Docs

Sau khi app chạy, truy cập:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🚢 Triển Khai trên Railway

### Bước 1: Kết nối Repository

```bash
# Push code lên GitHub
git push origin main

# Railway sẽ tự động detect Java project
```

### Bước 2: Set Environment Variables

Trong Railway Dashboard → Services → UTE-Training-Points-System:

```yaml
# Database (Railway MySQL Plugin)
DATABASE_URL=jdbc:mysql://mysql.railway.internal:3306/railway
DB_USER=root
DB_PASSWORD=<railway_generated_password>

# JWT & Security
JWT_SECRET=<strong_random_string_32+_chars>
SPRING_PROFILES_ACTIVE=production

# Email
RESEND_API_KEY=re_your_api_key
MAIL_FROM=onboarding@resend.dev

# Password Reset
RESET_PEPPER=<random_pepper>
RESET_FRONTEND_URL=https://your-frontend-domain.com/reset-password
```

### Bước 3: Deploy

```bash
# Railway auto-deploy on push
git push origin main

# Check deployment
# Railway Dashboard → Deployments
```

---

## 📚 API Endpoints

### 🔑 Authentication
```
POST   /api/auth/login
POST   /api/auth/refresh
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
```

### 📅 Events
```
GET    /api/events
GET    /api/events/{id}
GET    /api/events/by-category/{categoryId}
POST   /api/events                    (Admin)
PUT    /api/events/{id}               (Admin)
DELETE /api/events/{id}               (Admin)
POST   /api/events/{id}/close         (Admin)
```

### 📝 Event Registrations
```
POST   /api/event-registrations
GET    /api/event-registrations/by-student/{studentId}
GET    /api/event-registrations/by-event/{eventId}
PUT    /api/event-registrations/{id}/cancel
PUT    /api/event-registrations/{id}/check-in          (Admin)
PUT    /api/event-registrations/{id}/check-out         (Admin)
PUT    /api/event-registrations/{eventId}/complete-survey/{studentId}
```

### 💰 Points & Summary
```
GET    /api/points/summary/{studentId}
GET    /api/points/transactions
```

### 👤 Users
```
GET    /api/users
GET    /api/users/{id}
```

### 📱 Notifications
```
GET    /api/notifications/user/{userId}
PUT    /api/notifications/{id}/read
```

### 📊 Health & Metrics (Actuator)
```
GET    /actuator/health
GET    /actuator/info
GET    /actuator/metrics
```

---

## 🗂️ Project Structure

```
src/main/
├── java/vn/hcmute/trainingpoints/
│   ├── config/              # Security, WebConfig, Interceptor
│   ├── controller/          # REST Controllers
│   ├── dto/                 # Data Transfer Objects
│   ├── entity/              # JPA Entities
│   ├── exception/           # Exception Handlers
│   ├── repository/          # JPA Repositories
│   ├── service/             # Business Logic
│   ├── util/                # Utilities (JWT, Token, etc)
│   └── UteTrainingPointsSystemApiApplication.java
│
└── resources/
    ├── application.properties
    ├── application-production.yml
    └── db/migration/
        ├── V1__init_schema.sql
        ├── V2__add_indexes.sql
        ├── V3__add_password_reset_tokens.sql
        ├── V4__upgrade_semesters_and_cumulative.sql
        ├── V5__add_org_units_and_rbac.sql
        ├── V6__seed_org_units_roles_permissions.sql
        ├── V8__fix_event_mode_enum.sql
        └── V9__create_audit_logs_table.sql
```

---

## 🔄 Database Schema

### Bảng Chính
- **users**: Tài khoản (Student, Admin)
- **events**: Sự kiện / Hoạt động ngoài khóa
- **event_registrations**: Đăng ký tham gia sự kiện
- **point_transactions**: Lịch sử cộng trừ điểm
- **student_semester_summary**: Tổng kết điểm theo kỳ
- **student_points_cumulative**: Tích luỹ CTXH/CDNN
- **notifications**: Thông báo cho sinh viên
- **password_reset_tokens**: Token reset mật khẩu
- **org_units**: Đơn vị tổ chức (Khoa, Phòng, Đoàn, CLB)
- **audit_logs**: Lịch sử thay đổi & duyệt

---

## 🔐 Quy Trình Reset Mật Khẩu

### Flow 1: OTP via Email (Chuẩn)

```
1. User nhập email → POST /api/auth/forgot-password
2. Server gửi OTP (mã 6 số) → Email user
3. User nhập OTP → verify
4. User đặt mật khẩu mới → Password update
```

### Flow 2: Token/Link (Production Standard)

```
1. User nhập email → POST /api/auth/forgot-password
2. Server sinh token → gửi link email: /reset-password?token=xxx
3. User mở link → nhập mật khẩu mới
4. POST /api/auth/reset-password?token=xxx
5. Password update (token marked as used)
```

**Đặc điểm bảo mật:**
- ✅ OTP hash trong DB (không plaintext)
- ✅ Token expire 15 phút
- ✅ Token one-time use (dùng xong là vô hiệu)
- ✅ Rate limit: Max 5 request/phút per IP
- ✅ Không lộ email tồn tại hay không

---

## 🎯 Quy Trình Cộng Điểm

### Attendance (Sự kiện trực tiếp)

```
Admin check-in → check-out → Tính điểm tự động
DRL (kỳ): +3/4/5 điểm
CTXH: +5 điểm (max 40)
CDNN: +1/2/3 điểm (max 8)
```

### Online Survey (Khảo sát online)

```
Student hoàn thành survey + nhập Secret Code
→ Điểm cộng tức thì
→ Thông báo gửi mail
```

### Manual Approval (Phê duyệt thủ công)

```
Admin duyệt event → Cộng điểm manually
→ Audit log ghi lại (Ai duyệt, khi nào, bao nhiêu điểm)
```

---

## 📊 Ranking & Classification

**DRL theo kỳ** (0-100):
- 🥇 Xuất sắc: 90-100
- 🥈 Tốt: 80-89
- 🥉 Khá: 70-79
- 📋 Trung bình: 60-69
- ❌ Yếu: < 60

**CTXH/CDNN tích lũy** (với trần):
- CTXH: 0-40 điểm
- CDNN: 0-8 điểm

---

## 🛠️ Technologies & Dependencies

```
Java 17
Spring Boot 3.5.8
Spring Data JPA
Spring Security + JWT (JJWT)
MySQL 8.0 + Flyway migration
Lombok
OkHttp (Resend API)
SpringDoc OpenAPI (Swagger)
Spring Boot Actuator (Metrics/Health)
```

---

## 🧪 Testing

### Local Test

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Test coverage
mvn clean test jacoco:report
```

### API Test (Postman/Curl)

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"23162102","password":"password"}'

# Get Events
curl http://localhost:8080/api/events

# Register Event
curl -X POST http://localhost:8080/api/event-registrations \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"eventId":1,"studentId":2,"note":"Đăng ký tham gia"}'
```

---

## 📝 Logging & Monitoring

### Log Levels (Production)
- 🔴 ERROR: Lỗi nghiêm trọng
- 🟠 WARN: Cảnh báo (login fail, invalid token)
- 🟢 INFO: App-specific (successful actions)
- ⚪ DEBUG: Disabled (để tránh spam)

### Actuator Endpoints
```bash
GET /actuator/health                # App status
GET /actuator/metrics               # Performance metrics
GET /actuator/info                  # App info
```

---

## 🐛 Troubleshooting

### 1. Migration Flyway bị failed

```sql
-- Kiểm tra status
SELECT * FROM flyway_schema_history;

-- Xóa failed migration
DELETE FROM flyway_schema_history WHERE success = 0;
```

### 2. JWT Token expired

```
Error: "Invalid or expired JWT token"
Solution: Call /api/auth/refresh với refresh_token để lấy access_token mới
```

### 3. Password Reset Email không tới

```
- Check RESEND_API_KEY có đúng không
- Check email domain verified trong Resend dashboard
- Check Spam folder
```

### 4. Rate limit exceeded

```
Error: "Too many requests. Try again later."
Solution: Chờ 1 phút rồi thử lại (Rate limit: 5 request/phút per IP)
```

---

## 📖 Documentation

- 📚 **API Docs**: `/swagger-ui/index.html`
- 🔐 **Security**: `/docs/security.md`
- 🗂️ **Database Design**: `/docs/database_design.md`
- 🔄 **Workflows**: `/docs/workflows.md`

---

## 📄 License

MIT License - © 2026 HCMUTE

---

## 👥 Contributors

- **Development Team**: UTE Training Points System Project Group
- **Advisor**: HCMUTE Faculty

---

## 🌐 Live Demo

**API Base URL (Production):**
```
https://ute-training-points-system-production.up.railway.app
```

**Swagger UI:**
```
https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html
```

---

## 📞 Support & Contact

Gặp vấn đề? Tạo issue trên GitHub hoặc liên hệ team:

- Email: contact@hcmute.edu.vn
- GitHub: https://github.com/YueLouis/UTE-Training-Points-System

---

**Last Updated:** January 2026  
**Status:** 🟢 Active & Production Ready

