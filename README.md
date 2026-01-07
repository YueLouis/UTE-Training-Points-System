# UTE Training Points System - Backend API

Hệ thống Quản lý Điểm rèn luyện cho sinh viên Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE). Dự án được xây dựng với mục tiêu giúp sinh viên dễ dàng theo dõi, đăng ký sự kiện và tự động hóa quy trình cộng điểm rèn luyện, điểm công tác xã hội.

## 🚀 Công nghệ sử dụng
- **Ngôn ngữ:** Java 17
- **Framework:** Spring Boot 3.5.x
- **Cơ sở dữ liệu:** MySQL (Railway)
- **ORM:** Spring Data JPA / Hibernate
- **Bảo mật:** BCrypt Password Hashing, Token-based Authentication
- **Thông báo:** Tự động tạo Notification khi có biến động điểm
- **Tài liệu API:** Swagger UI / OpenAPI 3.0
- **Deployment:** Railway.app

## 📊 Cấu trúc Database (10 Bảng)
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

## 🔑 Các luồng nghiệp vụ chính
### 1. Luồng Xác thực (Authentication)
- Đăng nhập bằng MSSV hoặc Email.
- Khôi phục mật khẩu qua mã OTP gửi về Email cá nhân (3 bước bảo mật).

### 2. Luồng Sự kiện Online (Chống gian lận)
- Sinh viên làm khảo sát qua Google Forms.
- Lấy **Mã bí mật (Secret Code)** ở cuối bài khảo sát để nhập vào App.
- Hệ thống đối soát mã đúng mới thực hiện cộng điểm tự động.

### 3. Luồng Sự kiện Offline (Attendance)
- Đăng ký tham gia -> Check-in (Vào cổng) -> Check-out (Ra về).
- Điểm được cộng ngay khi hoàn thành bước Check-out.

### 4. Hệ thống Xếp loại (Ranking)
Tự động xếp loại điểm rèn luyện theo quy chế HCMUTE:
- **Xuất sắc:** 90 - 100
- **Tốt:** 80 - 89
- **Khá:** 70 - 79
- **Trung bình khá:** 60 - 69
- **Trung bình:** 50 - 59
- **Yếu:** 35 - 49
- **Kém:** < 35

## 📡 Danh sách API chính

### 🔐 Authentication (`/api/auth`)
- `POST /login`: Đăng nhập hệ thống (Trả về Token + Role).
- `POST /forgot-password/request`: Yêu cầu mã OTP.
- `POST /forgot-password/verify`: Xác thực mã OTP.
- `POST /forgot-password/reset`: Đổi mật khẩu mới.

### 📅 Sự kiện (`/api/events`)
- `GET /`: Lấy danh sách sự kiện (hỗ trợ lọc theo `semesterId`, `categoryId`, `q`).
- `POST /`: Tạo sự kiện mới (Admin).
- `PUT /{id}`: Cập nhật sự kiện.
- `POST /{id}/close`: Đóng sự kiện.

### 📝 Đăng ký & Điểm danh (`/api/event-registrations`)
- `POST /`: Đăng ký tham gia sự kiện.
- `PUT /{id}/check-in`: Admin xác nhận vào (Sử dụng ID đăng ký).
- `PUT /{id}/check-out`: Admin xác nhận ra (Cộng điểm + Rank).
- `PUT /{eventId}/complete-survey/{studentId}`: SV xác nhận mã bí mật Online.

### 📈 Điểm & Thống kê (`/api/points`)
- `GET /summary/{studentId}`: Lấy bảng điểm tổng kết và Xếp loại chuẩn HCMUTE.

### 🔔 Thông báo (`/api/notifications`)
- `GET /user/{userId}`: Lấy danh sách thông báo của người dùng.

## 🛠 Hướng dẫn chạy Local
1. Clone dự án.
2. Cấu hình kết nối MySQL trong `src/main/resources/application.properties`.
3. Cấu hình Gmail SMTP (Username và App Password) để dùng tính năng OTP.
4. Chạy file `UteTrainingPointsSystemApiApplication.java`.
5. Truy cập Swagger UI: `http://localhost:8080/swagger-ui/index.html`.

## 🌍 Deployment (Railway)
- **Active Profile:** `production`
- **Biến môi trường:** `SPRING_PROFILES_ACTIVE`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MYSQLHOST`,...

## 🔗 Tham khảo thêm
- **GitHub Repository:** [UTE Training Points System](https://github.com/YueLouis/UTE-Training-Points-System)
- **Swagger UI:** [API Documentation](https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html)
- **Railway Deployment:** [Railway Project](https://railway.com/invite/C8qZFcVV4S6)

---
© 2026 UTE Training Points Project Team.
