package vn.hcmute.trainingpoints.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.trainingpoints.entity.event.EventCategory;
import vn.hcmute.trainingpoints.service.event.EventCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/event-categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventCategoryController {

    private final EventCategoryService eventCategoryService;

    @GetMapping
    public List<EventCategory> getAll() {
        return eventCategoryService.getAll();
    }

    @GetMapping("/{id}")
    public EventCategory getById(@PathVariable Long id) {
        return eventCategoryService.getById(id);
    }
}
