-- V6__seed_org_units_roles_permissions.sql
-- Seed initial data for org_units, roles, permissions

-- ========================================
-- 1. SEED ORG_UNITS (Đơn vị tổ chức)
-- ========================================

-- Cấp trường
INSERT INTO org_units (id, type, code, name, parent_id, description) VALUES
(1, 'SCHOOL', 'SCHOOL', 'Trường Đại học Sư phạm Kỹ thuật TP.HCM', NULL, 'Cấp trường');

-- Các khoa
INSERT INTO org_units (id, type, code, name, parent_id, description) VALUES
(10, 'FACULTY', 'CNTT', 'Khoa Công nghệ Thông tin', 1, 'Faculty of Information Technology'),
(11, 'FACULTY', 'COKHI', 'Khoa Cơ khí', 1, 'Mechanical Engineering'),
(12, 'FACULTY', 'DIEN', 'Khoa Điện - Điện tử', 1, 'Electrical & Electronics Engineering'),
(13, 'FACULTY', 'XDDD', 'Khoa Xây dựng - Điện dân dụng', 1, 'Civil & Domestic Electrical Engineering'),
(14, 'FACULTY', 'NN', 'Khoa Ngoại ngữ', 1, 'Foreign Languages'),
(15, 'FACULTY', 'CN_MAY', 'Khoa Công nghệ May - Thời trang', 1, 'Garment Technology & Fashion');

-- Phòng ban
INSERT INTO org_units (id, type, code, name, parent_id, description) VALUES
(20, 'OFFICE', 'CTSV', 'Phòng Công tác Sinh viên', 1, 'Student Affairs Office'),
(21, 'OFFICE', 'ERO', 'Phòng Quan hệ Doanh nghiệp', 1, 'Enterprise Relations Office'),
(22, 'OFFICE', 'DAOTAO', 'Phòng Đào tạo', 1, 'Training Office'),
(23, 'OFFICE', 'KHCN', 'Phòng Khoa học Công nghệ', 1, 'Science & Technology Office');

-- Đoàn - Hội cấp trường
INSERT INTO org_units (id, type, code, name, parent_id, description) VALUES
(30, 'UNION_SCHOOL', 'DOAN_TRUONG', 'Đoàn Thanh niên Cộng sản Hồ Chí Minh', 1, 'Communist Youth Union - School Level'),
(31, 'UNION_SCHOOL', 'HOI_TRUONG', 'Hội Sinh viên', 1, 'Student Association - School Level');

-- Đoàn - Hội cấp khoa (ví dụ CNTT)
INSERT INTO org_units (id, type, code, name, parent_id, description) VALUES
(40, 'UNION_FACULTY', 'DOAN_CNTT', 'Đoàn khoa CNTT', 10, 'Youth Union - Faculty of IT'),
(41, 'UNION_FACULTY', 'HOI_CNTT', 'Hội sinh viên khoa CNTT', 10, 'Student Association - Faculty of IT'),
(42, 'UNION_FACULTY', 'DOAN_COKHI', 'Đoàn khoa Cơ khí', 11, 'Youth Union - Mechanical Engineering'),
(43, 'UNION_FACULTY', 'DOAN_DIEN', 'Đoàn khoa Điện', 12, 'Youth Union - Electrical Engineering');

-- Câu lạc bộ
INSERT INTO org_units (id, type, code, name, parent_id, description) VALUES
(50, 'CLUB', 'CLB_TINH_NGUYEN', 'Câu lạc bộ Tình nguyện', 1, 'Volunteering Club'),
(51, 'CLUB', 'CLB_AN_SINH', 'Câu lạc bộ An sinh xã hội', 1, 'Social Welfare Club'),
(52, 'CLUB', 'CLB_AI', 'Câu lạc bộ Trí tuệ nhân tạo', 10, 'AI Club - Faculty of IT');

-- ========================================
-- 2. SEED ROLES
-- ========================================

INSERT INTO roles (id, code, name, description, is_system) VALUES
-- Super Admin
(1, 'SUPER_ADMIN', 'Quản trị viên hệ thống', 'Toàn quyền', TRUE),
(2, 'STUDENT', 'Sinh viên', 'Người dùng thường', TRUE),

