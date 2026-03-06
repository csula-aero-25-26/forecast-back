package aerospaceproject.phase2.repositories;

import aerospaceproject.phase2.entities.ModelRegistry;
import aerospaceproject.phase2.entities.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByModel_ModelId(String modelId);

    List<Prediction> findByTargetDate(LocalDate targetDate);

    Optional<Prediction> findByModelAndTargetDate(ModelRegistry model, LocalDate targetDate);

    List<Prediction> findTop10ByOrderByRequestedAtDesc();
}
