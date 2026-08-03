-- Align the Flyway-managed schema with fields and uniqueness assumptions used by JPA.
-- Dynamic statements keep this migration safe for databases that previously relied on
-- Hibernate ddl-auto=update and may already contain some of these columns or indexes.

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE users ADD COLUMN updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'updated_at'
);
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE semesters ADD COLUMN code VARCHAR(50) NULL',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'semesters' AND column_name = 'code'
);
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

UPDATE semesters
SET code = CONCAT('SEM-', id)
WHERE code IS NULL OR code = '';

ALTER TABLE semesters MODIFY COLUMN code VARCHAR(50) NOT NULL;

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'CREATE UNIQUE INDEX uq_semesters_code ON semesters(code)',
        'SELECT 1')
    FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'semesters' AND index_name = 'uq_semesters_code'
);
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE semesters ADD COLUMN status INT NOT NULL DEFAULT 1',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'semesters' AND column_name = 'status'
);
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE point_types ADD COLUMN description TEXT NULL',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'point_types' AND column_name = 'description'
);
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'CREATE UNIQUE INDEX uq_users_phone ON users(phone)',
        'SELECT 1')
    FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'users' AND index_name = 'uq_users_phone'
);
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'CREATE UNIQUE INDEX uq_event_registration_student ON event_registrations(event_id, student_id)',
        'SELECT 1')
    FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'event_registrations' AND index_name = 'uq_event_registration_student'
);
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'CREATE UNIQUE INDEX uq_password_reset_token_hash ON password_reset_tokens(token_hash)',
        'SELECT 1')
    FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'password_reset_tokens' AND index_name = 'uq_password_reset_token_hash'
);
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;
