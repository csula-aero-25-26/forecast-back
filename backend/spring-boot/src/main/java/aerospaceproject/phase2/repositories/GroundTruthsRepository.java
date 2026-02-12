package aerospaceproject.phase2.repositories;

import aerospaceproject.phase2.entities.GroundTruths;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GroundTruthsRepository extends JpaRepository<GroundTruths, LocalDate> {

    Optional<GroundTruths> findByObservationDate(LocalDate observationDate);
}
