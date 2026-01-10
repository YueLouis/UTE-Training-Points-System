package vn.hcmute.trainingpoints.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import vn.hcmute.trainingpoints.config.security.JwtUtil;
import vn.hcmute.trainingpoints.dto.auth.AuthResponse;
import vn.hcmute.trainingpoints.dto.auth.LoginRequest;
import vn.hcmute.trainingpoints.dto.auth.RefreshTokenResponse;
import vn.hcmute.trainingpoints.entity.user.User;
import vn.hcmute.trainingpoints.repository.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .studentCode("23162099")
                .email("test@hcmute.edu.vn")
                .phone("0123456789")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Test User")
                .className("23162B")
                .faculty("CNTT")
                .role("STUDENT")
                .status(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void testLoginByEmail() {
        LoginRequest req = LoginRequest.builder()
                .username("test@hcmute.edu.vn")
                .password("password123")
                .build();

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("STUDENT", response.getUser().getRole());
        assertTrue(jwtUtil.validateToken(response.getToken()));
    }

    @Test
    void testLoginByStudentCode() {
        LoginRequest req = LoginRequest.builder()
                .username("23162099")
                .password("password123")
                .build();

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("STUDENT", response.getUser().getRole());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequest req = LoginRequest.builder()
                .username("test@hcmute.edu.vn")
                .password("wrongpassword")
                .build();

        assertThrows(ResponseStatusException.class, () -> {
            authService.login(req);
        });
    }

    @Test
    void testRefreshToken() {
        LoginRequest req = LoginRequest.builder()
                .username("test@hcmute.edu.vn")
                .password("password123")
                .build();

        AuthResponse loginResponse = authService.login(req);
        String refreshToken = loginResponse.getRefreshToken();

        RefreshTokenResponse refreshResponse = authService.refreshToken(refreshToken);

        assertNotNull(refreshResponse);
        assertNotNull(refreshResponse.getAccessToken());
        assertNotNull(refreshResponse.getRefreshToken());
        assertTrue(jwtUtil.validateToken(refreshResponse.getAccessToken()));
    }

    @Test
    void testRefreshTokenInvalid() {
        assertThrows(ResponseStatusException.class, () -> {
            authService.refreshToken("invalid.token.here");
        });
    }

    @Test
    void testPasswordResetFlow() {
        // Step 1: Request OTP
        assertDoesNotThrow(() -> {
            authService.requestReset(testUser.getEmail());
        });

        // Step 2: Verify OTP (in real test, would need to extract actual OTP)
        // This is simplified for demo

        // Step 3: Reset password
        // In production, would extract OTP from DB/console and verify it first
    }
}

