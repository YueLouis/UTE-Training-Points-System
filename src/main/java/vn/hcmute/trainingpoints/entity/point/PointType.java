package vn.hcmute.trainingpoints.entity.point;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_types")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PointType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="code", length = 20, unique = true, nullable = false)
    private String code; // DRL, CTXH, CDDN

    @Column(name="name", length = 100, nullable = false)
    private String name;

    @Column(name="description", columnDefinition = "text")
    private String description;
}
