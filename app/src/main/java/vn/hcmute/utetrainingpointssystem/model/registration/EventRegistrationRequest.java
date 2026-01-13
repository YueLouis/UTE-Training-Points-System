package vn.hcmute.utetrainingpointssystem.model.registration;

public class EventRegistrationRequest {
    public Long eventId;
    public Long studentId;

    public EventRegistrationRequest() {
    }

    public EventRegistrationRequest(Long eventId, Long studentId) {
        this.eventId = eventId;
        this.studentId = studentId;
    }
}
