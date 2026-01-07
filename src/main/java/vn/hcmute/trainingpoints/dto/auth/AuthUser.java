package vn.hcmute.trainingpoints.dto.auth;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthUser {
    private Long id;
    private String studentCode;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String className;
    private String faculty;
    private String avatarUrl;
    private Boolean status;
}
