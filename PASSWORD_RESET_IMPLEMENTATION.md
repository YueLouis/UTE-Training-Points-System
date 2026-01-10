# 🔑 Password Reset Implementation - Complete Guide

## ✅ What's Implemented

Bạn vừa implement **password reset token-based** (chuẩn production), thay thế hoàn toàn OTP flow cũ.

---

## 📦 Files Added

### Entity & Repository
- ✅ `PasswordResetToken.java` - Entity for token storage
- ✅ `PasswordResetTokenRepository.java` - JPA repository

### Services
- ✅ `PasswordResetService.java` - Core business logic
- ✅ `ResendEmailService.java` - Email sending via Resend API

### Utilities
- ✅ `ResetTokenUtil.java` - Token generation & hashing

### DTOs
- ✅ `ForgotPasswordRequest.java` - Request body
- ✅ `ResetPasswordRequest.java` - Reset body
- ✅ `SimpleMessageResponse.java` - Response envelope

### Controllers
- ✅ `AuthController.java` - Updated with new endpoints

### Tests
- ✅ `PasswordResetServiceTest.java` - 9+ test cases

### Database Migration
- ✅ `V3__add_password_reset_tokens.sql` - New table

### Documentation
- ✅ `docs/PASSWORD_RESET_FLOW.md` - Full technical guide

### Configuration
- ✅ `application.properties` - Updated with reset config
- ✅ `.env.example` - Updated with secrets

---

## 🚀 API Endpoints

### 1. Request Reset Link
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "student@hcmute.edu.vn"
}
```

**Response** (luôn 200 OK):
```json
{
  "message": "If the email exists, a reset link has been sent."
}
```

### 2. Reset Password
```http
POST /api/reset-password
Content-Type: application/json

{
  "token": "RAW_TOKEN_FROM_EMAIL_LINK",
  "newPassword": "NewPassword@123"
}
```

**Response**:
```json
{
  "message": "Password updated successfully."
}
```

---

## 🔐 Security Features

✅ **Token Storage**: Only SHA-256 hash in DB (raw token never saved)  
✅ **One-Time Use**: Token marked as used after reset  
✅ **Expiration**: Default 15 minutes (configurable)  
✅ **Server Pepper**: Extra layer with `RESET_PEPPER` env var  
✅ **Email Enumeration**: Always 200 OK (doesn't reveal email exists)  
✅ **Audit Trail**: Stores request IP + user agent  
✅ **Password Hash**: BCrypt with individual salt  
✅ **Resend API**: HTTP-based (no SMTP port issues)  

---

## ⚙️ Configuration

### Local Development

**`.env`**:
```bash
RESET_TOKEN_EXPIRY=15
RESET_PEPPER=your_secret_pepper_at_least_32_chars
RESET_FRONTEND_URL=http://localhost:3000/reset-password
RESEND_API_KEY=re_test_default
APP_MAIL_FROM="UTE Training Points <noreply@resend.dev>"
```

**Build & Run**:
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

### Production (Railway)

Set in Railway dashboard:
```
RESET_TOKEN_EXPIRY=15
RESET_PEPPER=<your_long_production_secret>
RESET_FRONTEND_URL=https://your-frontend-domain.com/reset-password
RESEND_API_KEY=re_your_production_key
APP_MAIL_FROM="UTE Training Points <noreply@mail.yourdomain.com>"
```

---

## 📊 Database Schema

```sql
CREATE TABLE password_reset_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,    -- SHA256(rawToken + pepper)
    expires_at DATETIME NOT NULL,        -- 15 min from creation
    used_at DATETIME NULL,               -- Set when token used
    created_at DATETIME NOT NULL,
    request_ip VARCHAR(45),              -- Client IP for audit
    user_agent VARCHAR(255),             -- Client user agent
    
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_prt_hash (token_hash),
    INDEX idx_prt_expires (expires_at)
);
```

**Flyway Migration**: `V3__add_password_reset_tokens.sql` (auto-applies on startup)

---

## 🧪 Testing

```bash
# Run specific test class
./mvnw test -Dtest=PasswordResetServiceTest

# Run all tests
./mvnw test
```

**Test Coverage** (9 test cases):
- ✅ Valid user reset request
- ✅ Non-existent email (silent fail)
- ✅ Disabled account (silent fail)
- ✅ Reset with valid token
- ✅ Reset with expired token
- ✅ Reset with used token
- ✅ Token generation randomness
- ✅ SHA256 hashing
- ✅ Pepper hashing

---

## 📱 Frontend Integration

### Android Deep Link

```kotlin
// AndroidManifest.xml: Setup deep link
<activity>
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="https" android:host="your-domain" android:path="/reset-password" />
    </intent-filter>
