package vn.hcmute.trainingpoints.service.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.hcmute.trainingpoints.entity.audit.AuditLog;
import vn.hcmute.trainingpoints.repository.audit.AuditLogRepository;

/**
 * Audit log service - ghi lại tất cả thay đổi quan trọng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Ghi log khi admin duyệt/từ chối event
     */
    public void logEventApproval(Long userId, Long eventId, String action, String description) {
        try {
            AuditLog audit = AuditLog.builder()
                    .userId(userId)
                    .action(action) // APPROVE, REJECT, PUBLISH, CLOSE
                    .entityType("EVENT")
                    .entityId(eventId)
                    .description(description)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();

            auditLogRepository.save(audit);
            log.info("Audit log created: User {} {} Event {}", userId, action, eventId);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    /**
     * Ghi log khi admin cộng/trừ điểm
     */
    public void logPointAdjustment(Long userId, Long studentId, String action, int points, String description) {
        try {
            AuditLog audit = AuditLog.builder()
                    .userId(userId)
                    .action(action) // AWARD, DEDUCT
                    .entityType("POINTS")
                    .entityId(studentId)
                    .description(description + " (" + (points > 0 ? "+" : "") + points + " điểm)")
                    .createdAt(java.time.LocalDateTime.now())
                    .build();

            auditLogRepository.save(audit);
            log.info("Audit log created: User {} adjusted {} points for Student {}", userId, points, studentId);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    /**
     * Ghi log user management
     */
    public void logUserAction(Long userId, Long targetUserId, String action, String description) {
        try {
            AuditLog audit = AuditLog.builder()
                    .userId(userId)
                    .action(action) // CREATE, UPDATE, DELETE, ENABLE, DISABLE
                    .entityType("USER")
                    .entityId(targetUserId)
                    .description(description)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();

            auditLogRepository.save(audit);
            log.info("Audit log created: User {} performed {} on User {}", userId, action, targetUserId);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    /**
     * Generic audit log
     */
    public void log(AuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }
}

