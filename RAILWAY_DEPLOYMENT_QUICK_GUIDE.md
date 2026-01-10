# 🚂 RAILWAY DEPLOYMENT - QUICK GUIDE

## ⚠️ **RAILWAY BLOCKS SMTP!**
Railway Free/Hobby plans **block ports 465/587** → Gmail SMTP không hoạt động!  
✅ **Giải pháp:** Dùng **Resend API** (HTTPS, port 443)

---

## ✅ **CHECKLIST BIẾN CẦN SET**

### **Bắt buộc (Must Have)**
- ✅ `SPRING_PROFILES_ACTIVE=production`
- ✅ `JWT_SECRET` (64 ký tự random)
- ✅ `RESEND_API_KEY` (từ resend.com)
- ✅ `MAIL_FROM` (email gửi)
- ✅ `RESET_PEPPER` (64 ký tự random)
- ✅ `RESET_FRONTEND_URL` (URL frontend)
- ✅ `CORS_ALLOWED_ORIGINS` (domain frontend)

### **Railway Tự Động (Không Set)**
- ⚠️ `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` (MySQL Plugin tự inject)
- ⚠️ `PORT` (Railway tự set 8080)

---

## 📋 **GIÁ TRỊ CỤ THỂ ĐỂ SET**

### **Copy Paste Vào Railway**

```bash
# Profile
SPRING_PROFILES_ACTIVE=production

# JWT (PHẢI ĐỔI thành chuỗi random 64 ký tự)
JWT_SECRET=UTE2026_Training_Points_JWT_Secret_CHANGE_THIS_abc123xyz789def456ghi

# Email (Resend API - KHÔNG phải Gmail SMTP)
RESEND_API_KEY=re_xxxxxxxxxxxxx
MAIL_FROM=onboarding@resend.dev

# Reset Password (PHẢI ĐỔI pepper thành random 64 ký tự, URL thành frontend URL)
RESET_PEPPER=UTE_Reset_Pepper_CHANGE_THIS_def456uvw789
RESET_TOKEN_EXPIRY=15
RESET_FRONTEND_URL=http://localhost:3000/reset-password

# CORS (PHẢI ĐỔI thành domain frontend của em)
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://your-frontend.com
```

---

## 🔐 **CÁCH TẠO CHUỖI NGẪU NHIÊN AN TOÀN**

### **Cách 1: Online Generator**
```
https://www.random.org/strings/?num=1&len=64&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain
```

### **Cách 2: Command Line (Python)**
```bash
python -c "import secrets; print(secrets.token_urlsafe(48))"
```

### **Cách 3: PowerShell**
```powershell
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 64 | % {[char]$_})
```

---

## 📧 **CÁCH LẤY RESEND API KEY**

⚠️ **Quan Trọng:** Railway **block SMTP** (Gmail không hoạt động trên Free/Hobby plan)  
✅ **Giải pháp:** Dùng Resend API (HTTPS)

### **Bước 1: Đăng Ký Resend (Miễn Phí)**
1. Vào https://resend.com/signup
2. Đăng ký bằng email + password
3. Verify email (check inbox)

**Free Plan:**
- ✅ 100 emails/day
- ✅ 3,000 emails/month
- ✅ Đủ cho demo/test

### **Bước 2: Lấy API Key**
1. Login: https://resend.com/login
2. Vào: https://resend.com/api-keys
3. Click: "Create API Key"
4. Name: "UTE Training Points Backend"
5. Permissions: "Sending Access"
6. Click "Create" → **Copy API key** (dạng: `re_xxxxxxxxxxxxx`)

⚠️ **Lưu ý:** API key chỉ hiện 1 lần! Lưu lại ngay.

### **Bước 3: Chọn Sender Email**

**Option 1: Dùng Default (Nhanh Nhất)**
```
MAIL_FROM=onboarding@resend.dev
```
✅ Không cần verify  
⚠️ Email sẽ hiện "from: onboarding@resend.dev"

**Option 2: Verify Email Cá Nhân**
1. Vào: https://resend.com/domains
2. Click: "Add Domain" → "Single Sender"
3. Nhập: `phttrongtin.nguyen@gmail.com`
4. Check inbox → Click verify link
5. Done! Giờ set: `MAIL_FROM=phttrongtin.nguyen@gmail.com`

---

## 🚀 **BƯỚC DEPLOY TRÊN RAILWAY**

### **1. Vào Railway Dashboard**
```
https://railway.app
→ Login
→ Chọn project "UTE Training Points System"
→ Click vào service "Backend" (hoặc main service)
```

### **2. Click Tab "Variables"**
```
Bên trái sidebar → Variables
```

### **3. Thêm Từng Biến**
```
Click "+ New Variable"
→ Name: SPRING_PROFILES_ACTIVE
→ Value: production
→ Click "Add"

Lặp lại cho tất cả biến trong checklist
```

### **4. Verify MySQL Plugin**
```
Click tab "Plugins" (nếu có)
→ Kiểm tra "MySQL" đã được add
→ Nếu chưa: Click "+" → Add MySQL

MySQL plugin tự động tạo MYSQL* variables
```

