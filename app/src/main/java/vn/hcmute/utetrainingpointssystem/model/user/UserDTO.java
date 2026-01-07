package vn.hcmute.utetrainingpointssystem.model.user;

import com.google.gson.annotations.SerializedName;

public class UserDTO {
    public long id;

    @SerializedName(value = "student_code", alternate = {"studentCode"})
    public String studentCode;

    @SerializedName(value = "full_name", alternate = {"fullName"})
    public String fullName;

    public String email;
    public String phone;
    public String role;

    @SerializedName(value = "class_name", alternate = {"className"})
    public String className;

    public String faculty;
    public boolean status;
}
