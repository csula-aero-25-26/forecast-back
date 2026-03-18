package aerospaceproject.phase2.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "model_registry")
public class ModelRegistry {

    @Id
    @Column(name = "model_id")
    private String modelId;

    @Column(name = "family", nullable = false)
    private String family;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "features", columnDefinition = "jsonb")
    private String features;


    protected ModelRegistry() {}

    public ModelRegistry(String modelId, String family, String description) {
        this.modelId = modelId;
        this.family = family;
        this.description = description;
        this.createdAt = Instant.now();
    }

    public String getModelId() {
        return modelId;
    }

    public String getFamily() {
        return family;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getFeatures() { return features; }

}
