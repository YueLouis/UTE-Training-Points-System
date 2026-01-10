# 🚂 RAILWAY DEPLOYMENT - QUICK GUIDE

## ✅ **CHECKLIST BIẾN CẦN SET**

### **Bắt buộc (Must Have)**
- ✅ `SPRING_PROFILES_ACTIVE=production`
- ✅ `JWT_SECRET` (64 ký tự random)
- ✅ `MAIL_USERNAME` + `MAIL_PASSWORD` (Gmail App Password)
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

# Mail Gmail (PHẢI ĐỔI thành email + app password của em)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=phttrongtin.nguyen@gmail.com
MAIL_PASSWORD=xxxx xxxx xxxx xxxx

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

## 📧 **CÁCH LẤY GMAIL APP PASSWORD**

### **Bước 1: Bật 2-Step Verification**
1. Vào https://myaccount.google.com/security
2. Tìm "2-Step Verification"
3. Bật (nếu chưa bật)

### **Bước 2: Tạo App Password**
1. Vào https://myaccount.google.com/apppasswords
2. Chọn:
   - **App:** Mail
   - **Device:** Other (Custom name) → nhập "UTE Railway Backend"
3. Click "Generate"
4. Copy password 16 ký tự (dạng: `abcd efgh ijkl mnop`)
5. Paste vào `MAIL_PASSWORD` trên Railway

⚠️ **Lưu ý:** 
- Dùng **App Password**, KHÔNG phải password Gmail thường
- Mỗi app password chỉ hiện 1 lần, nếu mất phải tạo lại

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
| `MAIL_HOST` | `smtp.gmail.com` | ✅ | Manual |
| `MAIL_PORT` | `587` | ✅ | Manual |
| `MAIL_USERNAME` | `your@gmail.com` | ✅ | Manual |
| `MAIL_PASSWORD` | `app password` | ✅ | Manual |
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

**Em chỉ cần set 7 biến này trên Railway:**
1. ✅ `SPRING_PROFILES_ACTIVE=production`
2. ✅ `JWT_SECRET=<64-char-random>`
3. ✅ `MAIL_USERNAME=<your-gmail>`
4. ✅ `MAIL_PASSWORD=<app-password>`
5. ✅ `RESET_PEPPER=<64-char-random>`
6. ✅ `RESET_FRONTEND_URL=<frontend-url>`
7. ✅ `CORS_ALLOWED_ORIGINS=<frontend-domain>`

**Railway tự động lo:**
- ⚪ MySQL credentials (MYSQL*)
- ⚪ Port (PORT)

**Không cần set:**
- MAIL_HOST (mặc định smtp.gmail.com)
- MAIL_PORT (mặc định 587)
- RESET_TOKEN_EXPIRY (mặc định 15)

---

**Last Updated:** January 10, 2026  
**Status:** ✅ Ready to Deploy

