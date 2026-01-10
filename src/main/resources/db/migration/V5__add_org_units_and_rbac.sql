-- V5__add_org_units_and_rbac.sql
-- Create organizational units structure and advanced RBAC

-- 1. Create org_units table (Khoa, Phòng, Đoàn, CLB...)
CREATE TABLE IF NOT EXISTS org_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL COMMENT 'SCHOOL, FACULTY, OFFICE, UNION_SCHOOL, UNION_FACULTY, CLUB',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT 'CNTT, CTXH, ERO, DOAN_TRUONG...',
    name VARCHAR(255) NOT NULL COMMENT 'Tên đầy đủ',
    parent_id BIGINT NULL COMMENT 'Đơn vị cha (Đoàn khoa thuộc Khoa)',
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_org_parent FOREIGN KEY (parent_id) REFERENCES org_units(id) ON DELETE SET NULL,

    INDEX idx_org_type (type),
    INDEX idx_org_code (code),
    INDEX idx_org_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT 'Đơn vị tổ chức (Khoa, Phòng, Đoàn, CLB)';

-- 2. Create user_org_units (many-to-many)
CREATE TABLE IF NOT EXISTS user_org_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    org_unit_id BIGINT NOT NULL,
    position VARCHAR(100) COMMENT 'Trưởng ban, Cộng tác viên, Thành viên...',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_uou_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_uou_org FOREIGN KEY (org_unit_id) REFERENCES org_units(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_org UNIQUE (user_id, org_unit_id),

    INDEX idx_uou_user (user_id),
    INDEX idx_uou_org (org_unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT 'Người dùng thuộc đơn vị nào';

-- 3. Create permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE COMMENT 'EVENT.CREATE, DRL.FINALIZE...',
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) COMMENT 'EVENT, DRL, POINT, SYSTEM',
    description TEXT,

    INDEX idx_perm_code (code),
    INDEX idx_perm_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT 'Danh sách quyền hệ thống';

-- 4. Create roles table (thay vì hardcode)
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE COMMENT 'SUPER_ADMIN, FACULTY_EVENT_ADMIN...',
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_system BOOLEAN DEFAULT FALSE COMMENT 'Role hệ thống không được xóa',

    INDEX idx_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT 'Vai trò trong hệ thống';

-- 5. Create role_permissions (many-to-many)
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    CONSTRAINT unique_role_permission UNIQUE (role_id, permission_id),

    INDEX idx_rp_role (role_id),
    INDEX idx_rp_permission (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT 'Role có quyền gì';

-- 6. Create user_roles_scoped (user + role + scope)
CREATE TABLE IF NOT EXISTS user_roles_scoped (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    scope_org_unit_id BIGINT NULL COMMENT 'NULL = toàn trường, có ID = phạm vi đơn vị',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT COMMENT 'Admin nào gán',

    CONSTRAINT fk_urs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_urs_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_urs_scope FOREIGN KEY (scope_org_unit_id) REFERENCES org_units(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_role_scope UNIQUE (user_id, role_id, scope_org_unit_id),

    INDEX idx_urs_user (user_id),
    INDEX idx_urs_role (role_id),
    INDEX idx_urs_scope (scope_org_unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT 'User có role gì ở phạm vi nào';

-- 7. Upgrade events table: add org_unit_id
ALTER TABLE events
ADD COLUMN org_unit_id BIGINT NULL COMMENT 'Đơn vị tổ chức',
ADD COLUMN scope_level VARCHAR(50) DEFAULT 'SCHOOL' COMMENT 'SCHOOL, FACULTY, CLUB',
ADD CONSTRAINT fk_event_org FOREIGN KEY (org_unit_id) REFERENCES org_units(id) ON DELETE SET NULL;

CREATE INDEX idx_event_org ON events(org_unit_id);
CREATE INDEX idx_event_scope ON events(scope_level);

-- 8. Add faculty to users (for students)
ALTER TABLE users
ADD COLUMN faculty_code VARCHAR(50) COMMENT 'Mã khoa (CNTT, COKHI, DIEN...)';

CREATE INDEX idx_user_faculty ON users(faculty_code);

