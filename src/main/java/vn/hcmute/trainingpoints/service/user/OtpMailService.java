package vn.hcmute.trainingpoints.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpMailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String from;

    public void sendOtp(String toEmail, String otpCode, int expireSeconds) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        if (from != null && !from.isBlank()) msg.setFrom(from);

        msg.setSubject("UTE Training Points - Password Reset OTP");
        msg.setText(
                "Your OTP code is: " + otpCode + "\n" +
                        "This code expires in " + expireSeconds + " seconds.\n\n" +
                        "If you did not request this, ignore this email."
        );
        mailSender.send(msg);
    }
}
