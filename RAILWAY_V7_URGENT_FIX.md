# 🚨 URGENT: FIX V7 MIGRATION ON RAILWAY

## ⚠️ **VẤN ĐỀ**

App không start được vì V7 migration failed. **Code đã fix nhưng Railway vẫn thấy V7 cũ (failed) trong database!**

---

## ✅ **GIẢI PHÁP: XÓA V7 KHỎI RAILWAY DB**

### **Bước 1: Connect tới Railway MySQL (3 cách)**

#### **Cách 1: Railway Data Tab (Nhanh Nhất)** ⭐

1. Vào Railway Dashboard: https://railway.app
2. Click vào **MySQL service** (không phải backend service)
3. Click tab **"Data"** (bên cạnh Variables/Metrics)
4. **Query Editor** sẽ mở ra
5. Copy script từ `scripts/repair_flyway_v7.sql` và paste vào
6. Click **"Run"**

---

#### **Cách 2: Railway Connect (CLI)**

```bash
# Install Railway CLI (nếu chưa có)
npm install -g @railway/cli

# Login
railway login

# Link project
railway link

# Connect to MySQL
railway connect mysql

# Paste SQL từ scripts/repair_flyway_v7.sql
# Hoặc chạy:
mysql> source /path/to/repair_flyway_v7.sql
```

---

#### **Cách 3: MySQL Client (Workbench/DBeaver)**

**Lấy credentials:**
```
Railway → MySQL service → Variables tab

Copy:
- MYSQLHOST
- MYSQLPORT (3306)
- MYSQLUSER (root)
- MYSQLPASSWORD
- MYSQLDATABASE (railway)
```

**Connect:**
```
MySQL Workbench → New Connection
Host: <MYSQLHOST>
Port: 3306
User: root
Password: <MYSQLPASSWORD>
Database: railway
```

**Run script:**
```sql
-- Copy từ scripts/repair_flyway_v7.sql và chạy
```

---

### **Bước 2: Chạy Repair Script**

Copy và chạy từng dòng (hoặc chạy cả file):

```sql
-- 1. Kiểm tra V7 status (should show success=0/failed)
SELECT version, description, success, installed_on 
FROM flyway_schema_history 
WHERE version = '7';

-- 2. XÓA V7 FAILED
DELETE FROM flyway_schema_history WHERE version = '7';

-- 3. Dọn dẹp data V7 insert dở (nếu có)
DELETE FROM user_org_units WHERE org_unit_id > 0;
DELETE FROM org_units WHERE id > 0;
ALTER TABLE org_units AUTO_INCREMENT = 1;

-- 4. Verify V7 đã xóa
SELECT COUNT(*) as v7_count 
FROM flyway_schema_history 
WHERE version = '7';
-- Kết quả phải là 0
```

---

### **Bước 3: Redeploy từ Railway Dashboard**

**Option A: Auto Redeploy (Nếu có new commit)**
- Railway đã detect code mới → Tự động redeploy trong 1-2 phút

**Option B: Manual Redeploy**
```
Railway → Backend Service → Deployments tab → Click "Redeploy"
```

---

### **Bước 4: Verify Success**

**Check Logs:**
```
Railway → Backend Service → Deployments → Latest → View Logs

Tìm dòng:
✅ "Flyway migration V7 SUCCESS"
✅ "Started UteTrainingPointsSystemApiApplication in X.XXX seconds"
```

**Check Database:**
```sql
-- V7 should be success=1 now
SELECT version, description, success 
FROM flyway_schema_history 
WHERE version = '7';

-- Should show 12 faculties
SELECT COUNT(*), type 
FROM org_units 
GROUP BY type;
```

---

## 🎯 **QUICK SUMMARY**

**3 bước:**

1. ✅ **Connect Railway MySQL** (Data tab hoặc CLI)
2. ✅ **Run repair script:**
   ```sql
   DELETE FROM flyway_schema_history WHERE version = '7';
   DELETE FROM user_org_units WHERE org_unit_id > 0;
   DELETE FROM org_units WHERE id > 0;
   ```
3. ✅ **Redeploy từ Railway** (auto hoặc manual)

**Sau đó app sẽ start OK!** 🎉

---

## 📝 **TẠI SAO PHẢI LÀM VẬY?**

1. V7 migration chạy lần đầu → Lỗi SQL syntax → Flyway mark FAILED
2. Code fix và push → Railway rebuild image mới
3. **NHƯNG** Flyway check database → Thấy V7 đã có (FAILED) → Không chạy lại → Báo lỗi validate
4. **Fix:** Xóa V7 khỏi DB → Flyway nghĩ V7 chưa chạy → Chạy V7 (fixed) → Success!

---

## ⚠️ **TROUBLESHOOTING**

### **"Cannot connect to Railway MySQL"**

**Check connection string:**
```bash
# Get from Railway Variables
MYSQLHOST=mysql.railway.internal  # Internal network
# Or use Public Host if connecting from outside
```

**Test connection:**
```bash
mysql -h <MYSQLHOST> -P 3306 -u root -p<PASSWORD> railway
```

---

### **"V7 still fails after repair"**

**Check if V7 SQL is really fixed:**
```bash
# Look at V7 file
cat src/main/resources/db/migration/V7__fix_org_units_12_faculties_hcmute.sql

# Should see:
INSERT INTO org_units (type, code, name, description, parent_id, is_active)
# NOT:
INSERT INTO org_units (type, code, name, parent_id, description, is_active)
```

**If still wrong order, fix and push again:**
```bash
git add V7__fix_org_units_12_faculties_hcmute.sql
git commit -m "fix: V7 column order"
git push origin main
```

---

### **"Partial data from failed V7"**

V7 có thể đã insert một số dòng trước khi fail. Check:

```sql
SELECT COUNT(*), type FROM org_units GROUP BY type;
```

**If có data lộn xộn:**
```sql
-- Clean all org_units
DELETE FROM user_org_units WHERE org_unit_id > 0;
DELETE FROM org_units WHERE id > 0;
ALTER TABLE org_units AUTO_INCREMENT = 1;
```

---

## 🎉 **EXPECTED RESULT**

**Logs:**
```
Flyway Community Edition 11.7.2
Database: jdbc:mysql://mysql.railway.internal:3306/railway
Successfully validated 7 migrations
Schema history table railway.flyway_schema_history exists
Current version of schema railway: 6
Migrating schema railway to version 7 - fix org units 12 faculties hcmute
Successfully applied 1 migration to schema railway, now at version v7

Started UteTrainingPointsSystemApiApplication in 8.123 seconds
```

**Database:**
```sql
mysql> SELECT type, COUNT(*) FROM org_units GROUP BY type;
+---------------+----------+
| type          | COUNT(*) |
+---------------+----------+
| UNIVERSITY    |        1 |
| INSTITUTE     |        3 |
| FACULTY       |       12 |
| OFFICE        |        5 |
| UNION_SCHOOL  |        2 |
| UNION_FACULTY |        5 |
| CLUB          |        4 |
+---------------+----------+
7 rows in set
```

---

## 📚 **FILES**

- `scripts/repair_flyway_v7.sql` - SQL script to run
- `FLYWAY_V7_FIX_GUIDE.md` - Detailed guide
- `V7__fix_org_units_12_faculties_hcmute.sql` - Fixed migration

---

**Last Updated:** January 10, 2026  
**Status:** ⏳ Waiting for em to run repair script on Railway

