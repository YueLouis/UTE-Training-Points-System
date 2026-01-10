# 🎓 UTE Training Points - Advanced Implementation Plan

## ✅ COMPLETED (Phase 1-2)

### Database Migrations
- ✅ **V4__upgrade_semesters_and_cumulative.sql**
  - Added `academic_year` to semesters
  - Created `student_points_cumulative` (CTXH/CDNN with cap)
  - Upgraded `point_transactions` (scope + point_code)
  - Seed initial cumulative data for all students
  
- ✅ **V5__add_org_units_and_rbac.sql**
  - Created `org_units` (Khoa, Phòng, Đoàn, CLB...)
  - Created `user_org_units` (many-to-many)
  - Created `permissions` table
  - Created `roles` table
  - Created `role_permissions` (many-to-many)
  - Created `user_roles_scoped` (user + role + scope)
  - Upgraded `events` (org_unit_id + scope_level)
  - Added `faculty_code` to users
  
- ✅ **V6__seed_org_units_roles_permissions.sql**
  - Seed 50+ org_units (6 khoa, 4 phòng, đoàn, CLB...)
  - Seed 14 roles (SUPER_ADMIN, FACULTY_EVENT_ADMIN, ADVISOR...)
  - Seed 30+ permissions (EVENT.CREATE, DRL.FINALIZE...)
  - Assign permissions to roles
  - Migrate existing users to new RBAC

### Entities Created
- ✅ `StudentPointsCumulative.java`
- ✅ `OrgUnit.java`
- ✅ `Role.java`
- ✅ `Permission.java`
- ✅ `UserRoleScoped.java`

---

## 📋 REMAINING IMPLEMENTATION (Phase 3-7)

### Phase 3: Repositories & Services

**Need to create:**

#### Repositories
```java
StudentPointsCumulativeRepository
OrgUnitRepository
RoleRepository
PermissionRepository
UserRoleScopedRepository
```

#### Services
```java
// Points Service (upgrade)
- calculateDRLSemester(studentId, semesterId)
- calculateDRLYear(studentId, academicYear)
- calculateDRLTotal(studentId)
- addCTXH(studentId, points) // with cap 40
- addCDNN(studentId, points) // with cap 8

// RBAC Service
- hasPermission(userId, permissionCode, scopeOrgUnitId)
- getUserRoles(userId)
- getUserPermissions(userId)
- canManageEvent(userId, eventId)
```

---

### Phase 4: API Endpoints

#### Enhanced Points Summary
```
GET /api/points/summary/{studentId}?semesterId=1

Response:
{
  "drl": {
    "semester": 75,        // DRL kỳ hiện tại
    "year": 78,            // DRL năm (avg 2 kỳ)
    "total": 76,           // DRL toàn khóa (avg all kỳ)
    "semesterRank": "Khá"
  },
  "ctxh": {
    "current": 25,
    "max": 40,
    "percentage": 62.5
  },
  "cdnn": {
    "current": 5,
    "max": 8,
    "percentage": 62.5
  }
}
```

#### Transactions History
```
GET /api/points/transactions?studentId=&scope=&pointCode=&page=0&size=20

Response: PageResponse<TransactionDTO>
```

#### RBAC Endpoints
```
GET /api/rbac/my-roles
GET /api/rbac/my-permissions
GET /api/rbac/can-manage-event/{eventId}
POST /api/rbac/assign-role (admin only)
```

---

### Phase 5: Business Logic Updates

