package aerospaceproject;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class F107PredictionService {

    private final F107PredictionRepository repo;

    public F107PredictionService(
            F107PredictionRepository f107PredictionRepository
    ) {
        this.repo = f107PredictionRepository;
    }

    public List<F107Prediction> getAllPredictions() {
        return repo.findAll();
    }

    public F107Prediction savePrediction(F107Prediction prediction) {
        return repo.save(prediction);
    }
}
