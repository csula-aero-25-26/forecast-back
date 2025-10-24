package aerospaceproject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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

    public F107Prediction savePrediction(F107Prediction prediction) {
        return repo.save(prediction);
    }
}