### **5. Deploy**
```
Railway tự động deploy khi có thay đổi variables
→ Click tab "Deployments"
→ Chờ status "Success" (2-3 phút)
```

---

## ✅ **VERIFY SAU KHI DEPLOY**

### **1. Check Logs**
```
Railway → Deployments → Click latest → View Logs

Tìm dòng:
"Started UteTrainingPointsSystemApiApplication in X.XXX seconds"
```

### **2. Test Health Endpoint**
```bash
curl https://ute-training-points-system-production.up.railway.app/actuator/health

# Expected:
{"status":"UP"}
```

### **3. Test Swagger UI**
```
https://ute-training-points-system-production.up.railway.app/swagger-ui/index.html
```

### **4. Test Login API**
```bash
curl -X POST https://ute-training-points-system-production.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"23162102","password":"Tin1867+"}'

# Expected: JWT token response
```

### **5. Test Forgot Password**
```bash
curl -X POST https://ute-training-points-system-production.up.railway.app/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"phttrongtin.nguyen@gmail.com"}'

# Expected: "If the email exists, a reset link has been sent."
# → Check email inbox
```

---

## 🐛 **TROUBLESHOOTING**

### **Lỗi: "Could not resolve placeholder 'JWT_SECRET'"**
**Nguyên nhân:** Chưa set `JWT_SECRET` trên Railway

**Fix:**
```
Railway → Variables → Add JWT_SECRET
```

---

### **Lỗi: "Access denied for user"**
**Nguyên nhân:** MySQL plugin chưa được add hoặc variables lỗi

**Fix:**
```
Railway → Plugins → Add MySQL (nếu chưa có)
→ Restart service
```

---

### **Lỗi: "Mail server connection failed"**
**Nguyên nhân:** 
- Sai Gmail App Password
- Chưa bật 2-Step Verification

**Fix:**
1. Xóa App Password cũ
2. Tạo App Password mới
3. Update `MAIL_PASSWORD` trên Railway

---

### **Lỗi CORS: "Access to fetch has been blocked"**
**Nguyên nhân:** Frontend domain không nằm trong `CORS_ALLOWED_ORIGINS`

**Fix:**
```
Railway → Variables → Update CORS_ALLOWED_ORIGINS
→ Thêm domain frontend (vd: https://your-app.com)
```

---

## 📊 **BIẾN MÔI TRƯỜNG - TỔNG KẾT**

| Biến | Giá Trị Mẫu | Bắt Buộc | Nguồn |
|------|------------|----------|-------|
| `SPRING_PROFILES_ACTIVE` | `production` | ✅ | Manual |
| `JWT_SECRET` | `64-char random` | ✅ | Manual |
| `RESEND_API_KEY` | `re_xxxxx` | ✅ | Manual |
| `MAIL_FROM` | `onboarding@resend.dev` | ✅ | Manual |
| `RESET_PEPPER` | `64-char random` | ✅ | Manual |
| `RESET_TOKEN_EXPIRY` | `15` | ⚪ Optional | Manual |
| `RESET_FRONTEND_URL` | `https://frontend/reset` | ✅ | Manual |
| `CORS_ALLOWED_ORIGINS` | `https://frontend.com` | ✅ | Manual |
| `MYSQLHOST` | Auto | ❌ Don't Set | Railway |
| `MYSQLPORT` | Auto | ❌ Don't Set | Railway |
| `MYSQLDATABASE` | Auto | ❌ Don't Set | Railway |
| `MYSQLUSER` | Auto | ❌ Don't Set | Railway |
| `MYSQLPASSWORD` | Auto | ❌ Don't Set | Railway |
| `PORT` | `8080` | ❌ Don't Set | Railway |

---

## 🎯 **TÓM TẮT NHANH**

**Em chỉ cần set 6 biến này trên Railway:**
1. ✅ `SPRING_PROFILES_ACTIVE=production`
2. ✅ `JWT_SECRET=<64-char-random>`
3. ✅ `RESEND_API_KEY=re_xxxxx` (từ https://resend.com/api-keys)
4. ✅ `MAIL_FROM=onboarding@resend.dev`
5. ✅ `RESET_PEPPER=<64-char-random>`
6. ✅ `RESET_FRONTEND_URL=<frontend-url>`
7. ✅ `CORS_ALLOWED_ORIGINS=<frontend-domain>`

**Railway tự động lo:**
- ⚪ MySQL credentials (MYSQL*)
- ⚪ Port (PORT)

**⚠️ KHÔNG dùng Gmail SMTP (bị Railway block):**
- ❌ ~~MAIL_HOST~~
- ❌ ~~MAIL_PORT~~
- ❌ ~~MAIL_USERNAME~~
- ❌ ~~MAIL_PASSWORD~~
- ⚪ MySQL credentials (MYSQL*)
- ⚪ Port (PORT)

**Không cần set:**
- MAIL_HOST (mặc định smtp.gmail.com)
- MAIL_PORT (mặc định 587)
- RESET_TOKEN_EXPIRY (mặc định 15)

---

**Last Updated:** January 10, 2026  
**Status:** ✅ Ready to Deploy

