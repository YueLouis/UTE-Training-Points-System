# ✅ HOÀN THÀNH - CHUYỂN SANG RESEND API

## 🚨 **VẤN ĐỀ ĐÃ GIẢI QUYẾT**

**Railway Free/Hobby block SMTP ports 465/587** → Gmail SMTP không hoạt động!

**✅ Giải pháp:** Dùng Resend API (HTTPS, port 443)

---

## 📋 **ĐÃ LÀM GÌ?**

### **1. Update Configuration Files**
- ✅ `application-production.yml`: Bỏ spring.mail config, thêm resend.api-key
- ✅ `.env.example`: Thay MAIL_HOST/PORT/USERNAME/PASSWORD → RESEND_API_KEY
- ✅ `RAILWAY_DEPLOYMENT_QUICK_GUIDE.md`: Update hướng dẫn dùng Resend

### **2. Code Sẵn Sàng**
- ✅ `ResendEmailService.java` đã có sẵn (dùng OkHttp + Resend API)
- ✅ `PasswordResetService.java` đã dùng ResendEmailService
- ✅ Email template HTML đã có

### **3. Documentation**
- ✅ `docs/RESEND_SETUP_GUIDE.md`: Hướng dẫn chi tiết setup Resend
- ✅ Tất cả guides đã update

---

## 🚀 **BƯỚC TIẾP THEO ĐỂ DEPLOY**

### **Bước 1: Đăng Ký Resend (2 phút)**

1. **Truy cập:** https://resend.com/signup
2. **Đăng ký:** Email + Password
3. **Verify email:** Check inbox, click link

**Free Plan: 100 emails/day** (đủ cho demo)

---

### **Bước 2: Lấy API Key (1 phút)**

1. **Login:** https://resend.com/login
2. **Vào:** https://resend.com/api-keys
3. **Create API Key:**
   - Name: `UTE Training Points Backend`
   - Permission: `Sending Access`
4. **Copy API key** (dạng: `re_xxxxxxxxxxxxx`)

⚠️ **Lưu lại ngay! Chỉ hiện 1 lần!**

---

### **Bước 3: Set Variables Trên Railway (2 phút)**

**Xóa các biến SMTP cũ (nếu có):**
```
❌ MAIL_HOST
❌ MAIL_PORT
❌ MAIL_USERNAME
❌ MAIL_PASSWORD
```

**Thêm biến Resend mới:**
```bash
# Railway → Variables → Add New Variable

RESEND_API_KEY=re_xxxxxxxxxxxxx
MAIL_FROM=onboarding@resend.dev
```

**Giữ nguyên các biến khác:**
```bash
SPRING_PROFILES_ACTIVE=production
JWT_SECRET=<64-char-random>
RESET_PEPPER=<64-char-random>
RESET_FRONTEND_URL=http://localhost:3000/reset-password
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

---

### **Bước 4: Deploy & Test (5 phút)**

**Railway tự động redeploy khi thay đổi variables.**

**Check logs:**
```
Railway → Deployments → Latest → View Logs
→ Tìm: "Started UteTrainingPointsSystemApiApplication"
```

**Test API:**
```bash
curl -X POST https://your-app.up.railway.app/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"phttrongtin.nguyen@gmail.com"}'
```

**Check email inbox** → Should receive reset link!

---

## 📧 **SENDER EMAIL OPTIONS**

### **Option 1: Default (Khuyến nghị cho demo)**
```
MAIL_FROM=onboarding@resend.dev
```
- ✅ Không cần verify
- ✅ Hoạt động ngay
- ⚠️ Email hiện "from: onboarding@resend.dev"

### **Option 2: Email Cá Nhân**
```
MAIL_FROM=phttrongtin.nguyen@gmail.com
```
**Cần verify trước:**
1. Resend Dashboard → Domains
2. Add Domain → Single Sender
3. Nhập email → Verify qua inbox
4. Done! Gửi từ email của em

### **Option 3: Domain Riêng (Pro)**
```
MAIL_FROM=noreply@mail.utetrainingpoints.com
```
**Cần:**
- Mua domain ($10-15/year)
- Cấu hình DNS (TXT/CNAME records)
- Verify trên Resend

---

## ✅ **SO SÁNH**

| Feature | Gmail SMTP | Resend API ✅ |
|---------|-----------|--------------|
| **Railway Free** | ❌ Blocked (port 465/587) | ✅ Works (HTTPS port 443) |
| **Setup** | App Password | API Key |
| **Free Limit** | 500/day | 100/day |
| **Verify** | 2-Step Auth | Email verify |
| **Deliverability** | Good | Excellent |
| **Speed** | Slow | Fast |

---

## 🎯 **TÓM TẮT NHANH**

**Railway block SMTP → Phải dùng Resend API!**

**3 bước để chạy:**
1. ✅ Đăng ký Resend: https://resend.com/signup
2. ✅ Lấy API key: https://resend.com/api-keys
3. ✅ Set 2 biến trên Railway:
   ```
   RESEND_API_KEY=re_xxxxx
   MAIL_FROM=onboarding@resend.dev
   ```

**Test:**
```bash
curl -X POST https://your-app/api/auth/forgot-password \
  -d '{"email":"test@gmail.com"}'
```

**Done!** Email sẽ gửi qua Resend API (không qua SMTP)! 🎉

---

## 📚 **TÀI LIỆU THAM KHẢO**

- ✅ `docs/RESEND_SETUP_GUIDE.md` - Hướng dẫn chi tiết
- ✅ `.env.example` - Template cập nhật
- ✅ `RAILWAY_DEPLOYMENT_QUICK_GUIDE.md` - Deployment guide
- ✅ Railway docs: https://docs.railway.com/reference/outbound-networking

---

**Last Updated:** January 10, 2026  
**Status:** ✅ Ready to Deploy with Resend API

