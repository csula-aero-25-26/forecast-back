package aerospaceproject.phase2.services;

import aerospaceproject.phase2.entities.GroundTruths;
import aerospaceproject.phase2.entities.ModelRegistry;
import aerospaceproject.phase2.entities.Predictions;
import aerospaceproject.phase2.repositories.PredictionsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PredictionsService {
    private final PredictionsRepository predictionsRepository;

    public PredictionsService(PredictionsRepository predictionsRepository) {
        this.predictionsRepository = predictionsRepository;
    }

    public Predictions savePrediction(ModelRegistry model,
                                      LocalDate predictionDate,
                                      LocalDate targetDate,
                                      Integer horizonDays,
                                      Double predictedValue,
                                      String featuresJson,
                                      String notes) {
        Predictions prediction = new Predictions();
        prediction.setModel(model);
        prediction.setPredictionDate(predictionDate);
        prediction.setTargetDate(targetDate);
        prediction.setHorizonDays(horizonDays);
        prediction.setPredictedValue(predictedValue);
        prediction.setFeatures(featuresJson);
        prediction.setNotes(notes);
        prediction.setRequestedAt(OffsetDateTime.now());

        return predictionsRepository.save(prediction);
    }

    public List<Predictions> getAll() {
        return predictionsRepository.findAll();
    }

    public List<Predictions> getRecent() {
        return predictionsRepository.findTop10ByOrderByRequestedAtDesc();
    }

    public List<Predictions> getByModel(String modelId) {
        return predictionsRepository.findByModel_ModelId(modelId);
    }

    public Double computeAbsoluteError(Predictions prediction, GroundTruths gt) {
        return Math.abs(prediction.getPredictedValue() - gt.getActualValue());
    }
}
