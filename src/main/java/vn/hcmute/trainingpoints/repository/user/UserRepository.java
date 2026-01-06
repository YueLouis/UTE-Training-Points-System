package vn.hcmute.trainingpoints.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.hcmute.trainingpoints.entity.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByStudentCode(String studentCode);

    // add
    Optional<User> findByEmail(String email);
}
