package vn.hcmute.trainingpoints.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.hcmute.trainingpoints.config.security.JwtUtil;
import vn.hcmute.trainingpoints.dto.auth.*;
import vn.hcmute.trainingpoints.entity.user.User;
import vn.hcmute.trainingpoints.repository.user.UserRepository;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Username is required");
        }
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Password is required");
        }

        // Tìm theo email, studentCode hoặc phone
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByStudentCode(username))
                .or(() -> userRepository.findByPhone(username))
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
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .build();

        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(authUser)
                .build();
    }

    /**
     * Refresh access token using refresh token
     */
    public RefreshTokenResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Refresh token is required");
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid refresh token");
        }

        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Refresh token expired");
        }

        String tokenType = jwtUtil.extractType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new ResponseStatusException(BAD_REQUEST, "Not a refresh token");
        }

        Long userId = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        if (user.getStatus() != null && !user.getStatus()) {
            throw new ResponseStatusException(FORBIDDEN, "Account is disabled");
        }

        // Generate new tokens
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

}
