package aerospaceproject.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "feature_catalog")
public class FeatureCatalog {

    @Id
    @Column(name = "feature_id", nullable = false)
    private String featureId;

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

    @ManyToMany(mappedBy = "features")
    private Set<ModelRegistry> models = new HashSet<>();

    protected FeatureCatalog() {}

    public FeatureCatalog(String featureId,
                          String name,
                          String source,
                          String transformation,
                          String description) {
        this.featureId = featureId;
        this.name = name;
        this.source = source;
        this.transformation = transformation;
        this.description = description;
        this.createdAt = Instant.now();
    }

    public String getFeatureId() {
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
