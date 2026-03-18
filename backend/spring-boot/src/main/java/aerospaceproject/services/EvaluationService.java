package aerospaceproject.services;

import aerospaceproject.entities.GroundTruth;
import aerospaceproject.entities.Prediction;
import aerospaceproject.repositories.GroundTruthRepository;
import aerospaceproject.repositories.PredictionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EvaluationService {

    private final PredictionRepository predictionRepository;
    private final GroundTruthRepository groundTruthRepository;

    public EvaluationService(PredictionRepository predictionRepository,
                             GroundTruthRepository groundTruthRepository) {
        this.predictionRepository = predictionRepository;
        this.groundTruthRepository = groundTruthRepository;
    }

    public Map<String, Object> evaluateModel(String modelId) {

        List<Prediction> predictions =
                predictionRepository.findByModel_ModelId(modelId);

        double sumAbsError = 0;
        double sumSquaredError = 0;
        int count = 0;

        for (Prediction p : predictions) {
            Optional<GroundTruth> gtOpt =
                    groundTruthRepository.findByObservationDate(p.getTargetDate());

            if (gtOpt.isEmpty()) continue;

            double actual = gtOpt.get().getActualValue();
            double predicted = p.getPredictedValue();

            double error = predicted - actual;

            sumAbsError += Math.abs(error);
            sumSquaredError += error * error;
            count++;
        }

        double mae = count > 0 ? sumAbsError / count: 0;
        double rmse = count > 0 ? Math.sqrt(sumSquaredError / count) : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("modelId", modelId);
        result.put("mae", mae);
        result.put("rmse", rmse);
        result.put("count", count);

        return result;
    }
}
