package vn.hcmute.trainingpoints.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.event.Semester;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
}
