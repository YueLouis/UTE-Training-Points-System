package vn.hcmute.trainingpoints.dto.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.hcmute.trainingpoints.entity.event.EventMode;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {
    @NotNull(message = "Học kỳ không được để trống")
    private Long semesterId;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;
    private String location;
    private String bannerUrl;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endTime;

    private LocalDateTime registrationDeadline;

    @Min(value = 0, message = "Số lượng người tham gia không được âm")
    private Integer maxParticipants;

    @NotNull(message = "Loại điểm không được để trống")
    private Long pointTypeId;

    @Min(value = 0, message = "Giá trị điểm không được âm")
    private Integer pointValue;

    private Long createdBy;

    private EventMode eventMode;
    private String surveyUrl;
    private String surveySecretCode;
}
