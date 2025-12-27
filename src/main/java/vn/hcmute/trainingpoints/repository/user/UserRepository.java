package vn.hcmute.trainingpoints.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentCode(String studentCode);
}
