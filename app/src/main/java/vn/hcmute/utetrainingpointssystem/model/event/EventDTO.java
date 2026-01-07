package vn.hcmute.utetrainingpointssystem.model.event;

import com.google.gson.annotations.SerializedName;

public class EventDTO {
    public long id;
    public String title;
    public String description;
    
    @SerializedName(value = "start_time", alternate = {"startTime"})
    public String startTime;
    
    @SerializedName(value = "end_time", alternate = {"endTime"})
    public String endTime;
    
    @SerializedName(value = "location", alternate = {"event_location"})
    public String location;

    @SerializedName(value = "event_mode", alternate = {"eventMode"})
    public String eventMode;

    @SerializedName(value = "survey_url", alternate = {"surveyUrl"})
    public String surveyUrl;

    @SerializedName(value = "survey_secret_code", alternate = {"surveySecretCode"})
    public String surveySecretCode;

    public String status;

    @SerializedName(value = "remaining_slots", alternate = {"remainingSlots", "max_participants", "maxParticipants"})
    public Integer remainingSlots;
    
    @SerializedName(value = "semester_id", alternate = {"semesterId"})
    public Long semesterId;
    
    @SerializedName(value = "category_id", alternate = {"categoryId", "point_type_id"})
    public Long categoryId; 

    @SerializedName(value = "registered", alternate = {"is_registered", "isRegistered"})
    public boolean registered;

    // --- CÁC PHƯƠNG THỨC GETTER ĐỂ SỬA LỖI BIÊN DỊCH ---
    public long getId() { return id; }
    public String getTitle() { return title != null ? title : ""; }
    public String getDescription() { return description != null ? description : ""; }
    public String getStartTime() { return startTime != null ? startTime : "Đang cập nhật"; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location != null ? location : "Đang cập nhật"; }
    public String getEventMode() { return eventMode; }
    public String getSurveyUrl() { return surveyUrl; }
    public String getSurveySecretCode() { return surveySecretCode; }
    public String getStatus() { return status; }
    
    public Integer getRemainingSlots() { 
        return remainingSlots != null ? remainingSlots : 0; 
    }
    
    public Long getSemesterId() { return semesterId; }
    public Long getCategoryId() { return categoryId; }
    public boolean isRegistered() { return registered; }
    public String getType() { return eventMode; }

    public String getPointType() {
        if (categoryId == null) return "DRL";
        if (categoryId == 2) return "CTXH";
        if (categoryId == 3) return "CDDN";
        return "DRL";
    }
}