#### Award Points Logic
```java
// When admin approves event completion:

if (eventPointType == DRL) {
    // Update student_semester_summary
    updateSemesterDRL(studentId, semesterId, points);
    
    // Create transaction: scope=SEMESTER
    createTransaction(studentId, semesterId, "DRL", points, "SEMESTER");
    
} else if (eventPointType == CTXH) {
    // Get current cumulative
    var cumulative = getCumulative(studentId, "CTXH");
    
    // Add with cap
    int newPoints = Math.min(cumulative.current + points, 40);
    cumulative.setCurrentPoints(newPoints);
    save(cumulative);
    
    // Create transaction: scope=CUMULATIVE
    createTransaction(studentId, null, "CTXH", points, "CUMULATIVE");
    
} else if (eventPointType == CDNN) {
    // Similar to CTXH but cap = 8
    var cumulative = getCumulative(studentId, "CDNN");
    int newPoints = Math.min(cumulative.current + points, 8);
    cumulative.setCurrentPoints(newPoints);
    save(cumulative);
    
    createTransaction(studentId, null, "CDNN", points, "CUMULATIVE");
}
```

#### DRL Calculation
```java
// DRL Year
public Double calculateDRLYear(Long studentId, String academicYear) {
    List<Semester> semesters = semesterRepo.findByAcademicYear(academicYear);
    
    List<Double> scores = semesters.stream()
        .map(s -> summaryRepo.findByStudentIdAndSemesterId(studentId, s.getId()))
        .filter(Optional::isPresent)
        .map(s -> s.get().getTotalDrl())
        .collect(Collectors.toList());
    
    if (scores.isEmpty()) return null;
    
    return scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
}

// DRL Total
public Double calculateDRLTotal(Long studentId) {
    List<StudentSemesterSummary> allSemesters = 
        summaryRepo.findByStudentId(studentId);
    
    if (allSemesters.isEmpty()) return null;
    
    return allSemesters.stream()
        .mapToDouble(StudentSemesterSummary::getTotalDrl)
        .average()
        .orElse(0.0);
}
```

---

### Phase 6: Security & RBAC Implementation

