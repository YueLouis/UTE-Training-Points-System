# UTE Training Points System – Backend API

Backend REST API cho hệ thống Quản lý hoạt động & điểm rèn luyện sinh viên (UTE).

---

## 🚀 Tech Stack

1. Java 17

3. Spring Boot 3.x

3. Spring Web, Spring Data JPA, Validation

4. MySQL

5. Spring Mail (OTP Email)

6. Lombok

7. Swagger / OpenAPI

8. Railway (Production Deployment)

---

## 🏗️ Architecture Overview
```
Controller  →  Service  →  Repository  →  MySQL
                 ↓
            Business Rules
```

1. RESTful API

2. JPA auto schema update

3. Environment-based config (local / production)

4. Standardized error handling

---

## 🗄️ Database (MySQL)

Main tables:

1. users

2. events

3. event_registrations

4. event_categories

5. password_reset_codes

6. Security:

Passwords stored as BCrypt hash

OTP stored as SHA-256 hash

OTP has expire time + used_at

---

## 🔐 Authentication – Forgot Password (OTP)

Flow:

1. Request OTP

2. Verify OTP

3. Reset password

```
POST /api/auth/forgot-password/request
POST /api/auth/forgot-password/verify
POST /api/auth/forgot-password/reset
```

Features:

1. OTP expires (configurable, default 120s)

2. Old OTP auto invalidated

3. Scheduled cleanup removes expired OTP

4. Email sent via Gmail SMTP

---

## 🎯 Core Business Flows
### A) OFFLINE Event (ATTENDANCE)

1. Create event (OPEN)

2. Student registers

3. Check-in

4. Check-out
→ Registration COMPLETED
→ Training points awarded

### B) ONLINE Event (Survey)

1. Create event (OPEN, surveyUrl)

2. Student completes survey
→ Registration COMPLETED
→ Training points awarded

---

## 🔌 API Endpoints (Core)
### 1. Events
```
GET    /api/events
GET    /api/events/{id}
GET    /api/events/by-category/{categoryId}
POST   /api/events
PUT    /api/events/{id}
POST   /api/events/{id}/close
DELETE /api/events/{id}
```

### 2. Event Registrations
```
POST /api/event-registrations

GET  /api/event-registrations/by-student/{studentId}
GET  /api/event-registrations/by-event/{eventId}

PUT  /api/event-registrations/{id}/cancel
PUT  /api/event-registrations/{eventId}/checkin/{studentId}
PUT  /api/event-registrations/{eventId}/checkout/{studentId}
PUT  /api/event-registrations/{eventId}/complete-survey/{studentId}
```

### 3. Auth 
```
POST /api/auth/forgot-password/request
POST /api/auth/forgot-password/verify
POST /api/auth/forgot-password/reset
```

### 4. User 
```
GET /api/users
GET /api/users/{id}
```

### 5. Points
```
GET /api/points/summary/{studentId}
```

### 6. Event Category
```
GET /api/event-categories
GET /api/event-categories/{id}
```

---

## ⚠️ Business Rules

1. ONLINE events cannot check-in / check-out

2. ATTENDANCE events must check-in before check-out

3. One student → one registration per event

4. Training points awarded once

5. Event auto-closes when full (if enabled)

---

### ❗ Error Handling

All errors are standardized:
```
400 – Bad Request
404 – Not Found
409 – Conflict
500 – Internal Server Error
```

---

## 🧪 Run Locally
```
mvn spring-boot:run
```

Swagger:
```
http://localhost:8080/swagger-ui/index.html
```

---

## ☁️ Production Deployment (Railway)

1. Spring Boot service + MySQL plugin

2. Environment variables:

MYSQLHOST, MYSQLPORT, MYSQLDATABASE

MYSQLUSER, MYSQLPASSWORD

SPRING_PROFILES_ACTIVE=production

Auto deploy on push

Swagger available on production URL

---

## ✅ Pre-push Checklist
✔ mvn clean test
✔ Swagger UI accessible
✔ Attendance flow tested
✔ Online flow tested
✔ OTP reset tested
✔ target/ ignored

---

## 📦 Note

This repository contains Backend API only. Frontend (Android / Web) is maintained separately.
