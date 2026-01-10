# 📚 UTE Training Points System - Backend API - Tài Liệu Hoàn Chỉnh

Hệ thống Quản lý Điểm rèn luyện cho sinh viên Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE). Dự án được xây dựng với mục tiêu giúp sinh viên dễ dàng theo dõi, đăng ký sự kiện và tự động hóa quy trình cộng điểm rèn luyện, điểm công tác xã hội.

---

## 🚀 Công Nghệ Sử Dụng
- **Ngôn ngữ:** Java 17
- **Framework:** Spring Boot 3.5.x
- **Cơ sở dữ liệu:** MySQL (Railway)
- **ORM:** Spring Data JPA / Hibernate
- **Bảo mật:** BCrypt Password Hashing, Token-based Authentication
- **Thông báo:** Tự động tạo Notification khi có biến động điểm
- **Tài liệu API:** Swagger UI / OpenAPI 3.0
- **Deployment:** Railway.app

---

## 📊 Cấu Trúc Database (10 Bảng)
Hệ thống sử dụng cơ sở dữ liệu quan hệ với 10 thực thể chính:
1. `users`: Thông tin Sinh viên và Quản trị viên (Admin).
2. `events`: Thông tin chi tiết các sự kiện (Offline & Online).
3. `event_categories`: Danh mục sự kiện (Hội thảo, Tình nguyện, Khảo sát...).
4. `event_registrations`: Quản lý việc đăng ký, check-in, check-out của sinh viên.
5. `point_types`: Các loại điểm (DRL, CTXH, CDNN).
6. `point_transactions`: Nhật ký chi tiết mỗi lần cộng điểm.
7. `student_semester_summary`: Bảng tổng kết điểm và xếp loại theo từng học kỳ.
8. `notifications`: Lưu trữ các thông báo gửi đến người dùng.
9. `password_reset_codes`: Quản lý mã OTP khôi phục mật khẩu qua Email.
10. `semesters`: (Dữ liệu danh mục) Quản lý thông tin học kỳ.

---

## 🔑 Các Luồng Nghiệp Vụ Chính

### 1. Luồng Xác Thực (Authentication)
- Đăng nhập bằng MSSV hoặc Email.
- Khôi phục mật khẩu qua mã OTP gửi về Email cá nhân (3 bước bảo mật).

### 2. Luồng Sự Kiện Online (Chống Gian Lận)
- Sinh viên làm khảo sát qua Google Forms.
- Lấy **Mã bí mật (Secret Code)** ở cuối bài khảo sát để nhập vào App.
- Hệ thống đối soát mã đúng mới thực hiện cộng điểm tự động.

### 3. Luồng Sự Kiện Offline (Attendance)
- Đăng ký tham gia → Check-in (Vào cổng) → Check-out (Ra về).
- Điểm được cộng ngay khi hoàn thành bước Check-out.

### 4. Hệ Thống Xếp Loại (Ranking)
Tự động xếp loại điểm rèn luyện theo quy chế HCMUTE:
- **Xuất sắc:** 90 - 100
- **Tốt:** 80 - 89
- **Khá:** 70 - 79
- **Trung bình khá:** 60 - 69
- **Trung bình:** 50 - 59
- **Yếu:** 35 - 49
- **Kém:** < 35

---

## 📡 Danh Sách API Chính

### 🔐 Authentication (`/api/auth`)
- `POST /login`: Đăng nhập hệ thống (Trả về Token + Role).
- `POST /forgot-password/request`: Yêu cầu mã OTP.
- `POST /forgot-password/verify`: Xác thực mã OTP.
- `POST /forgot-password/reset`: Đổi mật khẩu mới.

### 📅 Sự Kiện (`/api/events`)
- `GET /`: Lấy danh sách sự kiện (hỗ trợ lọc theo `semesterId`, `categoryId`, `q`).
- `POST /`: Tạo sự kiện mới (Admin).
- `PUT /{id}`: Cập nhật sự kiện.
- `POST /{id}/close`: Đóng sự kiện.

### 📝 Đăng Ký & Điểm Danh (`/api/event-registrations`)
- `POST /`: Đăng ký tham gia sự kiện.
- `PUT /{id}/check-in`: Admin xác nhận vào (Sử dụng ID đăng ký).
- `PUT /{id}/check-out`: Admin xác nhận ra (Cộng điểm + Rank).
- `PUT /{eventId}/complete-survey/{studentId}`: SV xác nhận mã bí mật Online.

