package aerospaceproject.phase2;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ground_truths")
public class GroundTruths {

    @Id
    @Column(name = "observation_date", nullable = false)
    private LocalDate observationDate;

    @Column(name = "actual_value", nullable = false)
    private Double actualValue;

    @Column(name = "source_meta", columnDefinition = "jsonb")
    private String sourceMeta;

    protected GroundTruths () {}

    public GroundTruths(LocalDate observationDate, Double actualValue) {
        this.observationDate = observationDate;
        this.actualValue = actualValue;
    }
}
