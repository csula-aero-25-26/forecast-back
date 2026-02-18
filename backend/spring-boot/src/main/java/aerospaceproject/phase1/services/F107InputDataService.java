package aerospaceproject.phase1.services;

import aerospaceproject.phase1.entities.F107InputData;
import aerospaceproject.phase1.repositories.F107InputDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class F107InputDataService {

    private final F107InputDataRepository repo;
    private final RestTemplate restTemplate;

    public F107InputDataService(
            F107InputDataRepository f107InputDataRepository,
            RestTemplate restTemplate) {
        this.repo = f107InputDataRepository;
        this.restTemplate = restTemplate;
    }

    public List<F107InputData> getAllInputs() {
        return repo.findAll();
    }

    public Optional<F107InputData> getInput(Long id) {
        return repo.findById(id);
    }

    public F107InputData saveInput(F107InputData input) {
        return repo.save(input);
    }
}
