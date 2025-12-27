package vn.hcmute.trainingpoints.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.event.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
}
