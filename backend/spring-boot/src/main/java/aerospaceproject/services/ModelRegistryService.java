package aerospaceproject.services;

import aerospaceproject.entities.ModelRegistry;
import aerospaceproject.repositories.ModelRegistryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModelRegistryService {

    private final ModelRegistryRepository repo;

    public ModelRegistryService(ModelRegistryRepository repo) {
        this.repo = repo;
    }

    public List<ModelRegistry> getAllModels() {
        return repo.findAll();
    }

    public Optional<ModelRegistry> getModel(String id) {
        return repo.findById(id);
    }

    public ModelRegistry saveModel(ModelRegistry model) {
        return repo.save(model);
    }
}