### 📈 Điểm & Thống Kê (`/api/points`)
- `GET /summary/{studentId}`: Lấy bảng điểm tổng kết và Xếp loại chuẩn HCMUTE.

### 🔔 Thông Báo (`/api/notifications`)
- `GET /user/{userId}`: Lấy danh sách thông báo của người dùng.

---

## 🏗️ Kiến Trúc Tổng Quan

### Layered Architecture
```
┌─────────────────────────────────────────┐
│         REST Controllers                │  (Tiếp nhận yêu cầu HTTP)
├─────────────────────────────────────────┤
│     Business Logic (Services)           │  (Xử lý logic nghiệp vụ)
├─────────────────────────────────────────┤
│    Data Access (Repository/JPA)         │  (Tương tác cơ sở dữ liệu)
├─────────────────────────────────────────┤
│       MySQL Database                    │  (Lưu trữ dữ liệu)
└─────────────────────────────────────────┘
```

### Thành Phần Chính:
- **Controller**: Xử lý HTTP requests/responses
- **Service**: Chứa logic xử lý, kiểm thử, validation
- **Repository**: Gọi database thông qua JPA
- **Entity**: Đại diện cho bảng trong database
- **DTO**: Data Transfer Object - truyền dữ liệu an toàn

---

## 🎯 Chi Tiết Các Module

### 1️⃣ AUTH MODULE (Xác Thực Người Dùng)

**Endpoints:**
```
POST   /api/auth/login
POST   /api/auth/forgot-password/request
POST   /api/auth/forgot-password/verify
POST   /api/auth/forgot-password/reset
```

#### 1.1 Login (`POST /api/auth/login`)

**Yêu cầu:**
```json
{
  "username": "23162099 || email@hcmute.edu.vn || 0123456789",
  "password": "password123"
}
```

**Xử Lý Logic:**
1. Kiểm tra `username` không trống
2. Kiểm tra `password` không trống
3. **Tìm User** theo:
   - Email (email column)
   - Student Code (mssv) - nếu email không có
   - Phone (sdt) - nếu cả 2 đều không có
4. **So sánh mật khẩu** bằng BCrypt (hàm `passwordEncoder.matches()`)
5. Kiểm tra account có bị disabled không (`status = false`)
6. **Trả về:**
   - Token (MOCK format: `MOCK_TOKEN_<userId>_<timestamp>`)
   - User Info (id, studentCode, fullName, email, phone, role, className, faculty, avatarUrl, status)

**Lỗi Có Thể Xảy Ra:**
- `400 BAD_REQUEST`: Username/password trống
- `401 UNAUTHORIZED`: Username không tồn tại hoặc password sai
- `403 FORBIDDEN`: Account bị disable

#### 1.2 Quên Mật Khẩu - Step 1: Request OTP (`POST /api/auth/forgot-password/request`)

**Yêu Cầu:**
```json
{
  "email": "student@hcmute.edu.vn"
}
```

**Xử Lý Logic:**
1. Kiểm tra email không trống
2. **Tìm User** bằng email
3. **Xóa tất cả OTP cũ chưa dùng** của email này (chỉ giữ các OTP đã dùng trong DB)
4. **Tạo OTP 6 số** ngẫu nhiên (100000 - 999999)
5. **Hash OTP** bằng SHA-256 trước khi lưu vào DB (không lưu plaintext)
6. **Lưu vào bảng `password_reset_codes`:**
   - `email`: email của user
   - `code_hash`: SHA256(OTP)
   - `created_at`: hiện tại
   - `expires_at`: hiện tại + 120 giây (config được: `app.reset.expireSeconds`)
   - `used_at`: null (chưa dùng)
7. **In OTP ra console** (để debug, chỉ bật trong dev, không production)
8. **Gửi email** qua Resend API (nếu email blocked vì sandbox mode thì log error)

**Lỗi Có Thể Xảy Ra:**
- `400 BAD_REQUEST`: Email trống
- `404 NOT_FOUND`: Email không tồn tại trong hệ thống

#### 1.3 Quên Mật Khẩu - Step 2: Xác Thực OTP (`POST /api/auth/forgot-password/verify`)

**Yêu Cầu:**
```json
{
  "email": "student@hcmute.edu.vn",
  "code": "123456",
  "otp": "123456"
}
```

