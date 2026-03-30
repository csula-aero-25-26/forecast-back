package aerospaceproject.controllers;

import aerospaceproject.entities.Prediction;
import aerospaceproject.services.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public List<Prediction> getAllPredictions() {
        return predictionService.getAll();
    }

    @GetMapping("/recent")
    public List<Prediction> getRecentPredictions() {
        return predictionService.getRecent();
    }

    @GetMapping("/model/{modelId}")
    public List<Prediction> getByModel(@PathVariable String modelId) {
        return predictionService.getByModel(modelId);
    }

    @GetMapping("/model/{modelId}/history")
    public ResponseEntity<?> getHistory(@PathVariable String modelId) {
        return ResponseEntity.ok(predictionService.getPredictionHistory(modelId));
    }
}
