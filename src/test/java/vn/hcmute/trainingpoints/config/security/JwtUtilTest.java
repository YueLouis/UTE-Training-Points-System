package vn.hcmute.trainingpoints.config.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

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

    @Test
    void refreshTokenCannotAuthenticateApiRequests() throws Exception {
        String refreshToken = jwtUtil.generateRefreshToken(7L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + refreshToken);

        jwtAuthenticationFilter.doFilter(
                request,
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void accessTokenAuthenticatesApiRequests() throws Exception {
        String accessToken = jwtUtil.generateAccessToken(7L, "STUDENT");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);

        jwtAuthenticationFilter.doFilter(
                request,
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(7L, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT")));
    }
}

