package vn.hcmute.trainingpoints.entity.point;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_semester_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSemesterSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="student_id", nullable = false)
    private Long studentId;

    @Column(name="semester_id", nullable = false)
    private Long semesterId;

    @Column(name="total_drl")
    private Integer totalDrl;

    @Column(name="total_ctxh")
    private Integer totalCtxh;

    @Column(name="total_cdnn")
    private Integer totalCdnn;

    @Column(name="rank_label", length = 50)
    private String rankLabel;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
