package aerospaceproject.repositories;

import aerospaceproject.entities.ModelRegistry;
import aerospaceproject.entities.Prediction;
import aerospaceproject.dto.PredictionHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByModel_ModelId(String modelId);

    List<Prediction> findByTargetDate(LocalDate targetDate);

    Optional<Prediction> findByModelAndTargetDate(ModelRegistry model, LocalDate targetDate);

    List<Prediction> findByModel_ModelIdOrderByTargetDateAsc(String modelId);

    List<Prediction> findTop10ByOrderByRequestedAtDesc();

    @Query("""
            SELECT new aerospaceproject.dto.PredictionHistoryDTO(
                p.targetDate,
                p.predictedValue,
                g.actualValue
            )
            FROM Prediction p
            JOIN GroundTruth g
                ON p.targetDate = g.observationDate
            WHERE p.model.modelId = :modelId
            ORDER BY p.targetDate
           """)
    List<PredictionHistoryDTO> getPredictionHistory(String modelId);
}
