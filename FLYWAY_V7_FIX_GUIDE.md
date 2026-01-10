# 🔧 FLYWAY V7 MIGRATION FIX

## 🚨 **VẤN ĐỀ**

```
Detected failed migration to version 7 (fix org units 12 faculties hcmute).
Please remove any half-completed changes then run repair to fix the schema history.
```

**Nguyên nhân:** V7 migration có lỗi SQL syntax (column order sai) → Chạy dở → Flyway mark FAILED.

---

## ✅ **ĐÃ FIX**

### **1. Sửa V7 Migration File** ✅

**Lỗi:** Column order sai trong INSERT statement
```sql
-- SAI:
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
('UNIVERSITY', 'UNIVERSITY', 'HCMUTE', 'Trường...', NULL, TRUE);
                                         ↑ parent_id   ↑ description

-- ĐÚNG:
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('UNIVERSITY', 'UNIVERSITY', 'Trường...', 'HCMUTE', NULL, TRUE);
                              ↑ description   ↑ parent_id
```

**Đã sửa tất cả INSERT statements trong V7.**

---

## 🚀 **BƯỚC SỬA TRÊN RAILWAY (2 CÁCH)**

### **Cách 1: Xóa V7 Failed Record (NHANH NHẤT)**

**Vào Railway MySQL:**
1. Railway Dashboard → MySQL service → Connect
2. Hoặc dùng MySQL client connect tới Railway DB

**Chạy SQL:**
```sql
-- 1. Check V7 status
SELECT version, description, installed_rank, success 
FROM flyway_schema_history 
WHERE version = '7';

-- 2. Delete V7 failed record
DELETE FROM flyway_schema_history WHERE version = '7';

-- 3. Verify deleted
SELECT version, description, installed_rank, success 
FROM flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 5;
```

**Sau đó:**
- Push code đã fix lên GitHub
- Railway auto-redeploy → Flyway sẽ chạy lại V7 (lần này succeed)

---

### **Cách 2: Flyway Repair (CÓ THỂ KHÔNG WORK VÌ RAILWAY)**

**Tạo script repair local:**

```bash
# Local (nếu connect được tới Railway MySQL)
./mvnw flyway:repair -Dflyway.url=jdbc:mysql://HOST:PORT/railway -Dflyway.user=root -Dflyway.password=XXX
```

**Hoặc thêm endpoint repair vào app** (không khuyến nghị production).

---

## 📋 **HƯỚNG DẪN CHI TIẾT CÁCH 1 (KHUYẾN NGHỊ)**

### **Step 1: Connect Railway MySQL**

**Option A: Railway CLI**
```bash
railway connect mysql
```

**Option B: MySQL Workbench / DBeaver**
```
Host: mysql.railway.internal (or public host from Railway)
Port: 3306
User: root
Password: <from Railway variables>
Database: railway
```

---

### **Step 2: Kiểm Tra V7 Status**

```sql
SELECT 
    installed_rank,
    version,
    description,
    script,
    success,
    execution_time,
    installed_on
FROM flyway_schema_history
WHERE version = '7';
```

**Expected output:**
```
version: 7
description: fix org units 12 faculties hcmute
success: 0  ← Failed!
```

---

### **Step 3: Xóa V7 Failed Record**

```sql
-- Delete failed V7
DELETE FROM flyway_schema_history WHERE version = '7';

-- Verify
SELECT COUNT(*) FROM flyway_schema_history WHERE version = '7';
-- Should return 0
```

---

### **Step 4: Kiểm Tra Partial Data**

V7 có thể đã insert một số data trước khi fail. Check:

```sql
-- Check org_units
SELECT COUNT(*), type FROM org_units GROUP BY type;

-- If has partial data from failed V7, clean up:
DELETE FROM user_org_units WHERE org_unit_id > 0;
DELETE FROM org_units WHERE id > 0;
```

---

### **Step 5: Push Fixed Code**

```bash
# Code đã fix rồi, chỉ cần push
git push origin main
```

Railway sẽ:
1. Auto-detect new commit
2. Rebuild Docker image
3. Start app
4. Flyway detect V7 missing → Run V7 (fixed version)
5. Success! ✅

---

### **Step 6: Verify Deploy Success**

**Check Railway logs:**
```
Railway → Deployments → Latest → View Logs

Tìm:
"Flyway migration V7 SUCCESS"
"Started UteTrainingPointsSystemApiApplication"
```

**Check database:**
```sql
-- Check V7 now success
SELECT version, success FROM flyway_schema_history WHERE version = '7';
-- success should be 1

-- Check org_units
SELECT COUNT(*), type FROM org_units GROUP BY type;
-- Should show:
-- UNIVERSITY: 1
-- INSTITUTE: 3
-- FACULTY: 12
-- OFFICE: 5
-- UNION_SCHOOL: 2
-- UNION_FACULTY: 5
-- CLUB: 4
```

---

## 🎯 **TÓM TẮT NHANH**

**Em chỉ cần 3 bước:**

1. ✅ **Connect Railway MySQL** (Railway CLI hoặc MySQL client)

2. ✅ **Xóa V7 failed:**
   ```sql
   DELETE FROM flyway_schema_history WHERE version = '7';
   ```

3. ✅ **Push code** (đã fix rồi):
   ```bash
   git push origin main
   ```

**Railway sẽ chạy lại V7 → Success!** 🎉

---

## 📚 **TROUBLESHOOTING**

### **Lỗi: "Cannot connect to Railway MySQL"**

**Check connection:**
```sql
-- Get MySQL credentials from Railway
Railway → MySQL service → Variables tab

MYSQLHOST=xxx.railway.internal
MYSQLPORT=3306
MYSQLUSER=root
MYSQLPASSWORD=xxx
MYSQLDATABASE=railway
```

**Test connection:**
```bash
mysql -h xxx.railway.internal -P 3306 -u root -p
# Enter password
```

---

### **Lỗi: "V7 still fails after fix"**

**Check V7 SQL syntax:**
```bash
# Test V7 locally
mysql -u root -p railway < src/main/resources/db/migration/V7__fix_org_units_12_faculties_hcmute.sql
```

**Check column order:**
```sql
DESC org_units;
-- Verify column order matches INSERT
```

---

### **Lỗi: "Flyway checksum mismatch"**

**Force V7 repair:**
```sql
-- Update checksum (last resort)
UPDATE flyway_schema_history 
SET checksum = NULL 
WHERE version = '7';

-- Or delete and re-run
DELETE FROM flyway_schema_history WHERE version = '7';
```

---

## ✅ **STATUS**

- ✅ V7 SQL fixed (column order corrected)
- ✅ Code committed
- ⏳ Need to delete V7 from Railway DB
- ⏳ Then push → auto-redeploy → success

---

**Last Updated:** January 10, 2026  
**Status:** Ready for Railway repair

