package aerospaceproject.services;

import aerospaceproject.dto.GroundTruthDTO;
import aerospaceproject.dto.PredictionHistoryDTO;
import aerospaceproject.entities.GroundTruth;
import aerospaceproject.entities.ModelRegistry;
import aerospaceproject.entities.Prediction;
import aerospaceproject.repositories.PredictionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionService {
    private final PredictionRepository predictionRepository;
    private final FetchServiceClient fetchServiceClient;

    public PredictionService(PredictionRepository predictionRepository,
                             FetchServiceClient fetchServiceClient) {
        this.predictionRepository = predictionRepository;
        this.fetchServiceClient = fetchServiceClient;
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

    // Returns past predictions sorted
    public List<Map<String, Object>> getPredictionHistory(String modelId) {
        List<Prediction> predictions =
                predictionRepository.findByModel_ModelIdOrderByTargetDateAsc(modelId);
        List<Map<String, Object>> result = new ArrayList();

        for (Prediction p : predictions) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("targetDate", p.getTargetDate());
            entry.put("value", p.getPredictedValue());
            result.add(entry);
        }

        return result;
    }

    // To be used for the History Graph
    public List<PredictionHistoryDTO> getHistory(String modelId) {

        List<Prediction> predictions =
                predictionRepository.findByModel_ModelIdOrderByTargetDate(modelId);

        List<GroundTruthDTO> groundTruths =
                fetchServiceClient.getGroundTruths(365);

        Map<LocalDate, Double> truthMap = groundTruths.stream()
                .collect(Collectors.toMap(
                        GroundTruthDTO::getObservation_date,
                        GroundTruthDTO::getActual_flux
                ));

        List<PredictionHistoryDTO> result = new ArrayList<>();

        for (Prediction p : predictions) {
            Double actual = truthMap.get(p.getTargetDate());

            result.add(new PredictionHistoryDTO(
                    p.getTargetDate(),
                    p.getPredictedValue(),
                    actual
            ));
        }

        return result;
    }

//    public Double computeAbsoluteError(Prediction prediction, GroundTruth gt) {
//        return Math.abs(prediction.getPredictedValue() - gt.getActualValue());
//    }
}
