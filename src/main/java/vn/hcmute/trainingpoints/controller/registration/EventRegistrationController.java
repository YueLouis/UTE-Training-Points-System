package vn.hcmute.trainingpoints.controller.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.trainingpoints.dto.registration.EventRegistrationDTO;
import vn.hcmute.trainingpoints.dto.registration.EventRegistrationRequest;
import vn.hcmute.trainingpoints.service.registration.EventRegistrationService;

import java.util.List;

@RestController
@RequestMapping("/api/event-registrations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    // POST /api/event-registrations
    @PostMapping
    public ResponseEntity<EventRegistrationDTO> register(
            @RequestBody EventRegistrationRequest request
    ) {
        return ResponseEntity.ok(eventRegistrationService.register(request));
    }

    // GET /api/event-registrations/by-student/{studentId}
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<EventRegistrationDTO>> getByStudent(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(eventRegistrationService.getByStudent(studentId));
    }

    // GET /api/event-registrations/by-event/{eventId}
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<EventRegistrationDTO>> getByEvent(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(eventRegistrationService.getByEvent(eventId));
    }

    // PUT /api/event-registrations/{id}/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<EventRegistrationDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(eventRegistrationService.cancel(id));
    }

    // PUT /api/event-registrations/{eventId}/checkin/{studentId}
    @PutMapping("/{eventId}/checkin/{studentId}")
    public ResponseEntity<EventRegistrationDTO> checkin(
            @PathVariable Long eventId,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(eventRegistrationService.checkin(eventId, studentId));
    }

    // PUT /api/event-registrations/{eventId}/checkout/{studentId}
    @PutMapping("/{eventId}/checkout/{studentId}")
    public ResponseEntity<EventRegistrationDTO> checkout(
            @PathVariable Long eventId,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(eventRegistrationService.checkout(eventId, studentId));
    }

    // PUT /api/event-registrations/{eventId}/complete-survey/{studentId}
    @PutMapping("/{eventId}/complete-survey/{studentId}")
    public ResponseEntity<EventRegistrationDTO> completeSurvey(
            @PathVariable Long eventId,
            @PathVariable Long studentId,
            @RequestParam(required = false) String secretCode
    ) {
        return ResponseEntity.ok(eventRegistrationService.completeSurvey(eventId, studentId, secretCode));
    }
}
