package vn.hcmute.utetrainingpointssystem.model.semester;

public class SemesterDTO {
    public Long id;
    public String code;
    public String name;
    public String startDate;
    public String endDate;
    public Integer status;

    public SemesterDTO() {}

    public SemesterDTO(Long id, String code, String name, String startDate, String endDate, Integer status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
}

