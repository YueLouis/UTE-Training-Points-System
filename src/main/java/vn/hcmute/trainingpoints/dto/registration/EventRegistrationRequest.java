package vn.hcmute.trainingpoints.dto.registration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRegistrationRequest {
    private Long eventId;
    private Long studentId;
    private String note;   // cho phép ghi chú lý do/hình thức đăng ký (nếu muốn)
}
