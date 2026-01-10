-- V7__fix_org_units_12_faculties_hcmute.sql
-- Replace old org_units with correct 12 faculties of HCMUTE

-- Truncate and reload (safer than delete for seeded data)
DELETE FROM user_org_units WHERE org_unit_id > 0;
DELETE FROM org_units WHERE id > 0;
ALTER TABLE org_units AUTO_INCREMENT = 1;

-- ========================================
-- 1. UNIVERSITY LEVEL
-- ========================================
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
(1, 'UNIVERSITY', 'HCMUTE', 'Trường Đại học Sư phạm Kỹ thuật TP.HCM', NULL, 'University', TRUE);
SET @university_id = LAST_INSERT_ID();

-- ========================================
-- 2. INSTITUTES (3 - Viện)
-- ========================================
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
('INSTITUTE', 'INST_SPKT', 'Viện Sư phạm Kỹ thuật', @university_id, 'Teacher Training Institute', TRUE),
('INSTITUTE', 'INST_DTQT', 'Viện Đào tạo Quốc tế', @university_id, 'International Training Institute', TRUE),
('INSTITUTE', 'INST_DTCLH', 'Viện Đào tạo Chất lượng cao', @university_id, 'High Quality Training Institute', TRUE);

-- ========================================
-- 3. FACULTIES (12 - Khoa)
-- ========================================
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
('FACULTY', 'FK_COKHI_CTM', 'Khoa Cơ khí Chế tạo máy', @university_id, 'Faculty of Mechanical Manufacturing Engineering', TRUE),
('FACULTY', 'FK_COKHI_DL', 'Khoa Cơ khí Động lực', @university_id, 'Faculty of Mechanical Power Engineering', TRUE),
('FACULTY', 'FK_CNHH_TP', 'Khoa Công nghệ Hóa học & Thực phẩm', @university_id, 'Faculty of Chemical Technology & Food Science', TRUE),
('FACULTY', 'FK_THOITRANG_DL', 'Khoa Thời trang và Du lịch', @university_id, 'Faculty of Fashion & Tourism', TRUE),
('FACULTY', 'FK_CNTT', 'Khoa Công nghệ Thông tin', @university_id, 'Faculty of Information Technology', TRUE),
('FACULTY', 'FK_DIEN_DT', 'Khoa Điện – Điện tử', @university_id, 'Faculty of Electrical & Electronics Engineering', TRUE),
('FACULTY', 'FK_IN_TT', 'Khoa In & Truyền thông', @university_id, 'Faculty of Printing & Communication', TRUE),
('FACULTY', 'FK_KHOA_UNG_DUG', 'Khoa Khoa học Ứng dụng', @university_id, 'Faculty of Applied Sciences', TRUE),
('FACULTY', 'FK_KINH_TE', 'Khoa Kinh tế', @university_id, 'Faculty of Economics', TRUE),
('FACULTY', 'FK_NGOAI_NGU', 'Khoa Ngoại ngữ', @university_id, 'Faculty of Foreign Languages', TRUE),
('FACULTY', 'FK_XAY_DUNG', 'Khoa Xây dựng', @university_id, 'Faculty of Construction', TRUE),
('FACULTY', 'FK_CHINH_TRI_LUAT', 'Khoa Chính trị và Luật', @university_id, 'Faculty of Politics & Law', TRUE);

-- Save faculty IDs for later use
SET @fk_cokhi_ctm = (SELECT id FROM org_units WHERE code = 'FK_COKHI_CTM');
SET @fk_cokhi_dl = (SELECT id FROM org_units WHERE code = 'FK_COKHI_DL');
SET @fk_cnhh_tp = (SELECT id FROM org_units WHERE code = 'FK_CNHH_TP');
SET @fk_thoitrang_dl = (SELECT id FROM org_units WHERE code = 'FK_THOITRANG_DL');
SET @fk_cntt = (SELECT id FROM org_units WHERE code = 'FK_CNTT');
SET @fk_dien_dt = (SELECT id FROM org_units WHERE code = 'FK_DIEN_DT');
SET @fk_in_tt = (SELECT id FROM org_units WHERE code = 'FK_IN_TT');
SET @fk_khoa_ung_dug = (SELECT id FROM org_units WHERE code = 'FK_KHOA_UNG_DUG');
SET @fk_kinh_te = (SELECT id FROM org_units WHERE code = 'FK_KINH_TE');
SET @fk_ngoai_ngu = (SELECT id FROM org_units WHERE code = 'FK_NGOAI_NGU');
SET @fk_xay_dung = (SELECT id FROM org_units WHERE code = 'FK_XAY_DUNG');
SET @fk_chinh_tri_luat = (SELECT id FROM org_units WHERE code = 'FK_CHINH_TRI_LUAT');

