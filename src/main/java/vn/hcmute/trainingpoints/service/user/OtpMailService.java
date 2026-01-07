package vn.hcmute.trainingpoints.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OtpMailService {

    @Value("${app.resend.apiKey:}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtp(String toEmail, String otpCode, int expireSeconds) {
        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new RuntimeException("Resend API Key is missing");
        }

        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        Map<String, Object> body = new HashMap<>();
        // Lưu ý: Nếu chưa verify domain, Resend chỉ cho gửi từ onboarding@resend.dev tới chính mail đăng ký của bạn
        body.put("from", "UTE Training Points <onboarding@resend.dev>");
        body.put("to", toEmail);
        body.put("subject", "UTE Training Points - Password Reset OTP");
        body.put("html", "<p>Your OTP code is: <strong>" + otpCode + "</strong></p>" +
                         "<p>This code expires in " + expireSeconds + " seconds.</p>" +
                         "<p>If you did not request this, ignore this email.</p>");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForObject(url, request, String.class);
            System.out.println("Email sent successfully via Resend API to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email via Resend: " + e.getMessage());
            throw new RuntimeException("Email sending failed");
        }
    }
}
