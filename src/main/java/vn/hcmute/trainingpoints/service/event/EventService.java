package vn.hcmute.trainingpoints.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import vn.hcmute.trainingpoints.dto.event.EventComputedStatus;
import vn.hcmute.trainingpoints.dto.event.EventDTO;
import vn.hcmute.trainingpoints.dto.event.EventRequest;
import vn.hcmute.trainingpoints.entity.event.Event;
import vn.hcmute.trainingpoints.entity.event.EventMode;
import vn.hcmute.trainingpoints.entity.event.EventStatus;
import vn.hcmute.trainingpoints.entity.registration.EventRegistration;
import vn.hcmute.trainingpoints.entity.registration.EventRegistrationStatus;
import vn.hcmute.trainingpoints.repository.event.EventRepository;
import vn.hcmute.trainingpoints.repository.registration.EventRegistrationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;

    public List<EventDTO> searchEvents(Long studentId, Long semesterId, Long categoryId, String q) {
        Specification<Event> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (semesterId != null) {
                predicates.add(cb.equal(root.get("semesterId"), semesterId));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (q != null && !q.isBlank()) {
                String searchPattern = "%" + q.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), searchPattern),
                        cb.like(cb.lower(root.get("description")), searchPattern)
                ));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        // Sắp xếp theo ID giảm dần (Mới nhất hiện lên đầu)
        List<Event> events = eventRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
        
        // Nếu có studentId -> map info đã đăng ký
        final Map<Long, EventRegistration> regMap;
        if (studentId != null) {
            regMap = eventRegistrationRepository.findByStudentId(studentId)
                    .stream()
                    .collect(Collectors.toMap(
                            EventRegistration::getEventId,
                            Function.identity(),
                            (a, b) -> (b.getId() != null && a.getId() != null && b.getId() > a.getId()) ? b : a
                    ));
        } else {
            regMap = Map.of();
        }

        LocalDateTime now = LocalDateTime.now();
        return events.stream()
                .map(e -> {
                    EventDTO dto = toDTO(e);
                    applyFlags(dto, e, regMap.get(e.getId()), now);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // --------- MAPPING ---------
    private EventDTO toDTO(Event e) {
        if (e == null) return null;

        return EventDTO.builder()
                .id(e.getId())
                .semesterId(e.getSemesterId())
                .categoryId(e.getCategoryId())
                .title(e.getTitle())
                .description(e.getDescription())
                .location(e.getLocation())
                .bannerUrl(e.getBannerUrl())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .registrationDeadline(e.getRegistrationDeadline())
                .maxParticipants(e.getMaxParticipants())
                .pointTypeId(e.getPointTypeId())
                .pointValue(e.getPointValue())
                .createdBy(e.getCreatedBy())
                .status(e.getStatus())
                .eventMode(e.getEventMode())
                .surveyUrl(e.getSurveyUrl())
                .surveySecretCode(e.getSurveySecretCode())
                .build();
    }

    private void updateEntityFromRequest(Event event, EventRequest req) {
        event.setSemesterId(req.getSemesterId());
        event.setCategoryId(req.getCategoryId());
        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        event.setLocation(req.getLocation());
        event.setBannerUrl(req.getBannerUrl());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setRegistrationDeadline(req.getRegistrationDeadline());
        event.setMaxParticipants(req.getMaxParticipants());
        event.setPointTypeId(req.getPointTypeId());
        event.setPointValue(req.getPointValue());
        event.setCreatedBy(req.getCreatedBy());

        // status set riêng (OPEN/CLOSED)

        event.setEventMode(req.getEventMode());
        event.setSurveyUrl(req.getSurveyUrl());
        event.setSurveySecretCode(req.getSurveySecretCode());
    }

    // --------- READ ---------
    public List<EventDTO> getAllEvents() {
        // admin view: không cần student flags
        return eventRepository.findAll()
                .stream()
                .map(e -> {
                    EventDTO dto = toDTO(e);
                    // vẫn set computed flags để FE dùng được
                    applyFlags(dto, e, null, LocalDateTime.now());
                    return dto;
                })
                .toList();
    }

    /**
     * FE view: trả danh sách event nhưng có thêm info student đã đăng ký/chk/chkout chưa.
     * GET /api/events?studentId=2
     */
    public List<EventDTO> getAllEventsForStudent(Long studentId) {
        Map<Long, EventRegistration> regMap = eventRegistrationRepository.findByStudentId(studentId)
                .stream()
                .collect(Collectors.toMap(
                        EventRegistration::getEventId,
                        Function.identity(),
                        (a, b) -> {
                            if (a.getId() == null) return b;
                            if (b.getId() == null) return a;
                            return b.getId() > a.getId() ? b : a;
                        }
                ));

        LocalDateTime now = LocalDateTime.now();

        return eventRepository.findAll()
                .stream()
                .map(e -> {
                    EventDTO dto = toDTO(e);
                    EventRegistration reg = regMap.get(e.getId());
                    applyFlags(dto, e, reg, now);
                    return dto;
                })
                .toList();
    }

    public List<EventDTO> getEventsByCategory(Long categoryId) {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByCategoryId(categoryId)
                .stream()
                .map(e -> {
                    EventDTO dto = toDTO(e);
                    applyFlags(dto, e, null, now);
                    return dto;
                })
                .toList();
    }

    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        EventDTO dto = toDTO(event);
        applyFlags(dto, event, null, LocalDateTime.now());
        return dto;
    }

    // --------- CREATE ---------
    public EventDTO createEvent(EventRequest req) {
        Event event = new Event();
        updateEntityFromRequest(event, req);

        event.setStatus(EventStatus.OPEN);

        // default mode
        if (event.getEventMode() == null) event.setEventMode(EventMode.ATTENDANCE);

        Event saved = eventRepository.save(event);
        EventDTO dto = toDTO(saved);
        applyFlags(dto, saved, null, LocalDateTime.now());
        return dto;
    }

    // --------- UPDATE ---------
    public EventDTO updateEvent(Long id, EventRequest req) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        updateEntityFromRequest(event, req);

        // nếu update mà mode null -> giữ default ATTENDANCE (tránh null DB)
        if (event.getEventMode() == null) event.setEventMode(EventMode.ATTENDANCE);

        Event saved = eventRepository.save(event);
        EventDTO dto = toDTO(saved);
        applyFlags(dto, saved, null, LocalDateTime.now());
        return dto;
    }

    // --------- CLOSE / DELETE ---------
    public void closeEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        event.setStatus(EventStatus.CLOSED);
        eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        eventRepository.deleteById(id);
    }

    public List<EventDTO> getEventsByCategoryForStudent(Long categoryId, Long studentId) {
        Map<Long, EventRegistration> regMap = eventRegistrationRepository.findByStudentId(studentId)
                .stream()
                .collect(Collectors.toMap(
                        EventRegistration::getEventId,
                        Function.identity(),
                        (a, b) -> (b.getId() != null && a.getId() != null && b.getId() > a.getId()) ? b : a
                ));

        LocalDateTime now = LocalDateTime.now();

        return eventRepository.findByCategoryId(categoryId)
                .stream()
                .map(e -> {
                    EventDTO dto = toDTO(e);
                    EventRegistration reg = regMap.get(e.getId());
                    applyFlags(dto, e, reg, now);
                    return dto;
                })
                .toList();
    }

    // --------- FLAGS & STATUS RULES ---------

    private EventComputedStatus computeStatus(Event e, LocalDateTime now) {
        if (e.getStatus() == EventStatus.CLOSED) return EventComputedStatus.CLOSED;

        LocalDateTime start = e.getStartTime();
        LocalDateTime end = e.getEndTime();

        // nếu thiếu time -> vẫn cho OPEN_FOR_REGISTRATION để FE hiển thị
        if (start == null || end == null) {
            // nếu đã quá deadline thì coi như REGISTRATION_CLOSED
            if (e.getRegistrationDeadline() != null && now.isAfter(e.getRegistrationDeadline())) {
                return EventComputedStatus.REGISTRATION_CLOSED;
            }
            return EventComputedStatus.OPEN_FOR_REGISTRATION;
        }

        if (now.isBefore(start)) {
            if (e.getRegistrationDeadline() != null && now.isAfter(e.getRegistrationDeadline())) {
                return EventComputedStatus.REGISTRATION_CLOSED;
            }
            return EventComputedStatus.OPEN_FOR_REGISTRATION;
        }

        if (!now.isAfter(end)) return EventComputedStatus.ONGOING;

        return EventComputedStatus.ENDED;
    }

    private void applyFlags(EventDTO dto, Event e, EventRegistration reg, LocalDateTime now) {
        // ---- student flags ----
        boolean registered = (reg != null) && reg.getStatus() != EventRegistrationStatus.CANCELLED;
        boolean checkedIn = (reg != null) && reg.getCheckinTime() != null;
        boolean completed = (reg != null) && (reg.getCheckoutTime() != null || reg.getStatus() == EventRegistrationStatus.COMPLETED);

        dto.setRegistered(registered);
        dto.setCheckedIn(checkedIn);
        dto.setCompleted(completed);

        if (reg != null) {
            dto.setRegistrationStatus(reg.getStatus() == null ? null : reg.getStatus().name());
            dto.setStudentCheckinTime(reg.getCheckinTime());
            dto.setStudentCheckoutTime(reg.getCheckoutTime());
        } else {
            dto.setRegistrationStatus(null);
            dto.setStudentCheckinTime(null);
            dto.setStudentCheckoutTime(null);
        }

        // ---- computed status ----
        EventComputedStatus cs = computeStatus(e, now);
        dto.setComputedStatus(cs);

        boolean open = e.getStatus() != EventStatus.CLOSED;

        // ---- canRegister ----
        boolean canRegister = open && !registered;

        // deadline chặn đăng ký (cả ATTENDANCE & ONLINE)
        if (e.getRegistrationDeadline() != null && now.isAfter(e.getRegistrationDeadline())) canRegister = false;

        // ATTENDANCE: chặn đăng ký khi đã bắt đầu
        if (e.getEventMode() == EventMode.ATTENDANCE) {
            if (e.getStartTime() != null && !now.isBefore(e.getStartTime())) canRegister = false;
        }
        // ONLINE: không chặn theo startTime (vì survey có thể làm trước/đúng hạn deadline)

        // full slot
        if (e.getMaxParticipants() != null) {
            long current = eventRegistrationRepository.countByEventIdAndStatusNot(e.getId(), EventRegistrationStatus.CANCELLED);
            if (current >= e.getMaxParticipants()) canRegister = false;
        }

        dto.setCanRegister(canRegister);

        // ---- canCheckin / canCheckout ----
        if (e.getEventMode() == null || e.getEventMode() == EventMode.ATTENDANCE) {
            // ATTENDANCE: checkin/checkout theo cs
            boolean canCheckin = open && (cs == EventComputedStatus.ONGOING) && registered && !checkedIn;
            boolean canCheckout = open && (cs == EventComputedStatus.ONGOING || cs == EventComputedStatus.ENDED) && checkedIn && !completed;

            dto.setCanCheckin(canCheckin);
            dto.setCanCheckout(canCheckout);
        } else {
            // ONLINE:
            dto.setCanCheckin(false);

            // ONLINE cho bấm "Complete survey" nếu:
            // - event OPEN
            // - có surveyUrl
            // - chưa completed
            // - chưa quá deadline (deadline dùng làm hạn nộp survey)
            boolean hasSurvey = e.getSurveyUrl() != null && !e.getSurveyUrl().isBlank();
            boolean beforeDeadline = (e.getRegistrationDeadline() == null) || now.isBefore(e.getRegistrationDeadline());

            // IMPORTANT: không bắt buộc registered trước (đúng ý em)
            boolean canCompleteSurvey = open && hasSurvey && beforeDeadline && !completed;

            dto.setCanCheckout(canCompleteSurvey); // reuse field canCheckout = "canCompleteSurvey" cho ONLINE
        }
    }
}
