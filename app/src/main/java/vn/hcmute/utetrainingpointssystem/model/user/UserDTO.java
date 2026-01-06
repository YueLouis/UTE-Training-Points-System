package vn.hcmute.utetrainingpointssystem.model.user;

import com.google.gson.annotations.SerializedName;

public class UserDTO {
    public long id;

    @SerializedName("student_code")
    public String studentCode;

    @SerializedName("full_name")
    public String fullName;

    public String email;
    public String phone;
    public String role;

    @SerializedName("class_name")
    public String className;

    public String faculty;
    public boolean status;
}
