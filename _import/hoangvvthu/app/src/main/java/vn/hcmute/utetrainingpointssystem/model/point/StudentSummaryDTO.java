package vn.hcmute.utetrainingpointssystem.model.point;

import com.google.gson.annotations.SerializedName;

public class StudentSummaryDTO {
    @SerializedName(value = "student_id", alternate = {"studentId"})
    public Long studentId;
    
    @SerializedName(value = "semester_id", alternate = {"semesterId"})
    public Long semesterId;
    
    @SerializedName(value = "drl", alternate = {"diem_ren_luyen"})
    public int drl;
    
    @SerializedName(value = "ctxh", alternate = {"diem_ctxh"})
    public int ctxh;
    
    @SerializedName(value = "cddn", alternate = {"diem_cddn"})
    public int cddn;
}
