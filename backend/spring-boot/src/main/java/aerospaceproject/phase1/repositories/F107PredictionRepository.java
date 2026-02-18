package aerospaceproject.phase1.repositories;

import aerospaceproject.phase1.entities.F107Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface F107PredictionRepository extends JpaRepository<F107Prediction, Long> {
    List<F107Prediction> findByDateBetween(LocalDate start, LocalDate end);
}
