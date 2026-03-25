package aerospaceproject.controllers;

import aerospaceproject.services.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final PredictionService predictionService;

    public MetricsController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(
            @RequestParam(required = false) String modelId
    ) {
        return ResponseEntity.ok(
          predictionService.getHistory(modelId)
        );
    }
}
