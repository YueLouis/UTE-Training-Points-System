package vn.hcmute.trainingpoints.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetRepo;

    private static final long EXPIRE_SECONDS = 10 * 60; // 10 phút
    private static final SecureRandom RNG = new SecureRandom();

    public ForgotPasswordResponse requestReset(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is required");
        }

        String emailNorm = email.trim();

        User user = userRepository.findByEmail(emailNorm)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Email not found"));

        // ✅ Invalidate mã cũ còn hiệu lực (nếu có)
        resetRepo.findTopByEmailOrderByCreatedAtDesc(user.getEmail()).ifPresent(old -> {
            if (old.getUsedAt() == null && old.getExpiresAt() != null
                    && old.getExpiresAt().isAfter(LocalDateTime.now())) {
                old.setUsedAt(LocalDateTime.now()); // đóng mã cũ
                resetRepo.save(old);
            }
        });

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

        row = resetRepo.save(row);

        return ForgotPasswordResponse.builder()
                .email(user.getEmail())
                .resetId(row.getId())
                .expiresInSec(EXPIRE_SECONDS)
                .demoCode(code) // ✅ DEMO ONLY
                .build();
    }

    public AuthResponse verify(String email, String code) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is required");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Code is required");
        }

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Email not found"));

        PasswordResetCode latest = resetRepo.findTopByEmailOrderByCreatedAtDesc(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "No reset request found"));

        if (latest.getUsedAt() != null) {
            throw new ResponseStatusException(CONFLICT, "Code already used");
        }
        if (latest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "Code expired");
        }

        String inputHash = sha256(code.trim());
        if (!inputHash.equalsIgnoreCase(latest.getCodeHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid code");
        }

        latest.setUsedAt(LocalDateTime.now());
        resetRepo.save(latest);

        String fakeToken = "demo-" + UUID.randomUUID(); // ✅ token giả

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
                .token(fakeToken)
                .user(authUser)
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
