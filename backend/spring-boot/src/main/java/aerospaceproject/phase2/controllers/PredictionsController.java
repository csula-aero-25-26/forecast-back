package aerospaceproject.phase2.controllers;

import aerospaceproject.phase2.entities.Predictions;
import aerospaceproject.phase2.services.PredictionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class PredictionsController {

    private final PredictionsService predictionService;

    public PredictionsController(PredictionsService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public List<Predictions> getAllPredictions() {
        return predictionService.getAll();
    }

    @GetMapping("/recent")
    public List<Predictions> getRecentPredictions() {
        return predictionService.getRecent();
    }

    @GetMapping("/model/{modelId}")
    public List<Predictions> getByModel(@PathVariable String modelId) {
        return predictionService.getByModel(modelId);
    }
}
