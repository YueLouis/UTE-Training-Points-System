# 🔐 Password Reset Flow - Token/Link Method

## Overview

Hệ thống sử dụng **Token/Link method** (industry standard) thay vì OTP code để reset mật khẩu.

**Flow:** Email → Click Link → Enter New Password

---

## 🎯 Why Token/Link (not OTP)?

| Aspect | OTP 6-digit | Token/Link ✅ |
|--------|-------------|--------------|
| **Security** | 6 digits = 1 triệu combinations | 256-bit token = không thể đoán |
| **UX** | 3 steps (request/verify/reset) | 2 steps (request/reset) |
| **Expiration** | 120 seconds (quá ngắn) | 15 minutes (đủ thời gian) |
| **User friendly** | Phải nhập code thủ công | Click link và nhập password |
| **Industry** | Ít phổ biến | Gmail, Facebook, GitHub dùng |

---

## 📋 Technical Flow

### Step 1: Request Reset Link

**Endpoint:** `POST /api/auth/forgot-password`

**Request:**
```json
{
  "email": "23162102@student.hcmute.edu.vn"
}
```

**Response:** (always 200 OK, không lộ email existence)
```json
{
  "message": "If the email exists, a reset link has been sent."
}
```

**Backend Process:**
1. Check email tồn tại + user active
2. Generate random 256-bit token (Base64 URL-safe)
3. Hash token với SHA-256 + server pepper
4. Lưu vào `password_reset_tokens`:
   ```sql
   INSERT INTO password_reset_tokens (
     user_id, 
     token_hash, 
     expires_at,
     request_ip,
     user_agent
   ) VALUES (
     123,
     'sha256_hash_of_token+pepper',
     NOW() + INTERVAL 15 MINUTE,
     '42.118.x.x',
     'Mozilla/5.0...'
   )
   ```
5. Gửi email với link:
   ```
   https://your-frontend.com/reset-password?token=RAW_TOKEN_HERE
   ```

**Email Template:**
```html
<h2>Reset your password</h2>
<p>Click the button below to set a new password. This link expires in 15 minutes.</p>
<a href="https://frontend/reset-password?token=ABC123XYZ">
  Reset Password
</a>
<p>If you didn't request this, ignore this email.</p>
```

---

### Step 2: Reset Password

**Endpoint:** `POST /api/reset-password`

**Request:**
```json
{
  "token": "ABC123XYZ...long_random_string",
  "newPassword": "NewSecurePassword123"
}
```

**Response:**
```json
{
  "message": "Password updated successfully."
}
```

**Backend Process:**
1. Hash token (SHA-256 + pepper) để tìm trong DB
2. Validate:
   - Token tồn tại
   - `used_at` IS NULL (chưa dùng)
   - `expires_at` > NOW() (chưa hết hạn)
3. Check password mới != password cũ (BCrypt compare)
4. Update user password (BCrypt hash)
5. Mark token `used_at = NOW()`
6. (Optional) Invalidate all JWT tokens / force re-login

**Errors:**
- `400 Bad Request`: Token invalid/expired
- `409 Conflict`: Token already used
- `400 Bad Request`: New password same as old

---

## 🗄️ Database Schema

### Table: `password_reset_tokens`

```sql
CREATE TABLE password_reset_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(255) NOT NULL,    -- SHA-256(token + pepper)
  expires_at DATETIME NOT NULL,        -- NOW() + 15 minutes
  used_at DATETIME NULL,               -- NULL = chưa dùng
  created_at DATETIME DEFAULT NOW(),
  request_ip VARCHAR(45),              -- Audit: IP nào request
  user_agent VARCHAR(255),             -- Audit: device gì
  
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_token_hash (token_hash),
  INDEX idx_expires (expires_at)
);
```

**Key Points:**
- ✅ Chỉ lưu **hash**, không lưu raw token
- ✅ One-time use: `used_at` != NULL → reject
- ✅ Auto-expire: 15 phút
- ✅ Audit trail: IP + user agent

---

## 🔒 Security Measures

### 1. Token Generation
```java
// Generate 256-bit random token
byte[] tokenBytes = new byte[32];
secureRandom.nextBytes(tokenBytes);
String rawToken = Base64.getUrlEncoder()
    .withoutPadding()
    .encodeToString(tokenBytes);
```

### 2. Token Hashing
```java
// Hash with server pepper (NEVER store raw token)
String tokenHash = sha256(rawToken + serverPepper);
```

### 3. Validation
```java
// Find token by hash
var token = tokenRepo.findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
    sha256(inputToken + pepper),
    LocalDateTime.now()
);

if (token.isEmpty()) {
    throw new BadRequestException("Invalid or expired token");
}
```

### 4. One-Time Use
```java
// After successful reset
token.setUsedAt(LocalDateTime.now());
tokenRepo.save(token);
```

---

## 🎨 Frontend Implementation

### React/Vue/Angular Example

