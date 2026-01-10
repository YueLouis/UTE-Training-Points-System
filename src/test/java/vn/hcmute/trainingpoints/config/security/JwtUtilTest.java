package vn.hcmute.trainingpoints.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testGenerateAndValidateAccessToken() {
        Long userId = 1L;
        String role = "STUDENT";

        String token = jwtUtil.generateAccessToken(userId, role);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(userId, jwtUtil.extractUserId(token));
        assertEquals(role, jwtUtil.extractRole(token));
        assertEquals("access", jwtUtil.extractType(token));
    }

    @Test
    void testGenerateAndValidateRefreshToken() {
        Long userId = 2L;

        String token = jwtUtil.generateRefreshToken(userId);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(userId, jwtUtil.extractUserId(token));
        assertEquals("refresh", jwtUtil.extractType(token));
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertFalse(jwtUtil.validateToken(invalidToken));
    }
}

