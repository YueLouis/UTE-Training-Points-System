package vn.hcmute.utetrainingpointssystem.model.event;

public class EventDTO {
    public long id;
    public String title;        // nếu BE trả "title" thì nên dùng title
    public String description;
    public String startTime;
    public String endTime;

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}

