package vn.hcmute.utetrainingpointssystem.model.registration;

public class EventRegistrationDTO {
    public Long id;
    public Long eventId;
    public Long studentId;

    public String studentCode;      // MSSV
    public String studentName;
    public String studentClass;
    public String studentFaculty;

    public String registrationTime;
    public String status;
    public String checkinTime;
    public String checkoutTime;
    public String note;
}
