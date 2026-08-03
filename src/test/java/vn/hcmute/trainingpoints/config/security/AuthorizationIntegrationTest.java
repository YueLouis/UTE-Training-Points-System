package vn.hcmute.trainingpoints.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.trainingpoints.entity.user.User;
import vn.hcmute.trainingpoints.repository.user.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User student;
    private User otherStudent;

    @BeforeEach
    void setUp() {
        student = userRepository.save(user("security.student@hcmute.edu.vn", "23162001"));
        otherStudent = userRepository.save(user("security.other@hcmute.edu.vn", "23162002"));
    }

    @Test
    void studentCanReadOwnProfileWithoutPasswordHash() throws Exception {
        mockMvc.perform(get("/api/users/{id}", student.getId())
                        .header("Authorization", bearerFor(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void studentCannotReadAnotherProfile() throws Exception {
        mockMvc.perform(get("/api/users/{id}", otherStudent.getId())
                        .header("Authorization", bearerFor(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void refreshTokenCannotBeUsedAsApiAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/{id}", student.getId())
                        .header("Authorization", "Bearer " + jwtUtil.generateRefreshToken(student.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentCannotRegisterAnotherStudent() throws Exception {
        String body = """
                {"eventId":999,"studentId":%d}
                """.formatted(otherStudent.getId());

        mockMvc.perform(post("/api/event-registrations")
                        .header("Authorization", bearerFor(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousCallerCannotRequestStudentSpecificEventState() throws Exception {
        mockMvc.perform(get("/api/events")
                        .queryParam("studentId", student.getId().toString()))
                .andExpect(status().isForbidden());
    }

    private User user(String email, String studentCode) {
        return User.builder()
                .studentCode(studentCode)
                .email(email)
                .fullName("Security Test Student")
                .passwordHash(passwordEncoder.encode("password123"))
                .role("STUDENT")
                .status(true)
                .build();
    }

    private String bearerFor(User user) {
        return "Bearer " + jwtUtil.generateAccessToken(user.getId(), user.getRole());
    }
}
