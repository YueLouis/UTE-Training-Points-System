```
# UTE Training Points System - Android Frontend

> **Ứng dụng Mobile** - Hệ thống Quản lý Điểm Rèn luyện  
> **Platform**: Android 8.0+ (API 26+)  
> **Technology**: Java/Kotlin, Retrofit, MVVM

---

## 📋 Features

### 👨‍🎓 Student Features
- ✅ **Authentication**: Login với MSSV/Email, OTP reset password
- ✅ **Event Discovery**: Duyệt sự kiện theo danh mục (DRL, CTXH, CDNN)
- ✅ **Event Registration**: Đăng ký + hủy đăng ký sự kiện
- ✅ **Check-in**: QR code / manual check-in tại sự kiện
- ✅ **Online Survey**: Tham gia khảo sát với mã bí mật
- ✅ **Points Dashboard**: Xem điểm theo kỳ/năm/toàn khóa
- ✅ **Notifications**: Thông báo khi được cộng điểm
- ✅ **Profile**: Xem thông tin cá nhân & lịch sử tham gia

### 🔐 Admin Features
- ✅ **Event Management**: Tạo, sửa, đóng, xóa sự kiện
- ✅ **Check-in Management**: Điểm danh sinh viên at event
- ✅ **Point Approval**: Duyệt điểm cho sinh viên
- ✅ **Reports**: Xem thống kê điểm & tham gia
- ✅ **User Management**: Quản lý danh sách sinh viên

---

## 🔧 Setup & Installation

### Prerequisites
```bash
- Android Studio (2023.1+)
- Android SDK: API 26+ (Android 8.0+)
- JDK 11+
- Gradle 8.0+
```

### Clone Project
```bash
cd frontend/UTE-Training-Points-System
```

### Configure Backend URL

**File**: `app/src/main/java/vn/hcmute/utetrainingpointssystem/network/ApiConstants.java`

```java
public class ApiConstants {
    // Production (Railway)
    public static final String BASE_URL = "https://ute-training-points-system-production.up.railway.app/";
    
    // Local Development
    // public static final String BASE_URL = "http://10.0.2.2:8080/";
}
```

### Build & Run
```bash
# Build project
./gradlew build

# Run on emulator/device
./gradlew installDebug

# Or via Android Studio
# Build → Make Project
# Run → Run 'app'
```

---

## 📱 Architecture

### MVVM Pattern
```
UI Layer (Activity/Fragment)
   ↓
ViewModel (Business Logic)
   ↓
Repository (Data Access)
   ↓
Network/Local Data Source
   ↓
Backend API / Database
```

### Folder Structure
```
app/src/main/java/vn/hcmute/utetrainingpointssystem/
├── network/               # Retrofit APIs
│   ├── api/              # Interface definitions
│   ├── interceptor/      # Auth, Logging
│   └── RetrofitClient.java
├── core/                 # TokenManager, BaseViewModel
├── model/                # DTOs
├── repository/           # Data layer
├── viewmodel/            # Business logic
├── ui/                   # Activities & Fragments
│   ├── auth/
│   ├── event/
│   ├── registration/
│   └── profile/
└── util/                 # Helpers
```

---

## 🌐 API Integration

### Backend Endpoints
All endpoints require JWT Bearer token in Authorization header:

```
Authorization: Bearer <access_token>
```

#### Authentication
```
POST /api/auth/login
Request:  { "username": "23162102", "password": "password" }
Response: { "accessToken": "...", "refreshToken": "...", "user": {...} }

POST /api/auth/refresh
Request:  { "refreshToken": "..." }
Response: { "accessToken": "..." }

POST /api/auth/forgot-password
Request:  { "email": "23162102@student.hcmute.edu.vn" }
Response: { "message": "If the email exists, a reset link has been sent." }

POST /api/auth/reset-password
Request:  { "token": "...", "newPassword": "..." }
Response: { "message": "Password updated successfully." }
```

#### Events
```
GET /api/events
Response: [{ id, title, description, status, eventMode, ... }]

GET /api/events/{id}
Response: { Event details }

GET /api/events/by-category/{categoryId}
Response: [{ Events in category }]

