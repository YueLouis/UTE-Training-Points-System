# 🚀 Hướng Dẫn Triển Khai (Deployment Guide)

## 📋 Mục Lục
1. [Local Development Setup](#local-development-setup)
2. [Railway Deployment](#railway-deployment)
3. [Environment Variables](#environment-variables)
4. [Database Migration](#database-migration)
5. [Health Checks](#health-checks)
6. [Troubleshooting](#troubleshooting)

---

## Local Development Setup

### Prerequisites
- Java 17 (JDK)
- Maven 3.8+
- MySQL 8.0+
- Git

### Step 1: Clone & Configure

```bash
git clone https://github.com/YueLouis/UTE-Training-Points-System.git
cd "UTE-Training-Points-System"

# Copy environment template
cp .env.example .env

# Edit .env with your local MySQL credentials
nano .env
```

### Step 2: Database Setup

```bash
# Create MySQL database
mysql -u root -p << EOF
CREATE DATABASE training_points_db;
CREATE USER 'training_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON training_points_db.* TO 'training_user'@'localhost';
FLUSH PRIVILEGES;
EOF

# Update application.properties with credentials
# Or set environment variables:
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=training_points_db
export DB_USER=training_user
export DB_PASSWORD=your_password
```

### Step 3: Run Locally

```bash
# Build project
./mvnw clean install

# Run with dev profile
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=development"

# Or run JAR directly
java -jar target/ute-training-points-system-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=development
```

### Step 4: Access Application

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

---

## Railway Deployment

### Prerequisites
- Railway account (https://railway.app)
- GitHub account
- MySQL plugin enabled in Railway

### Step 1: Connect GitHub Repository

1. Log in to Railway dashboard
2. Create new project → "Deploy from GitHub"
3. Authorize GitHub & select repository
4. Railway auto-detects Spring Boot app

### Step 2: Configure Environment Variables

In Railway dashboard → Variables tab, set:

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=production

# Database (Railway provides these automatically if using Railway MySQL)
DATABASE_URL=mysql://user:password@host:port/dbname
DB_USER=<railway_mysql_user>
DB_PASSWORD=<railway_mysql_password>

# JWT Secret (Generate a strong one!)
JWT_SECRET=<your_secret_key_min_256_bits>
JWT_ACCESS_EXPIRATION=1800000
JWT_REFRESH_EXPIRATION=604800000

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<your_gmail@gmail.com>
MAIL_PASSWORD=<gmail_app_password>

# OTP
OTP_EXPIRATION_SECONDS=120

# Optional: Server Port
PORT=8080
```

### Step 3: Generate JWT Secret

```bash
# Linux/Mac
openssl rand -base64 32 | tr -d '\n'

# Windows PowerShell
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((Get-Random -Count 32 | ConvertTo-Hex))) | Cut -c1-256
```

### Step 4: Deploy

- Railway auto-deploys on every push to `main` branch
- Monitor deployment progress in Railway dashboard
- Check logs for any errors

```bash
# Push to trigger deployment
git add .
git commit -m "Deploy to production"
git push origin main
```

---

## Environment Variables

### Development (`application.properties`)

```properties
# Database (Local MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/training_points_db
spring.datasource.username=training_user
spring.datasource.password=your_password

# Logging (Verbose)
logging.level.root=INFO
logging.level.vn.hcmute=DEBUG

# Flyway
spring.flyway.enabled=true

# JWT
jwt.secret=dev_secret_key
```

### Production (`application-production.yml`)

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: none
  profiles:
    active: production
  flyway:
    enabled: true

logging:
  level:
    root: WARN
    vn.hcmute: INFO

jwt:
  secret: ${JWT_SECRET}
  access:
    expiration: ${JWT_ACCESS_EXPIRATION:1800000}
  refresh:
    expiration: ${JWT_REFRESH_EXPIRATION:604800000}

mail:
  host: ${MAIL_HOST}
  port: ${MAIL_PORT}
  username: ${MAIL_USERNAME}
  password: ${MAIL_PASSWORD}

server:
  port: ${PORT:8080}
```

---

## Database Migration

### Flyway Auto-Migration

On application startup, Flyway automatically:
1. Creates `flyway_schema_history` table
2. Runs pending migrations in order (`V1__init_schema.sql`, `V2__add_indexes.sql`, ...)
3. Validates existing schema

### Manual Migration (if needed)

```bash
# View migration status
./mvnw flyway:info

# Validate schema
./mvnw flyway:validate

# Clean & reset (DANGER: deletes all data)
./mvnw flyway:clean
./mvnw flyway:migrate
```

### Creating New Migration

Create file: `src/main/resources/db/migration/V3__add_new_table.sql`

```sql
-- V3__add_new_table.sql
CREATE TABLE my_new_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Then push code & deploy. Flyway will auto-run it.

---

## Health Checks

### Railway Health Endpoint

Railway automatically pings `/actuator/health`:

```bash
curl https://ute-training-points-system-production.up.railway.app/actuator/health

# Response:
{
  "status": "UP"
}
```

### Application Metrics

```bash
curl https://ute-training-points-system-production.up.railway.app/actuator/metrics

curl https://ute-training-points-system-production.up.railway.app/actuator/metrics/jvm.memory.used
```

### Database Connection Test

Check logs in Railway:
```
[INFO] HikariPool-1 - Starting...
[INFO] HikariPool-1 - Started. Validation query succeeded.
```

---

## Troubleshooting

### Issue: Database Connection Timeout

**Symptoms:**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Solutions:**
1. Verify `DATABASE_URL` format:
   ```
   mysql://username:password@host:3306/dbname
   ```
2. Check MySQL is running
3. Verify firewall allows connection
4. Test with MySQL client:
   ```bash
   mysql -h host -u user -p database_name
   ```

### Issue: JWT Token Invalid

**Symptoms:**
```
401 UNAUTHORIZED: Invalid refresh token
```

**Solutions:**
1. Verify `JWT_SECRET` is set (min 256 bits)
2. Generate new secret:
   ```bash
   openssl rand -base64 32
   ```
3. Update in Railway → redeploy

### Issue: Email Not Sending

**Symptoms:**
```
SMTP authentication failed / Connection refused
```

**Solutions:**
1. **Gmail**: Generate app password (not regular password)
   - Go to https://myaccount.google.com/apppasswords
   - Select "Mail" + "Other (custom name)"
   - Copy generated password → `MAIL_PASSWORD`
2. **Alternative email provider**: Update `MAIL_HOST`, `MAIL_PORT`, credentials
3. Check logs for actual error

### Issue: Flyway Migration Failed

**Symptoms:**
```
Migration V1__init_schema.sql failed
```

**Solutions:**
1. Check if table already exists
2. Review migration file syntax
3. Use `flyway:validate` to check issues
4. If DB is corrupted:
   ```bash
   # Warning: This deletes all data!
   ./mvnw flyway:clean
   ./mvnw flyway:migrate
   ```

### Issue: High Memory Usage / Slowness

**Symptoms:**
- Railway pod keeps restarting
- Requests timeout

**Solutions:**
1. Reduce logging verbosity:
   ```yaml
   logging.level.root=WARN
   ```
2. Enable connection pooling in `application.properties`:
   ```properties
   spring.datasource.hikari.maximum-pool-size=10
   spring.datasource.hikari.minimum-idle=2
   ```
3. Scale up Railway instance (pay-as-you-go)

---

## Monitoring & Logging

### View Logs in Railway

1. Go to Railway → Deployments tab
2. Click running deployment
3. View real-time logs

### View Metrics

```bash
# CPU/Memory usage
curl https://your-app/actuator/metrics

# Request count
curl https://your-app/actuator/metrics/http.server.requests

# Database connections
curl https://your-app/actuator/metrics/hikaricp.connections
```

### Correlation ID Tracking

All requests get a correlation ID in logs:
```
[correlationId: 3f8a9e2c-4d15-40a2-8b91-5c2d6e7f1a3b] GET /api/events
```

Use this to trace related log entries.

---

## Best Practices

✅ **DO:**
- Keep `JWT_SECRET` private (never commit)
- Use strong passwords for database
- Monitor logs regularly
- Backup database regularly
- Use HTTPS (Railway provides auto-SSL)

❌ **DON'T:**
- Hardcode secrets in code
- Use `ddl-auto=update` in production
- Disable CORS without reason
- Keep old migrations in git (archive after success)

---

## Rollback Plan

If deployment fails:

1. **Auto-rollback** (Railway):
   - Previous version auto-deployed if current fails
   - Check "Deployments" tab to revert

2. **Manual rollback**:
   ```bash
   git revert <commit_hash>
   git push origin main
   ```

3. **Database rollback**:
   - Flyway tracks migrations in `flyway_schema_history`
   - Cannot auto-rollback. Need manual SQL or restore backup.

---

## Support

- 📖 [Spring Boot Docs](https://spring.io/projects/spring-boot)
- 🚂 [Railway Docs](https://docs.railway.app)
- 🗄️ [Flyway Docs](https://flywaydb.org)
- 🔐 [JWT Security](https://tools.ietf.org/html/rfc7519)

Last updated: 2026-01-10