**Xử Lý Logic:**
1. Kiểm tra email không trống
2. Kiểm tra code/otp không trống
3. **Tìm User** bằng email
4. **Lấy OTP gần nhất** (ORDER BY `created_at` DESC, LIMIT 1)
5. Kiểm tra `used_at IS NOT NULL` → báo lỗi "Code already used"
6. Kiểm tra `expires_at < NOW()` → báo lỗi "Code expired"
7. **So sánh:**
   - Hash input (SHA256(OTP nhập vào)) === `code_hash` trong DB
   - Nếu khác → báo lỗi "Invalid code"
8. Trả về: `{ "message": "OTP valid" }` (không set `used_at` ở bước này)

**Lỗi Có Thể Xảy Ra:**
- `400 BAD_REQUEST`: Email/code trống
- `404 NOT_FOUND`: Email không tồn tại
- `409 CONFLICT`: Code đã dùng rồi
- `401 UNAUTHORIZED`: Code sai hoặc hết hạn

#### 1.4 Quên Mật Khẩu - Step 3: Đặt Lại Mật Khẩu (`POST /api/auth/forgot-password/reset`)

**Yêu Cầu:**
```json
{
  "email": "student@hcmute.edu.vn",
  "code": "123456",
  "newPassword": "newPassword123"
}
```

**Xử Lý Logic:**
1. Kiểm tra email/code/newPassword không trống
2. Kiểm tra `newPassword.length() >= 6`
3. **Tìm User** bằng email
4. **Lấy OTP gần nhất**
5. Kiểm tra `used_at IS NOT NULL` → báo lỗi "Code already used"
6. Kiểm tra `expires_at < NOW()` → báo lỗi "Code expired"
7. **So sánh code:**
   - Hash input === `code_hash` trong DB
8. **Kiểm tra mật khẩu trùng:**
   - `BCryptPasswordEncoder.matches(newPassword, user.passwordHash)` 
   - Nếu trùng → báo lỗi "Mật khẩu trùng lặp"
9. **Cập nhật mật khẩu:**
   - `user.setPasswordHash(BCryptPasswordEncoder.encode(newPassword))`
   - `userRepository.save(user)`
10. **Đánh dấu OTP đã dùng:**
    - `otp.setUsedAt(LocalDateTime.now())`
    - `resetRepo.save(otp)`

**Lỗi Có Thể Xảy Ra:**
- `400 BAD_REQUEST`: Email/code/password trống hoặc password < 6 ký tự
- `404 NOT_FOUND`: Email không tồn tại
- `409 CONFLICT`: Code đã dùng
- `401 UNAUTHORIZED`: Code sai/hết hạn

---

### 2️⃣ EVENT MODULE (Quản Lý Sự Kiện)

**Endpoints:**
```
GET    /api/events
GET    /api/events/{id}
GET    /api/events/by-category/{categoryId}
POST   /api/events                    [Admin]
PUT    /api/events/{id}               [Admin]
POST   /api/events/{id}/close         [Admin]
DELETE /api/events/{id}               [Admin]
```

#### 2.1 Lấy Danh Sách Sự Kiện (`GET /api/events`)

**Query Parameters:**
- `studentId` (optional): để check trạng thái đã đăng ký chưa
- `semesterId` (optional): lọc theo học kỳ
- `categoryId` (optional): lọc theo danh mục
- `q` (optional): tìm kiếm theo title/description

**Xử Lý Logic:**
1. **Build Specification** (JPA criteria) dựa trên các filter
2. **Lấy danh sách events** và sắp xếp theo `id DESC` (mới nhất lên đầu)
3. **Nếu có studentId:** Lấy danh sách đăng ký của student
4. **Chuyển đổi sang DTO** (toDTO)
5. **Áp dụng flags** (applyFlags): registered, checkedIn, completed, canRegister, canCheckin, canCheckout, computedStatus

#### 2.2 Tạo Sự Kiện (`POST /api/events`)

**Yêu Cầu (Admin):**
```json
{
  "semesterId": 1,
  "categoryId": 2,
  "title": "Tình Nguyện Xuân",
  "startTime": "2026-01-10T09:00:00",
  "endTime": "2026-01-10T17:00:00",
  "registrationDeadline": "2026-01-09T23:59:59",
  "maxParticipants": 100,
  "pointTypeId": 1,
  "pointValue": 5,
  "eventMode": "ATTENDANCE"
}
```

**Xử Lý Logic:**
1. **Validate thời gian với học kỳ**
2. **Set default:** `eventMode = ATTENDANCE` (nếu null)
3. **Set status:** `status = OPEN`
4. **Lưu vào database** và trả về DTO

#### 2.3 Sửa Sự Kiện (`PUT /api/events/{id}`)