-- ========================================
-- 4. OFFICES (Phòng ban)
-- ========================================
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
('OFFICE', 'OFF_CTSV', 'Phòng Công tác Sinh viên', @university_id, 'Student Affairs Office', TRUE),
('OFFICE', 'OFF_DAOTAO', 'Phòng Đào tạo', @university_id, 'Academic Affairs Office', TRUE),
('OFFICE', 'OFF_ERO', 'Phòng Quan hệ Doanh nghiệp', @university_id, 'Enterprise Relations Office', TRUE),
('OFFICE', 'OFF_KHCN', 'Phòng Khoa học Công nghệ', @university_id, 'Science & Technology Office', TRUE),
('OFFICE', 'OFF_NHAN_SU', 'Phòng Nhân sự', @university_id, 'Human Resources Office', TRUE);

SET @off_ctsv = (SELECT id FROM org_units WHERE code = 'OFF_CTSV');
SET @off_ero = (SELECT id FROM org_units WHERE code = 'OFF_ERO');

-- ========================================
-- 5. UNIONS & ASSOCIATIONS (Đoàn - Hội cấp trường)
-- ========================================
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
('UNION_SCHOOL', 'UNION_TRUONG', 'Đoàn Thanh niên Cộng sản Hồ Chí Minh', @university_id, 'Communist Youth Union - School', TRUE),
('UNION_SCHOOL', 'UNION_HOISV', 'Hội Sinh viên', @university_id, 'Student Association - School', TRUE);

SET @union_truong = (SELECT id FROM org_units WHERE code = 'UNION_TRUONG');

-- ========================================
-- 6. FACULTY-LEVEL UNIONS (Đoàn - Hội cấp khoa)
-- ========================================
-- Example for CNTT (FK_CNTT)
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
('UNION_FACULTY', 'UNION_CNTT', 'Đoàn Khoa CNTT', @fk_cntt, 'Youth Union - IT Faculty', TRUE),
('UNION_FACULTY', 'UNION_HOISV_CNTT', 'Hội Sinh viên Khoa CNTT', @fk_cntt, 'Student Association - IT Faculty', TRUE);

-- Add for other major faculties as needed
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
('UNION_FACULTY', 'UNION_COKHI', 'Đoàn Khoa Cơ khí', @fk_cokhi_ctm, 'Youth Union - Mechanical Faculty', TRUE),
('UNION_FACULTY', 'UNION_DIEN', 'Đoàn Khoa Điện', @fk_dien_dt, 'Youth Union - Electrical Faculty', TRUE),
('UNION_FACULTY', 'UNION_NGOAIGU', 'Đoàn Khoa Ngoại ngữ', @fk_ngoai_ngu, 'Youth Union - FL Faculty', TRUE);

-- ========================================
-- 7. CLUBS (CLB) - Sample
-- ========================================
INSERT INTO org_units (type, code, name, parent_id, description, is_active) VALUES
('CLUB', 'CLB_TINH_NGUYEN', 'Câu lạc bộ Tình nguyện', @union_truong, 'Volunteering Club', TRUE),
('CLUB', 'CLB_AN_SINH', 'Câu lạc bộ An sinh xã hội', @union_truong, 'Social Welfare Club', TRUE),
('CLUB', 'CLB_AI_CNTT', 'Câu lạc bộ Trí tuệ nhân tạo', @fk_cntt, 'AI Club - IT Faculty', TRUE),
('CLUB', 'CLB_ROBOCON', 'Câu lạc bộ Robocon', @fk_cokhi_ctm, 'Robocon Club - Mechanical Faculty', TRUE);

-- ========================================
-- VERIFY
-- ========================================
-- SELECT type, COUNT(*) as count FROM org_units GROUP BY type;
-- Expected:
-- UNIVERSITY: 1
-- INSTITUTE: 3
-- FACULTY: 12
-- OFFICE: 5
-- UNION_SCHOOL: 2
-- UNION_FACULTY: 5
-- CLUB: 4
-- TOTAL: 32+

