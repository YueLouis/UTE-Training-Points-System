package vn.hcmute.utetrainingpointssystem.model.registration;

import com.google.gson.annotations.SerializedName;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;

public class EventRegistrationDTO {
    public Long id;
    
    @SerializedName(value = "student_id", alternate = {"studentId"})
    public Long studentId;
    
    @SerializedName(value = "event_id", alternate = {"eventId"})
    public Long eventId;
    
    @SerializedName(value = "registration_date", alternate = {"registrationDate"})
    public String registrationDate;
    
    public String status; 
    public EventDTO event;

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public Long getEventId() { return eventId; }
    public String getRegistrationDate() { return registrationDate; }
    public String getStatus() { return status; }
    public EventDTO getEvent() { return event; }
}
