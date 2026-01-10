package vn.hcmute.trainingpoints.repository.org;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.hcmute.trainingpoints.entity.org.UserRoleScoped;

import java.util.List;

@Repository
public interface UserRoleScopedRepository extends JpaRepository<UserRoleScoped, Long> {

    List<UserRoleScoped> findByUserId(Long userId);

    List<UserRoleScoped> findByUserIdAndScopeOrgUnitId(Long userId, Long scopeOrgUnitId);

    @Query("SELECT DISTINCT urs.roleId FROM UserRoleScoped urs WHERE urs.userId = :userId")
    List<Long> findRoleIdsByUserId(Long userId);
}

