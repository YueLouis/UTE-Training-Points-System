package vn.hcmute.trainingpoints.config.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitFilter implements Filter {

    private static final Set<String> LIMITED_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/reset-password"
    );
    private static final long EVICTION_AFTER_MILLIS = Duration.ofMinutes(10).toMillis();
    private static final long CLEANUP_INTERVAL = 100;

    private final Map<String, ClientBucket> buckets = new ConcurrentHashMap<>();
    private final AtomicLong requestCount = new AtomicLong();

    @Value("${app.security.trust-forwarded-headers:false}")
    private boolean trustForwardedHeaders;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Apply rate limit to auth endpoints
        if (LIMITED_PATHS.contains(path)) {

            String ip = getClientIP(httpRequest);
            ClientBucket clientBucket = buckets.computeIfAbsent(ip, ignored -> new ClientBucket(createBucket()));
            clientBucket.touch();
            cleanupExpiredBuckets();

            if (clientBucket.bucket().tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(429); // Too Many Requests
                httpResponse.setContentType("application/json");
                httpResponse.setHeader("Retry-After", "60");
                httpResponse.getWriter().write(
                    "{\"error\":\"Too many requests\",\"message\":\"Please try again later\"}"
                );
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private Bucket createBucket() {
        // Allow 10 requests per minute per IP
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillIntervally(10, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String getClientIP(HttpServletRequest request) {
        if (!trustForwardedHeaders) {
            return request.getRemoteAddr();
        }

        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private void cleanupExpiredBuckets() {
        if (requestCount.incrementAndGet() % CLEANUP_INTERVAL != 0) {
            return;
        }

        long cutoff = System.currentTimeMillis() - EVICTION_AFTER_MILLIS;
        buckets.entrySet().removeIf(entry -> entry.getValue().lastSeen() < cutoff);
    }

    private static final class ClientBucket {
        private final Bucket bucket;
        private volatile long lastSeen;

        private ClientBucket(Bucket bucket) {
            this.bucket = bucket;
            touch();
        }

        private Bucket bucket() {
            return bucket;
        }

        private long lastSeen() {
            return lastSeen;
        }

        private void touch() {
            lastSeen = System.currentTimeMillis();
        }
    }
}

