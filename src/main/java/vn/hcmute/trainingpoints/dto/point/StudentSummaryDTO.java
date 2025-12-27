package vn.hcmute.trainingpoints.dto.point;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StudentSummaryDTO {
    private Long studentId;
    private Long semesterId;
    private Integer DRL;
    private Integer CTXH;
    private Integer CDDN;
}
