package vn.hcmute.trainingpoints.repository.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.registration.EventRegistration;
import vn.hcmute.trainingpoints.entity.registration.EventRegistrationStatus;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository
        extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration> findByEventIdAndStudentId(Long eventId, Long studentId);

    long countByEventIdAndStatusNot(Long eventId, EventRegistrationStatus status);

    List<EventRegistration> findByStudentId(Long studentId);

    List<EventRegistration> findByEventId(Long eventId);
}
