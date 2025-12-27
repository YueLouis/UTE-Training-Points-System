package vn.hcmute.trainingpoints.dto.event;

import lombok.*;
import vn.hcmute.trainingpoints.entity.event.EventMode;
import vn.hcmute.trainingpoints.entity.event.EventStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {

    private Long id;

    private Long semesterId;
    private Long categoryId;

    private String title;
    private String description;
    private String location;
    private String bannerUrl;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registrationDeadline;

    private Integer maxParticipants;

    private Long pointTypeId;
    private Integer pointValue;

    private Long createdBy;

    private EventStatus status;

    private EventComputedStatus computedStatus;
    private Boolean canRegister;
    private Boolean canCheckin;
    private Boolean canCheckout;
    // private Integer currentParticipants;     // optional

    // ---- Student view fields (optional but recommended for FE) ----
    private Boolean registered;              // student đã đăng ký event này chưa
    private Boolean checkedIn;               // đã checkin chưa
    private Boolean completed;               // đã checkout/completed chưa
    private String registrationStatus;       // REGISTERED / CHECKED_IN / COMPLETED ...
    private LocalDateTime studentCheckinTime;
    private LocalDateTime studentCheckoutTime;

    private EventMode eventMode;
    private String surveyUrl;
}
