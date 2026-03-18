package aerospaceproject.phase2.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "feature_catalog")
public class FeatureCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_id")
    private Long featureId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "source")
    private String source;

    @Column(name = "transformation")
    private String transformation;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Transient
    private Set<ModelRegistry> models = new HashSet<>();

    protected FeatureCatalog() {}

    public FeatureCatalog(
            String name,
            String source,
            String transformation,
            String description
    ) {
        this.name = name;
        this.source = source;
        this.transformation = transformation;
        this.description = description;
        this.createdAt = Instant.now();
    }

    public Long getFeatureId() {
        return featureId;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getTransformation() {
        return transformation;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<ModelRegistry> getModels() {
        return models;
    }
}