#### Spring Security Update
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/events/**").permitAll() // read
            .requestMatchers(HttpMethod.POST, "/api/events")
                .hasAnyRole("SUPER_ADMIN", "SCHOOL_EVENT_ADMIN", 
                           "FACULTY_EVENT_ADMIN", "YOUTH_UNION_SCHOOL")
            .requestMatchers("/api/points/summary/**").authenticated()
            .requestMatchers("/api/rbac/**").authenticated()
            // ...
        );
    }
}
```

#### Custom Permission Check
```java
@Service
public class RBACService {
    
    public boolean hasPermission(Long userId, String permissionCode, Long scopeOrgId) {
        // Get user roles (with scope)
        List<UserRoleScoped> userRoles = userRoleScopedRepo.findByUserId(userId);
        
        for (UserRoleScoped ur : userRoles) {
            // Check if role has permission
            boolean hasP = rolePermissionRepo.existsByRoleIdAndPermission_Code(
                ur.getRoleId(), permissionCode
            );
            
            if (!hasP) continue;
            
            // Check scope match
            if (scopeOrgId == null || ur.getScopeOrgUnitId() == null) {
                return true; // Global permission
            }
            
            if (Objects.equals(ur.getScopeOrgUnitId(), scopeOrgId)) {
                return true; // Scoped permission match
            }
        }
        
        return false;
    }
}
```

---

### Phase 7: Testing & Verification

#### Unit Tests
```java
// Test cumulative cap
@Test
void testCTXHCap() {
    addCTXH(studentId, 30); // current = 30
    addCTXH(studentId, 15); // should cap at 40, not 45
    
    var c = getCumulative(studentId, "CTXH");
    assertEquals(40, c.getCurrentPoints());
}

// Test DRL year calculation
@Test
void testDRLYear() {
    // HK1: 80, HK2: 90
    double year = calculateDRLYear(studentId, "2025-2026");
    assertEquals(85.0, year, 0.01);
}
```

#### Integration Tests
```java
// Test approve event → cumulative update
@Test
void testApproveEventCTXH() {
    // Create CTXH event (5 points)
    var event = createEvent(pointType=CTXH, points=5);
    
    // Student registers & completes
    register(studentId, eventId);
    checkout(eventId, studentId);
    
    // Check cumulative updated
    var c = getCumulative(studentId, "CTXH");
    assertEquals(5, c.getCurrentPoints());
    
    // Check transaction created
    var tx = getTransaction(studentId, event.getId());
    assertEquals("CUMULATIVE", tx.getScope());
    assertEquals("CTXH", tx.getPointCode());
}
```

---

## 🎯 Deployment Checklist

### Railway Setup
1. ✅ Backup current DB
2. ✅ Deploy new code (with migrations V4, V5, V6)
3. ✅ Run migrations (auto via Flyway)
4. ✅ Verify:
   - `org_units` has 50+ rows
   - `roles` has 14 rows
   - `permissions` has 30+ rows
   - All students have 2 cumulative rows (CTXH, CDNN)
5. ✅ Smoke test:
   - Login as student → see new summary format
   - Login as FACULTY_EVENT_ADMIN → create event (should check scope)
   - Approve CTXH event → cumulative updated

---

## 📚 Documentation Updates Needed

### For Presentation/Report

**CHƯƠNG 4: Backend Implementation (Update)**

Add sections:
- **4.9 Advanced RBAC** (10+ roles, org_units, scoped permissions)
- **4.10 Points Calculation** (DRL kỳ/năm/toàn khóa, CTXH/CDNN cumulative)
- **4.11 Organizational Structure** (Khoa, Phòng, Đoàn mapping)

**CHƯƠNG 5: Kết luận (Update)**

Add achievements:
- ✅ Chuẩn nghiệp vụ trường (RBAC theo đơn vị)
- ✅ DRL tính theo kỳ/năm/toàn khóa
- ✅ CTXH/CDNN tích lũy có trần (40/8)
- ✅ Phân quyền chi tiết (14 roles, 30+ permissions)

---

## 🚀 Next Steps (Priority Order)

1. **Create Repositories** (5 files) - 30 min
2. **Update Semester Entity** (add academicYear field) - 5 min
3. **Create/Update PointService** (DRL calculations) - 1 hour
4. **Create RBACService** (permission checks) - 1 hour
5. **Update PointController** (new summary format) - 30 min
6. **Create RBACController** (my-roles, my-permissions) - 30 min
7. **Write Tests** (10+ test cases) - 2 hours
8. **Update Documentation** (README, IMPLEMENTATION_SUMMARY) - 1 hour

**Total Estimate**: ~6-7 hours remaining work

---

## 💡 Key Design Decisions

### Why org_units?
- **Scalability**: Easy to add new Khoa/Phòng/CLB without code change
- **Scope Control**: "Đoàn khoa CNTT" can only create events for CNTT faculty
- **Real-world mapping**: Matches actual university structure

### Why separate cumulative table?
- **Performance**: Don't need to SUM transactions every time
- **Cap enforcement**: Easy to check current <= max
- **Audit trail**: Transactions still keep history

### Why scope in transactions?
- **Reporting**: Easy to separate DRL kỳ vs CTXH/CDNN tích lũy
- **Business logic**: DRL affects semester summary, CTXH/CDNN affects cumulative

---

## 📊 Final Database Schema

```
Total Tables: 20+ (up from 10)

New:
- student_points_cumulative
- org_units
- user_org_units
- roles
- permissions
- role_permissions
- user_roles_scoped

Upgraded:
- semesters (+ academic_year)
- events (+ org_unit_id, scope_level)
- users (+ faculty_code)
- point_transactions (+ scope, point_code)
```

---

## ✅ Summary

**What's Done:**
- ✅ 3 new migrations (V4, V5, V6)
- ✅ 5 new entities (Cumulative, OrgUnit, Role, Permission, UserRoleScoped)
- ✅ Database seeded with real UTE structure (6 khoa, 4 phòng, đoàn, CLB...)
- ✅ 14 roles defined (SUPER_ADMIN → CHECKIN_STAFF)
- ✅ 30+ permissions defined (EVENT.CREATE → REPORT.EXPORT)

**What's Next:**
- Implement repositories & services
- Update API endpoints
- Write comprehensive tests
- Deploy & verify

**Status**: 🟡 Database & entities ready, services/APIs in progress

---

Last Updated: 2026-01-10

