package aerospaceproject.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ground_truths")
public class GroundTruth {

    @Id
    @Column(name = "observation_date", nullable = false)
    private LocalDate observationDate;

    @Column(name = "actual_value", nullable = false)
    private Double actualValue;

    @Column(name = "source_meta", columnDefinition = "jsonb")
    private String sourceMeta;

    protected GroundTruth() {}

    public GroundTruth(LocalDate observationDate, Double actualValue) {
        this.observationDate = observationDate;
        this.actualValue = actualValue;
    }

    public LocalDate getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(LocalDate observationDate) {
        this.observationDate = observationDate;
    }

    public Double getActualValue() {
        return actualValue;
    }

    public void setActualValue(Double actualValue) {
        this.actualValue = actualValue;
    }

    public String getSourceMeta() {
        return sourceMeta;
    }

    public void setSourceMeta(String sourceMeta) {
        this.sourceMeta = sourceMeta;
    }
}
