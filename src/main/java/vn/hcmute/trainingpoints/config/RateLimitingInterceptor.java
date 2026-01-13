package vn.hcmute.trainingpoints.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter cho login/forgot-password
 * Giới hạn: 5 lần/phút per IP
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SIZE_MS = 60 * 1000; // 1 phút
    private static final ConcurrentHashMap<String, RateLimit> limiter = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Chỉ apply rate limit cho các endpoint này
        if (path.contains("/api/auth/login") || path.contains("/api/auth/forgot-password")) {
            String clientIP = getClientIP(request);
            RateLimit limit = limiter.compute(clientIP, (key, existing) -> {
                if (existing == null) {
                    return new RateLimit();
                }
                if (System.currentTimeMillis() - existing.firstRequestTime > WINDOW_SIZE_MS) {
                    return new RateLimit();
                }
                return existing;
            });

            synchronized (limit) {
                if (limit.attempts.incrementAndGet() > MAX_ATTEMPTS) {
                    response.setStatus(429); // Too Many Requests
                    response.getWriter().write("{\"error\": \"Too many requests. Try again later.\"}");
                    return false;
                }
            }
        }

        return true;
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private static class RateLimit {
        long firstRequestTime = System.currentTimeMillis();
        AtomicInteger attempts = new AtomicInteger(0);
    }
}

