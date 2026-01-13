package vn.hcmute.utetrainingpointssystem.model.event;

public class EventRequest {
    public Long semesterId;                 // * required
    public Long categoryId;                 // * required

    public String title;                    // * required
    public String description;
    public String location;
    public String bannerUrl;

    public String startTime;                // * required (date-time)
    public String endTime;                  // * required (date-time)
    public String registrationDeadline;     // optional (date-time)

    public Integer maxParticipants;         // >= 0
    public Long pointTypeId;                // * required
    public Integer pointValue;              // >= 0

    public Long createdBy;
    public String eventMode;                // string (enum bên BE)

    public String surveyUrl;
    public String surveySecretCode;

    public EventRequest() {}
}
