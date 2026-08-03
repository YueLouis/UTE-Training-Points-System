package vn.hcmute.trainingpoints.dto.registration;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Student ID is required")
    private Long studentId;
    private String note;   // cho phép ghi chú lý do/hình thức đăng ký (nếu muốn)
}