**Xử Lý Logic:**
- Tương tự Create Event
- **Ngoài ra:** Kiểm tra event tồn tại rồi update field

#### 2.4 Đóng Sự Kiện (`POST /api/events/{id}/close`)

**Xử Lý Logic:**
1. Tìm event bằng id
2. Set `status = CLOSED` (soft delete - dữ liệu vẫn giữ)
3. Lưu vào database

#### 2.5 Xóa Sự Kiện (`DELETE /api/events/{id}`)

**Xử Lý Logic:**
- Hard delete - xóa hoàn toàn khỏi database

---

### 3️⃣ EVENT REGISTRATION MODULE (Đăng Ký Sự Kiện)

**Endpoints:**
```
POST   /api/event-registrations
GET    /api/event-registrations/by-student/{studentId}
GET    /api/event-registrations/by-event/{eventId}
PUT    /api/event-registrations/{id}/cancel
PUT    /api/event-registrations/{eventId}/checkin/{studentId}
PUT    /api/event-registrations/{eventId}/checkout/{studentId}
PUT    /api/event-registrations/{id}/check-in
PUT    /api/event-registrations/{id}/check-out
PUT    /api/event-registrations/{eventId}/complete-survey/{studentId}
```

#### 3.1 Đăng Ký Tham Gia (`POST /api/event-registrations`)

**Yêu Cầu (Student):**
```json
{
  "eventId": 1,
  "studentId": 23162099,
  "note": "Mô tả lý do đăng ký"
}
```

**Xử Lý Logic:**
1. Tìm event bằng `eventId`
2. Kiểm tra `event.status != CLOSED`
3. **Kiểm tra đã đăng ký chưa:** Query `findByEventIdAndStudentId(eventId, studentId)`
4. Kiểm tra `now < registrationDeadline`
5. **ATTENDANCE ONLY:** Kiểm tra `now < startTime`
6. Kiểm tra slot: `currentCount < maxParticipants`
7. **Tạo EventRegistration**
8. **Lưu và trả về DTO**

#### 3.2 Hủy Đăng Ký (`PUT /api/event-registrations/{id}/cancel`)

**Query Parameters:**
- `userId`: id của người hủy (phải là admin hoặc chính chủ SV)

**Xử Lý Logic:**
1. Tìm registration bằng `id`
2. Kiểm tra quyền
3. Set `status = CANCELLED`
4. Lưu và trả về DTO

#### 3.3 Check-In (`PUT /api/event-registrations/{eventId}/checkin/{studentId}`)

**Query Parameters:**
- `adminId` (bắt buộc): id của admin thực hiện check-in

**Xử Lý Logic (ATTENDANCE ONLY):**
1. **Xác thực Admin**
2. Tìm event
3. **Tìm hoặc tạo registration**
4. Set status = CHECKED_IN, checkinTime = now
5. Lưu và trả về DTO

#### 3.4 Check-Out → Cộng Điểm (`PUT /api/event-registrations/{eventId}/checkout/{studentId}`)

**Query Parameters:**
- `adminId` (bắt buộc): id của admin thực hiện checkout

**Xử Lý Logic (ATTENDANCE ONLY):**
1. Xác thực Admin
2. Tìm event & registration
3. **Idempotent:** Nếu đã checkout → return (không cộng lại)
4. Set `checkoutTime = now`, `status = COMPLETED`
5. **Cộng điểm:** Gọi `pointService.awardPointsForCompletedEvent()`
6. **Auto-close nếu đủ slot**
7. Lưu và trả về DTO

**Cộng Điểm Logic:**
- Kiểm tra không cộng trùng
- Tạo `PointTransaction`
- **Cập nhật summary:** totalDrl, totalCtxh, totalCdnn (cộng thêm + cap)
- Tính `rankLabel` dựa trên DRL
- **Tạo thông báo** cho student

#### 3.5 Complete Survey (ONLINE Mode) (`PUT /api/event-registrations/{eventId}/complete-survey/{studentId}`)

**Query Parameters:**
- `secretCode` (optional): mã bí mật nếu admin thiết lập

**Xử Lý Logic (ONLINE ONLY):**
1. Tìm event
2. Kiểm tra `event.eventMode == ONLINE`
3. **Kiểm tra thời gian bắt đầu:** `now >= startTime`
4. **Kiểm tra mã bí mật** (nếu có)
5. Kiểm tra `surveyUrl` có tồn tại
6. Kiểm tra deadline: `now < registrationDeadline`
7. Kiểm tra slot
8. **Tìm hoặc tạo registration**
9. **Cộng điểm** (null vì SV tự làm)
10. **Auto-close nếu đủ slot**
11. Lưu và trả về DTO

