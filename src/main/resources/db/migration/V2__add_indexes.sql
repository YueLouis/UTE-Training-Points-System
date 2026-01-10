-- V2__add_indexes.sql
-- Performance optimization indexes

-- Additional indexes for event_registrations
CREATE INDEX idx_reg_event_status ON event_registrations(event_id, status);
CREATE INDEX idx_reg_student_status ON event_registrations(student_id, status);
CREATE INDEX idx_reg_checkin ON event_registrations(checkin_time);
CREATE INDEX idx_reg_checkout ON event_registrations(checkout_time);

-- Additional indexes for point_transactions
CREATE INDEX idx_tx_student_type ON point_transactions(student_id, point_type_id);
CREATE INDEX idx_tx_semester_type ON point_transactions(semester_id, point_type_id);

-- Additional indexes for notifications
CREATE INDEX idx_notif_user_created ON notifications(user_id, created_at DESC);
CREATE INDEX idx_notif_type ON notifications(type);

-- Additional indexes for password_reset_codes
CREATE INDEX idx_reset_email_created ON password_reset_codes(email, created_at DESC);
CREATE INDEX idx_reset_expires ON password_reset_codes(expires_at);

