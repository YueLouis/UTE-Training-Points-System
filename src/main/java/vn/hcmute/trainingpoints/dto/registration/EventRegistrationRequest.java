package vn.hcmute.trainingpoints.dto.registration;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationRequest {
    private Long eventId;
    private Long studentId;
    private String note;   // cho phép ghi chú lý do/hình thức đăng ký (nếu muốn)
}