---

### 4️⃣ POINT MODULE (Quản Lý Điểm Rèn Luyện)

**Endpoints:**
```
GET /api/points/summary/{studentId}?semesterId=1
```

#### 4.1 Lấy Bảng Điểm Học Kỳ (`GET /api/points/summary/{studentId}`)

**Query Parameters:**
- `semesterId` (bắt buộc): id của học kỳ

**Xử Lý Logic:**
1. Tìm hoặc tạo `StudentSemesterSummary`
2. **Trả về DTO:**
```json
{
  "studentId": 23162099,
  "semesterId": 1,
  "DRL": 65,
  "CTXH": 20,
  "CDDN": 5,
  "rankLabel": "Trung bình khá"
}
```

**Giải Thích Các Loại Điểm:**
- **DRL (Đạo Đức Rèn Luyện):** Max 100
- **CTXH (Công Tác Xã Hội):** Max 40
- **CDNN (Chăm Đục Nghề Nghiệp):** Max 8

---

### 5️⃣ NOTIFICATION MODULE (Hệ Thống Thông Báo)

**Endpoints:**
```
GET    /api/notifications/user/{userId}
PUT    /api/notifications/{id}/read
PUT    /api/notifications/user/{userId}/read-all
```

#### 5.1 Lấy Danh Sách Thông Báo (`GET /api/notifications/user/{userId}`)

**Xử Lý Logic:**
1. Query: `findByUserIdOrderByCreatedAtDesc(userId)`
2. Trả về danh sách thông báo (mới nhất lên đầu)

#### 5.2 Đánh Dấu Đã Đọc (`PUT /api/notifications/{id}/read`)

**Xử Lý Logic:**
1. Tìm notification bằng `id`
2. Set `isRead = true`
3. Lưu

#### 5.3 Đánh Dấu Tất Cả Đã Đọc (`PUT /api/notifications/user/{userId}/read-all`)

**Xử Lý Logic:**
1. Lấy danh sách notification chưa đọc
2. Set tất cả `isRead = true`
3. Lưu hết

---

### 6️⃣ USER MODULE (Quản Lý Người Dùng)

**Endpoints:**
```
GET /api/users
GET /api/users/{id}
```

#### 6.1 Lấy Danh Sách Người Dùng (`GET /api/users`)

**Query Parameters:**
- `role` (optional): lọc theo role (STUDENT, ADMIN)
- `q` (optional): tìm kiếm theo studentCode/fullName/email

#### 6.2 Lấy Thông Tin Người Dùng (`GET /api/users/{id}`)

**Xử Lý Logic:**
1. Tìm user bằng `id`
2. Trả về User object

---

### 7️⃣ EVENT CATEGORY MODULE (Danh Mục Sự Kiện)

**Endpoints:**
```
GET /api/event-categories
GET /api/event-categories/{id}
```

#### 7.1 Lấy Danh Sách Danh Mục (`GET /api/event-categories`)

**Xử Lý Logic:**
1. Query tất cả `EventCategory`
2. Trả về danh sách

#### 7.2 Lấy Chi Tiết Danh Mục (`GET /api/event-categories/{id}`)

**Xử Lý Logic:**
1. Tìm category bằng `id`
2. Trả về object

---

## 🔄 Luồng Xử Lý Chính

### Luồng 1: Đăng Ký & Tham Gia Sự Kiện ATTENDANCE (Có Check-in/Check-out)

```
┌─────────────────────────────────────────────────────┐
│ 1. Student Login                                    │
│    POST /api/auth/login                             │
│    → Nhận Token                                      │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 2. Xem Danh Sách Sự Kiện                            │
│    GET /api/events?studentId=23162099&semesterId=1 │
│    → Trả về list events + flags (canRegister, ...)  │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 3. Đăng Ký Tham Gia                                 │
│    POST /api/event-registrations                    │
│    Body: { eventId, studentId, note }              │
│    → status = REGISTERED                            │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 4. Ngày Diễn Ra Sự Kiện - Admin Check-in           │
│    PUT /api/event-registrations/{eventId}/checkin/ │
│        {studentId}?adminId=1                        │
│    → status = CHECKED_IN, checkinTime = now         │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 5. Kết Thúc Sự Kiện - Admin Check-out (Cộng Điểm) │
│    PUT /api/event-registrations/{eventId}/checkout/│
│        {studentId}?adminId=1                        │
│    → status = COMPLETED, checkoutTime = now         │
│    → PointTransaction tạo, Summary cập nhật         │
│    → Notification gửi cho SV                        │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 6. Student Xem Bảng Điểm                            │
│    GET /api/points/summary/23162099?semesterId=1   │
│    → DRL=65, CTXH=20, CDDN=5, rankLabel="..."      │
└─────────────────────────────────────────────────────┘
```

