package vn.hcmute.trainingpoints.controller.point;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.trainingpoints.dto.point.StudentSummaryDTO;
import vn.hcmute.trainingpoints.service.point.PointService;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@CrossOrigin(origins="*")
public class PointController {

    private final PointService pointService;

    // GET /api/points/summary/{studentId}?semesterId=1
    @GetMapping("/summary/{studentId}")
    public ResponseEntity<StudentSummaryDTO> getSummary(
            @PathVariable Long studentId,
            @RequestParam Long semesterId
    ) {
        return ResponseEntity.ok(pointService.getSummary(studentId, semesterId));
    }
}
