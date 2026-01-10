# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- JWT authentication with access & refresh tokens
- Spring Security with RBAC (ADMIN/STUDENT)
- Bucket4j rate limiting for auth endpoints
- Flyway database migrations
- Standardized API response envelope
- Pagination, sorting, filtering for list endpoints
- Transaction boundaries for checkout → award → notify
- Idempotency checks for point awards
- Actuator health checks & metrics
- Correlation ID logging
- CI/CD pipeline with GitHub Actions
- Unit & integration tests

### Security
- Password policy enforcement (min 8 chars, alphanumeric)
- OTP attempt limiting (anti-brute-force)
- JWT secret moved to environment variables

## [0.1.0] - 2026-01-10

### Added
- Initial release
- Token-based authentication (MOCK)
- BCrypt password hashing
- OTP email verification (SHA-256)
- Event management (CRUD)
- Event registration (online/offline)
- Check-in/Check-out workflow
- Point transaction system
- Student semester summary & ranking
- Notification system
- Role-based access (STUDENT/ADMIN)
- Swagger API documentation
- Railway deployment

### Security
- BCrypt password storage
- SHA-256 OTP hashing
- One-time use OTP with expiration
- Basic RBAC checks

---

**Legend:**
- `Added` - New features
- `Changed` - Changes in existing functionality
- `Deprecated` - Soon-to-be removed features
- `Removed` - Removed features
- `Fixed` - Bug fixes
- `Security` - Security improvements

