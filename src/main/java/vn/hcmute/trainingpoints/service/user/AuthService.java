package vn.hcmute.trainingpoints.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.hcmute.trainingpoints.dto.auth.*;
import vn.hcmute.trainingpoints.entity.user.PasswordResetCode;
import vn.hcmute.trainingpoints.entity.user.User;
import vn.hcmute.trainingpoints.repository.user.PasswordResetCodeRepository;
import vn.hcmute.trainingpoints.repository.user.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetRepo;
    private final OtpMailService otpMailService;

    // ✅ OTP 120 giây (config được)
    @Value("${app.reset.expireSeconds:120}")
    private long EXPIRE_SECONDS;

    private static final SecureRandom RNG = new SecureRandom();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse login(LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Username is required");
        }
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Password is required");
        }

        // Tìm theo email hoặc studentCode
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByStudentCode(username))
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid username or password");
        }

        if (user.getStatus() != null && !user.getStatus()) {
            throw new ResponseStatusException(FORBIDDEN, "Account is disabled");
        }

        AuthUser authUser = AuthUser.builder()
                .id(user.getId())
                .studentCode(user.getStudentCode())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .className(user.getClassName())
                .faculty(user.getFaculty())
                .status(user.getStatus())
                .build();

        return AuthResponse.builder()
                .token("MOCK_TOKEN_" + user.getId() + "_" + System.currentTimeMillis())
                .user(authUser)
                .build();
    }

    /**
     * Step 1: Request OTP
     */
    @Transactional
    public SimpleMessageResponse requestReset(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is required");
        }

        String emailNorm = email.trim();

        User user = userRepository.findByEmail(emailNorm)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Email not found"));

        // ✅ Xoá toàn bộ OTP chưa dùng của email này (đúng ý em: chỉ giữ used trong DB)
        resetRepo.deleteAllByEmailAndUsedAtIsNull(user.getEmail());

        // ✅ Tạo OTP 6 số
        String code = random6Digits();
        String codeHash = sha256(code);

        LocalDateTime now = LocalDateTime.now();
        PasswordResetCode row = PasswordResetCode.builder()
                .email(user.getEmail())
                .codeHash(codeHash)
                .createdAt(now)
                .expiresAt(now.plusSeconds(EXPIRE_SECONDS))
                .usedAt(null)
                .build();

        resetRepo.save(row);

        // ✅ Gửi mail thật
        otpMailService.sendOtp(user.getEmail(), code, (int) EXPIRE_SECONDS);

        return SimpleMessageResponse.builder()
                .message("OTP sent")
                .build();
    }

    /**
     * Step 2: Verify OTP (KHÔNG set used_at)
     */
    @Transactional(readOnly = true)
    public SimpleMessageResponse verify(String email, String code, String otp) {
        String finalCode = code != null ? code : otp;
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is required");
        }
        if (finalCode == null || finalCode.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Code/OTP is required");
        }

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Email not found"));

        PasswordResetCode latest = resetRepo.findTopByEmailOrderByCreatedAtDesc(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "No reset request found"));

        if (latest.getUsedAt() != null) {
            throw new ResponseStatusException(CONFLICT, "Code already used");
        }
        if (latest.getExpiresAt() == null || latest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "Code expired");
        }

        String inputHash = sha256(finalCode.trim());
        if (!inputHash.equalsIgnoreCase(latest.getCodeHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid code");
        }

        return SimpleMessageResponse.builder()
                .message("OTP valid")
                .build();
    }

    /**
     * Step 3: Reset password (set used_at = now)
     */
    @Transactional
    public SimpleMessageResponse resetPassword(String email, String code, String newPassword) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is required");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Code is required");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "New password is required");
        }
        if (newPassword.length() < 6) {
            throw new ResponseStatusException(BAD_REQUEST, "Password must be at least 6 characters");
        }

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Email not found"));

        PasswordResetCode latest = resetRepo.findTopByEmailOrderByCreatedAtDesc(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "No reset request found"));

        if (latest.getUsedAt() != null) {
            throw new ResponseStatusException(CONFLICT, "Code already used");
        }
        if (latest.getExpiresAt() == null || latest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "Code expired");
        }

        String inputHash = sha256(code.trim());
        if (!inputHash.equalsIgnoreCase(latest.getCodeHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid code");
        }

        // ✅ Update password (đổi field cho đúng entity User của em)
        // Nếu entity em là user.setPassword(...); thì sửa dòng này:
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // ✅ Mark OTP used
        latest.setUsedAt(LocalDateTime.now());
        resetRepo.save(latest);

        return SimpleMessageResponse.builder()
                .message("Password updated")
                .build();
    }

    private static String random6Digits() {
        int x = RNG.nextInt(900000) + 100000;
        return String.valueOf(x);
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(dig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
