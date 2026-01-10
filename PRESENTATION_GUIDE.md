# 📢 HƯỚNG DẪN LÊN LỚP BÁO CÁO - UTE Training Points System

## 📋 MỤC LỤC BÁOL CÁO

### **PHẦN 1: GIỚI THIỆU (2-3 phút)**

#### 1.1 Tên & Mục Đích Dự Án
**Nội Dung Nói:**
- "Dự án của chúng em là **UTE Training Points System** (Hệ Thống Quản Lý Điểm Rèn Luyện)"
- "Mục đích: **Tự động hóa và quản lý điểm rèn luyện cho sinh viên HCMUTE**, giải quyết việc cộng điểm thủ công bằng giấy/Excel"
- "Hệ thống giúp:"
  - ✅ **Sinh viên:** Dễ dàng xem danh sách sự kiện, đăng ký, xem bảng điểm
  - ✅ **Admin:** Quản lý sự kiện, điểm danh, cộng điểm tự động
  - ✅ **Nhà trường:** Giảm sai sót, minh bạch, chống gian lận

#### 1.2 Nhóm Thực Hiện
**Nội Dung Nói:**
- "Nhóm gồm 3 thành viên:"
  - **Backend API (Java Spring Boot)** - Em (hoặc bạn nào làm)
  - **Frontend Mobile (Android)** - Bạn A
  - **Database Design** - Bạn B

---

### **PHẦN 2: PHÂN TÍCH VẤN ĐỀ (1-2 phút)**

#### 2.1 Vấn Đề Hiện Tại
**Nội Dung Nói:**
- "Hiện nay, việc quản lý điểm rèn luyện gặp những vấn đề:"
  - ❌ **Thủ công:** Nhập liệu từng sinh viên, dễ sai sót
  - ❌ **Không minh bạch:** Sinh viên không biết lịch sử cộng điểm
  - ❌ **Chống gian lận kém:** Khó kiểm soát ai đã tham gia sự kiện
  - ❌ **Mất thời gian:** Admin phải nhập lại dữ liệu nhiều lần

#### 2.2 Giải Pháp Đề Xuất
**Nội Dung Nói:**
- "Chúng tôi xây dựng 1 hệ thống web + mobile app:**
  - ✅ **Tự động:** Cộng điểm tự động khi sinh viên hoàn thành sự kiện
  - ✅ **Minh bạch:** Sinh viên có thể xem lịch sử cộng điểm theo thời gian thực
  - ✅ **Chống gian lận:** Sử dụng mã bí mật cho sự kiện online
  - ✅ **Tiết kiệm thời gian:** Admin chỉ cần check-in/check-out

---

### **PHẦN 3: THIẾT KẾ HỆ THỐNG (2-3 phút)**

#### 3.1 Kiến Trúc Tổng Quan
**Nội Dung Nói + SHOW:**
- "Hệ thống được chia thành 3 lớp:"
  
**SHOW HÌNH:**
```
┌─────────────────────────────────────┐
│   Frontend (Android App)            │  (UI cho sinh viên & admin)
├─────────────────────────────────────┤
│   Backend API (Java Spring Boot)    │  (Xử lý logic nghiệp vụ)
├─────────────────────────────────────┤
│   Database (MySQL on Railway)       │  (Lưu trữ dữ liệu)
└─────────────────────────────────────┘
```

#### 3.2 Cơ Sở Dữ Liệu
**Nội Dung Nói + SHOW:**
- "Database gồm 10 bảng chính:"

**SHOW BẢNG:**
| Bảng | Mục Đích |
|------|----------|
| users | Lưu thông tin sinh viên & admin |
| events | Danh sách sự kiện |
| event_registrations | Theo dõi sinh viên đã đăng ký/hoàn thành sự kiện nào |
| point_transactions | Lịch sử cộng điểm |
| student_semester_summary | Bảng tổng kết điểm theo học kỳ |
| notifications | Thông báo cho sinh viên |
| password_reset_codes | OTP khôi phục mật khẩu |
| event_categories | Danh mục sự kiện |
| semesters | Quản lý học kỳ |
| point_types | Loại điểm (DRL, CTXH, CDNN) |

