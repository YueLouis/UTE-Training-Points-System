package vn.hcmute.trainingpoints.dto.registration;

import lombok.*;
import vn.hcmute.trainingpoints.entity.registration.EventRegistrationStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationDTO {
    private Long id;
    private Long eventId;
    private Long studentId;

    // Thêm các trường thông tin sinh viên để FE hiển thị
    private String studentCode;
    private String studentName;
    private String studentClass;
    private String studentFaculty;

    private LocalDateTime registrationTime;
    private EventRegistrationStatus status;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private String note;
}
