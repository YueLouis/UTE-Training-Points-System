-- V7__fix_org_units_12_faculties_hcmute.sql
-- Replace old org_units with correct 12 faculties of HCMUTE

-- Truncate and reload (safer than delete for seeded data)
DELETE FROM user_org_units WHERE org_unit_id > 0;
DELETE FROM org_units WHERE id > 0;
ALTER TABLE org_units AUTO_INCREMENT = 1;

-- ========================================
-- 1. UNIVERSITY LEVEL
-- ========================================
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('UNIVERSITY', 'UNIVERSITY', 'Trường Đại học Sư phạm Kỹ thuật TP.HCM', 'HCMUTE', NULL, TRUE);
SET @university_id = LAST_INSERT_ID();

-- ========================================
-- 2. INSTITUTES (3 - Viện)
-- ========================================
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('INSTITUTE', 'INST_SPKT', 'Viện Sư phạm Kỹ thuật', 'Teacher Training Institute', @university_id, TRUE),
('INSTITUTE', 'INST_DTQT', 'Viện Đào tạo Quốc tế', 'International Training Institute', @university_id, TRUE),
('INSTITUTE', 'INST_DTCLH', 'Viện Đào tạo Chất lượng cao', 'High Quality Training Institute', @university_id, TRUE);

-- ========================================
-- 3. FACULTIES (12 - Khoa)
-- ========================================
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('FACULTY', 'FK_COKHI_CTM', 'Khoa Cơ khí Chế tạo máy', 'Faculty of Mechanical Manufacturing Engineering', @university_id, TRUE),
('FACULTY', 'FK_COKHI_DL', 'Khoa Cơ khí Động lực', 'Faculty of Mechanical Power Engineering', @university_id, TRUE),
('FACULTY', 'FK_CNHH_TP', 'Khoa Công nghệ Hóa học & Thực phẩm', 'Faculty of Chemical Technology & Food Science', @university_id, TRUE),
('FACULTY', 'FK_THOITRANG_DL', 'Khoa Thời trang và Du lịch', 'Faculty of Fashion & Tourism', @university_id, TRUE),
('FACULTY', 'FK_CNTT', 'Khoa Công nghệ Thông tin', 'Faculty of Information Technology', @university_id, TRUE),
('FACULTY', 'FK_DIEN_DT', 'Khoa Điện – Điện tử', 'Faculty of Electrical & Electronics Engineering', @university_id, TRUE),
('FACULTY', 'FK_IN_TT', 'Khoa In & Truyền thông', 'Faculty of Printing & Communication', @university_id, TRUE),
('FACULTY', 'FK_KHOA_UNG_DUG', 'Khoa Khoa học Ứng dụng', 'Faculty of Applied Sciences', @university_id, TRUE),
('FACULTY', 'FK_KINH_TE', 'Khoa Kinh tế', 'Faculty of Economics', @university_id, TRUE),
('FACULTY', 'FK_NGOAI_NGU', 'Khoa Ngoại ngữ', 'Faculty of Foreign Languages', @university_id, TRUE),
('FACULTY', 'FK_XAY_DUNG', 'Khoa Xây dựng', 'Faculty of Construction', @university_id, TRUE),
('FACULTY', 'FK_CHINH_TRI_LUAT', 'Khoa Chính trị và Luật', 'Faculty of Politics & Law', @university_id, TRUE);

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
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('OFFICE', 'OFF_CTSV', 'Phòng Công tác Sinh viên', 'Student Affairs Office', @university_id, TRUE),
('OFFICE', 'OFF_DAOTAO', 'Phòng Đào tạo', 'Academic Affairs Office', @university_id, TRUE),
('OFFICE', 'OFF_ERO', 'Phòng Quan hệ Doanh nghiệp', 'Enterprise Relations Office', @university_id, TRUE),
('OFFICE', 'OFF_KHCN', 'Phòng Khoa học Công nghệ', 'Science & Technology Office', @university_id, TRUE),
('OFFICE', 'OFF_NHAN_SU', 'Phòng Nhân sự', 'Human Resources Office', @university_id, TRUE);

SET @off_ctsv = (SELECT id FROM org_units WHERE code = 'OFF_CTSV');
SET @off_ero = (SELECT id FROM org_units WHERE code = 'OFF_ERO');

-- ========================================
-- 5. UNIONS & ASSOCIATIONS (Đoàn - Hội cấp trường)
-- ========================================
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('UNION_SCHOOL', 'UNION_TRUONG', 'Đoàn Thanh niên Cộng sản Hồ Chí Minh', 'Communist Youth Union - School', @university_id, TRUE),
('UNION_SCHOOL', 'UNION_HOISV', 'Hội Sinh viên', 'Student Association - School', @university_id, TRUE);

SET @union_truong = (SELECT id FROM org_units WHERE code = 'UNION_TRUONG');

-- ========================================
-- 6. FACULTY-LEVEL UNIONS (Đoàn - Hội cấp khoa)
-- ========================================
-- Example for CNTT (FK_CNTT)
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('UNION_FACULTY', 'UNION_CNTT', 'Đoàn Khoa CNTT', 'Youth Union - IT Faculty', @fk_cntt, TRUE),
('UNION_FACULTY', 'UNION_HOISV_CNTT', 'Hội Sinh viên Khoa CNTT', 'Student Association - IT Faculty', @fk_cntt, TRUE);

-- Add for other major faculties as needed
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('UNION_FACULTY', 'UNION_COKHI', 'Đoàn Khoa Cơ khí', 'Youth Union - Mechanical Faculty', @fk_cokhi_ctm, TRUE),
('UNION_FACULTY', 'UNION_DIEN', 'Đoàn Khoa Điện', 'Youth Union - Electrical Faculty', @fk_dien_dt, TRUE),
('UNION_FACULTY', 'UNION_NGOAIGU', 'Đoàn Khoa Ngoại ngữ', 'Youth Union - FL Faculty', @fk_ngoai_ngu, TRUE);

-- ========================================
-- 7. CLUBS (CLB) - Sample
-- ========================================
INSERT INTO org_units (type, code, name, description, parent_id, is_active) VALUES
('CLUB', 'CLB_TINH_NGUYEN', 'Câu lạc bộ Tình nguyện', 'Volunteering Club', @union_truong, TRUE),
('CLUB', 'CLB_AN_SINH', 'Câu lạc bộ An sinh xã hội', 'Social Welfare Club', @union_truong, TRUE),
('CLUB', 'CLB_AI_CNTT', 'Câu lạc bộ Trí tuệ nhân tạo', 'AI Club - IT Faculty', @fk_cntt, TRUE),
('CLUB', 'CLB_ROBOCON', 'Câu lạc bộ Robocon', 'Robocon Club - Mechanical Faculty', @fk_cokhi_ctm, TRUE);

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

