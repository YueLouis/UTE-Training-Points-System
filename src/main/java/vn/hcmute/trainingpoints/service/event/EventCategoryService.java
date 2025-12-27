package vn.hcmute.trainingpoints.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hcmute.trainingpoints.entity.event.EventCategory;
import vn.hcmute.trainingpoints.repository.event.EventCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCategoryService {

    private final EventCategoryRepository eventCategoryRepository;

    public List<EventCategory> getAll() {
        return eventCategoryRepository.findAll();
    }

    public EventCategory getById(Long id) {
        return eventCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event category not found with id = " + id));
    }
}
