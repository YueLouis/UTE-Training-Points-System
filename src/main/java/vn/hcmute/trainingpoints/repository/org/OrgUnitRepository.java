package vn.hcmute.trainingpoints.repository.org;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.trainingpoints.entity.org.OrgUnit;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgUnitRepository extends JpaRepository<OrgUnit, Long> {

    Optional<OrgUnit> findByCode(String code);

    List<OrgUnit> findByType(String type);

    List<OrgUnit> findByParentId(Long parentId);

    List<OrgUnit> findByIsActiveTrue();
}