</activity>

// Handle deep link
fun handleDeepLink(uri: Uri) {
    val token = uri.getQueryParameter("token")
    showResetPasswordScreen(token)
}

// API call
suspend fun resetPassword(token: String, newPassword: String) {
    apiService.resetPassword(ResetPasswordRequest(token, newPassword))
}
```

### Web (React/Vue)

```javascript
// Extract token from URL
const token = new URLSearchParams(location.search).get("token");

// Call API
fetch("https://api/reset-password", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ token, newPassword })
})
.then(r => r.json())
.then(data => {
  if (data.message) navigate("/login");
});
```

---

## ✅ Checklist: What You Can Now Do

- [x] Any user can reset password (không chỉ khi admin gửi)
- [x] Reset qua email link (token trong URL)
- [x] Token tự động expire sau 15 phút
- [x] Không lộ email tồn tại hay không
- [x] Token chỉ dùng được 1 lần
- [x] Mật khẩu mới phải khác cũ
- [x] Lịch sử reset lưu audit trail (IP + user agent)
- [x] Email HTML đẹp với reset link
- [x] Resend API integration (HTTP, no SMTP config hell)
- [x] Full test coverage
- [x] Production-ready (Railway deployment)

---

## 🐛 Troubleshooting

### Token không tìm thấy?
```
Nguyên nhân: Token hash không match
Cách fix: 
  1. Verify pepper là chuỗi giống nhau (dev vs production)
  2. Check token chưa hết hạn (expires_at > NOW)
  3. Check token chưa được dùng (used_at IS NULL)
```

### Email không gửi?
```
Nguyên nhân: Resend API key sai hoặc domain chưa verify
Cách fix:
  1. Check RESEND_API_KEY có chính xác không
  2. Nếu dùng custom domain: verify domain trong Resend dashboard
  3. Kiểm tra logs trong Resend → Email Activity
  4. Fallback: Dùng onboarding@resend.dev (default, không cần verify)
```

### Password update thất bại?
```
Nguyên nhân: Database constraint hoặc BCrypt error
Cách fix:
  1. Check password_hash column exists & datatype VARCHAR(255)+
  2. Verify BCrypt encode() không throw exception
  3. Check transaction commit (Flyway migration applied?)
```

---

## 📋 Migration Path from OTP

Nếu em muốn remove OTP table hoàn toàn:

```sql
-- Create V4__remove_otp_tokens.sql
DROP TABLE IF EXISTS password_reset_codes;
```

Hoặc giữ cả 2 table (OTP + token) tuỳ yêu cầu.

---

## 🎯 Production Deployment

### Step 1: Generate Strong Pepper
```bash
openssl rand -base64 32
# Result: example_output_long_random_string
```

### Step 2: Set Railway Variables
In Railway dashboard, set:
```
RESET_PEPPER=<output_from_above>
RESET_FRONTEND_URL=https://your-fe-domain/reset-password
RESEND_API_KEY=re_your_api_key
APP_MAIL_FROM="UTE Training Points <noreply@mail.yourdomain.com>"
```

### Step 3: Deploy
```bash
git add .
git commit -m "feat: Implement token-based password reset"
git push origin main
# Railway auto-deploys
```

### Step 4: Verify
```bash
curl https://your-app.up.railway.app/actuator/health
# {"status":"UP"}
```

---

## 📚 Additional Resources

- 📖 [Full Technical Guide](docs/PASSWORD_RESET_FLOW.md)
- 🔐 [Security Best Practices](https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html)
- 📧 [Resend Documentation](https://resend.com/docs)
- 🛠️ [Spring Security Guide](https://spring.io/projects/spring-security)

---

## 🎉 Summary

**Bạn vừa implement:**
- ✅ Chuẩn production password reset (token + email)
- ✅ Không phải OTP (dùng link thay vào)
- ✅ Bảo mật cao (hash + pepper + one-time)
- ✅ Mọi user đều có thể reset (không chỉ admin gửi)
- ✅ Hoàn toàn test + document
- ✅ Ready for production (Railway)

**Status**: ✅ PRODUCTION-READY

Lúc báo cáo có thể nói: "Em implement password reset bằng email token link, chuẩn OWASP, bảo mật cao, mọi user đều có thể reset password!"

Good luck! 🚀

