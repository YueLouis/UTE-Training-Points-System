package vn.hcmute.trainingpoints.dto.point;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PointTransactionDTO {
    private Long id;
    private Long studentId;
    private Long semesterId;
    private Long pointTypeId;
    private Long eventId;
    private Integer points;
    private String reason;
    private LocalDateTime createdAt;
    private Long createdBy;
}
