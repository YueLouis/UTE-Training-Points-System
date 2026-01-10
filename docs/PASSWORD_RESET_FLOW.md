# 🔐 Password Reset Flow - Token-Based (Standard Implementation)

## Overview

Đây là **chuẩn production** để reset mật khẩu qua **email token link**, không dùng OTP.

### Flow Đơn Giản

```
User nhập email
    ↓
Backend tạo token + gửi email (có link)
    ↓
User bấm link → nhập mật khẩu mới
    ↓
Backend verify token + cập nhật mật khẩu
```

---

## 1. API Endpoints

### **(A) Request Reset Link**

```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "student@hcmute.edu.vn"
}
```

**Response** (luôn giống nhau, không reveal email tồn tại):
```json
{
  "message": "If the email exists, a reset link has been sent."
}
```

**Status**: ✅ 200 OK (dù email có hay không)

---

### **(B) Reset Password**

```http
POST /api/reset-password
Content-Type: application/json

{
  "token": "RAW_TOKEN_FROM_EMAIL_LINK",
  "newPassword": "NewSecurePassword@123"
}
```

**Response**:
```json
{
  "message": "Password updated successfully."
}
```

**Status**: ✅ 200 OK (nếu thành công), ❌ 400 Bad Request (token sai/hết hạn)

---

## 2. Backend Logic

### **Step 1: Generate Token & Send Email**

```java
PasswordResetService.requestPasswordReset(
  email,        // User email
  ip,           // Client IP (for audit)
  userAgent     // User Agent (for audit)
)
```

**Quá trình**:
1. ✅ Check email tồn tại (nếu không → silent fail, không tiết lộ)
2. ✅ Kiểm tra account active
3. ✅ Tạo token ngẫu nhiên (256-bit)
4. ✅ Hash token: `tokenHash = SHA256(rawToken + pepper)`
5. ✅ Lưu DB: `tokenHash`, `expires_at`, `user_id`, `request_ip`, `user_agent`
6. ✅ Gửi email: link = `{FRONTEND_URL}?token={rawToken}`
7. ✅ **KHÔNG lưu** token raw trong DB (chỉ hash!)

### **Step 2: Reset Password**

```java
PasswordResetService.resetPassword(
  rawToken,     // Token từ email link
  newPassword   // Mật khẩu mới
)
```

**Quá trình**:
1. ✅ Validate `newPassword` length >= 6
2. ✅ Hash token: `tokenHash = SHA256(rawToken + pepper)`
3. ✅ Query DB: `find token where tokenHash = ? AND usedAt IS NULL AND expiresAt > NOW`
4. ✅ Nếu không tìm thấy → throw error "Invalid or expired token"
5. ✅ Tìm user
6. ✅ Verify mật khẩu mới ≠ mật khẩu cũ
7. ✅ Hash mật khẩu mới: `BCrypt.encode(newPassword)`
8. ✅ Cập nhật `user.passwordHash`
9. ✅ Mark token used: `token.usedAt = NOW()`
10. ✅ Trả về success

---

## 3. Database Schema

```sql
CREATE TABLE password_reset_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,      -- SHA256 hash (NEVER raw token)
    expires_at DATETIME NOT NULL,          -- 15 minutes từ now
    used_at DATETIME NULL,                 -- Set khi dùng lần đầu
    created_at DATETIME DEFAULT NOW(),
    request_ip VARCHAR(45),                -- Audit: IP request
    user_agent VARCHAR(255),               -- Audit: User agent
    
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_hash (token_hash),
    INDEX idx_expires (expires_at)
);
```

---

## 4. Security Details

### ✅ Bảo Mật

| Feature | Implementation | Why |
|---------|---|---|
| **Token Storage** | Only hash (SHA-256) in DB | DB breach → tokens vô dụng |
| **Token Length** | 256-bit random | Khó brute force |
| **Token Format** | URL-safe Base64 | Dùng qua email/URL |
| **Token Expiry** | 15 phút | Hạn chế window attack |
| **One-Time Use** | `usedAt` field | Replay attack protection |
| **Email Enumeration** | Always 200 OK | Không reveal email tồn tại |
| **Server Pepper** | Hardcoded + env var | Layer thêm security |
| **Password Hashing** | BCrypt | Industry standard |
| **Audit Trail** | IP + User Agent | Trace lại nếu bị hack |

---

## 5. Configuration

### `application.properties`

```properties
# Password Reset
app.reset.token.expiry-minutes=15
app.reset.pepper=ute_training_points_default_pepper_change_in_production
app.reset.frontend-url=https://your-fe-domain.com/reset-password

# Email via Resend
resend.api-key=re_xxxxx
app.mail.from=UTE Training Points <noreply@resend.dev>
```

### `.env` (Local)

```bash
RESET_TOKEN_EXPIRY=15
RESET_PEPPER=long_random_secret_at_least_32_chars
RESET_FRONTEND_URL=http://localhost:3000/reset-password
RESEND_API_KEY=re_your_key
APP_MAIL_FROM="UTE Training Points <noreply@resend.dev>"
```

