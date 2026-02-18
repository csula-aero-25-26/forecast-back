package aerospaceproject.phase1.services;

import aerospaceproject.phase1.entities.F107Prediction;
import aerospaceproject.phase1.repositories.F107PredictionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class F107PredictionService {

    private final F107PredictionRepository repo;
    private final RestTemplate restTemplate;

    public F107PredictionService(
            F107PredictionRepository f107PredictionRepository,
            RestTemplate restTemplate) {
        this.repo = f107PredictionRepository;
        this.restTemplate = restTemplate;
    }

    public List<F107Prediction> getAllPredictions() {
        return repo.findAll();
    }

    public Optional<F107Prediction> getPrediction(Long id) {
        return repo.findById(id);
    }

    public F107Prediction savePrediction(F107Prediction prediction) {
        return repo.save(prediction);
    }
}
