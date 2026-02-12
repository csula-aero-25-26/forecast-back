package aerospaceproject.phase2.repositories;

import aerospaceproject.phase2.entities.Predictions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PredictionsRepository extends JpaRepository<Predictions, Long> {
    List<Predictions> findByModel_ModelId(String modelId);

    List<Predictions> findByTargetDate(LocalDate targetDate);

    List<Predictions> findTop10ByOrderByRequestedAtDesc();
}
