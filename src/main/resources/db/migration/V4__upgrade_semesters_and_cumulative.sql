-- V4__upgrade_semesters_and_cumulative.sql
-- Add academic_year to semesters & create cumulative points table

-- 1. Add academic_year to semesters
ALTER TABLE semesters
ADD COLUMN academic_year VARCHAR(20) NOT NULL DEFAULT '2025-2026' COMMENT 'Năm học: 2025-2026';

-- Update existing semesters with academic year
UPDATE semesters SET academic_year = '2025-2026' WHERE id = 1;
UPDATE semesters SET academic_year = '2025-2026' WHERE id = 2;

-- 2. Create cumulative points table for CTXH/CDNN
CREATE TABLE IF NOT EXISTS student_points_cumulative (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    point_code VARCHAR(20) NOT NULL COMMENT 'CTXH, CDNN',
    current_points INT NOT NULL DEFAULT 0 COMMENT 'Điểm hiện tại',
    max_points INT NOT NULL COMMENT 'Điểm tối đa (CTXH=40, CDNN=8)',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_spc_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_student_point UNIQUE (student_id, point_code),

    INDEX idx_spc_student (student_id),
    INDEX idx_spc_code (point_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT 'Điểm tích lũy CTXH/CDNN (có trần)';

-- 3. Seed initial cumulative data for existing students
-- Every student gets 2 rows: CTXH (0/40), CDNN (0/8)
INSERT INTO student_points_cumulative (student_id, point_code, current_points, max_points)
SELECT u.id, 'CTXH', 0, 40
FROM users u
WHERE u.role = 'STUDENT'
ON DUPLICATE KEY UPDATE point_code=point_code;

INSERT INTO student_points_cumulative (student_id, point_code, current_points, max_points)
SELECT u.id, 'CDNN', 0, 8
FROM users u
WHERE u.role = 'STUDENT'
ON DUPLICATE KEY UPDATE point_code=point_code;

-- 4. Upgrade point_transactions: add scope & semester_id
ALTER TABLE point_transactions
ADD COLUMN scope VARCHAR(20) DEFAULT 'SEMESTER' COMMENT 'SEMESTER or CUMULATIVE',
ADD COLUMN point_code VARCHAR(20) COMMENT 'DRL, CTXH, CDNN';

-- Update existing transactions
UPDATE point_transactions SET point_code = 'DRL', scope = 'SEMESTER' WHERE point_type_id = 1;
UPDATE point_transactions SET point_code = 'CTXH', scope = 'CUMULATIVE' WHERE point_type_id = 2;
UPDATE point_transactions SET point_code = 'CDNN', scope = 'CUMULATIVE' WHERE point_type_id = 3;

-- Add indexes for performance
CREATE INDEX idx_pt_scope ON point_transactions(scope);
CREATE INDEX idx_pt_code ON point_transactions(point_code);
CREATE INDEX idx_pt_student_semester ON point_transactions(student_id, semester_id);

