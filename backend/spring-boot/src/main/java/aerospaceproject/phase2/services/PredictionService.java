package aerospaceproject.phase2.services;

import aerospaceproject.phase2.entities.GroundTruths;
import aerospaceproject.phase2.entities.ModelRegistry;
import aerospaceproject.phase2.entities.Prediction;
import aerospaceproject.phase2.repositories.PredictionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PredictionService {
    private final PredictionRepository predictionRepository;

    public PredictionService(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    public Prediction savePrediction(ModelRegistry model,
                                     LocalDate predictionDate,
                                     LocalDate targetDate,
                                     Integer horizonDays,
                                     Double predictedValue,
                                     Map<String, Object> featuresJson,
                                     String notes) {
        Prediction prediction = new Prediction();
        prediction.setModel(model);
        prediction.setPredictionDate(predictionDate);
        prediction.setTargetDate(targetDate);
        prediction.setHorizonDays(horizonDays);
        prediction.setPredictedValue(predictedValue);
        prediction.setFeatures(featuresJson);
        prediction.setNotes(notes);
        prediction.setRequestedAt(OffsetDateTime.now());

        return predictionRepository.save(prediction);
    }

    public List<Prediction> getAll() {
        return predictionRepository.findAll();
    }

    public List<Prediction> getRecent() {
        return predictionRepository.findTop10ByOrderByRequestedAtDesc();
    }

    public List<Prediction> getByModel(String modelId) {
        return predictionRepository.findByModel_ModelId(modelId);
    }

    public Optional<Prediction> findByModelAndTargetDate(ModelRegistry model, LocalDate targetDate) {
        return predictionRepository.findByModelAndTargetDate(model, targetDate);
    }

    public Double computeAbsoluteError(Prediction prediction, GroundTruths gt) {
        return Math.abs(prediction.getPredictedValue() - gt.getActualValue());
    }
}
