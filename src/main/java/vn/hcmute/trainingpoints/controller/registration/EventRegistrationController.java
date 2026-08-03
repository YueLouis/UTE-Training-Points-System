package vn.hcmute.trainingpoints.controller.registration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.trainingpoints.dto.registration.EventRegistrationDTO;
import vn.hcmute.trainingpoints.dto.registration.EventRegistrationRequest;
import vn.hcmute.trainingpoints.service.registration.EventRegistrationService;

import java.util.List;

@RestController
@RequestMapping("/api/event-registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    // POST /api/event-registrations
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or #request.studentId == authentication.principal")
    public ResponseEntity<EventRegistrationDTO> register(
            @Valid @RequestBody EventRegistrationRequest request
    ) {
        return ResponseEntity.ok(eventRegistrationService.register(request));
    }

    // GET /api/event-registrations/by-student/{studentId}
    @GetMapping("/by-student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or #studentId == authentication.principal")
    public ResponseEntity<List<EventRegistrationDTO>> getByStudent(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(eventRegistrationService.getByStudent(studentId));
    }

    // GET /api/event-registrations/by-event/{eventId}
    @GetMapping("/by-event/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventRegistrationDTO>> getByEvent(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(eventRegistrationService.getByEvent(eventId));
    }

    // PUT /api/event-registrations/{id}/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<EventRegistrationDTO> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return ResponseEntity.ok(eventRegistrationService.cancel(id, currentUserId));
    }

    // PUT /api/event-registrations/{eventId}/checkin/{studentId}
    @PutMapping("/{eventId}/checkin/{studentId}")
    public ResponseEntity<EventRegistrationDTO> checkin(
            @PathVariable Long eventId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return ResponseEntity.ok(eventRegistrationService.checkin(eventId, studentId, currentUserId));
    }

    // PUT /api/event-registrations/{eventId}/checkout/{studentId}
    @PutMapping("/{eventId}/checkout/{studentId}")
    public ResponseEntity<EventRegistrationDTO> checkout(
            @PathVariable Long eventId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return ResponseEntity.ok(eventRegistrationService.checkout(eventId, studentId, currentUserId));
    }

    @PutMapping("/{id}/check-in")
    public ResponseEntity<EventRegistrationDTO> checkinById(
            @PathVariable Long id,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return ResponseEntity.ok(eventRegistrationService.checkinById(id, currentUserId));
    }

    @PutMapping("/{id}/check-out")
    public ResponseEntity<EventRegistrationDTO> checkoutById(
            @PathVariable Long id,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return ResponseEntity.ok(eventRegistrationService.checkoutById(id, currentUserId));
    }

    // PUT /api/event-registrations/{eventId}/complete-survey/{studentId}
    @PutMapping("/{eventId}/complete-survey/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or #studentId == authentication.principal")
    public ResponseEntity<EventRegistrationDTO> completeSurvey(
            @PathVariable Long eventId,
            @PathVariable Long studentId,
            @RequestParam(required = false) String secretCode
    ) {
        return ResponseEntity.ok(eventRegistrationService.completeSurvey(eventId, studentId, secretCode));
    }
}
