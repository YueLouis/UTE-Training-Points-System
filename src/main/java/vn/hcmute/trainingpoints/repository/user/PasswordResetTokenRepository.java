package vn.hcmute.trainingpoints.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.trainingpoints.entity.user.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find valid (unused & not expired) token by hash
     */
    Optional<PasswordResetToken> findTopByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
            String tokenHash,
            LocalDateTime now
    );

    /**
     * Find by user ID for cleanup
     */
    Optional<PasswordResetToken> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}