### Luồng 2: Đăng Ký & Khảo Sát Online (Không Cần Check-in/Check-out)

```
┌─────────────────────────────────────────────────────┐
│ 1. Student Xem Danh Sách Sự Kiện ONLINE             │
│    GET /api/events?studentId=23162099&semesterId=1 │
│    → Event ONLINE có canCheckout = canCompleteSurvey│
│    → Không bắt buộc registered trước               │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 2. Option A: Student Đăng Ký Trước                 │
│    POST /api/event-registrations                    │
│    → status = REGISTERED                            │
│    (Hoặc skip bước này - không bắt buộc)            │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 3. Student Làm Khảo Sát & Nhập Mã Bí Mật            │
│    PUT /api/event-registrations/{eventId}/         │
│        complete-survey/23162099                     │
│    Query: ?secretCode=ABC123                        │
│    → Tạo registration nếu chưa có                   │
│    → status = COMPLETED                             │
│    → Cộng điểm tự động                              │
│    → Notification gửi cho SV                        │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 4. Student Xem Bảng Điểm                            │
│    GET /api/points/summary/23162099?semesterId=1   │
│    → DRL đã được cộng                               │
└─────────────────────────────────────────────────────┘
```

### Luồng 3: Quên Mật Khẩu

```
┌─────────────────────────────────────────────────────┐
│ 1. Student Nhập Email                              │
│    POST /api/auth/forgot-password/request           │
│    Body: { email }                                  │
│    → OTP 6 số được tạo & gửi qua mail              │
│    → OTP expire sau 120 giây                        │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 2. Student Nhập OTP Để Xác Thực                    │
│    POST /api/auth/forgot-password/verify            │
│    Body: { email, code/otp }                       │
│    → Verify OTP (so sánh hash)                      │
│    → Trả về message "OTP valid"                     │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 3. Student Đặt Lại Mật Khẩu                         │
│    POST /api/auth/forgot-password/reset             │
│    Body: { email, code, newPassword }              │
│    → Verify OTP lần cuối                            │
│    → Check mật khẩu mới ≠ mật khẩu cũ              │
│    → Hash mật khẩu mới & lưu                        │
│    → Mark OTP as used (used_at = now)              │
│    → Trả về message "Password updated"             │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│ 4. Student Đăng Nhập Với Mật Khẩu Mới              │
│    POST /api/auth/login                             │
│    Body: { username, password }                    │
│    → Password khớp với hash mới                     │
│    → Login thành công                               │
└─────────────────────────────────────────────────────┘
```

---

## 💾 Cơ Sở Dữ Liệu

### Bảng Chính

#### 1. users
```sql
id (Long, PK)
student_code (String, UNIQUE) -- MSSV
email (String, UNIQUE)
phone (String)
password_hash (String) -- BCrypt
full_name (String)
class_name (String)
faculty (String)
avatar_url (String)
role (String) -- STUDENT, ADMIN
status (Boolean) -- true=active, false=disabled
created_at (LocalDateTime)
```

#### 2. events
```sql
id (Long, PK)
semester_id (Long, FK)
category_id (Long, FK)
title (String)
description (Text)
location (String)
banner_url (String)
start_time (LocalDateTime)
end_time (LocalDateTime)
registration_deadline (LocalDateTime)
max_participants (Integer)
point_type_id (Long, FK)
point_value (Integer)
created_by (Long, FK -> users)
status (Enum: OPEN, CLOSED)
event_mode (Enum: ATTENDANCE, ONLINE)
survey_url (String)
survey_secret_code (String)
created_at (LocalDateTime)
updated_at (LocalDateTime)
```

#### 3. event_registrations
```sql
id (Long, PK)
event_id (Long, FK)
student_id (Long, FK -> users)
registration_time (LocalDateTime)
status (Enum: REGISTERED, CHECKED_IN, COMPLETED, CANCELLED)
checkin_time (LocalDateTime)
checkout_time (LocalDateTime)
note (String)
created_at (LocalDateTime)
updated_at (LocalDateTime)
```