POST /api/events (Admin only)
PUT /api/events/{id} (Admin only)
DELETE /api/events/{id} (Admin only)
```

#### Registrations
```
POST /api/event-registrations
Request:  { "eventId": 1, "studentId": 2, "note": "..." }
Response: { EventRegistration }

PUT /api/event-registrations/{id}/cancel?userId=2
Response: { status: "CANCELLED" }

PUT /api/event-registrations/{id}/check-in?adminId=1
Response: { status: "CHECKED_IN", checkinTime: "..." }

PUT /api/event-registrations/{eventId}/complete-survey/{studentId}?secretCode=XXX
Response: { status: "COMPLETED" }
```

#### Points
```
GET /api/points/summary/{studentId}
Response: {
  drl: { semester: 80, year: 75, total: 70 },
  ctxh: { current: 30, max: 40 },
  cdnn: { current: 5, max: 8 }
}
```

#### Notifications
```
GET /api/notifications/user/{userId}
Response: [{ id, title, message, isRead, ... }]

PUT /api/notifications/{id}/read
Response: { isRead: true }

PUT /api/notifications/user/{userId}/read-all
Response: { message: "All marked as read" }
```

---

## 🔐 Token Management

### Token Lifecycle
```
1. User Login
   ├─ Backend returns: accessToken (15m) + refreshToken (7d)
   └─ Stored in SharedPreferences

2. API Requests
   ├─ AuthInterceptor adds: Authorization: Bearer <accessToken>
   └─ All requests include token

3. Token Expired
   ├─ Check if expired every 13 minutes
   └─ Call POST /api/auth/refresh with refreshToken
   
4. Get New AccessToken
   ├─ Backend returns new accessToken
   └─ Update local TokenManager
   
5. Logout
   ├─ Clear SharedPreferences
   └─ Redirect to Login screen
```

### Implementation
```java
// TokenManager.java
public String getAccessToken()      // Get current token
public String getRefreshToken()     // Get refresh token
public void updateAccessToken(...)  // Update after refresh
public void saveAuth(...)           // Save on login
public void clear()                 // Clear on logout
```

---

## 🚀 Authentication Flow

### Login
```
1. User enters MSSV + Password
2. POST /api/auth/login
3. Backend returns accessToken + refreshToken + userInfo
4. App stores tokens & redirects to Dashboard
```

### Forgot Password
```
1. User enters email → POST /api/auth/forgot-password
2. Backend sends email with reset link (contains token)
3. User opens link → Deep link to app → Extract token from URL
4. User enters new password → POST /api/auth/reset-password
5. Backend validates token & updates password
6. Redirect to login screen
```

### Token Refresh
```
1. App detects token expired
2. POST /api/auth/refresh { refreshToken }
3. Backend returns new accessToken
4. Update TokenManager with new token
5. Retry original request
```

---

## 📊 Data Models

### User
```java
class UserDTO {
    Long id;
    String username;      // MSSV
    String email;
    String fullName;
    String className;
    String faculty;
    String role;          // STUDENT, ADMIN
    Boolean status;
}
```

### Event
```java
class EventDTO {
    Long id;
    String title;
    String description;
    String location;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Integer maxParticipants;
    EventStatus status;   // DRAFT, PUBLISHED, CLOSED
    EventMode eventMode;  // ATTENDANCE, ONLINE
    Integer pointValue;
    String surveySecretCode;  // For online events
}
```

### EventRegistration
```java
class EventRegistrationDTO {
    Long id;
    Long eventId;
    Long studentId;
    LocalDateTime registrationTime;
    RegistrationStatus status;  // REGISTERED, CHECKED_IN, COMPLETED, CANCELLED
    LocalDateTime checkinTime;
    LocalDateTime checkoutTime;
}
```

### Points Summary
```java
class StudentSummaryDTO {
    DrlSummary drl;      // { semester, year, total }
    PointSummary ctxh;   // { current, max }
    PointSummary cdnn;   // { current, max }
}
```

---

## 🧪 Testing

### Test Login
```java
// LoginActivity.java
String username = "23162102";
String password = "password";
authViewModel.login(username, password);

// Expected: Token saved, redirect to Dashboard
```

### Test Event Registration
```java
// EventDetailActivity.java
EventRegistrationRequest req = new EventRegistrationRequest(
    eventId, studentId, "note"
);
registrationViewModel.register(req);

