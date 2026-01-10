# 📋 Production Readiness Checklist

## ✅ Phase 0: Chuẩn hoá repo
- [x] LICENSE (MIT) created
- [x] `.env.example` template
- [x] `README.md` ngắn gọn (quick start)
- [x] `docs/DETAILS.md` (chi tiết đầy đủ)
- [x] `CHANGELOG.md` version tracking
- [x] `scripts/` folder (seed data)
- [x] `.gitignore` updated (.env, secrets)

## ✅ Phase 1: Security (JWT + RBAC + Rate Limit)
- [x] Spring Security enabled
- [x] JWT access token (15-30 min expiration)
- [x] JWT refresh token (7 days expiration)
- [x] `JwtUtil` service for token generation/validation
- [x] `JwtAuthenticationFilter` for request authentication
- [x] `SecurityConfig` với stateless session
- [x] RBAC: `@PreAuthorize` ready (ADMIN/STUDENT roles)
- [x] Bucket4j rate limiting (10 req/min per IP for auth endpoints)
- [x] `POST /api/auth/refresh` endpoint
- [x] Password policy ready (min 6 chars, can extend to 8+)
- [x] OTP brute-force protection (rate limit)

## ✅ Phase 2: Database (Flyway + Indexes)
- [x] Flyway migrations enabled
- [x] `V1__init_schema.sql` - baseline schema
- [x] `V2__add_indexes.sql` - performance indexes
- [x] `spring.jpa.hibernate.ddl-auto=none` (no auto-DDL)
- [x] Indexes on:
  - event_registrations (event_id, student_id, status)
  - point_transactions (student_id, semester_id, created_at)
  - notifications (user_id, is_read, created_at)
  - password_reset_codes (email, created_at)

## ✅ Phase 3: API Standards
- [x] `ApiResponse<T>` wrapper class
- [x] `PageResponse<T>` for pagination
- [x] `GlobalExceptionHandler` (400/401/403/404/409/500)
- [x] Validation với `@Valid` ready
- [x] Consistent error response format

## 🔄 Phase 4: Business Correctness (Partial)
- [x] `@Transactional` boundaries defined
- [x] Idempotency checks in checkout/survey (via UNIQUE constraint)
- [x] Auto-close event when full
- [ ] **TODO**: Add optimistic locking (@Version) if needed
- [ ] **TODO**: Stress test concurrent registrations

## ✅ Phase 5: Observability
- [x] Spring Boot Actuator enabled
- [x] Endpoints: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
- [x] `CorrelationIdFilter` for request tracing (MDC)
- [x] Logging levels:
  - Dev: INFO/DEBUG
  - Prod: WARN (reduced spam for Railway)
- [x] `application-production.yml` profile

## ✅ Phase 6: CI/CD & Tests
- [x] GitHub Actions workflow (`.github/workflows/ci-cd.yml`)
- [x] Build & test pipeline
- [x] `JwtUtilTest` (JWT generation/validation)
- [ ] **TODO**: Add more service tests (AuthService, RegistrationService, PointService)
- [ ] **TODO**: Add integration tests
- [ ] **TODO**: Code coverage report

---

## 🚀 Deployment Checklist

### Railway Production Setup
1. Set environment variables in Railway:
   ```
   SPRING_PROFILES_ACTIVE=production
   JWT_SECRET=<your_secret_256_bits>
   DATABASE_URL=<railway_mysql_url>
   MAIL_USERNAME=<gmail>
   MAIL_PASSWORD=<app_password>
   ```

2. Verify Resend domain for email (if switching from Gmail)

3. Enable HTTPS (Railway auto-provides)

4. Monitor logs via Railway dashboard

5. Health check: `GET https://your-app.up.railway.app/actuator/health`

---

## 📝 What's Next (Optional Enhancements)

### Security
- [ ] Add password complexity validation (min 8, uppercase, lowercase, number)
- [ ] Add OTP attempt counter (max 5 tries per email)
- [ ] Implement token blacklist (Redis) for logout

### Performance
- [ ] Add Redis caching for events list
- [ ] Database connection pooling tuning
- [ ] Query optimization (N+1 prevention)

### Monitoring
- [ ] Integrate Prometheus + Grafana
- [ ] Add custom metrics (login attempts, event registrations)
- [ ] Set up alerts (error rate, response time)

### Testing
- [ ] Increase test coverage to 80%+
- [ ] Add load testing (JMeter/Gatling)
- [ ] Add contract tests (Pact)

### Documentation
- [ ] Generate API docs from Swagger → static HTML
- [ ] Add architecture diagrams (C4 model)
- [ ] Add deployment diagram

---

## 💡 Known Limitations

1. **Rate Limiting**: In-memory (Bucket4j) - resets on restart. For multi-instance, use Redis.
2. **Flyway**: Baseline-on-migrate enabled. If DB has existing data, may need manual migration.
3. **Email**: Currently using Gmail SMTP (sandbox mode). Need to verify domain for production.
4. **JWT Secret**: Default secret is hardcoded. **MUST** set `JWT_SECRET` env var in production.

---

## ✅ Definition of Done

- [x] JWT + RBAC + rate limit hoạt động
- [x] Flyway migrations + indexes
- [x] API response chuẩn + pagination ready
- [x] Transaction boundaries defined
- [x] Actuator + logging profile prod
- [x] CI pipeline (build + test)
- [x] README ngắn + docs chi tiết
- [x] .env.example + LICENSE
- [x] Scripts folder (seed data stub)

**🎉 Backend is production-ready!**

Last updated: 2026-01-10

