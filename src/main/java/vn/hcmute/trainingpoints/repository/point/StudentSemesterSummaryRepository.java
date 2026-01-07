package vn.hcmute.trainingpoints.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.point.StudentSemesterSummary;

import java.util.Optional;

public interface StudentSemesterSummaryRepository extends JpaRepository<StudentSemesterSummary, Long> {
    Optional<StudentSemesterSummary> findByStudentIdAndSemesterId(Long studentId, Long semesterId);
}