// Expected: Registration status = REGISTERED
```

### Test Points Summary
```java
// ProfileFragment.java
pointViewModel.getSummary(studentId);

// Expected: DRL + CTXH + CDNN values displayed
```

---

## 🔧 Troubleshooting

### Connection Issues
```
Error: Failed to connect to /10.0.2.2:8080

Solution:
1. Check if backend is running
2. For local testing, use ApiConstants.BASE_URL = "http://10.0.2.2:8080/"
3. For production, use Railway URL: "https://ute-training-points-system-production.up.railway.app/"
```

### Token Expired
```
Error: 401 Unauthorized

Solution:
1. App auto-refreshes token using refreshToken
2. If refresh fails, redirect to login screen
3. User needs to login again
```

### Email Not Received
```
Issue: Reset password email not arriving

Solution:
1. Check spam folder
2. Verify email address is correct
3. Wait 1-2 minutes (email may be delayed)
4. Check Resend API dashboard for sending status
```

### Build Fails
```
Solution:
1. ./gradlew clean
2. Invalidate Caches (File → Invalidate Caches)
3. Rebuild project
```

---

## 📚 Key Dependencies

```gradle
// Network
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.11.0'

// MVVM
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.1'
implementation 'androidx.lifecycle:lifecycle-livedata:2.6.1'

// UI
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.10.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// JSON
implementation 'com.google.code.gson:gson:2.10.1'

// Room Database (optional)
implementation 'androidx.room:room-runtime:2.5.2'
kapt 'androidx.room:room-compiler:2.5.2'
```

---

## 🎯 Development Workflow

### Local Development
```bash
# 1. Start backend locally
cd ../..
mvn spring-boot:run

# 2. Update ApiConstants.BASE_URL
// http://10.0.2.2:8080/

# 3. Build & run app
./gradlew installDebug
```

### Production Deployment
```bash
# 1. Backend deployed on Railway
# URL: https://ute-training-points-system-production.up.railway.app/

# 2. Update ApiConstants.BASE_URL
// https://ute-training-points-system-production.up.railway.app/

# 3. Build release APK
./gradlew assembleRelease

# 4. Sign APK & upload to Play Store
```

---

## 🔐 Security Checklist

- ✅ JWT tokens stored securely in SharedPreferences
- ✅ Access token expires in 15 minutes
- ✅ Refresh token expires in 7 days
- ✅ HTTPS enforced for production API
- ✅ Certificate pinning (optional, can add)
- ✅ No sensitive data in logs
- ✅ Interceptor adds Authorization header automatically
- ✅ Expired token triggers automatic refresh

---

## 📊 Performance Tips

1. **Caching**: Implement Room database for offline support
2. **Pagination**: Use pagination for large lists
3. **Image Loading**: Use Glide/Picasso for efficient image loading
4. **Network Timeout**: Currently 60s connect, 120s read/write
5. **Memory**: Use ViewModels to survive configuration changes

---

## 🐛 Known Issues & Limitations

- ❌ Offline mode not implemented (can add with Room)
- ❌ No image upload support (can add MultipartBody)
- ❌ No push notifications (can integrate Firebase Cloud Messaging)
- ❌ Limited error handling (can expand)

---

## 📝 Commit History

```
feat: update frontend API integration - match backend production endpoints
- Updated AuthApi endpoints (forgot-password, reset-password)
- Added RefreshTokenRequest DTO
- Updated TokenManager for refresh token support
- Updated AuthInterceptor to use accessToken
- Updated all API interfaces to match backend
```

---

## 🚀 Next Steps

1. ✅ Frontend API integration completed
2. 📱 Test all flows on Android device
3. 🚀 Deploy APK to Play Store (optional)
4. 📊 Add offline support with Room Database
5. 📲 Integrate Firebase Cloud Messaging for push notifications

---

## 📞 Support

**GitHub Repository**:
```
https://github.com/YueLouis/UTE-Training-Points-System
```

**Backend API**:
```
https://ute-training-points-system-production.up.railway.app
```

**Swagger API Docs**:
```
https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html
```

---

**Status**: 🟢 **READY FOR TESTING**  
**Last Updated**: January 13, 2026  
**Frontend Branch**: fe-integration

*Lên cây test tất cả flows trên Android device em nhé!* 🚀
```

