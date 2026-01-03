package vn.hcmute.utetrainingpointssystem.model.event;

public class EventDTO {
    public Long id;
    public Long semesterId;
    public Long categoryId;

    public String title;
    public String description;
    public String location;
    public String bannerUrl;

    public String startTime;
    public String endTime;
    public String registrationDeadline;

    public Integer maxParticipants;
    public Long pointTypeId;
    public Integer pointValue;

    public Long createdBy;
    public String status;          // "OPEN", "CLOSED", ...
    public String computedStatus;  // backend compute

    public String eventMode;       // "ATTENDANCE"
    public String surveyUrl;
}
