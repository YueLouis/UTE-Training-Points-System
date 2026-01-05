package vn.hcmute.trainingpoints.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.trainingpoints.repository.user.PasswordResetCodeRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetCleanupService {

    private final PasswordResetCodeRepository resetRepo;

    // chạy mỗi 60s
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cleanupExpiredUnused() {
        resetRepo.deleteAllByUsedAtIsNullAndExpiresAtBefore(LocalDateTime.now());
    }
}