```javascript
// Step 1: Request reset (on forgot password page)
async function requestReset(email) {
  const response = await fetch('/api/auth/forgot-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
  
  // Always shows success (don't reveal email existence)
  alert('If email exists, check your inbox for reset link');
}

// Step 2: Reset password (on /reset-password?token=XXX page)
async function resetPassword(token, newPassword) {
  const response = await fetch('/api/reset-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token, newPassword })
  });
  
  if (response.ok) {
    alert('Password updated! Please login.');
    // Redirect to login page
    window.location.href = '/login';
  } else {
    const error = await response.json();
    alert(error.message); // "Invalid or expired token"
  }
}

// On reset page load: extract token from URL
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get('token');
```

---

## 📧 Email Service Configuration

### Using Gmail SMTP

```properties
# application-production.yml
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}  # App password, not regular password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

app.reset.frontend-url=${RESET_FRONTEND_URL}
```

**Gmail App Password:**
1. Google Account → Security → 2-Step Verification (enable)
2. App passwords → Generate for "Mail"
3. Use generated password in `MAIL_PASSWORD`

### Using Resend API (Alternative)

```properties
resend.api-key=${RESEND_API_KEY}
```

**Benefits:**
- ✅ No SMTP config needed
- ✅ Better deliverability
- ✅ Email analytics
- ✅ Custom domain support

---

## 🧪 Testing

### Manual Test

1. Request reset:
   ```bash
   curl -X POST http://localhost:8080/api/auth/forgot-password \
     -H "Content-Type: application/json" \
     -d '{"email":"test@student.hcmute.edu.vn"}'
   ```

2. Check email inbox for link (or check backend logs for token if mail blocked)

3. Extract token from link, then reset:
   ```bash
   curl -X POST http://localhost:8080/api/reset-password \
     -H "Content-Type: application/json" \
     -d '{"token":"ABC123XYZ","newPassword":"NewPassword123"}'
   ```

### Unit Test Example

```java
@Test
void testPasswordResetFlow() {
    // 1. Request reset
    var email = "test@hcmute.edu.vn";
    passwordResetService.requestPasswordReset(email, "127.0.0.1", "TestAgent");
    
    // 2. Get token from DB (in real flow, from email)
    var token = tokenRepo.findTopByUser_EmailOrderByCreatedAtDesc(email)
        .orElseThrow();
    
    // 3. Reset password
    passwordResetService.resetPassword(
        extractRawToken(token), // helper method
        "NewPassword123"
    );
    
    // 4. Verify token marked as used
    assertNotNull(token.getUsedAt());
    
    // 5. Verify password changed
    var user = userRepo.findByEmail(email).orElseThrow();
    assertTrue(passwordEncoder.matches("NewPassword123", user.getPasswordHash()));
}
```

---

## 🚨 Edge Cases & Handling

### 1. Token Expired
```
expires_at < NOW()
→ 400 Bad Request: "Token expired. Please request a new reset link."
```

### 2. Token Already Used
```
used_at IS NOT NULL
→ 409 Conflict: "Token already used. Please request a new reset link."
```

### 3. Invalid Token
```
Token not found in DB
→ 400 Bad Request: "Invalid token."
```

### 4. New Password = Old Password
```
BCrypt.matches(newPassword, user.passwordHash)
→ 400 Bad Request: "New password cannot be same as current password."
```

### 5. Email Not Found
```
User not exists
→ Still return 200 OK (don't reveal email existence)
→ But don't send email
```

---

## 🔄 Migration from OTP to Token/Link

### If You Have OTP Code Currently

**Old Endpoints (deprecated):**
- `POST /api/auth/forgot-password/request` → send OTP code
- `POST /api/auth/forgot-password/verify` → verify OTP
- `POST /api/auth/forgot-password/reset` → reset with OTP

**New Endpoints (active):**
- `POST /api/auth/forgot-password` → send reset link
- `POST /api/reset-password` → reset with token

**Migration Steps:**
1. ✅ Update frontend to use new endpoints
2. ✅ Keep old endpoints for 1 week (backward compatibility)
3. ✅ Add deprecation warning to old endpoints
4. ✅ Monitor usage → remove old endpoints after migration complete
5. ✅ Drop `password_reset_codes` table (optional cleanup)

---

## 📊 Comparison Summary

| Feature | This System ✅ | Industry Standard |
|---------|---------------|-------------------|
| Method | Token/Link | Token/Link ✓ |
| Token Length | 256-bit | 128-256 bit ✓ |
| Hashing | SHA-256 + pepper | SHA-256/bcrypt ✓ |
| Expiration | 15 minutes | 15-60 min ✓ |
| One-time use | Yes | Yes ✓ |
| Audit trail | IP + UA | Yes ✓ |
| Email obfuscation | Yes (always 200) | Yes ✓ |

**Result:** ✅ **Production-ready, industry-standard implementation**

---

## 📚 References

- [OWASP Password Reset Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html)
- [RFC 4086 - Randomness Requirements](https://datatracker.ietf.org/doc/html/rfc4086)
- [NIST SP 800-63B - Digital Identity Guidelines](https://pages.nist.gov/800-63-3/sp800-63b.html)

---

**Last Updated:** January 10, 2026  
**Status:** ✅ Production-ready

