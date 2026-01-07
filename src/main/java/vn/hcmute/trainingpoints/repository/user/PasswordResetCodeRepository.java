package vn.hcmute.trainingpoints.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.user.PasswordResetCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findTopByEmailOrderByCreatedAtDesc(String email);

    void deleteAllByEmailAndUsedAtIsNull(String email);

    void deleteAllByUsedAtIsNullAndExpiresAtBefore(java.time.LocalDateTime time);

    List<PasswordResetCode> findAllByEmailAndUsedAtAfter(String email, LocalDateTime usedAt);
}
