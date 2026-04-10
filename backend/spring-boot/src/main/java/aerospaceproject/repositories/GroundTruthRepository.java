// May be deprecated later or modified

package aerospaceproject.repositories;

import aerospaceproject.entities.GroundTruth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GroundTruthRepository extends JpaRepository<GroundTruth, LocalDate> {

    Optional<GroundTruth> findByObservationDate(LocalDate observationDate);
}
