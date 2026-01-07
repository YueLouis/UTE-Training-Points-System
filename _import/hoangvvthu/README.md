# 📱 UTE Training Points - Android Application

Ứng dụng di động trong hệ sinh thái **UTE Training Points System**, dành cho Sinh viên và Quản trị viên trường Đại học Sư phạm Kỹ thuật TP.HCM. Ứng dụng giúp số hóa quy trình quản lý điểm rèn luyện, đăng ký sự kiện và thông báo tức thì.

## 🌟 Tính năng nổi bật

### 1. Dành cho Sinh viên (Student)
- **Đăng nhập & Bảo mật:** Đăng nhập bằng MSSV/Email. Khôi phục mật khẩu qua mã OTP gửi về Email.
- **Khám phá Sự kiện:** Xem danh sách sự kiện đang mở, lọc theo danh mục (Hội thảo, Tình nguyện, Khảo sát...).
- **Đăng ký & Tham gia:**
    - Đăng ký tham gia chỉ với một chạm.
    - **Sự kiện Online:** Nhập mã bí mật từ khảo sát để được cộng điểm tự động.
- **Theo dõi Điểm & Xếp loại:** Xem bảng tổng kết điểm DRL, CTXH, CDNN và Xếp loại chuẩn HCMUTE (Xuất sắc, Tốt, Khá...).
- **Thông báo:** Nhận thông báo "Real-time" ngay khi được cộng điểm.

### 2. Dành cho Quản trị viên (Admin)
- **Quản lý Sự kiện:** Tạo mới, chỉnh sửa thông tin sự kiện. Thiết lập mã bí mật chống gian lận cho sự kiện Online.
- **Điểm danh thông minh:** Danh sách sinh viên đăng ký trực quan. Xác nhận "Vào/Ra" cho sinh viên cực nhanh.
- **Quản lý Sinh viên:** Tìm kiếm và kiểm tra bảng điểm chi tiết của từng sinh viên.

---

## 🛠 Công nghệ sử dụng
- **Ngôn ngữ:** Java / Kotlin (Android SDK).
- **Thư viện Networking:** Retrofit 2 / Volley (Kết nối API).
- **Xử lý hình ảnh:** Glide / Picasso.
- **Giao diện:** Material Design Components, Lottie Animation (cho hiệu ứng cộng điểm).
- **Kiến trúc:** MVVM (Model-View-ViewModel).

---

## ⚙️ Cài đặt & Kết nối Backend

Để ứng dụng có thể hoạt động, bạn cần kết nối tới Backend đã deploy trên Railway:

1. **Cấu hình API URL:**
   Mở file cấu hình (ví dụ: `ApiClient.java` hoặc `Constants.java`), thay đổi đường dẫn URL:
   ```java
   public static final String BASE_URL = "https://your-app-name.up.railway.app/api/";
   ```

2. **Cấp quyền (Permissions):**
   Đảm bảo file `AndroidManifest.xml` đã có quyền truy cập Internet:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

3. **Build & Run:**
   - Mở dự án bằng **Android Studio**.
   - Chờ Gradle đồng bộ (Sync).
   - Chạy trên máy ảo (Emulator) hoặc thiết bị thật (Physical Device).

---

## 📡 Kết nối API (Flows)

App thực hiện giao tiếp với Backend qua 3 luồng chính:
- **Auth Flow:** `/api/auth/login`, `/api/auth/forgot-password/*`.
- **Student Flow:** `/api/events`, `/api/event-registrations`, `/api/points/summary/{id}`.
- **Admin Flow:** `/api/events` (POST/PUT), `/api/event-registrations/by-event/{id}`.

*Chi tiết tham khảo tài liệu Swagger tại: `https://your-app-name.up.railway.app/swagger-ui.html`*

---

## 🎨 Giao diện (Figma)
Nhóm sử dụng thiết kế chuẩn Material Design để tối ưu trải nghiệm người dùng.
- https://www.figma.com/design/Qjm4AKK7DBIPjhcTtt9uX5/ute-uniscore?node-id=0-1&m=dev&t=kqj3w5fccjfuPAbI-1

---

## 👨‍💻 Thành viên thực hiện
- **Trịnh Trâm Anh** - 23###005
- **Hoàng Văn Vương Thu** - 23###099
- **Nguyễn Trọng Tín** - 23###102
- **Trần Ngọc Nhất** - 24###086

---
© 2026 UTE Training Points Project Team.
