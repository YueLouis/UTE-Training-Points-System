package vn.hcmute.trainingpoints.entity.point;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="student_id", nullable = false)
    private Long studentId;

    @Column(name="semester_id", nullable = false)
    private Long semesterId;

    @Column(name="point_type_id", nullable = false)
    private Long pointTypeId;

    @Column(name="event_id")
    private Long eventId;

    @Column(name="points", nullable = false)
    private Integer points;

    @Column(name="reason", length = 255)
    private String reason;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="created_by")
    private Long createdBy;
}
