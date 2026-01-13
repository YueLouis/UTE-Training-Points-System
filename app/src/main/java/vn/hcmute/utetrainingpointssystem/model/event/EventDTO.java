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

    public String status;

    @com.google.gson.annotations.SerializedName("computedStatus")
    public String computedStatus;

    public Boolean canRegister;
    public Boolean canCheckin;
    public Boolean canCheckout;

    public Boolean registered;
    public Boolean checkedIn;
    public Boolean completed;

    public String registrationStatus;
    public String studentCheckinTime;
    public String studentCheckoutTime;

    public String eventMode;
    public String surveyUrl;
    public String surveySecretCode;
}