**SHOW ERD (Entity Relationship Diagram):**
- Open file: `docs/erd_doancuoiki.png`
- Giải thích: "Mũi tên chỉ mối quan hệ giữa các bảng"

#### 3.3 Các Chức Năng Chính
**Nội Dung Nói:**

**Dành cho Sinh Viên:**
- ✅ Đăng nhập (MSSV, Email, SĐT)
- ✅ Xem danh sách sự kiện
- ✅ Đăng ký tham gia sự kiện
- ✅ Xem bảng điểm + xếp loại
- ✅ Nhận thông báo khi được cộng điểm
- ✅ Khôi phục mật khẩu qua OTP

**Dành cho Admin:**
- ✅ Quản lý sự kiện (thêm, sửa, đóng)
- ✅ Điểm danh (check-in/check-out)
- ✅ Phê duyệt cộng điểm (có audit log)
- ✅ Tra cứu dữ liệu sinh viên

---

### **PHẦN 4: DEMO CHỨC NĂNG (3-5 phút)**

#### 4.1 Demo Đăng Nhập
**Nội Dung Nói + ACTION:**
- "Trước tiên, em xin demo luồng đăng nhập:"
- **OPEN APP (hoặc Swagger UI)**
- **TYPE:** Username = `23162099`, Password = `password123`
- **CLICK:** Login
- **SHOW RESULT:** 
  ```json
  {
    "token": "MOCK_TOKEN_4_1704627600000",
    "user": {
      "id": 4,
      "studentCode": "23162099",
      "fullName": "Nguyễn Văn A",
      "role": "STUDENT"
    }
  }
  ```
- **NÓI:** "Hệ thống xác thực thành công, trả về Token + thông tin user"

#### 4.2 Demo Xem Danh Sách Sự Kiện
**Nội Dung Nói + ACTION:**
- "Tiếp theo, sinh viên xem danh sách sự kiện:"
- **OPEN Swagger UI hoặc APP**
- **CALL API:** `GET /api/events?studentId=4&semesterId=1`
- **SHOW RESULT:**
  ```json
  [
    {
      "id": 16,
      "title": "XUÂN TÌNH NGUYỆN",
      "eventMode": "ATTENDANCE",
      "pointValue": 5,
      "canRegister": true,
      "registered": false,
      "computedStatus": "OPEN_FOR_REGISTRATION"
    },
    {
      "id": 12,
      "title": "Hội thảo Chống gian lận Online",
      "eventMode": "ONLINE",
      "pointValue": 3,
      "canRegister": true,
      "registered": false,
      "computedStatus": "ONGOING",
      "surveyUrl": "https://forms.gle/test",
      "canCheckout": true
    }
  ]
  ```
- **NÓI:** "Hệ thống trả về danh sách events, kèm theo flags để app biết:"
  - `canRegister` = có thể đăng ký không?
  - `registered` = đã đăng ký chưa?
  - `computedStatus` = trạng thái sự kiện (OPEN, ONGOING, ENDED, CLOSED)

#### 4.3 Demo Đăng Ký Sự Kiện
**Nội Dung Nói + ACTION:**
- "Sinh viên chọn 1 sự kiện và đăng ký:"
- **CALL API:** `POST /api/event-registrations`
  ```json
  {
    "eventId": 16,
    "studentId": 4,
    "note": "Tôi muốn tham gia"
  }
  ```
- **SHOW RESULT:**
  ```json
  {
    "id": 43,
    "eventId": 16,
    "studentId": 4,
    "studentCode": "23162099",
    "studentName": "Nguyễn Văn A",
    "status": "REGISTERED",
    "registrationTime": "2026-01-08T10:00:00"
  }
  ```
- **NÓI:** "Đăng ký thành công, hệ thống lưu vào database"

#### 4.4 Demo Check-in (Admin)
**Nội Dung Nói + ACTION:**
- "Ngày sự kiện diễn ra, Admin điểm danh sinh viên check-in:"
- **CALL API:** `PUT /api/event-registrations/16/checkin/4?adminId=1`
- **SHOW RESULT:**
  ```json
  {
    "status": "CHECKED_IN",
    "checkinTime": "2026-01-10T09:05:00"
  }
  ```
