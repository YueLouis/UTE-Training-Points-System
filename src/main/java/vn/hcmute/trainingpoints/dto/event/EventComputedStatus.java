package vn.hcmute.trainingpoints.dto.event;

public enum EventComputedStatus {
    CLOSED,                 // admin đóng
    OPEN_FOR_REGISTRATION,  // còn hạn đăng ký
    REGISTRATION_CLOSED,    // hết hạn đăng ký nhưng chưa bắt đầu
    ONGOING,                // đang diễn ra
    ENDED                   // đã kết thúc
}
