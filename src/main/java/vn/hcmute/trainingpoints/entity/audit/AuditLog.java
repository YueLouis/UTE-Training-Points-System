package vn.hcmute.trainingpoints.entity.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Audit log entity - theo dõi thay đổi trong hệ thống
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user_id", columnList = "user_id"),
        @Index(name = "idx_audit_entity", columnList = "entity_type"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // Ai thực hiện hành động

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, APPROVE, REJECT...

    @Column(nullable = false)
    private String entityType; // EVENT, REGISTRATION, USER, POINTS...

    @Column(nullable = false)
    private Long entityId; // ID của entity bị thay đổi

    @Column(columnDefinition = "LONGTEXT")
    private String oldValue; // Giá trị cũ (JSON format)

    @Column(columnDefinition = "LONGTEXT")
    private String newValue; // Giá trị mới (JSON format)

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 500)
    private String description;
}

