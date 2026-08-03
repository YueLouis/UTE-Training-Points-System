package vn.hcmute.trainingpoints.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vn.hcmute.trainingpoints.dto.event.EventDTO;
import vn.hcmute.trainingpoints.dto.event.EventRequest;
import vn.hcmute.trainingpoints.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // GET all (option: studentId để FE biết trạng thái đăng ký)
    @GetMapping
    public List<EventDTO> getAllEvents(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
            Authentication authentication
    ) {
        return eventService.searchEvents(
                authorizedStudentId(studentId, authentication),
                semesterId,
                categoryId,
                q
        );
    }

    // GET by id
    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/by-category/{categoryId}")
    public List<EventDTO> getEventsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Long studentId,
            Authentication authentication
    ) {
        Long authorizedStudentId = authorizedStudentId(studentId, authentication);
        if (authorizedStudentId != null) {
            return eventService.getEventsByCategoryForStudent(categoryId, authorizedStudentId);
        }
        return eventService.getEventsByCategory(categoryId);
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO createEvent(
            @Valid @RequestBody EventRequest req,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return eventService.createEvent(req, currentUserId);
    }

    // UPDATE
    @PutMapping("/{id}")
    public EventDTO updateEvent(@PathVariable Long id,
                                @Valid @RequestBody EventRequest req) {
        return eventService.updateEvent(id, req);
    }

    // CLOSE (soft delete)
    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeEvent(@PathVariable Long id) {
        eventService.closeEvent(id);
    }

    // HARD DELETE (nếu thực sự cần)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    private Long authorizedStudentId(Long requestedStudentId, Authentication authentication) {
        if (requestedStudentId == null) {
            return null;
        }

        if (authentication == null || !(authentication.getPrincipal() instanceof Long currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authentication is required for student-specific event data");
        }

        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        if (!admin && !requestedStudentId.equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot view another student's registration status");
        }

        return requestedStudentId;
    }
}
