package vn.hcmute.trainingpoints.service.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.trainingpoints.dto.point.StudentSummaryDTO;
import vn.hcmute.trainingpoints.entity.event.Event;
import vn.hcmute.trainingpoints.entity.point.PointTransaction;
import vn.hcmute.trainingpoints.entity.point.PointType;
import vn.hcmute.trainingpoints.entity.point.StudentSemesterSummary;
import vn.hcmute.trainingpoints.entity.notification.NotificationType;
import vn.hcmute.trainingpoints.entity.user.User;
import vn.hcmute.trainingpoints.repository.event.EventRepository;
import vn.hcmute.trainingpoints.repository.point.PointTransactionRepository;
import vn.hcmute.trainingpoints.repository.point.PointTypeRepository;
import vn.hcmute.trainingpoints.repository.point.StudentSemesterSummaryRepository;
import vn.hcmute.trainingpoints.repository.user.UserRepository;
import vn.hcmute.trainingpoints.exception.PointsAlreadyAwardedException;
import vn.hcmute.trainingpoints.service.notification.NotificationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointTransactionRepository pointTransactionRepository;
    private final StudentSemesterSummaryRepository studentSemesterSummaryRepository;
    private final PointTypeRepository pointTypeRepository;
    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Cộng điểm khi event COMPLETED (đủ checkin + checkout).
     * - Idempotent: nếu đã cộng cho event này rồi thì không cộng lại.
     */
    @Transactional
    public void awardPointsForCompletedEvent(Long eventId, Long studentId, Long adminIdOrNull) {
        if (adminIdOrNull != null) {
            // Xác thực Admin thật
            User admin = userRepository.findById(adminIdOrNull).orElse(null);
            if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
                throw new RuntimeException("Chỉ Admin mới có quyền phê duyệt điểm thủ công");
            }
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getSemesterId() == null || event.getPointTypeId() == null || event.getPointValue() == null) {
            throw new RuntimeException("Event missing semester_id / point_type_id / point_value");
        }

        Long semesterId = event.getSemesterId();
        Long pointTypeId = event.getPointTypeId();
        Integer points = event.getPointValue();

        // chống cộng trùng cho cùng event
        pointTransactionRepository.findByStudentIdAndSemesterIdAndEventId(studentId, semesterId, eventId)
                .ifPresent(tx -> { throw new PointsAlreadyAwardedException("Points already awarded for this event"); });

        // insert transaction
        PointTransaction tx = PointTransaction.builder()
                .studentId(studentId)
                .semesterId(semesterId)
                .pointTypeId(pointTypeId)
                .eventId(eventId)
                .points(points)
                .reason("Tham gia sự kiện: " + event.getTitle())
                .createdAt(LocalDateTime.now())
                .createdBy(adminIdOrNull)
                .build();

        pointTransactionRepository.save(tx);

        // update summary
        upsertSummary(studentId, semesterId, pointTypeId, points);

        // create notification
        PointType pt = pointTypeRepository.findById(pointTypeId).orElse(null);
        String pointTypeName = pt != null ? pt.getName() : "điểm";
        String notiTitle = "Bạn đã được cộng " + points + " " + pointTypeName;
        String notiContent = "Bạn đã hoàn thành sự kiện \"" + event.getTitle() + "\" và được cộng " + points + " " + pointTypeName + ".";
        notificationService.createNotification(studentId, notiTitle, notiContent, NotificationType.EVENT);
    }

    @Transactional
    protected void upsertSummary(Long studentId, Long semesterId, Long pointTypeId, Integer delta) {
        StudentSemesterSummary summary = studentSemesterSummaryRepository
                .findByStudentIdAndSemesterId(studentId, semesterId)
                .orElseGet(() -> StudentSemesterSummary.builder()
                        .studentId(studentId)
                        .semesterId(semesterId)
                        .totalDrl(0)
                        .totalCtxh(0)
                        .totalCdnn(0)
                        .rankLabel(null)
                        .updatedAt(LocalDateTime.now())
                        .build()
                );

        // map pointTypeId -> code
        PointType pt = pointTypeRepository.findById(pointTypeId)
                .orElseThrow(() -> new RuntimeException("PointType not found: " + pointTypeId));

        String code = pt.getCode();
        if ("DRL".equalsIgnoreCase(code)) {
            int newPoints = safe(summary.getTotalDrl()) + delta;
            summary.setTotalDrl(Math.min(newPoints, 100)); // Cap max 100
        } else if ("CTXH".equalsIgnoreCase(code)) {
            int newPoints = safe(summary.getTotalCtxh()) + delta;
            summary.setTotalCtxh(Math.min(newPoints, 40)); // Cap max 40
        } else if ("CDDN".equalsIgnoreCase(code) || "CDNN".equalsIgnoreCase(code)) {
            int newPoints = safe(summary.getTotalCdnn()) + delta;
            summary.setTotalCdnn(Math.min(newPoints, 8)); // Cap max 8
        } else {
            throw new RuntimeException("Unknown point type code: " + code);
        }

        summary.setRankLabel(calculateRank(summary.getTotalDrl()));
        summary.setUpdatedAt(LocalDateTime.now());
        studentSemesterSummaryRepository.save(summary);
    }

    private String calculateRank(Integer drl) {
        if (drl == null) return "Chưa xếp loại";
        if (drl >= 90) return "Xuất sắc";
        if (drl >= 80) return "Tốt";
        if (drl >= 70) return "Khá";
        if (drl >= 60) return "Trung bình khá";
        if (drl >= 50) return "Trung bình";
        if (drl >= 35) return "Yếu";
        return "Kém";
    }

    private int safe(Integer v) { return v == null ? 0 : v; }

    // API lấy tổng điểm theo học kỳ
    public StudentSummaryDTO getSummary(Long studentId, Long semesterId) {
        StudentSemesterSummary s = studentSemesterSummaryRepository
                .findByStudentIdAndSemesterId(studentId, semesterId)
                .orElse(StudentSemesterSummary.builder()
                        .studentId(studentId)
                        .semesterId(semesterId)
                        .totalDrl(0).totalCtxh(0).totalCdnn(0)
                        .rankLabel("Chưa xếp loại")
                        .build()
                );

        return StudentSummaryDTO.builder()
                .studentId(studentId)
                .semesterId(semesterId)
                .DRL(safe(s.getTotalDrl()))
                .CTXH(safe(s.getTotalCtxh()))
                .CDDN(safe(s.getTotalCdnn()))
                .rankLabel(s.getRankLabel())
                .build();
    }
}
