package vn.hcmute.utetrainingpointssystem.model.registration;

import com.google.gson.annotations.SerializedName;

public class EventRegistrationRequest {
    @SerializedName("student_id")
    private Long studentId;
    
    @SerializedName("event_id")
    private Long eventId;

    public EventRegistrationRequest(Long studentId, Long eventId) {
        this.studentId = studentId;
        this.eventId = eventId;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
}