-- Event Organizers
(10, 'SCHOOL_EVENT_ADMIN', 'Ban tổ chức cấp trường', 'Tạo/quản lý sự kiện cấp trường', FALSE),
(11, 'FACULTY_EVENT_ADMIN', 'Ban tổ chức cấp khoa', 'Tạo/quản lý sự kiện cấp khoa', FALSE),
(12, 'YOUTH_UNION_SCHOOL', 'Đoàn - Hội trường', 'Tạo sự kiện phong trào cấp trường', FALSE),
(13, 'YOUTH_UNION_FACULTY', 'Đoàn - Hội khoa', 'Tạo sự kiện phong trào cấp khoa', FALSE),
(14, 'ERO_EVENT_ADMIN', 'ERO - Quan hệ doanh nghiệp', 'Tạo sự kiện hội thảo/CDNN', FALSE),
(15, 'CLUB_EVENT_ADMIN', 'Quản lý Câu lạc bộ', 'Tạo sự kiện CLB', FALSE),

-- Reviewers/Approvers
(20, 'STUDENT_AFFAIRS_ADMIN', 'Phòng CTSV', 'Chốt điểm, cấu hình hệ thống', FALSE),
(21, 'ADVISOR', 'Cố vấn học tập', 'Duyệt DRL cấp lớp', FALSE),
(22, 'FACULTY_REVIEWER', 'Giáo vụ/CTSV khoa', 'Duyệt cấp khoa', FALSE),
(23, 'CHECKIN_STAFF', 'Nhân viên điểm danh', 'Check-in/out', FALSE),
(24, 'POINT_AUDITOR', 'Kiểm tra điểm', 'Xem log, báo cáo', FALSE);

-- ========================================
-- 3. SEED PERMISSIONS
-- ========================================

INSERT INTO permissions (id, code, name, category, description) VALUES
-- Event permissions
(1, 'EVENT.READ', 'Xem sự kiện', 'EVENT', 'Xem danh sách & chi tiết sự kiện'),
(2, 'EVENT.CREATE', 'Tạo sự kiện', 'EVENT', 'Tạo sự kiện mới'),
(3, 'EVENT.UPDATE', 'Sửa sự kiện', 'EVENT', 'Chỉnh sửa thông tin sự kiện'),
(4, 'EVENT.DELETE', 'Xóa sự kiện', 'EVENT', 'Xóa sự kiện'),
(5, 'EVENT.CLOSE', 'Đóng sự kiện', 'EVENT', 'Đóng sự kiện'),

-- Registration permissions
(10, 'REG.CREATE', 'Đăng ký sự kiện', 'REGISTRATION', 'Đăng ký tham gia sự kiện'),
(11, 'REG.CANCEL_SELF', 'Hủy đăng ký của mình', 'REGISTRATION', 'Hủy đăng ký'),
(12, 'REG.APPROVE', 'Duyệt đăng ký', 'REGISTRATION', 'Phê duyệt đăng ký sinh viên'),
(13, 'REG.REJECT', 'Từ chối đăng ký', 'REGISTRATION', 'Từ chối đăng ký'),

-- Attendance permissions
(20, 'CHECKIN.EXECUTE', 'Điểm danh vào', 'ATTENDANCE', 'Check-in sinh viên'),
(21, 'CHECKOUT.EXECUTE', 'Điểm danh ra', 'ATTENDANCE', 'Check-out sinh viên'),

-- Points permissions
(30, 'POINT.AWARD_AUTO', 'Cộng điểm tự động', 'POINT', 'Cộng điểm khi hoàn thành sự kiện'),
(31, 'POINT.AWARD_MANUAL', 'Cộng điểm thủ công', 'POINT', 'Cộng điểm thủ công cho sinh viên'),
(32, 'POINT.ADJUST', 'Điều chỉnh điểm', 'POINT', 'Sửa/trừ điểm'),
(33, 'POINT.VIEW_ALL', 'Xem điểm tất cả', 'POINT', 'Xem điểm mọi sinh viên'),
(34, 'POINT.VIEW_SELF', 'Xem điểm của mình', 'POINT', 'Sinh viên xem điểm cá nhân'),