- **NÓI:** "Sinh viên đã check-in thành công"

#### 4.5 Demo Check-out → Cộng Điểm (Admin)
**Nội Dung Nói + ACTION:**
- "Kết thúc sự kiện, Admin check-out (hệ thống tự động cộng điểm):"
- **CALL API:** `PUT /api/event-registrations/16/checkout/4?adminId=1`
- **SHOW RESULT:**
  ```json
  {
    "status": "COMPLETED",
    "checkoutTime": "2026-01-10T17:00:00"
  }
  ```
- **NÓI:** "Hệ thống tự động:"
  - Tạo `PointTransaction` lưu lịch sử cộng điểm
  - Cập nhật `StudentSemesterSummary` (tăng CTXH từ 15 → 20)
  - Tính toán lại xếp loại (từ Trung bình → Trung bình khá)
  - Tạo `Notification` để thông báo cho sinh viên

#### 4.6 Demo Xem Bảng Điểm
**Nội Dung Nói + ACTION:**
- "Sinh viên xem bảng điểm:"
- **CALL API:** `GET /api/points/summary/4?semesterId=1`
- **SHOW RESULT:**
  ```json
  {
    "studentId": 4,
    "semesterId": 1,
    "DRL": 65,      // Đạo Đức Rèn Luyện
    "CTXH": 20,     // Công Tác Xã Hội
    "CDDN": 5,      // Chăm Đức Nghề Nghiệp
    "rankLabel": "Trung bình khá"
  }
  ```
- **NÓI:** "Điểm tự động cập nhật, sinh viên có thể xem bảng điểm và xếp loại của mình"

#### 4.7 Demo Sự Kiện Online (Secret Code)
**Nội Dung Nói + ACTION:**
- "Hệ thống cũng hỗ trợ sự kiện ONLINE với mã bí mật (chống gian lận):"
- **SHOW:** Event ID 12 (Hội thảo Chống gian lận Online)
  ```json
  {
    "eventMode": "ONLINE",
    "surveyUrl": "https://forms.gle/test",
    "surveySecretCode": "UTE-PRO-2026"
  }
  ```
- **NÓI:** "Sinh viên làm khảo sát trực tuyến, ở cuối form có mã bí mật. Khi nhập mã bí mật vào app:"
- **CALL API:** `PUT /api/event-registrations/12/complete-survey/4?secretCode=UTE-PRO-2026`
- **SHOW RESULT:**
  ```json
  {
    "status": "COMPLETED",
    "checkoutTime": "2026-01-08T15:30:00"
  }
  ```
- **NÓI:** "Hệ thống xác thực mã bí mật đúng → tự động cộng điểm (không cần admin)"

#### 4.8 Demo Quên Mật Khẩu (OTP)
**Nội Dung Nói + ACTION:**
- "Nếu sinh viên quên mật khẩu, hệ thống gửi OTP qua email (3 bước bảo mật):"
- **STEP 1: Request OTP**
  - **CALL API:** `POST /api/auth/forgot-password/request`
    ```json
    { "email": "23162102@student.hcmute.edu.vn" }
    ```
  - **SHOW:** "OTP 6 số được gửi qua email + log ra console (để demo)"
  
- **STEP 2: Verify OTP**
  - **CALL API:** `POST /api/auth/forgot-password/verify`
    ```json
    { "email": "23162102@student.hcmute.edu.vn", "code": "123456" }
    ```
  - **SHOW RESULT:** "OTP hợp lệ"

- **STEP 3: Reset Password**
  - **CALL API:** `POST /api/auth/forgot-password/reset`
    ```json
    { 
      "email": "23162102@student.hcmute.edu.vn", 
      "code": "123456", 
      "newPassword": "newPassword123" 
    }
    ```
  - **SHOW RESULT:** "Mật khẩu đã cập nhật thành công"

- **NÓI:** "Lợi ích: OTP chỉ có hiệu lực 120 giây, dùng 1 lần, hash SHA-256 trong DB"

---

### **PHẦN 5: NỘI DUNG TECHNICAL (1-2 phút)**

