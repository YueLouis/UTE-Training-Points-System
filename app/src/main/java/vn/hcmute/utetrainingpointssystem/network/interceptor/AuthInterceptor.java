package vn.hcmute.utetrainingpointssystem.network.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;

/**
 * AuthInterceptor - Add JWT Bearer token to all requests
 * Header: Authorization: Bearer <access_token>
 */
public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        String token = tokenManager.getAccessToken();
        if (token == null || token.trim().isEmpty()) {
            return chain.proceed(original);
        }

        Request newReq = original.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newReq);
    }
}
