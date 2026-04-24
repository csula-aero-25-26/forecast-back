package aerospaceproject.repositories;

import aerospaceproject.entities.ModelRegistry;
import aerospaceproject.entities.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByModel_ModelId(String modelId);

//    List<Prediction> findByTargetDate(LocalDate targetDate);

    Optional<Prediction> findByModelAndTargetDate(ModelRegistry model, LocalDate targetDate);

    List<Prediction> findByModel_ModelIdOrderByTargetDateAsc(String modelId);

    List<Prediction> findTop10ByOrderByRequestedAtDesc();

    List<Prediction> findByModel_ModelIdOrderByTargetDate(String modelId);
}
