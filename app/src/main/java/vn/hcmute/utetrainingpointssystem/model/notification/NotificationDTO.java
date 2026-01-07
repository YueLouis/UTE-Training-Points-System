package vn.hcmute.utetrainingpointssystem.model.notification;

public class NotificationDTO {
    public Long id;
    public String title;
    public String content;
    public String type; // ví dụ: POINT_ADDED, EVENT_REMINDER
    public String createdAt;
    public Boolean isRead;
}