#### 5.1 Kiến Trúc Mã Nguồn
**Nội Dung Nói + SHOW:**
- "Backend được tổ chức theo Layered Architecture:"

**SHOW STRUCTURE:**
```
src/main/java/vn/hcmute/trainingpoints/
├── controller/       (Tiếp nhận HTTP requests)
│   ├── auth/
│   ├── event/
│   ├── registration/
│   ├── point/
│   ├── notification/
│   └── user/
├── service/         (Xử lý logic nghiệp vụ)
│   ├── AuthService
│   ├── EventService
│   ├── EventRegistrationService
│   ├── PointService
│   └── ...
├── repository/      (Tương tác database)
│   └── UserRepository, EventRepository, ...
├── entity/          (Đại diện bảng database)
│   └── User, Event, EventRegistration, ...
├── dto/             (Truyền dữ liệu an toàn)
│   └── EventDTO, UserDTO, ...
└── exception/       (Xử lý lỗi)
    └── GlobalExceptionHandler
```

- **NÓI:** "Tách biệt trách nhiệm, dễ maintain và test"

#### 5.2 Công Nghệ Sử Dụng
**Nội Dung Nói:**
- Backend: **Java 17 + Spring Boot 3.5.x**
- Database: **MySQL 8.0 (trên Railway)**
- ORM: **Spring Data JPA / Hibernate**
- Bảo Mật: **BCrypt (mật khẩu), SHA-256 (OTP)**
- Authentication: **Token-based (MOCK → JWT khi production)**
- API Documentation: **Swagger UI / OpenAPI 3.0**

#### 5.3 Xử Lý Ngoại Lệ
**Nội Dung Nói + SHOW:**
- "Tất cả lỗi được bắt bởi GlobalExceptionHandler, trả về response chuẩn:"

**SHOW EXAMPLE:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email không tồn tại",
  "timestamp": "2026-01-08T10:00:00.123456789",
  "path": "/api/auth/forgot-password/request"
}
```

---

### **PHẦN 6: DEPLOYMENT & MONITORING (1 phút)**

#### 6.1 Triển Khai
**Nội Dung Nói:**
- "Backend được deploy trên **Railway.app** (Cloud Platform)"
- "URL sản phẩm:" `https://ute-training-points-system-production.up.railway.app`
- "Swagger UI (API Doc):" `https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html`

#### 6.2 Vấn Đề Gặp & Giải Pháp
**Nội Dung Nói:**
- "Trong quá trình phát triển, gặp 1 số thách thức:"
  - ❌ **Railway Rate Limiting (500 logs/sec):** Giảm logging level
  - ❌ **Event Mode Typo (ATTENDACE):** Fix database
  - ❌ **Password Reset Logic:** Đơn giản hóa (chỉ check ≠ cũ)
  - ❌ **Event Validation:** Deadline phải < startTime
- "Tất cả đều được fix trước khi demo"

---

### **PHẦN 7: KỲ VỌNG & HƯỚNG PHÁT TRIỂN (1 phút)**

#### 7.1 Kỳ Vọng Sản Phẩm
**Nội Dung Nói:**
- ✅ **Giảm 80% thời gian cộng điểm** (từ thủ công → tự động)
- ✅ **Minh bạch 100%** (sinh viên thấy lịch sử mỗi lúc)
- ✅ **Chống gian lận** (mã bí mật, audit log)
- ✅ **Dễ sử dụng** (Mobile app + Web dashboard)

#### 7.2 Hướng Phát Triển
**Nội Dung Nói:**
- "Trong tương lai, có thể thêm:"
  - 📊 **Dashboard Thống Kê** (biểu đồ điểm theo thời gian)
  - 💬 **Chat/Notification** (tương tác real-time)
  - 🔔 **Push Notification** (thông báo mobile)
  - 📈 **Machine Learning** (dự đoán xếp loại)
  - 🔐 **JWT Token** (thay MOCK token)
  - 📱 **Progressive Web App** (web version)

---

## 📊 ĐỒ VẬT CẦN CHUẨN BỊ ĐỂ SHOW

