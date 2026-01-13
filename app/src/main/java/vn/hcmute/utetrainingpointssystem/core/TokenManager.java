package vn.hcmute.utetrainingpointssystem.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * TokenManager - Handle JWT token (access + refresh)
 * Backend: Spring Boot JWT (15m access + 7d refresh)
 */
public class TokenManager {

    private static final String PREF = "auth_pref";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";

    private final SharedPreferences sp;

    public TokenManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveAuth(String accessToken, String refreshToken, long userId, String role) {
        sp.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putLong(KEY_USER_ID, userId)
                .putLong(KEY_STUDENT_ID, userId) // demo: studentId = userId
                .putString(KEY_ROLE, role)
                .putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + 15 * 60 * 1000) // 15 min
                .apply();
    }

    public String getAccessToken() {
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sp.getString(KEY_REFRESH_TOKEN, null);
    }

    public void updateAccessToken(String accessToken) {
        sp.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + 15 * 60 * 1000)
                .apply();
    }

    public String getRole() {
        return sp.getString(KEY_ROLE, null);
    }

    public Long getUserId() {
        long v = sp.getLong(KEY_USER_ID, -1);
        return v == -1 ? null : v;
    }

    public Long getStudentId() {
        long v = sp.getLong(KEY_STUDENT_ID, -1);
        return v == -1 ? null : v;
    }

    public boolean isTokenExpired() {
        long expiry = sp.getLong(KEY_TOKEN_EXPIRY, 0);
        return System.currentTimeMillis() > expiry;
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null && getUserId() != null;
    }

    public void clear() {
        sp.edit().clear().apply();
    }
}
