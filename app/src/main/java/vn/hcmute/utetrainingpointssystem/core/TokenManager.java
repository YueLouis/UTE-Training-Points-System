package vn.hcmute.utetrainingpointssystem.core;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF = "auth_pref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences sp;

    public TokenManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveAuth(String token, long userId, String role) {
        sp.edit()
                .putString(KEY_TOKEN, token)
                .putLong(KEY_USER_ID, userId)
                .putLong(KEY_STUDENT_ID, userId) // demo: studentId = userId
                .putString(KEY_ROLE, role)
                .apply();
    }

    public String getToken() {
        return sp.getString(KEY_TOKEN, null);
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

    public boolean isLoggedIn() {
        return getToken() != null && getUserId() != null;
    }

    public void clear() {
        sp.edit().clear().apply();
    }
}
