-- V3__add_password_reset_tokens.sql
-- Add new password_reset_tokens table for token-based password reset flow

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    request_ip VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_prt_user (user_id),
    INDEX idx_prt_hash (token_hash),
    INDEX idx_prt_expires (expires_at),
    INDEX idx_prt_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Note: Old table password_reset_codes (for OTP) can be kept or dropped if no longer used
-- Optional: Drop old OTP table
-- DROP TABLE IF EXISTS password_reset_codes;