#### 4. point_transactions
```sql
id (Long, PK)
student_id (Long, FK -> users)
semester_id (Long, FK)
point_type_id (Long, FK)
event_id (Long, FK)
points (Integer)
reason (String)
created_at (LocalDateTime)
created_by (Long, FK -> users) -- Admin đã phê duyệt, NULL nếu tự động
UNIQUE(student_id, semester_id, event_id) -- Chống cộng trùng
```

#### 5. student_semester_summary
```sql
id (Long, PK)
student_id (Long, FK -> users)
semester_id (Long, FK)
total_drl (Integer, default=0, max=100)
total_ctxh (Integer, default=0, max=40)
total_cddn (Integer, default=0, max=8)
rank_label (String) -- Xuất sắc, Tốt, Khá, ...
updated_at (LocalDateTime)
UNIQUE(student_id, semester_id)
```

#### 6. password_reset_codes
```sql
id (Long, PK)
email (String)
code_hash (String) -- SHA-256(OTP)
created_at (LocalDateTime)
expires_at (LocalDateTime)
used_at (LocalDateTime, nullable) -- null = chưa dùng
```

#### 7. notifications
```sql
id (Long, PK)
user_id (Long, FK -> users)
title (String)
content (String)
type (Enum: EVENT, SYSTEM, ANNOUNCEMENT)
is_read (Boolean, default=false)
created_at (LocalDateTime)
```

#### 8. event_categories
```sql
id (Long, PK)
name (String)
description (String)
icon_url (String)
```

#### 9. semesters
```sql
id (Long, PK)
name (String) -- "HK1 2025-2026"
start_date (LocalDate)
end_date (LocalDate)
```

#### 10. point_types
```sql
id (Long, PK)
code (String) -- DRL, CTXH, CDNN
name (String) -- Đạo Đức Rèn Luyện, Công Tác Xã Hội, ...
max_points (Integer)
```

---

## 🔐 Bảo Mật & Xác Thực

### 1. Password Security
- **Không lưu plaintext:** Sử dụng BCrypt (hashing + salting tự động)
- **Login:** So sánh input password hash với stored hash bằng `BCryptPasswordEncoder.matches()`
- **Reset:** 
  - Check mật khẩu mới ≠ mật khẩu cũ (bằng so sánh hash)
  - Mã OTP được hash trước khi lưu (SHA-256)

### 2. OTP Security
- **OTP Format:** 6 ký tự số (100000-999999)
- **Hashing:** SHA-256 trước khi lưu, không lưu plaintext
- **Expiration:** 120 giây (config được)
- **One-Time Use:** Chỉ dùng được 1 lần, sau đó `used_at` được set
- **Cleanup:** Xóa OTP chưa dùng của email khi request OTP mới

### 3. Token-Based Authentication
- **Format hiện tại (MOCK):** `MOCK_TOKEN_<userId>_<timestamp>`
- **Cần upgrade khi deploy thật:** JWT token
- **Stateless:** Backend không cần session storage

### 4. Role-Based Access Control (RBAC)
- **STUDENT:** Đăng ký, xem sự kiện, xem bảng điểm, hủy đăng ký của chính mình
- **ADMIN:** Quản lý sự kiện, check-in/check-out, phê duyệt điểm, xem tất cả sinh viên

### 5. Audit Log
- **Created_by field:** Lưu id của admin phê duyệt điểm
- **Timestamps:** `created_at`, `updated_at` cho mọi transaction
- **Status tracking:** `used_at` cho OTP, `status` cho registration, etc.

---

## ⚠️ Xử Lý Ngoại Lệ

