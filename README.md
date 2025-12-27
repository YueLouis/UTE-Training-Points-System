# UTE Training Points System - API

Backend Spring Boot for Training Points System (Events + Registrations + Points).

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Web, Spring Data JPA, Validation
- MySQL
- Lombok
- Swagger/OpenAPI (springdoc)

## Run locally
### 1) Configure database
Update `src/main/resources/application.properties` (or `application.yml`):

```
properties
spring.datasource.url=jdbc:mysql://localhost:3306/<db_name>?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=<username>
spring.datasource.password=<password>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 2) Start server
```
bash
mvn spring-boot:run
```
Server runs at:
```
http://localhost:8080
```
Swagger UI
```
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
```

---

# Main Flow (Demo)
## A) Event ATTENDANCE (Offline)
### 1. Create event 
```
status = OPEN
eventMode = ATTENDANCE
```
### 2. Student registers for event

### 3. Student check-in

### 4. Student check-out
```
→ Registration becomes COMPLETED
→ Training points are awarded
```
### 5. Event can auto-close when reaching maxParticipants (if enabled)

---

## B) Event ONLINE (Survey)
### 1. Create event with:
```
status = OPEN
eventMode = ONLINE
surveyUrl required
```

### 2. Student completes survey

### 3. Registration becomes COMPLETED
```
→ Training points are awarded
```
### 4. Event can auto-close when reaching maxParticipants (if enabled)

---

# API Endpoints (Core)
## 1. Event APIs
```
GET /api/events

GET /api/events?studentId={studentId}

GET /api/events/{id}

GET /api/events/by-category/{categoryId}

GET /api/events/by-category/{categoryId}?studentId={studentId}

POST /api/events

PUT /api/events/{id}

POST /api/events/{id}/close

DELETE /api/events/{id}
```

## 2. Event Registrations
```
POST /api/event-registrations

GET /api/event-registrations/by-student/{studentId}

GET /api/event-registrations/by-event/{eventId}

PUT /api/event-registrations/{id}/cancel

PUT /api/event-registrations/{eventId}/checkin/{studentId} (ATTENDANCE only)

PUT /api/event-registrations/{eventId}/checkout/{studentId} (ATTENDANCE only)

PUT /api/event-registrations/{eventId}/complete-survey/{studentId} (ONLINE only)
```

---

## Notes
```
ONLINE events do NOT allow check-in/checkout.

ATTENDANCE events use check-in/checkout.

Error responses are standardized by GlobalExceptionHandler (400/404/409/500).
```

---

## Business Rules
```
ONLINE events do not allow check-in / check-out

ATTENDANCE events require check-in before check-out

Training points are awarded once per event

Duplicate registrations are prevented

Events can auto-close when full (if configured)
```

---

## Error Handling
```
All errors are standardized via GlobalExceptionHandler:

400 – Bad Request

404 – Not Found

409 – Conflict (duplicate registration / points already awarded)

500 – Internal Server Error
```

---

Pre-push Checklist
```
✅ mvn clean test runs successfully

✅ Swagger UI accessible

✅ ATTENDANCE flow tested (register → check-in → check-out)

✅ ONLINE flow tested (complete survey)

✅ .gitignore excludes target/
```

---

This repository contains Backend API only.
Frontend (Android / Web) is maintained in a separate repository.
