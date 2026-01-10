package vn.hcmute.trainingpoints.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResendEmailService {

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${app.mail.from}")
    private String mailFrom;

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Send password reset link via Resend API
     * @param to recipient email
     * @param resetUrl full reset link (includes token)
     */
    public void sendResetLink(String to, String resetUrl) {
        String subject = "UTE Training Points - Reset Your Password";
        String htmlBody = buildResetEmail(resetUrl);

        String jsonPayload = """
        {
            "from": "%s",
            "to": ["%s"],
            "subject": "%s",
            "html": %s
        }
        """.formatted(
            escapeJson(mailFrom),
            escapeJson(to),
            escapeJson(subject),
            toJsonString(htmlBody)
        );

        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://api.resend.com/emails")
                .addHeader("Authorization", "Bearer " + resendApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String error = response.body() != null ? response.body().string() : "Unknown error";
                log.error("Failed to send reset email to {}: {} - {}", to, response.code(), error);
                throw new RuntimeException("Email sending failed: " + error);
            }
            log.info("Reset email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Error sending reset email", e);
            throw new RuntimeException("Failed to send reset link", e);
        }
    }

    /**
     * Build HTML email template for password reset
     */
    private String buildResetEmail(String resetUrl) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; background-color: #f5f5f5; }
                .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; }
                .header { text-align: center; margin-bottom: 30px; }
                .header h2 { color: #333; margin: 0; }
                .content { color: #555; line-height: 1.6; }
                .button { display: inline-block; padding: 12px 24px; background-color: #0066cc; color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                .footer { font-size: 12px; color: #999; text-align: center; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                .warning { background-color: #fff3cd; color: #856404; padding: 10px; border-radius: 4px; margin-top: 20px; font-size: 13px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h2>Reset Your Password</h2>
                </div>
                <div class="content">
                    <p>Hello,</p>
                    <p>We received a request to reset the password for your UTE Training Points account.</p>
                    <p>Click the button below to set a new password. This link expires in <strong>15 minutes</strong>.</p>
                    <div style="text-align: center;">
                        <a href="%s" class="button">Reset Password</a>
                    </div>
                    <p>Or paste this link in your browser:</p>
                    <p style="word-break: break-all; background-color: #f5f5f5; padding: 10px; border-radius: 4px;">%s</p>
                    <div class="warning">
                        ⚠️ If you did not request this password reset, please ignore this email. Your account is safe.
                    </div>
                </div>
                <div class="footer">
                    <p>UTE Training Points System | University of Technology Education</p>
                    <p>© 2026. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(resetUrl, resetUrl);
    }

    /**
     * Convert string to JSON string (with escaping)
     */
    private static String toJsonString(String s) {
        return "\"" + escapeJson(s) + "\"";
    }

    /**
     * Escape special characters for JSON
     */
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

