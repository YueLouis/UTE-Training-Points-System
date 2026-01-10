package vn.hcmute.trainingpoints.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.trainingpoints.entity.point.StudentPointsCumulative;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPointsCumulativeRepository extends JpaRepository<StudentPointsCumulative, Long> {

    /**
     * Find cumulative by student and point code (CTXH, CDNN)
     */
    Optional<StudentPointsCumulative> findByStudentIdAndPointCode(Long studentId, String pointCode);

    /**
     * Get all cumulative for a student
     */
    List<StudentPointsCumulative> findByStudentId(Long studentId);

    /**
     * Check if student has cumulative records
     */
    boolean existsByStudentId(Long studentId);
}