### `.env` (Production)

```bash
RESET_TOKEN_EXPIRY=15
RESET_PEPPER=your_long_random_production_secret
RESET_FRONTEND_URL=https://your-frontend-domain.com/reset-password
RESEND_API_KEY=re_your_production_key
APP_MAIL_FROM="UTE Training Points <noreply@mail.yourdomain.com>"
```

---

## 6. Email Template

Gửi HTML email với link:

```html
<h2>Reset Your Password</h2>
<p>Click the button below to set a new password. This link expires in 15 minutes.</p>
<a href="https://your-fe-domain.com/reset-password?token=RAW_TOKEN">
  Reset Password
</a>
<p>Or paste this link: https://your-fe-domain.com/reset-password?token=RAW_TOKEN</p>
<p>⚠️ If you did not request this, please ignore this email.</p>
```

---

## 7. Frontend Integration (Android/Web)

### **Màn Forgot Password**

```javascript
// FE: User enters email
const email = "student@hcmute.edu.vn";

const response = await fetch("https://api/auth/forgot-password", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ email })
});

// Always returns 200
console.log("Check your email for reset link");
```

### **Reset Password (from link)**

```javascript
// FE: Extract token from URL ?token=xxxxx
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get("token");

// FE: User enters new password
const newPassword = "NewSecurePassword@123";

const response = await fetch("https://api/reset-password", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ token, newPassword })
});

if (response.ok) {
  console.log("Password reset successful!");
  // Redirect to login
}
```

### **Android Implementation**

```kotlin
// Kotlin: Deep link from email
fun handleResetPasswordDeepLink(uri: Uri) {
    val token = uri.getQueryParameter("token")
    
    // Show reset password fragment with token
    showResetPasswordFragment(token)
}

// UI: User enters new password & submit
suspend fun resetPassword(token: String, newPassword: String) {
    val request = ResetPasswordRequest(token, newPassword)
    val response = apiService.resetPassword(request)
    
    if (response.isSuccessful) {
        showToast("Password reset successful")
        navigateToLogin()
    }
}
```

---

## 8. Edge Cases Handled

| Scenario | Behavior |
|----------|----------|
| Email không tồn tại | Return 200 (silent fail) |
| Token sai | Return 400 "Invalid or expired token" |
| Token hết hạn | Return 400 "Invalid or expired token" |
| Token đã dùng | Return 400 "Invalid or expired token" |
| Mật khẩu mới = cũ | Return 400 "New password must be different" |
| Mật khẩu quá ngắn | Return 400 "Password must be >= 6 chars" |
| Database error | Log + return 500 (generic error) |
| Email send fail | Log + return 200 (silently fail, user can request again) |

---

## 9. Rate Limiting (Optional)

Nên thêm rate limit để chống spam:

```
/api/auth/forgot-password:
  - 5 requests per email per hour
  - 10 requests per IP per hour

/api/reset-password:
  - 10 requests per IP per hour
```

Implementation: Dùng Redis hoặc in-memory (Bucket4j đã có)

---

## 10. Monitoring & Audit

### Logs

```
[INFO] Password reset requested for email: student@hcmute.edu.vn from IP: 192.168.1.1
[INFO] Reset token created: token_hash=abc123..., expires_at=2026-01-11T16:45:00
[INFO] Email sent successfully to: student@hcmute.edu.vn
[INFO] Password reset completed for user ID: 4
[WARN] Password reset attempted with invalid token
[ERROR] Email sending failed for: student@hcmute.edu.vn
```

### Database Audit

```sql
-- Audit: Xem ai reset mật khẩu khi nào
SELECT user_id, created_at, request_ip, used_at 
FROM password_reset_tokens 
WHERE used_at IS NOT NULL 
ORDER BY used_at DESC;

-- Xem những token chưa dùng
SELECT * FROM password_reset_tokens 
WHERE used_at IS NULL AND expires_at > NOW();

-- Cleanup: Xóa expired tokens (tuỳ chọn)
DELETE FROM password_reset_tokens 
WHERE expires_at < NOW() AND used_at IS NULL;
```

---

## 11. Troubleshooting

### Token không tìm thấy?
- ✅ Check token hash calculation (raw_token + pepper)
- ✅ Verify token chưa hết hạn
- ✅ Verify token chưa được dùng

### Email không gửi?
- ✅ Check Resend API key đúng
- ✅ Check domain verified (nếu dùng custom domain)
- ✅ Check sender email hợp lệ
- ✅ Check logs trong Resend dashboard

### Mật khẩu không cập nhật?
- ✅ Check `password_hash` column exists
- ✅ Verify BCrypt encoding
- ✅ Check transaction commit

---

## Summary

✅ **Chuẩn production**: Token link email  
✅ **Bảo mật cao**: SHA256 hash + pepper + one-time use  
✅ **UX tốt**: Một click → reset password  
✅ **Scalable**: Stateless, không dùng session  
✅ **Audit trail**: Lưu IP + user agent  

**Đây là best practice cho password reset flow!**

