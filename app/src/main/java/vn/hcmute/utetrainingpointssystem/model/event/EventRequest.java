package vn.hcmute.utetrainingpointssystem.model.event;

public class EventRequest {
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

    public String eventMode;
    public String surveyUrl;

    public EventRequest() {}
}
