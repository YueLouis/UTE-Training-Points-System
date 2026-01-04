package vn.hcmute.trainingpoints.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.trainingpoints.dto.auth.*;
import vn.hcmute.trainingpoints.service.user.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/forgot-password/request")
    public ForgotPasswordResponse request(@RequestBody ForgotPasswordRequest body) {
        return authService.requestReset(body.getEmail());
    }

    @PostMapping("/forgot-password/verify")
    public AuthResponse verify(@RequestBody VerifySecretRequest body) {
        return authService.verify(body.getEmail(), body.getCode());
    }
}
