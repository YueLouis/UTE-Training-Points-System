package vn.hcmute.trainingpoints.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.trainingpoints.entity.event.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategoryId(Long categoryId);

    List<Event> findBySemesterId(Long semesterId);
}
