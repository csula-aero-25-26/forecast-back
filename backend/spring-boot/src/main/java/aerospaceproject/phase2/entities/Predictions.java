package aerospaceproject.phase2.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "predictions")
public class Predictions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_at", nullable = false)
    private OffsetDateTime requestedAt;

    @Column(name = "prediction_date", nullable = false)
    private LocalDate predictionDate;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "horizon_days", nullable = false)
    private Integer horizonDays;

    @Column(name = "predicted_value", nullable = false)
    private Double predictedValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelRegistry model;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> features;

    @Column(name = "notes")
    private String notes;

    public Predictions() {}

    public Predictions(ModelRegistry model,
                       LocalDate predictionDate,
                       LocalDate targetDate,
                       Integer horizonDays,
                       Double predictedValue) {
        this.model = model;
        this.predictionDate = predictionDate;
        this.targetDate = targetDate;
        this.horizonDays = horizonDays;
        this.predictedValue = predictedValue;
        this.requestedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(OffsetDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDate getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDate predictionDate) {
        this.predictionDate = predictionDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public Integer getHorizonDays() {
        return horizonDays;
    }

    public void setHorizonDays(Integer horizonDays) {
        this.horizonDays = horizonDays;
    }

    public Double getPredictedValue() {
        return predictedValue;
    }

    public void setPredictedValue(Double predictedValue) {
        this.predictedValue = predictedValue;
    }

    public ModelRegistry getModel() {
        return model;
    }

    public void setModel(ModelRegistry model) {
        this.model = model;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
