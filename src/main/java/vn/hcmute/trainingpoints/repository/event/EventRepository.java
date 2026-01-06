package vn.hcmute.trainingpoints.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.hcmute.trainingpoints.entity.event.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByCategoryId(Long categoryId);

    List<Event> findBySemesterId(Long semesterId);
}