### **Trên Laptop/Desktop:**
1. ✅ **Swagger UI** - API Documentation
   - URL: `https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html`
   - CÓ THỂ test trực tiếp API ở đây

2. ✅ **Source Code** - GitHub
   - URL: `https://github.com/YueLouis/UTE-Training-Points-System`
   - Show cấu trúc project, các file chính

3. ✅ **Database ERD** - Hình vẽ thiết kế
   - File: `docs/erd_doancuoiki.png`
   - Show quan hệ giữa các bảng

4. ✅ **Application.properties** - Cấu hình
   - Show database connection, logging level

5. ✅ **COMPLETE_DOCUMENTATION.md** - Tài liệu chi tiết
   - Có thể chiếu lên để show ghi chú

### **Trong Điện Thoại/Emulator:**
6. ✅ **Android App** - Mobile Frontend
   - Demo giao diện sinh viên & admin
   - Test flow: Login → Xem events → Đăng ký → Xem điểm

7. ✅ **Postman** - Test API
   - Chuẩn bị các request sẵn (Login, Register, Checkout, ...)
   - Export collection để dễ demo

---

## ⏱️ PHÂN CHIA THỜI GIAN (Tổng ~15-20 phút)

| Phần | Nội Dung | Thời Gian |
|------|----------|-----------|
| 1 | Giới thiệu dự án | 2-3 min |
| 2 | Phân tích vấn đề | 1-2 min |
| 3 | Thiết kế hệ thống | 2-3 min |
| 4 | Demo chức năng | 5-7 min |
| 5 | Content technical | 1-2 min |
| 6 | Deployment | 1 min |
| 7 | Kỳ vọng & phát triển | 1 min |
| - | **Hỏi đáp** | 2-3 min |

---

## 💡 MẸO TRÌNH BÀY

### **Các Điều Nên Làm:**
✅ **Chuẩn bị kỹ:** Test toàn bộ API trước khi lên báo cáo
✅ **Nói rõ ràng:** Giải thích từng bước, không vội vã
✅ **Show hình ảnh:** Dùng diagram, screenshot để minh họa
✅ **Demo live:** Gọi API trực tiếp, không chỉ nói
✅ **Có backup:** Chuẩn bị video demo nếu API bị lỗi
✅ **Tương tác:** Hỏi "Có câu hỏi gì không?" sau mỗi phần
✅ **Highlight:** Nhấn mạnh những điểm khó & cách giải quyết

### **Các Điều Không Nên Làm:**
❌ **Không đọc slides:** Hãy nói theo ý hiểu
❌ **Không show code cả trang:** Chỉ show những đoạn quan trọng
❌ **Không quên credit:** Nói rõ ai làm phần nào (backend, frontend, DB)
❌ **Không vội:** Chờ thầy cô hỏi xong trước khi tiếp tục
❌ **Không nói khó:** Dùng ngôn ngữ đơn giản, dễ hiểu

---

## 🎤 CÂU MỞ ĐẦU GỢI Ý

**"Thưa thầy cô, hôm nay em xin trình bày dự án UTE Training Points System - một hệ thống quản lý điểm rèn luyện tự động cho sinh viên HCMUTE. Mục tiêu chính là giúp sinh viên dễ dàng xem điểm, giảm công việc thủ công của nhà trường, và tăng tính minh bạch trong quá trình cộng điểm. Hệ thống gồm 3 phần: Backend API (Java), Frontend Mobile (Android), và Database MySQL. Bây giờ em sẽ demo từng chức năng chính."**

---

## 🎯 CÂU KẾT LUẬN GỢI Ý

**"Qua dự án này, nhóm em đã tìm hiểu sâu về kiến trúc backend, xử lý bảo mật (BCrypt, OTP), và triển khai trên cloud (Railway). Hệ thống hiện đã hoạt động ổn định, có thể áp dụng thực tế tại nhà trường. Trong tương lai, chúng tôi sẽ thêm các tính năng như dashboard thống kê, JWT token, và push notification. Em cảm ơn thầy cô đã lắng nghe."**

---

**CHÚC EM LÊN BÁO CÁO ĐẠT ĐIỂM CAO! 🚀**