-- DRL workflow permissions
(40, 'DRL.SUBMIT', 'Nộp DRL', 'DRL', 'Sinh viên nộp bảng DRL'),
(41, 'DRL.REVIEW_ADVISOR', 'Duyệt DRL (CVHT)', 'DRL', 'Cố vấn duyệt DRL'),
(42, 'DRL.REVIEW_FACULTY', 'Duyệt DRL (Khoa)', 'DRL', 'Khoa duyệt DRL'),
(43, 'DRL.FINALIZE', 'Chốt điểm cuối', 'DRL', 'CTSV chốt điểm học kỳ'),

-- System permissions
(50, 'SYSTEM.SETTINGS', 'Cấu hình hệ thống', 'SYSTEM', 'Cài đặt hệ thống'),
(51, 'USER.MANAGE', 'Quản lý người dùng', 'SYSTEM', 'Tạo/sửa/xóa user'),
(52, 'AUDIT.VIEW', 'Xem log', 'SYSTEM', 'Xem audit trail'),
(53, 'REPORT.EXPORT', 'Xuất báo cáo', 'SYSTEM', 'Xuất Excel/PDF');

-- ========================================
-- 4. ASSIGN PERMISSIONS TO ROLES
-- ========================================

-- SUPER_ADMIN: all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- STUDENT: basic permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
(2, 1),  -- EVENT.READ
(2, 10), -- REG.CREATE
(2, 11), -- REG.CANCEL_SELF
(2, 34), -- POINT.VIEW_SELF
(2, 40); -- DRL.SUBMIT

-- SCHOOL_EVENT_ADMIN: event management
INSERT INTO role_permissions (role_id, permission_id) VALUES
(10, 1), (10, 2), (10, 3), (10, 4), (10, 5), -- Event CRUD
(10, 12), (10, 13), -- Approve/reject registration
(10, 20), (10, 21); -- Checkin/out

-- FACULTY_EVENT_ADMIN: same as school but scoped
INSERT INTO role_permissions (role_id, permission_id) VALUES
(11, 1), (11, 2), (11, 3), (11, 4), (11, 5),
(11, 12), (11, 13),
(11, 20), (11, 21);

-- YOUTH_UNION_SCHOOL & FACULTY: event organizers
INSERT INTO role_permissions (role_id, permission_id) VALUES
(12, 1), (12, 2), (12, 3), (12, 5), (12, 20), (12, 21),
(13, 1), (13, 2), (13, 3), (13, 5), (13, 20), (13, 21);

-- ERO_EVENT_ADMIN: enterprise events
INSERT INTO role_permissions (role_id, permission_id) VALUES
(14, 1), (14, 2), (14, 3), (14, 5), (14, 20), (14, 21);

-- CLUB_EVENT_ADMIN: club events
INSERT INTO role_permissions (role_id, permission_id) VALUES
(15, 1), (15, 2), (15, 3), (15, 5), (15, 20), (15, 21);

-- STUDENT_AFFAIRS_ADMIN: full DRL & point management
INSERT INTO role_permissions (role_id, permission_id) VALUES
(20, 1), (20, 12), (20, 13),
(20, 30), (20, 31), (20, 32), (20, 33),
(20, 41), (20, 42), (20, 43),
(20, 50), (20, 51), (20, 52), (20, 53);

-- ADVISOR: review DRL at class level
INSERT INTO role_permissions (role_id, permission_id) VALUES
(21, 1), (21, 33), (21, 41);

-- FACULTY_REVIEWER: review at faculty level
INSERT INTO role_permissions (role_id, permission_id) VALUES
(22, 1), (22, 33), (22, 42);

-- CHECKIN_STAFF: only checkin/out
INSERT INTO role_permissions (role_id, permission_id) VALUES
(23, 1), (23, 20), (23, 21);

-- POINT_AUDITOR: view logs & reports
INSERT INTO role_permissions (role_id, permission_id) VALUES
(24, 1), (24, 33), (24, 52), (24, 53);

-- ========================================
-- 5. MIGRATE EXISTING USERS TO NEW RBAC
-- ========================================

-- Assign STUDENT role to all students
INSERT INTO user_roles_scoped (user_id, role_id, scope_org_unit_id)
SELECT u.id, 2, NULL
FROM users u
WHERE u.role = 'STUDENT'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- Assign SUPER_ADMIN to existing admins
INSERT INTO user_roles_scoped (user_id, role_id, scope_org_unit_id)
SELECT u.id, 1, NULL
FROM users u
WHERE u.role = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id=role_id;

