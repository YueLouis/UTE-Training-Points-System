package vn.hcmute.trainingpoints.service.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hcmute.trainingpoints.dto.registration.EventRegistrationDTO;
import vn.hcmute.trainingpoints.dto.registration.EventRegistrationRequest;
import vn.hcmute.trainingpoints.entity.event.Event;
import vn.hcmute.trainingpoints.entity.event.EventMode;
import vn.hcmute.trainingpoints.entity.event.EventStatus;
import vn.hcmute.trainingpoints.entity.registration.EventRegistration;
import vn.hcmute.trainingpoints.entity.registration.EventRegistrationStatus;
import vn.hcmute.trainingpoints.repository.event.EventRepository;
import vn.hcmute.trainingpoints.repository.registration.EventRegistrationRepository;
import vn.hcmute.trainingpoints.service.point.PointService;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final PointService pointService;

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    private EventRegistration getRegOrNull(Long eventId, Long studentId) {
        return eventRegistrationRepository.findByEventIdAndStudentId(eventId, studentId)
                .orElse(null);
    }

    // Map entity → DTO
    private EventRegistrationDTO toDTO(EventRegistration reg) {
        return EventRegistrationDTO.builder()
                .id(reg.getId())
                .eventId(reg.getEventId())
                .studentId(reg.getStudentId())
                .registrationTime(reg.getRegistrationTime())
                .status(reg.getStatus())
                .checkinTime(reg.getCheckinTime())
                .checkoutTime(reg.getCheckoutTime())
                .note(reg.getNote())
                .build();
    }

    // 1) Đăng ký tham gia sự kiện (cả ATTENDANCE và ONLINE đều dùng được)
    @Transactional
    public EventRegistrationDTO register(EventRegistrationRequest request) {
        Event event = getEventOrThrow(request.getEventId());

        // closed
        if (event.getStatus() == EventStatus.CLOSED) {
            throw new RuntimeException("Event is closed");
        }

        // trùng đăng ký (có record và chưa cancel)
        eventRegistrationRepository.findByEventIdAndStudentId(request.getEventId(), request.getStudentId())
                .ifPresent(r -> {
                    if (r.getStatus() != EventRegistrationStatus.CANCELLED) {
                        throw new RuntimeException("Student already registered for this event");
                    }
                });

        // deadline
        if (event.getRegistrationDeadline() != null &&
                LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration deadline has passed");
        }

        // ATTENDANCE: chặn đăng ký khi đã bắt đầu
        if (event.getEventMode() == null || event.getEventMode() == EventMode.ATTENDANCE) {
            if (event.getStartTime() != null && !LocalDateTime.now().isBefore(event.getStartTime())) {
                throw new RuntimeException("Event already started");
            }
        }

        // slot
        if (event.getMaxParticipants() != null) {
            long current = eventRegistrationRepository
                    .countByEventIdAndStatusNot(event.getId(), EventRegistrationStatus.CANCELLED);
            if (current >= event.getMaxParticipants()) {
                throw new RuntimeException("Event is full");
            }
        }

        EventRegistration registration = EventRegistration.builder()
                .eventId(request.getEventId())
                .studentId(request.getStudentId())
                .registrationTime(LocalDateTime.now())
                .status(EventRegistrationStatus.REGISTERED)
                .note(request.getNote())
                .build();

        registration = eventRegistrationRepository.save(registration);
        return toDTO(registration);
    }

    // 2) Danh sách đăng ký của 1 sinh viên
    public List<EventRegistrationDTO> getByStudent(Long studentId) {
        return eventRegistrationRepository.findByStudentId(studentId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 3) Danh sách sinh viên đăng ký theo event
    public List<EventRegistrationDTO> getByEvent(Long eventId) {
        return eventRegistrationRepository.findByEventId(eventId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 4) Hủy đăng ký
    @Transactional
    public EventRegistrationDTO cancel(Long registrationId) {
        EventRegistration reg = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi đăng ký"));

        if (reg.getStatus() != EventRegistrationStatus.REGISTERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể hủy khi ở trạng thái Đã đăng ký");
        }

        reg.setStatus(EventRegistrationStatus.CANCELLED);
        return toDTO(eventRegistrationRepository.save(reg));
    }

    // 5) Check-in (CHỈ cho ATTENDANCE)
    @Transactional
    public EventRegistrationDTO checkin(Long eventId, Long studentId) {
        Event event = getEventOrThrow(eventId);

        if (event.getStatus() == EventStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sự kiện đã đóng");
        }
        if (event.getEventMode() == EventMode.ONLINE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đây là sự kiện ONLINE. Vui lòng hoàn thành khảo sát.");
        }

        EventRegistration reg = getRegOrNull(eventId, studentId);

        if (reg == null) {
            reg = EventRegistration.builder()
                    .eventId(eventId)
                    .studentId(studentId)
                    .registrationTime(LocalDateTime.now())
                    .status(EventRegistrationStatus.CHECKED_IN)
                    .checkinTime(LocalDateTime.now())
                    .build();
        } else {
            if (reg.getStatus() == EventRegistrationStatus.CANCELLED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đăng ký này đã bị hủy");
            }
            if (reg.getCheckinTime() == null) {
                reg.setCheckinTime(LocalDateTime.now());
            }
            reg.setStatus(EventRegistrationStatus.CHECKED_IN);
        }

        reg = eventRegistrationRepository.save(reg);
        return toDTO(reg);
    }

    // 6) Check-out (CHỈ cho ATTENDANCE) -> COMPLETED + cộng điểm
    @Transactional
    public EventRegistrationDTO checkout(Long eventId, Long studentId) {
        Event event = getEventOrThrow(eventId);

        if (event.getStatus() == EventStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sự kiện đã đóng");
        }
        if (event.getEventMode() == EventMode.ONLINE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đây là sự kiện ONLINE. Dùng completeSurvey thay vì checkout.");
        }

        EventRegistration reg = eventRegistrationRepository
                .findByEventIdAndStudentId(eventId, studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu điểm danh. Vui lòng check-in trước."));

        if (reg.getStatus() == EventRegistrationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đăng ký này đã bị hủy");
        }
        if (reg.getCheckinTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải check-in trước khi check-out");
        }

        // Idempotent
        if (reg.getCheckoutTime() != null || reg.getStatus() == EventRegistrationStatus.COMPLETED) {
            return toDTO(reg);
        }

        reg.setCheckoutTime(LocalDateTime.now());
        reg.setStatus(EventRegistrationStatus.COMPLETED);
        reg = eventRegistrationRepository.save(reg);

        pointService.awardPointsForCompletedEvent(eventId, studentId, null);

        // auto-close nếu đủ slot
        autoCloseIfFull(event);

        return toDTO(reg);
    }

    // 7) ONLINE: hoàn thành khảo sát -> COMPLETED + cộng điểm (KHÔNG bắt buộc register trước)
    @Transactional
    public EventRegistrationDTO completeSurvey(Long eventId, Long studentId, String secretCode) {
        Event event = getEventOrThrow(eventId);

        if (event.getStatus() == EventStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sự kiện đã đóng");
        }
        if (event.getEventMode() != EventMode.ONLINE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đây là sự kiện OFFLINE. Vui lòng check-in/check-out.");
        }

        // Kiểm tra mã bí mật (Nếu Admin có thiết lập)
        if (event.getSurveySecretCode() != null && !event.getSurveySecretCode().isBlank()) {
            if (secretCode == null || !event.getSurveySecretCode().equals(secretCode)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã bí mật không đúng. Vui lòng tìm mã ở cuối bài khảo sát.");
            }
        }

        // survey url bắt buộc có
        if (event.getSurveyUrl() == null || event.getSurveyUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu link khảo sát");
        }

        // deadline dùng làm hạn nộp survey
        if (event.getRegistrationDeadline() != null &&
                LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đã quá hạn hoàn thành khảo sát");
        }

        // slot: nếu đã full thì không cho complete mới
        if (event.getMaxParticipants() != null) {
            long current = eventRegistrationRepository
                    .countByEventIdAndStatusNot(event.getId(), EventRegistrationStatus.CANCELLED);
            if (current >= event.getMaxParticipants()) {
                // đóng luôn cho nhất quán
                event.setStatus(EventStatus.CLOSED);
                eventRepository.save(event);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sự kiện đã đủ số lượng tham gia");
            }
        }

        EventRegistration reg = getRegOrNull(eventId, studentId);

        // nếu chưa có record -> tạo luôn, rồi complete
        if (reg == null) {
            reg = EventRegistration.builder()
                    .eventId(eventId)
                    .studentId(studentId)
                    .registrationTime(LocalDateTime.now())
                    .status(EventRegistrationStatus.COMPLETED)
                    .checkoutTime(LocalDateTime.now()) // dùng checkoutTime làm completedAt
                    .note("ONLINE_SURVEY")
                    .build();
        } else {
            if (reg.getStatus() == EventRegistrationStatus.CANCELLED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đăng ký này đã bị hủy");
            }
            // Idempotent
            if (reg.getStatus() == EventRegistrationStatus.COMPLETED || reg.getCheckoutTime() != null) {
                return toDTO(reg);
            }
            reg.setCheckoutTime(LocalDateTime.now());
            reg.setStatus(EventRegistrationStatus.COMPLETED);
        }

        reg = eventRegistrationRepository.save(reg);

        // cộng điểm
        pointService.awardPointsForCompletedEvent(eventId, studentId, null);

        // auto-close nếu đủ slot
        autoCloseIfFull(event);

        return toDTO(reg);
    }

    @Transactional
    public EventRegistrationDTO checkinById(Long registrationId) {
        EventRegistration reg = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        return checkin(reg.getEventId(), reg.getStudentId());
    }

    @Transactional
    public EventRegistrationDTO checkoutById(Long registrationId) {
        EventRegistration reg = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        return checkout(reg.getEventId(), reg.getStudentId());
    }

    private void autoCloseIfFull(Event event) {
        if (event.getMaxParticipants() == null) return;

        long current = eventRegistrationRepository
                .countByEventIdAndStatusNot(event.getId(), EventRegistrationStatus.CANCELLED);

        if (current >= event.getMaxParticipants()) {
            event.setStatus(EventStatus.CLOSED);
            eventRepository.save(event);
        }
    }
}
