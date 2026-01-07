package vn.hcmute.trainingpoints.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.point.PointType;

import java.util.Optional;

public interface PointTypeRepository extends JpaRepository<PointType, Long> {
    Optional<PointType> findByCode(String code);
}
