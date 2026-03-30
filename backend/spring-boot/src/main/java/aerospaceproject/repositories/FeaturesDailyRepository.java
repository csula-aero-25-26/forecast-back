package aerospaceproject.repositories;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import aerospaceproject.entities.FeaturesDaily;

@Repository
public interface FeaturesDailyRepository extends JpaRepository<FeaturesDaily, LocalDate> {

}
