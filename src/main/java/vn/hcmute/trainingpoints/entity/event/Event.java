package vn.hcmute.trainingpoints.entity.event;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 200)
    private String location;

    @Column(name = "banner_url", length = 255)
    private String bannerUrl;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "point_type_id")
    private Long pointTypeId;

    @Column(name = "point_value")
    private Integer pointValue;

    @Column(name = "created_by")
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_mode", nullable = false)
    private EventMode eventMode;

    @Column(name = "survey_url", length = 500)
    private String surveyUrl;

    @Column(name = "survey_secret_code", length = 50)
    private String surveySecretCode;
}
