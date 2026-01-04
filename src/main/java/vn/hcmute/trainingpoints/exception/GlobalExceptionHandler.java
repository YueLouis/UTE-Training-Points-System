package vn.hcmute.trainingpoints.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> body(HttpStatus status, String message, HttpServletRequest req) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "path", req.getRequestURI(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        );
    }

    @ExceptionHandler(PointsAlreadyAwardedException.class)
    public ResponseEntity<?> handlePointsAlreadyAwarded(PointsAlreadyAwardedException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.CONFLICT;
        return ResponseEntity.status(st).body(body(st, ex.getMessage(), req));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();

        if (msg != null && msg.contains("uq_point_tx_student_semester_event")) {
            HttpStatus st = HttpStatus.CONFLICT;
            return ResponseEntity.status(st).body(body(st, "Points already awarded for this event", req));
        }

        HttpStatus st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st).body(body(st, "Data integrity violation", req));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.valueOf(ex.getStatusCode().value());
        String msg = ex.getReason() == null ? "Request failed" : ex.getReason();
        return ResponseEntity.status(st).body(body(st, msg, req));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        HttpStatus st = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(st).body(body(st, msg, req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(st).body(body(st, ex.getMessage(), req));
    }
}
