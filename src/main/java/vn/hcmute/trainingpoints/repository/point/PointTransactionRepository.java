package vn.hcmute.trainingpoints.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.point.PointTransaction;

import java.util.List;
import java.util.Optional;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    Optional<PointTransaction> findByStudentIdAndSemesterIdAndEventId(Long studentId, Long semesterId, Long eventId);
    List<PointTransaction> findByStudentIdAndSemesterId(Long studentId, Long semesterId);
}
