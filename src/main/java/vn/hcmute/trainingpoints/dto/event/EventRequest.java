package vn.hcmute.trainingpoints.dto.event;

import lombok.*;
import vn.hcmute.trainingpoints.entity.event.EventMode;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {
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

    // thêm cái này
    private EventMode eventMode;
    private String surveyUrl;
}
