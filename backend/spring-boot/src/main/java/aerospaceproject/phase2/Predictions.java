package aerospaceproject.phase2;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

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

    @Column(name = "features", columnDefinition = "jsonb")
    private String features;

    @Column(name = "notes")
    private String notes;

    protected Predictions() {}

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
}
