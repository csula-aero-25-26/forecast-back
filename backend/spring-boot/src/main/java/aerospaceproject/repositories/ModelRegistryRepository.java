package aerospaceproject.repositories;
import aerospaceproject.entities.ModelRegistry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRegistryRepository
    extends JpaRepository<ModelRegistry, String> {
}
