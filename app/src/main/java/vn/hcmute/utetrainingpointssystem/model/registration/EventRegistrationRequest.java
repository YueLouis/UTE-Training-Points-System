package vn.hcmute.utetrainingpointssystem.model.registration;

/**
 * EventRegistrationRequest - Request body for event registration
 */
public class EventRegistrationRequest {
    public Long eventId;
    public Long studentId;
    public String note;

    public EventRegistrationRequest() {
    }

    public EventRegistrationRequest(Long eventId, Long studentId) {
        this.eventId = eventId;
        this.studentId = studentId;
    }

    public EventRegistrationRequest(Long eventId, Long studentId, String note) {
        this.eventId = eventId;
        this.studentId = studentId;
        this.note = note;
    }
}