### Global Exception Handler
Tất cả lỗi được bắt và trả về response chuẩn:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Chi tiết lỗi",
  "timestamp": "2026-01-07T15:30:00.123456789",
  "path": "/api/..."
}
```

### Các HTTP Status Code Thường Dùng

| Status | Ý Nghĩa | Ví Dụ |
|--------|---------|-------|
| 200 | OK | Login thành công |
| 201 | Created | Tạo event thành công |
| 400 | Bad Request | Email trống, password < 6 ký tự |
| 401 | Unauthorized | Password sai, OTP không hợp lệ |
| 403 | Forbidden | Không có quyền admin |
| 404 | Not Found | User/event/registration không tồn tại |
| 409 | Conflict | Email đã đăng ký, code đã dùng |
| 500 | Internal Server Error | Lỗi hệ thống |

### Custom Exception
- **PointsAlreadyAwardedException:** Chống cộng trùng điểm cho cùng 1 event

---

## 📊 Ví Dụ Workflow Hoàn Chỉnh

### Scenario: Sinh viên 23162099 tham gia sự kiện CTXH và nhận 5 điểm

**Step 1: Login**
```bash
POST /api/auth/login
{
  "username": "23162099",
  "password": "password123"
}
Response:
{
  "token": "MOCK_TOKEN_4_1704627600000",
  "user": { "id": 4, "studentCode": "23162099", "fullName": "Nguyễn Văn A", ... }
}
```

**Step 2: Xem sự kiện có thể đăng ký**
```bash
GET /api/events?studentId=4&semesterId=1&categoryId=2
Response: [
  {
    "id": 17,
    "title": "Tình Nguyện Xuân",
    "eventMode": "ATTENDANCE",
    "pointValue": 5,
    "canRegister": true,
    "registered": false,
    "computedStatus": "OPEN_FOR_REGISTRATION",
    ...
  }
]
```

**Step 3: Đăng ký sự kiện**
```bash
POST /api/event-registrations
{
  "eventId": 17,
  "studentId": 4,
  "note": "Mong được tham gia"
}
Response:
{
  "id": 36,
  "eventId": 17,
  "studentId": 4,
  "status": "REGISTERED",
  "registrationTime": "2026-01-07T15:00:00",
  ...
}
```

**Step 4: Ngày diễn ra - Admin check-in**
```bash
PUT /api/event-registrations/17/checkin/4?adminId=1
Response:
{
  "status": "CHECKED_IN",
  "checkinTime": "2026-01-10T09:05:00",
  ...
}
```

**Step 5: Kết thúc sự kiện - Admin check-out (cộng điểm)**
```bash
PUT /api/event-registrations/17/checkout/4?adminId=1
Response:
{
  "status": "COMPLETED",
  "checkoutTime": "2026-01-10T17:00:00",
  ...
}
```

**Database Changes:**
- Insert `point_transactions`: `student_id=4, semester_id=1, event_id=17, points=5`
- Update/Insert `student_semester_summary`: `total_ctxh = 20` (cộng 5 từ cũ)
- Create `notification`: "Bạn đã được cộng 5 CTXH"

**Step 6: Student xem bảng điểm**
```bash
GET /api/points/summary/4?semesterId=1
Response:
{
  "DRL": 65,
  "CTXH": 20,
  "CDDN": 5,
  "rankLabel": "Trung bình khá"
}
```

**Step 7: Student xem thông báo**
```bash
GET /api/notifications/user/4
Response: [
  {
    "id": 10,
    "title": "Bạn đã được cộng 5 CTXH",
    "content": "Bạn đã hoàn thành sự kiện \"Tình Nguyện Xuân\" và được cộng 5 CTXH.",
    "type": "EVENT",
    "isRead": false,
    ...
  }
]
```

---

## 🛠️ Hướng Dẫn Chạy Local
1. Clone dự án.
2. Cấu hình kết nối MySQL trong `src/main/resources/application.properties`.
3. Cấu hình Gmail SMTP (Username và App Password) để dùng tính năng OTP.
4. Chạy file `UteTrainingPointsSystemApiApplication.java`.
5. Truy cập Swagger UI: `http://localhost:8080/swagger-ui/index.html`.

---

## 🌍 Deployment (Railway)
- **Active Profile:** `production`
- **Biến môi trường:** `SPRING_PROFILES_ACTIVE`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MYSQLHOST`,...

---

## 🚀 Deployment Considerations

### Current Issues (Logging)
- Railway logs rate limit: 500 logs/sec
- Giảm logging rate bằng cách:
  - Tắt debug logs
  - Giảm frequency của scheduled tasks (PasswordResetCleanupService)
  - Tối ưu query logs

### Production Checklist
- ✅ Verify Resend domain (email sending)
- ✅ Upgrade MOCK token → JWT token
- ✅ Enable HTTPS/SSL
- ✅ Configure CORS properly
- ✅ Add rate limiting
- ✅ Set up centralized logging
- ✅ Database backup strategy
- ✅ Monitor and alerting

---

## 🔗 Tham Khảo Thêm
- **GitHub Repository:** [UTE Training Points System](https://github.com/YueLouis/UTE-Training-Points-System)
- **Swagger UI:** [API Documentation](https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html)
- **Railway Deployment:** [Railway Project](https://railway.com/invite/C8qZFcVV4S6)

---

© 2026 UTE Training Points Project Team.

