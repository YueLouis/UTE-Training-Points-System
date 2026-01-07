package vn.hcmute.utetrainingpointssystem.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    public String username;
    public String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
