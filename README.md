# UTE Training Points System API

Spring Boot backend for a university training-points and event-management course project. It provides JWT authentication, event registration, attendance, point summaries, notifications, password-reset links, MySQL persistence, Flyway migrations, and OpenAPI documentation for local development.

## Repository layout

- `backend` is the default branch and contains this API.
- `fe-integration` contains the Android client.
- [`frontend/README.md`](frontend/README.md) explains how to check out the Android branch in a separate worktree.

The two clients are not built together. The old backend gitlink for the Android app was removed because it had no `.gitmodules` configuration and cloned as an empty directory.

## Stack

- Java 17 and Spring Boot 3.5
- Spring Web, Security, Validation, and Data JPA
- MySQL 8 with Flyway migrations
- JWT access and refresh tokens
- Resend API for password-reset email
- H2, JUnit 5, Mockito, and MockMvc for tests
- Maven Wrapper, Docker, and GitHub Actions

## Run locally

Prerequisites: JDK 17 and MySQL 8. Docker Desktop can provide MySQL and the API together.

1. Copy `.env.example` to a local, ignored environment file and replace its placeholders.
2. Create an empty MySQL database.
3. Export the development variables, then run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Flyway applies migrations from `src/main/resources/db/migration`. Hibernate schema mutation is disabled; schema changes belong in a new migration.

For the local Docker setup:

```bash
docker compose up --build
```

The API listens on `http://localhost:8080`. In non-production profiles, Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` and health information at `http://localhost:8080/actuator/health`.

## Configuration

Development accepts safe local defaults for JWT signing and the reset-token pepper. Do not reuse those defaults outside a local machine. The `production` profile requires all security-sensitive values and fails fast when they are absent.

Required production variables:

```text
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:mysql://host:3306/database
DB_USER=database_user
DB_PASSWORD=database_password
JWT_SECRET=a_random_secret_of_at_least_32_bytes
RESEND_API_KEY=re_xxxxx
MAIL_FROM=noreply@example.com
RESET_PEPPER=a_separate_random_secret
RESET_FRONTEND_URL=https://frontend.example.com/reset-password
CORS_ALLOWED_ORIGINS=https://frontend.example.com
```

Swagger is disabled in the production profile. CORS is configured centrally; controller-level wildcard policies are intentionally not used. Only set `TRUST_FORWARDED_HEADERS=true` when the API is behind a trusted reverse proxy that replaces client-supplied forwarding headers.

## Main API areas

| Area | Base path | Notes |
|---|---|---|
| Authentication | `/api/auth` | Login, refresh, forgot password, reset password |
| Events | `/api/events` | Public reads; admin mutations |
| Categories | `/api/event-categories` | Public reads |
| Registrations | `/api/event-registrations` | Self-service actions; admin attendance operations |
| Points | `/api/points` | Students can read their own summary; admins can read all |
| Notifications | `/api/notifications` | Owner-or-admin access |
| Users | `/api/users` | Admin list; owner-or-admin detail |

Access tokens authenticate API requests. Refresh tokens are accepted only by `/api/auth/refresh`; they cannot authenticate protected endpoints. Password hashes and event survey secret codes are not serialized in API responses.

## Tests and CI

Run the complete build and test suite with:

```bash
./mvnw clean verify
```

The tests use the `test` profile and an in-memory H2 database, so they do not require a developer's MySQL credentials. Integration tests cover token types, profile access controls, and cross-user authorization. GitHub Actions runs the same Maven Wrapper command for pushes and pull requests targeting the default `backend` branch.

## Current limitations

- No public deployment is claimed by this repository. Configure and verify a deployment before adding a live-demo badge or URL.
- Rate limiting is in-memory and suitable for a single application instance; a distributed deployment should use a shared store or gateway policy.
- Authorization currently uses the legacy `users.role` value (`ADMIN` or `STUDENT`). The scoped RBAC tables exist in migrations but are not yet wired into request authorization.
- The Android client has its own build and test lifecycle on `fe-integration`.

## License

This project is distributed under the terms in [`LICENSE`](LICENSE).
