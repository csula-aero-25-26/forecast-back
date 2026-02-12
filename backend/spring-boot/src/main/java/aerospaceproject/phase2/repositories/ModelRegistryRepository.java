package aerospaceproject.phase2.repositories;
import aerospaceproject.phase2.entities.ModelRegistry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRegistryRepository
    extends JpaRepository<ModelRegistry, String> {
}
