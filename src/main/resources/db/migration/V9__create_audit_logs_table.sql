-- V9__create_audit_logs_table.sql
-- Tạo bảng audit logs để theo dõi tất cả thay đổi quan trọng

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    old_value LONGTEXT,
    new_value LONGTEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(500),
    INDEX idx_audit_user_id (user_id),
    INDEX idx_audit_entity (entity_type),
    INDEX idx_audit_action (action),
    INDEX idx_audit_created_at (created_at)
);

